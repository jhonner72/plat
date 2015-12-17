using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Threading.Tasks;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Common.Queues;
using Serilog;
using Autofac;
#pragma warning disable 1998

namespace Lombard.Adapters.DipsAdapter.MessageProcessors
{
    public class ValidateCodelineRequestProcessor : IMessageProcessor<ValidateBatchCodelineRequest>
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IMapper<ValidateBatchCodelineRequest, DipsQueue> dipsQueueMapper;
        private readonly IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsNabChq>> dipsVoucherMapper;
        private readonly IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsDbIndex>> dipsDbIndexMapper;

        public ValidateBatchCodelineRequest Message { get; set; }

        public ValidateCodelineRequestProcessor(
            ILifetimeScope component,
            IMapper<ValidateBatchCodelineRequest, DipsQueue> dipsQueueMapper,
            IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsNabChq>> dipsVoucherMapper,
            IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsDbIndex>> dipsDbIndexMapper,
            IDipsDbContext dbContext = null)
        {
            this.component = component;
            this.dbContext = dbContext;
            this.dipsQueueMapper = dipsQueueMapper;
            this.dipsVoucherMapper = dipsVoucherMapper;
            this.dipsDbIndexMapper = dipsDbIndexMapper;
        }

        //  using (var context = new DipsDbContext(new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["dips"].ConnectionString)))

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            Log.Information("Processing ValidateCodelineRequest '{@request}', '{@correlationId}'", request, correlationId);

            try
            {
                //Mapping queue table
                var queue = dipsQueueMapper.Map(request);
                queue.CorrelationId = correlationId;
                queue.RoutingKey = routingKey;

                //Mapping voucher fields
                var vouchers = dipsVoucherMapper.Map(request);
                var jobIdentifier = correlationId;
                var batchNumber = string.IsNullOrEmpty(request.voucherBatch.scannedBatchNumber) ? queue.S_BATCH : request.voucherBatch.scannedBatchNumber;

                //Mapping index fields
                var dbIndexes = dipsDbIndexMapper.Map(request);

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
                                    Log.Verbose("Adding new db index {@batchNumber} - {@sequenceNumber} to the database", dbIndex.BATCH, dbIndex.SEQUENCE);
                                }

                                dipsDbContext.SaveChanges();

                                //Adding to queue table
                                dipsDbContext.Queues.Add(queue);
                                Log.Verbose("Adding new queue {@batchNumber} to the database", queue.S_BATCH);

                                dipsDbContext.SaveChanges();

                                tx.Commit();

                                Log.Information("Successfully processed ValidateCodelineRequest '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a ValidateCodelineRequest '{@ValidationCodelineRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    request, jobIdentifier);

                                tx.Rollback();

                                throw;
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a ValidateCodelineRequest '{@ValidationCodelineRequest}', '{@jobIdentifier}'",
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
                Log.Error(ex, "Error processing ValidationCodelineRequest {@ValidationCodelineRequest}", request);
                throw;
            }
        }
    }
}
