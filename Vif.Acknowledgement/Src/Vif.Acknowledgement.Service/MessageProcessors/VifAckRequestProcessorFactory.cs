using Autofac;
using Lombard.Common.Queues;
using Lombard.Vif.Acknowledgement.Service.Mappers;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.Acknowledgement.Service.MessageProcessors
{
    public class VifAckRequestProcessorFactory : IMessageProcessorFactory<ProcessValueInstructionFileAcknowledgmentRequest>
    {
        private readonly IComponentContext container;

        public VifAckRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<ProcessValueInstructionFileAcknowledgmentRequest> CreateMessageProcessor(ProcessValueInstructionFileAcknowledgmentRequest message)
        {
            // Construct an instance per request. The dependencies should not be injected to this contructor, 
            // so that we can request new instances as we construct the MessageProcessor:

            var publisher = container.Resolve<IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse>>();
            var requestSplitter = container.Resolve<IRequestSplitter>();
            var requestConverter = container.Resolve<IRequestConverter>();

            return new VifAckRequestProcessor(publisher, requestSplitter, requestConverter)
            {
                Message = message
            };
        }
    }
}
