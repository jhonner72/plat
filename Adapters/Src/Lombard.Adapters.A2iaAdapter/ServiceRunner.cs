using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter
{
    public class ServiceRunner
    {
        private readonly IAdapterConfiguration adapterConfiguration;
        // ReSharper disable once InconsistentNaming
        private readonly IOcrProcessingService carService;
        private readonly IQueueConsumer<RecogniseBatchCourtesyAmountRequest> carRequestQueueConsumer;

        public ServiceRunner(
            IOcrProcessingService carService,
            IAdapterConfiguration adapterConfiguration,
            IQueueConsumer<RecogniseBatchCourtesyAmountRequest> carRequestQueueConsumer)
        {
            this.adapterConfiguration = adapterConfiguration;
            this.carRequestQueueConsumer = carRequestQueueConsumer;
            this.carService = carService;
        }

        /// <summary>
        /// Start point of the service
        /// </summary>
        public void Start()
        {
            carService.Initialise();

            //TODO: Initialise with correct queue and exchange name
            carRequestQueueConsumer.Subscribe(adapterConfiguration.InboundQueueName);

            Log.Information("A2IA Adapter Service Started");
        }

        /// <summary>
        /// Shut down the service
        /// </summary>
        public void Stop()
        {
            Log.Information("A2IA Adapter Service Stopped");
            carService.Shutdown();
        }


    }
}
