using System;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using FujiXerox.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.DateAndTime;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class ValidateTransactionRequestSubscriber: SubscriberBase<ValidateBatchTransactionRequest>
    {
        private ValidateBatchTransactionRequestToDipsQueueMapper QueueMapper { get; set; }
        private ValidateBatchTransactionRequestToDipsNabChqScanPodMapper VoucherMapper { get; set; }
        private ValidateBatchTransactionRequestToDipsDbIndexMapper DbIndexMapper { get; set; }

        public ValidateTransactionRequestSubscriber(DipsConfiguration configuration, ILogger logger, RabbitMqConsumer consumer, RabbitMqExchange invalidExchange, string invalidRoutingKey, string recoverableRoutingKey) 
            : base(configuration, logger, consumer, invalidExchange, invalidRoutingKey, recoverableRoutingKey)
        {
            var transactionHelper = new BatchTransactionRequestMapHelper(new DateTimeProvider(), Configuration);
            QueueMapper = new ValidateBatchTransactionRequestToDipsQueueMapper(transactionHelper);
            VoucherMapper = new ValidateBatchTransactionRequestToDipsNabChqScanPodMapper(transactionHelper);
            DbIndexMapper = new ValidateBatchTransactionRequestToDipsDbIndexMapper(transactionHelper);
        }

        public override void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            base.Consumer_ReceiveMessage(message);
            if (!ContinueProcessing) return;
            try
            {
                //Mapping queue table
                var queue = QueueMapper.Map(Message);
                queue.CorrelationId = CorrelationId;
                queue.RoutingKey = RoutingKey;

                //Mapping voucher fields
                var vouchers = VoucherMapper.Map(Message);
                var jobIdentifier = CorrelationId;
                var batchNumber = string.IsNullOrEmpty(Message.voucherBatch.scannedBatchNumber) ? queue.S_BATCH : Message.voucherBatch.scannedBatchNumber;

                //Mapping index fields
                var dbIndexes = DbIndexMapper.Map(Message);

                using (var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
                {
                    using (var dbContext = new DipsDbContext(dbConnection))
                    {
                        using (var tx = dbContext.BeginTransaction())
                        {
                            try
                            {
                                //Adding to queue table
                                dbContext.Queues.Add(queue);
                                Log.Verbose("Adding new queue {@batchNumber} to the database", queue.S_BATCH);

                                dbContext.SaveChanges();

                                //Adding to voucher table
                                foreach (var voucher in vouchers)
                                {
                                    dbContext.NabChqPods.Add(voucher);
                                    Log.Verbose(
                                        "Adding new voucher {@batchNumber} - {@sequenceNumber} to the database",
                                        voucher.S_BATCH,
                                        voucher.S_SEQUENCE);
                                }

                                dbContext.SaveChanges();

                                //Adding to index table
                                foreach (var dbIndex in dbIndexes)
                                {
                                    dbContext.DbIndexes.Add(dbIndex);
                                    Log.Verbose("Adding new db index {@batchNumber} - {@sequenceNumber} to the database", dbIndex.BATCH, dbIndex.SEQUENCE);
                                }

                                dbContext.SaveChanges();

                                tx.Commit();

                                Log.Information("Successfully processed ValidateTransactionRequest '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a ValidateTransactionRequest '{@ValidationTransactionRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    Message, jobIdentifier);
                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a ValidateTransactionRequest '{@ValidationTransactionRequest}', '{@jobIdentifier}'",
                                    Message, jobIdentifier);
                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                        }
                    }
                    
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing ValidationTransactionRequest {@ValidationTransactionRequest}", Message);
                InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, CorrelationId);
            }
        }
    }
}