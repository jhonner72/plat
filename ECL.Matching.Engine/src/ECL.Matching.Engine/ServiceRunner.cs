using Lombard.Common.Queues;
using Lombard.ECLMatchingEngine.Service.Configuration;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.ECLMatchingEngine.Service
{
    public class ServiceRunner
    {
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<MatchVoucherRequest> createECLFileConsumer;
        private readonly IExchangePublisher<MatchVoucherResponse> createECLFilePublisher;

        public ServiceRunner(
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<MatchVoucherRequest> createVifFileConsumer,
            IExchangePublisher<MatchVoucherResponse> createVifFilePublisher)
        {
            this.queueConfiguration = queueConfiguration;
            this.createECLFileConsumer = createVifFileConsumer;
            this.createECLFilePublisher = createVifFilePublisher;    
        }

        public void Start()
        {
            StartListeningForInputMessages();

            Log.Information("ECL Matching Engine Service Started");
        }

        public void Stop()
        {
            createECLFileConsumer.Dispose();

            Log.Information("ECL Matching Engine Service Stopped");
        }

        private void StartListeningForInputMessages()
        {
            createECLFileConsumer.Subscribe(queueConfiguration.RequestExchangeName + ".queue");
            createECLFilePublisher.Declare(queueConfiguration.ResponseExchangeName);
        }
    }
}
