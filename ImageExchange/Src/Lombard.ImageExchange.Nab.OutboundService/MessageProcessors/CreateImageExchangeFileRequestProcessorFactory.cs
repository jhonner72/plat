using Autofac;
using Lombard.Common.Queues;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;

namespace Lombard.ImageExchange.Nab.OutboundService.MessageProcessors
{
    public class CreateImageExchangeFileRequestProcessorFactory : IMessageProcessorFactory<CreateImageExchangeFileRequest>
    {
        private readonly IComponentContext container;

        public CreateImageExchangeFileRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CreateImageExchangeFileRequest> CreateMessageProcessor(CreateImageExchangeFileRequest message)
        {
            // Construct an instance per request. The dependencies should not be injected to this contructor, 
            // so that we can request new instances as we construct the MessageProcessor:

            var publisher = container.Resolve<IExchangePublisher<CreateImageExchangeFileResponse>>();
            var messageToBatchConverter = container.Resolve<IMessageToBatchConverter>();
            var batchCreator = container.Resolve<IBatchCreator>();
            var coinFileCreator = container.Resolve<ICoinFileCreator>();

            return new CreateImageExchangeFileRequestProcessor(publisher, messageToBatchConverter, batchCreator, coinFileCreator)
            {
                Message = message
            };
        }
    }
}
