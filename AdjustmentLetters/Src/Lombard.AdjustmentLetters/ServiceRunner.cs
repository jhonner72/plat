using Lombard.AdjustmentLetters.Configuration;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.AdjustmentLetters
{
    public class ServiceRunner
    {
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<CreateBatchAdjustmentLettersRequest> consumer;
        private readonly IExchangePublisher<CreateBatchAdjustmentLettersResponse> publisher;

        public ServiceRunner(
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<CreateBatchAdjustmentLettersRequest> consumer,
            IExchangePublisher<CreateBatchAdjustmentLettersResponse> publisher)
        {
            this.queueConfiguration = queueConfiguration;
            this.consumer = consumer;
            this.publisher = publisher;
        }

        public void Start()
        {
            StartListeningForInputMessages();

            Log.Information("Adjustment Letters Service Started");
        }

        public void Stop()
        {
            consumer.Dispose();

            Log.Information("Adjustment Letters Service Stopped");
        }

        private void StartListeningForInputMessages()
        {
            consumer.Subscribe(queueConfiguration.RequestExchangeName + ".queue");
            publisher.Declare(queueConfiguration.ResponseExchangeName);
        }
    }
}
