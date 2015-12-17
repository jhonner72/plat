using Lombard.Common.Queues;
using Lombard.Vif.Acknowledgement.Service.Configuration;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.Vif.Acknowledgement.Service
{
    public class ServiceRunner
    {
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<ProcessValueInstructionFileAcknowledgmentRequest> createVifFileConsumer;
        private readonly IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse> createVifFilePublisher;

        public ServiceRunner(
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<ProcessValueInstructionFileAcknowledgmentRequest> createVifFileConsumer,
            IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse> createVifFilePublisher)
        {
            this.queueConfiguration = queueConfiguration;
            this.createVifFileConsumer = createVifFileConsumer;
            this.createVifFilePublisher = createVifFilePublisher;
        }

        public void Start()
        {
            StartListeningForInputMessages();

            Log.Information("VIF Service Started");
        }

        public void Stop()
        {
            createVifFileConsumer.Dispose();

            Log.Information("VIF Service Stopped");
        }

        private void StartListeningForInputMessages()
        {
            createVifFileConsumer.Subscribe(queueConfiguration.RequestExchangeName + ".queue");
            createVifFilePublisher.Declare(queueConfiguration.ResponseExchangeName);
        }
    }
}
