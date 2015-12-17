using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Linq;
using System.Threading.Tasks;
using Autofac;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors
{
    public class CorrectCodelineRequestProcessor : IMessageProcessor<CorrectBatchCodelineRequest>
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IMapper<CorrectBatchCodelineRequest, DipsQueue> dipsQueueMapper;
        private readonly IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>> dipsVoucherMapper;
        private readonly IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>> dipsDbIndexMapper;
        private readonly IImageMergeHelper imageMergeHelper;
        private readonly IAdapterConfiguration adapterConfiguration;

        public CorrectBatchCodelineRequest Message { get; set; }

        public CorrectCodelineRequestProcessor(
            ILifetimeScope component,
            IMapper<CorrectBatchCodelineRequest, DipsQueue> dipsQueueMapper,
            IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>> dipsVoucherMapper,
            IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>> dipsDbIndexMapper,
            IImageMergeHelper imageMergeHelper,
            IAdapterConfiguration adapterConfiguration,
            IDipsDbContext dbContext = null)
        {
            this.component = component;
            this.dbContext = dbContext;
            this.dipsQueueMapper = dipsQueueMapper;
            this.dipsVoucherMapper = dipsVoucherMapper;
            this.dipsDbIndexMapper = dipsDbIndexMapper;
            this.imageMergeHelper = imageMergeHelper;
            this.adapterConfiguration = adapterConfiguration;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            Log.Information("Processing CorrectCodelineRequest '{@request}', '{@correlationId}'", request, correlationId);

            try
            {
                //TFS [18420] - generate a batch number using a database sequence
                request.voucherBatch.scannedBatchNumber = string.IsNullOrEmpty(request.voucherBatch.scannedBatchNumber) ? GenerateBatchNumber() : request.voucherBatch.scannedBatchNumber;
                
                //Mapping queue table
                var queue = dipsQueueMapper.Map(request);
                queue.CorrelationId = correlationId;
                queue.RoutingKey = routingKey;

                //Mapping voucher fields
                var vouchers = dipsVoucherMapper.Map(request).ToList();
                var jobIdentifier = correlationId;
                var batchNumber = request.voucherBatch.scannedBatchNumber;
                var processingDate = request.voucher.First().processingDate;

                //Peform image merge for Dips
                await imageMergeHelper.EnsureMergedImageFilesExistAsync(jobIdentifier, batchNumber, processingDate);
                imageMergeHelper.PopulateMergedImageInfo(jobIdentifier, batchNumber, vouchers);

                //Mapping index fields
                var dbIndexes = dipsDbIndexMapper.Map(request).ToList();

                //TFS [25842] - fix inward for value long DRN handling rules
                if (request.voucher.Any(v => v.documentReferenceNumber.Length > 9))
                {
                    UpdateVoucherDrns(queue, vouchers, dbIndexes);
                }

                using (var lifetimeScope = component.BeginLifetimeScope())
                {
                    using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                    {
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //Adding to voucher table
                                foreach (var voucher in vouchers)
                                {
                                    dipsDbContext.NabChqPods.Add(voucher);
                                    Log.Verbose(
                                        "Adding new voucher {@batchNumber} - {@sequenceNumber} to the database",
                                        voucher.S_BATCH,
                                        voucher.S_SEQUENCE);
                                }

                                dipsDbContext.SaveChanges();

                                //Adding to index table
                                foreach (var dbIndex in dbIndexes)
                                {
                                    dipsDbContext.DbIndexes.Add(dbIndex);
                                    Log.Verbose(
                                        "Adding new db index {@batchNumber} - {@sequenceNumber} to the database",
                                        dbIndex.BATCH, dbIndex.SEQUENCE);
                                }

                                dipsDbContext.SaveChanges();

                                //Adding to queue table
                                dipsDbContext.Queues.Add(queue);
                                Log.Verbose("Adding new queue {@batchNumber} to the database", queue.S_BATCH);

                                dipsDbContext.SaveChanges();

                                tx.Commit();

                                Log.Information(
                                    "Successfully processed CorrectCodelineRequest '{@batchNumber}', '{@jobIdentifier}'",
                                    batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.
                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a CorrectCodelineRequest '{@CorrectionCodelineRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    request, jobIdentifier);

                                tx.Rollback();

                                throw;
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a CorrectCodelineRequest '{@CorrectionCodelineRequest}', '{@jobIdentifier}'",
                                    request, jobIdentifier);
                                tx.Rollback();

                                throw;
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CorrectionCodelineRequest {@CorrectionCodelineRequest}", request);
                throw;
            }
        }

        /// <summary>
        /// Long DRN’s need to be converted/regenerated to 9 digits
        /// </summary>
        /// <param name="queue"></param>
        /// <param name="vouchers"></param>
        /// <param name="dbIndexes"></param>
        private void UpdateVoucherDrns(DipsQueue queue, List<DipsNabChq> vouchers, List<DipsDbIndex> dbIndexes)
        {
            Dictionary<string, string> drnMap = GenerateDrnMap(vouchers);

            try
            {
                if (drnMap.ContainsKey(queue.S_TRACE))
                    queue.S_TRACE = drnMap[queue.S_TRACE];

                vouchers.ForEach(v =>
                {
                    if (drnMap.ContainsKey(v.doc_ref_num))
                    {
                        string shortDrn = drnMap[v.doc_ref_num];
                        v.S_TRACE = shortDrn;
                        v.trace = shortDrn;
                        v.doc_ref_num = shortDrn;
                    }
                });

                dbIndexes.ForEach(d =>
                {
                    if (drnMap.ContainsKey(d.TRACE))
                    {
                        d.TRACE = drnMap[d.TRACE];
                    }
                });
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CorrectBatchCodelineRequest.UpdateVoucherDrns");
                throw;
            }
        }

        /// <summary>
        /// Generate DRN mapping
        /// </summary>
        /// <param name="vouchers"></param>
        /// <returns></returns>
        private Dictionary<string, string> GenerateDrnMap(List<DipsNabChq> vouchers)
        {
            Dictionary<string, string> drnMap = new Dictionary<string, string>();

            try
            {
                using (var lifetimeScope = component.BeginLifetimeScope())
                {
                    using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                    {
                        foreach (var voucher in vouchers.Where(v => v.doc_ref_num.Length > 9))
                        {
                            if (drnMap.ContainsKey(voucher.doc_ref_num)) continue;

                            string drnSequence;

                            do
                            {
                                drnSequence = GenerateDrnSequence(dipsDbContext);
                            } while (vouchers.Any(v => v.doc_ref_num == drnSequence));
                            
                            drnMap.Add(voucher.doc_ref_num, drnSequence);

                            Log.Information(
                                "For batch {@scannedBatchNumber}. Voucher drn mapped from '{@documentReferenceNumber}' to generated drn '{@drnSequence}'",
                                voucher.batch, voucher.doc_ref_num, drnSequence);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CorrectBatchCodelineRequest.GenerateVoucherDrns");
                throw;
            }

            return drnMap;
        }
        
        /// <summary>
        /// Generate a drn
        /// </summary>
        /// <param name="dipsDbContext"></param>
        /// <returns></returns>
        private string GenerateDrnSequence(IDipsDbContext dipsDbContext)
        {
            var generatedSequence = dipsDbContext.GetNextFromDrnSequence();
            var drnSequence = string.Format(adapterConfiguration.DrnFormat, generatedSequence.ToString().PadLeft(6, '0'));
            return drnSequence;
        }

        /// <summary>
        /// Generate a batch number
        /// </summary>
        private string GenerateBatchNumber()
        {
            string batchSequence;

            try
            {
                using (var lifetimeScope = component.BeginLifetimeScope())
                {
                    using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                    {
                        var generatedSequence = dipsDbContext.GetNextFromBatchNumberSequence();
                        batchSequence = string.Format(adapterConfiguration.BatchNumberFormat, generatedSequence.ToString().PadLeft(5, '0'));
                        Log.Information("Generated batch sequence '{@batchSequence}'", batchSequence);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CorrectBatchCodelineRequest.GenerateBatchNumber");
                throw;
            }

            return batchSequence;
        }
    }
}