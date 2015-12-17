using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Adapters.A2iaAdapter.Messages;
using Lombard.Adapters.A2iaAdapter.Wrapper;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter
{
    public class ServiceRunner
    {
        private readonly IAdapterConfiguration adapterConfiguration;
        // ReSharper disable once InconsistentNaming
        private readonly IA2IAService carService;
        private readonly IQueueConsumer<RecogniseBatchCourtesyAmountRequest> carRequestQueueConsumer;
        private readonly IExchangePublisher<RecogniseBatchCourtesyAmountResponse> carResponseExchangePublisher;

        public ServiceRunner(
            IA2IAService carService,
            IAdapterConfiguration adapterConfiguration, 
            IQueueConsumer<RecogniseBatchCourtesyAmountRequest> carRequestQueueConsumer, 
            IExchangePublisher<RecogniseBatchCourtesyAmountResponse> carResponseExchangePublisher)
        {
            this.adapterConfiguration = adapterConfiguration;
            this.carRequestQueueConsumer = carRequestQueueConsumer;
            this.carResponseExchangePublisher = carResponseExchangePublisher;
            this.carService = carService;
        }

        /// <summary>
        /// Start point of the service
        /// </summary>
        public void Start()
        {
            //TODO: Initialise correctly
            carService.Initialise(adapterConfiguration.ParameterPath, adapterConfiguration.TablePath, adapterConfiguration.CpuNames.Split(','), true, true, false);

            //TODO: Initiliase with correct queue and exchange name
            carRequestQueueConsumer.Subscribe(adapterConfiguration.InboundQueueName);
            carResponseExchangePublisher.Declare(adapterConfiguration.OutboundQueueName);

            Log.Information("A2IA Adapter Service Started");
        }

        /// <summary>
        /// Shut down the service
        /// </summary>
        public void Stop()
        {

            if (carService != null)
            {
                carService.Dispose();
            }

            Log.Information("A2IA Adapter Service Stopped");
        }


    }
}
