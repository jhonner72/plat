using System;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.DateAndTime;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class CorrectTransactionRequestSubscriber : SubscriberBase<CorrectBatchTransactionRequest>
    {
        private CorrectBatchTransactionRequestToDipsQueueMapper QueueMapper { get; set; }
        private CorrectBatchTransactionRequestToDipsNabChqScanPodMapper VoucherMapper { get; set; }
        private CorrectBatchTransactionRequestToDipsDbIndexMapper DbIndexMapper { get; set; }

        public CorrectTransactionRequestSubscriber(DipsConfiguration configuration, ILogger logger, RabbitMqConsumer consumer, RabbitMqExchange invalidExchange, string invalidRoutingKey, string recoverableRoutingKey) 
            : base(configuration, logger, consumer, invalidExchange, invalidRoutingKey, recoverableRoutingKey)
        {
            var helper = new BatchTransactionRequestMapHelper(new DateTimeProvider(), Configuration);
            QueueMapper = new CorrectBatchTransactionRequestToDipsQueueMapper(helper);
            VoucherMapper = new CorrectBatchTransactionRequestToDipsNabChqScanPodMapper(helper);
            DbIndexMapper = new CorrectBatchTransactionRequestToDipsDbIndexMapper(helper);
        }

        public override void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            base.Consumer_ReceiveMessage(message);
            if (!ContinueProcessing) return;
            var request = Message;

            Log.Information("Processing CorrectTransactionRequest '{@request}', '{@correlationId}'", request, CorrelationId);

            try
            {
                //Mapping queue table
                var queue = QueueMapper.Map(request);
                queue.CorrelationId = CorrelationId;
                queue.RoutingKey = RoutingKey;

                //Mapping voucher fields
                var vouchers = VoucherMapper.Map(request).ToList();
                var jobIdentifier = CorrelationId;
                var batchNumber = request.voucherBatch.scannedBatchNumber;
                var processingDate = request.voucher.First().voucher.processingDate;

                //Peform image merge for Dips
                var imageMergeHelper = new ImageMergeHelper(Configuration);
                imageMergeHelper.EnsureMergedImageFilesExist(jobIdentifier, batchNumber, processingDate);
                imageMergeHelper.PopulateMergedImageInfo(jobIdentifier, batchNumber, vouchers);

                //Mapping index fields
                var dbIndexes = DbIndexMapper.Map(request);

                using(var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
                using (var dipsDbContext =new DipsDbContext(dbConnection))
                {
                    using (var tx = dipsDbContext.BeginTransaction())
                    {
                        try
                        {
                            //Adding to queue table
                            dipsDbContext.Queues.Add(queue);
                            Log.Verbose("Adding new queue {@batchNumber} to the database", queue.S_BATCH);

                            dipsDbContext.SaveChanges();

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

                            tx.Commit();

                            Log.Information("Successfully processed CorrectTransactionRequest '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                        }
                        catch (OptimisticConcurrencyException)
                        {
                            //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                            //basically ignore the message by loggin a warning and rolling back.
                            //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                            Log.Warning(
                                "Could not create a CorrectTransactionRequest '{@CorrectTransactionRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                request, jobIdentifier);

                            tx.Rollback();
                            InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                        }
                        catch (Exception ex)
                        {
                            Log.Error(
                                ex,
                                "Could not complete and create a CorrectTransactionRequest '{@CorrectTransactionRequest}', '{@jobIdentifier}'",
                                request, jobIdentifier);
                            tx.Rollback();
                            InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CorrectTransactionRequest {@CorrectTransactionRequest}", request);
                InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, CorrelationId);
            }
        }
    }
}