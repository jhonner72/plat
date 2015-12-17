using Autofac;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Mappers;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;

namespace Lombard.Vif.Service.MessageProcessors
{
    public class VifRequestProcessorFactory : IMessageProcessorFactory<CreateValueInstructionFileRequest>
    {
        private readonly IComponentContext container;

        public VifRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CreateValueInstructionFileRequest> CreateMessageProcessor(CreateValueInstructionFileRequest message)
        {
            // Construct an instance per request. The dependencies should not be injected to this contructor, 
            // so that we can request new instances as we construct the MessageProcessor:

            var publisher = container.Resolve<IExchangePublisher<CreateValueInstructionFileResponse>>();
            var requestSplitter = container.Resolve<IRequestSplitter>();
            var requestConverter = container.Resolve<IRequestConverter>();
            var fileWriter = container.Resolve<IFileWriter>();
            var pathHelper = container.Resolve<IPathHelper>();

            return new VifRequestProcessor(publisher, requestSplitter, requestConverter, fileWriter, pathHelper)
            {
                Message = message
            };
        }
    }
}
