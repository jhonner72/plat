using Autofac;
using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
using Lombard.Common.Queues;

namespace Lombard.Adapters.A2iaAdapter.MessageProcessors
{
    public class CarRequestMessageProcessorFactory : IMessageProcessorFactory<RecogniseBatchCourtesyAmountRequest>
    {
        // ReSharper disable once InconsistentNaming
        private readonly IComponentContext container;

        public CarRequestMessageProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<RecogniseBatchCourtesyAmountRequest> CreateMessageProcessor(RecogniseBatchCourtesyAmountRequest message)
        {
            var a2IaService = container.Resolve<IOcrProcessingService>();
            var requestMapper = container.Resolve<IMapper<RecogniseBatchCourtesyAmountRequest, OcrBatch>>();
            var exchangePublisher = container.Resolve<IExchangePublisher<RecogniseBatchCourtesyAmountResponse>>();
            var responseMapper = container.Resolve<IMapper<OcrBatch, RecogniseBatchCourtesyAmountResponse>>();
            var adapterConfiguration = container.Resolve<IAdapterConfiguration>();

            exchangePublisher.Declare(adapterConfiguration.OutboundExchangeName);

            return new CarRequestMessageProcessor(a2IaService, requestMapper, exchangePublisher, responseMapper)
            {
                Message = message
            };
        }
    }
}
