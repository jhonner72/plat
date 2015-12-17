using Lombard.Common.Queues;
using Lombard.Vif.Service.Configuration;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.Vif.Service
{
    public class ServiceRunner
    {
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<CreateValueInstructionFileRequest> createVifFileConsumer;
        private readonly IExchangePublisher<CreateValueInstructionFileResponse> createVifFilePublisher;
        
        public ServiceRunner(
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<CreateValueInstructionFileRequest> createVifFileConsumer,
            IExchangePublisher<CreateValueInstructionFileResponse> createVifFilePublisher)
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
