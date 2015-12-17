using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Linq;
using System.Threading.Tasks;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Common.Queues;
using Serilog;
using Autofac;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;

#pragma warning disable 1998

namespace Lombard.Adapters.DipsAdapter.MessageProcessors
{
    public class GenerateBulkCreditRequestProcessor : IMessageProcessor<GenerateBatchBulkCreditRequest>
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IMapper<VoucherInformation[], DipsQueue> dipsQueueMapper;
        private readonly IMapper<VoucherInformation[], IEnumerable<DipsNabChq>> dipsVoucherMapper;
        private readonly IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>> dipsDbIndexMapper;
        private readonly IScannedBatchHelper scannedBatchHelper;
        private readonly IAdapterConfiguration adapterConfiguration;

        public GenerateBatchBulkCreditRequest Message { get; set; }

        public GenerateBulkCreditRequestProcessor(
            ILifetimeScope component,
            IMapper<VoucherInformation[], DipsQueue> dipsQueueMapper,
            IMapper<VoucherInformation[], IEnumerable<DipsNabChq>> dipsVoucherMapper,
            IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>> dipsDbIndexMapper,
            IScannedBatchHelper scannedBatchHelper,
            IAdapterConfiguration adapterConfiguration,
            IDipsDbContext dbContext = null)
        {
            this.component = component;
            this.dbContext = dbContext;
            this.dipsQueueMapper = dipsQueueMapper;
            this.dipsVoucherMapper = dipsVoucherMapper;
            this.dipsDbIndexMapper = dipsDbIndexMapper;
            this.scannedBatchHelper = scannedBatchHelper;
            this.adapterConfiguration = adapterConfiguration;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            Log.Information("Processing GenerateBulkCreditRequest '{@request}', '{@correlationId}'", request, correlationId);

            try
            {
                VoucherInformation[] bulkCreditVouchers = scannedBatchHelper.ReadScannedBatch(request, request.jobIdentifier, DateTime.Now);
                
                //TFS [18420] - generate a batch number using a database sequence
                string generatedBatchNumber = GenerateBatchNumber();
                SetBatchNumber(bulkCreditVouchers, generatedBatchNumber);

                //Mapping queue table
                var queue = dipsQueueMapper.Map(bulkCreditVouchers);
                queue.CorrelationId = correlationId;
                queue.RoutingKey = routingKey;

                //Mapping voucher fields
                var vouchers = dipsVoucherMapper.Map(bulkCreditVouchers).ToList();
                vouchers.ForEach(v => v.maxVouchers = request.maxDebitVouchers.ToString());

                var jobIdentifier = correlationId;

                //Mapping index fields
                var dbIndexes = dipsDbIndexMapper.Map(bulkCreditVouchers);

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

                                Log.Information("Successfully processed GenerateBulkCreditRequest '{@batchNumber}', '{@jobIdentifier}'", generatedBatchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a GenerateBulkCreditRequest '{@GenerateCorrespondingVoucherRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    request, jobIdentifier);

                                tx.Rollback();

                                throw;
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a GenerateBulkCreditRequest '{@GenerateCorrespondingVoucherRequest}', '{@jobIdentifier}'",
                                    request, jobIdentifier);
                                tx.Rollback();

                                throw;
                            }
                        }
                    }
                }

                Log.Information("Successfully processed GenerateBulkCreditRequest {@CorrelationId}", correlationId);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing GenerateBulkCreditRequest {@GenerateBulkCreditRequest}", request);
                throw;
            }
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
                        Int64 generatedSequence = dipsDbContext.GetNextFromBatchNumberSequence();
                        batchSequence = string.Format(adapterConfiguration.BatchNumberFormat, generatedSequence.ToString().PadLeft(5, '0'));
                        Log.Information("Generated batch sequence '{@batchSequence}'", batchSequence);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing GenerateBulkCreditRequest.GenerateBatchNumber");
                throw;
            }

            return batchSequence;
        }

        /// <summary>
        /// Update db entities with the generated batch number
        /// </summary>
        private void SetBatchNumber(VoucherInformation[] bulkCreditVouchers, string batchNumber)
        {
            try
            {
                bulkCreditVouchers.ToList().ForEach(v => { v.voucherBatch.scannedBatchNumber = batchNumber; });
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing GenerateBulkCreditRequest.SetBatchNumber");
                throw;
            }
        }
    }
}