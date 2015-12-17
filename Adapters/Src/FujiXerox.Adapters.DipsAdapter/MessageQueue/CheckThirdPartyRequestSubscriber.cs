using System;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Linq;
using EasyNetQ;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using FujiXerox.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.DateAndTime;
using Lombard.Common.MessageQueue;
using Serilog;
using IBasicGetResult = Lombard.Common.MessageQueue.IBasicGetResult;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class CheckThirdPartyRequestSubscriber : SubscriberBase<CheckThirdPartyBatchRequest>
    {
        private CheckThirdPartyBatchRequestToDipsQueueMapper QueueMapper { get; set; }
        private CheckThirdPartyRequestToDipsNabChqScanPodMapper VoucherMapper { get; set; }
        private CheckThirdPartyBatchRequestToDipsDbIndexMapper DbIndexMapper { get; set; }

        public CheckThirdPartyRequestSubscriber(DipsConfiguration configuration, ILogger logger, RabbitMqConsumer consumer, RabbitMqExchange invalidExchange, string invalidRoutingKey, string recoverableRoutingKey) 
            : base(configuration, logger, consumer, invalidExchange, invalidRoutingKey, recoverableRoutingKey)
        {
            var helper = new BatchCheckThirdPartyRequestMapHelper(new DateTimeProvider(), Configuration);
            QueueMapper = new CheckThirdPartyBatchRequestToDipsQueueMapper(helper);
            VoucherMapper = new CheckThirdPartyRequestToDipsNabChqScanPodMapper(helper);
            DbIndexMapper = new CheckThirdPartyBatchRequestToDipsDbIndexMapper(helper);
        }

        public override void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            base.Consumer_ReceiveMessage(message);
            if (!ContinueProcessing) return;

            try
            {
                //Mapping queue fields
                var queue = QueueMapper.Map(Message);
                queue.CorrelationId = CorrelationId;
                queue.RoutingKey = RoutingKey;

                //Mapping voucher fields
                var vouchers = VoucherMapper.Map(Message).ToList();
                var jobIdentifier = CorrelationId;
                var batchNumber = string.IsNullOrEmpty(Message.voucherBatch.scannedBatchNumber) ? queue.S_BATCH : Message.voucherBatch.scannedBatchNumber;
                var processingDate = Message.voucher.First().voucher.processingDate;

                //Peform image merge for Dips
                var imageMergeHelper = new ImageMergeHelper(Configuration);
                imageMergeHelper.EnsureMergedImageFilesExist(jobIdentifier, batchNumber, processingDate);
                imageMergeHelper.PopulateMergedImageInfo(jobIdentifier, batchNumber, vouchers);

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
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                                Log.Information("Successfully processed CheckThirdPartyRequest '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a CheckThirdPartyRequest '{@CheckThirdPartyRequest}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    Message, jobIdentifier);

                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a CheckThirdPartyRequest '{@CheckThirdPartyRequest}', '{@jobIdentifier}'",
                                    Message, jobIdentifier);
                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                        }
                        
                    }
                }

                Log.Information("Successfully processed CheckThirdPartyRequest {@CorrelationId}", CorrelationId);
                InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, CorrelationId);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CheckThirdPartyRequest {@CheckThirdPartyRequest}", Message);
                throw new EasyNetQException("Error processing CheckThirdPartyRequest", ex);
            }
        }
    }
}