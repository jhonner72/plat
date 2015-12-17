using EasyNetQ;
using Lombard.Common.Queues;
using Lombard.Documentum.Service.Messages;

namespace Lombard.Documentum.Service.Jobs
{
    public class DocumentumMessageProcessorFactory : IMessageProcessorFactory<DocumentumRequestMessage>
    {
        private readonly IBus messageBus;
        
        public DocumentumMessageProcessorFactory(IBus messageBus)
        {
            this.messageBus = messageBus;
        }

        public IMessageProcessor<DocumentumRequestMessage> CreateMessageProcessor(DocumentumRequestMessage message)
        {
            return new DocumentumMessageProcessor(messageBus)
            {
                Message = message
            };
        }
    }
}
