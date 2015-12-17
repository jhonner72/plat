using System.IO;
using System.Linq;
using FujiXerox.Adapters.A2iaAdapter.MessageQueue;
using FujiXerox.Adapters.A2iaAdapter.Model;
using FujiXerox.Adapters.A2iaAdapter.OcrService;
using FujiXerox.Adapters.A2iaAdapter.Serialization;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Serilog;

namespace FujiXerox.Adapters.A2iaAdapter
{
    public static class ProcessingService
    {
        public static IOcrProcessingService OcrService { private get; set; }

        //public static RabbitMq Queue { private get; set; }
        public static RabbitMqExchange Exchange { get; set; }
        public static RabbitMqExchange InvalidExchange { get; set; }
        public static RabbitMqConsumer Consumer { get; set; }

        //public static IBus MessageBus { private get; set; }
        private static bool running;

        public static ILogger Log { private get; set; }
        public static string InvalidQueueName { private get; set; }
        public static string RoutingKey { private get; set; }
        public static string InvalidRoutingKey { private get; set; }
        public static string ImageFilePath { private get; set; }
        public static string ImageFileNameTemplate { private get; set; }

        public static void Run()
        {
            if (running) return;
            running = true;
            OcrService.Initialise();
            OcrService.BatchComplete += OcrService_BatchComplete;

            //MessageBus.Subscribe<RecogniseBatchCourtesyAmountRequest>(QueueSubscriptionId, Queue_MessageReceived);
            StartConsumer();
            //var msgThread = new Thread(MessageThread);
            //msgThread.Start();
        }

        public static void StartConsumer()
        {
            Consumer.ReceiveMessage += Consumer_ReceiveMessage;
            Consumer.StartConsuming();
        }

        public static void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            // Process Message from queue
            if (message == null) return;// queue is empty
            var batch = CustomJsonSerializer.BytesToMessage<RecogniseBatchCourtesyAmountRequest>(message.Body);
            //var batch = message;

            if (message.Body.Count() == 0)
                Log.Error(
                    "ProcessingService: Queue_MessageRecieved(message) - message.Body contains no data for message id {0}",
                    message.BasicProperties.MessageId);
            if (batch == null)
            {
                if (message.Body.Count() > 0)
                    Log.Error(
                        "ProcessingService: Queue_MessageRecieved(message) - message.Body contains data which is not compatible with RecogniseBatchCourtesyAmountRequest for message id {0}",
                        message.BasicProperties.MessageId);
                // need to re-route message to CAR.Invalid queue
                if (!string.IsNullOrEmpty(InvalidQueueName))
                    InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, "");
                return; // acknowledge message to remove from queue;
            }
            RoutingKey = message.RoutingKey;
            if (batch.voucher == null || batch.voucher.Length == 0)
            {
                Log.Error(
                    "ProcessingService: Queue_MessageRecieved(message) - there are no vouchers present for message id {0}",
                    message.BasicProperties.MessageId);
                // need to re-route message to CAR.Invalid queue
                if (!string.IsNullOrEmpty(InvalidQueueName))
                    InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, "");
                return; // acknowledge message to remove from queue;
            }
            var ocrBatch = new OcrBatch
            {
                JobIdentifier = batch.jobIdentifier,
                Vouchers = batch.voucher.Select(v => new OcrVoucher
                {
                    Id = v.documentReferenceNumber,
                    ImagePath = Path.Combine(ImageFilePath, batch.jobIdentifier, string.Format(ImageFileNameTemplate, v.processingDate, v.documentReferenceNumber)),
                    VoucherType = ParseTransactionCode(v.transactionCode)
                }).ToList()
            };
            // Validate the file path
            if (!ValidateImageFiles(ocrBatch)) return;// probably should send to an error queue
            Log.Information("Batch {0} received from message queue containing {1} vouchers", ocrBatch.JobIdentifier, ocrBatch.Vouchers.Count());
            OcrService.ProcessBatch(ocrBatch);
        }

        private static void OcrService_BatchComplete(object sender, OcrBatch batch)
        {
            //Console.WriteLine("Batch {0} completed", batch.JobIdentifier);
            // publish result to outgoing queue/exchange
            var messageBatch = new RecogniseBatchCourtesyAmountResponse
            {
                jobIdentifier = batch.JobIdentifier,
                voucher = batch.Vouchers.Select(v => new RecogniseCourtesyAmountResponse
                {
                    documentReferenceNumber = v.Id,
                    capturedAmount = v.AmountResult.Result ?? "0",
                    amountConfidenceLevel = v.AmountResult.Score ?? "0",
                    amountRegionOfInterest = new RegionOfInterest
                    {
                        height = v.AmountResult.Location.Height,
                        left = v.AmountResult.Location.Left,
                        top = v.AmountResult.Location.Top,
                        width = v.AmountResult.Location.Width
                    }
                }).ToArray()
            };
            //MessageBus.Publish(messageBatch, RoutingKey);
            //Queue.PublishToExchange(OutboundExchangeName, batch.JobIdentifier, RoutingKey, CustomJsonSerializer.MessageToBytes(messageBatch));
            Exchange.SendMessage(CustomJsonSerializer.MessageToBytes(messageBatch), RoutingKey, batch.JobIdentifier);
        }

        public static void Stop()
        {
            Consumer.Dispose();
            OcrService.BatchComplete -= OcrService_BatchComplete;
            running = false;
        }

        //private static void MessageThread()
        //{
        //    while (running)
        //    {
        //        Queue.ReadFromQueue(InboundQueueName, Queue_MessageReceived);
        //    }
        //}

        private static VoucherType ParseTransactionCode(string transactionCode)
        {
            int transactionCodeInteger;
            if (int.TryParse(transactionCode, out transactionCodeInteger))
            {
                return (transactionCodeInteger >= 50) ? VoucherType.Credit : VoucherType.Debit;
            }

            return VoucherType.Debit;
        }

        private static bool ValidateImageFiles(OcrBatch batch)
        {
            var missingImages = batch.Vouchers
                .Where(v => !File.Exists(v.ImagePath))
                .Select(v => v.Id)
                .ToList();

            if (!missingImages.Any()) return true;
            Log.Warning(string.Format("Could not find image files for the following vouchers : {0}",
                string.Join(", ", missingImages)));
            return false;
        }
    }
}
