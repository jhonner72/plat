using System.IO;
using Lombard.Common.Queues;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Serilog;

namespace Lombard.ImageExchange.Nab.OutboundService
{
    public class ServiceRunner
    {
        private readonly IOutboundConfiguration outboundConfiguration; 
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<CreateImageExchangeFileRequest> createBatchImageExchangeFileConsumer;
        private readonly IExchangePublisher<CreateImageExchangeFileResponse> createBatchImageExchangeFilePublisher;
        
        public ServiceRunner(
            IOutboundConfiguration outboundConfiguration,
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<CreateImageExchangeFileRequest> createBatchImageExchangeFileConsumer,
            IExchangePublisher<CreateImageExchangeFileResponse> createBatchImageExchangeFilePublisher)
        {
            this.outboundConfiguration = outboundConfiguration;
            this.queueConfiguration = queueConfiguration;
            this.createBatchImageExchangeFileConsumer = createBatchImageExchangeFileConsumer;
            this.createBatchImageExchangeFilePublisher = createBatchImageExchangeFilePublisher;
        }

        public void Start()
        {
            if (!CanFindBitLockerLocation())
            {
                Log.Fatal("Failed to start service due to application configuration errors.");
                return;
            }
            
            StartListeningForInputMessages();

            Log.Information("Image Exchange Outbound Service Started");
        }

        public void Stop()
        {
            createBatchImageExchangeFileConsumer.Dispose();

            Log.Information("Image Exchange Outbound Service Stopped");
        }

        private bool CanFindBitLockerLocation()
        {
            if (string.IsNullOrEmpty(outboundConfiguration.BitLockerLocation))
            {
                Log.Error("Configuration for 'BitLockerLocation' must contain an non-empty path.");
                return false;
            }

            if (!Directory.Exists(outboundConfiguration.BitLockerLocation))
            {
                Log.Error("Configuration for 'BitLockerLocation' has a path = '{location} which does not exist.'", outboundConfiguration.BitLockerLocation);
                return false;
            }

            return true;
        }

        private void StartListeningForInputMessages()
        {
            createBatchImageExchangeFileConsumer.Subscribe(queueConfiguration.RequestExchangeName + ".queue");
            createBatchImageExchangeFilePublisher.Declare(queueConfiguration.ResponseExchangeName);
        }
    }
}
