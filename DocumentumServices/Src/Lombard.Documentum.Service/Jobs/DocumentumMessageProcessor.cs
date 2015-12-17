using System.Threading.Tasks;
using EasyNetQ;
using Lombard.Common.Queues;
using Lombard.Documentum.Service.Messages;
using Serilog;

namespace Lombard.Documentum.Service.Jobs
{
    public class DocumentumMessageProcessor : IMessageProcessor<DocumentumRequestMessage>
    {
        private readonly IBus messageBus;

        public DocumentumRequestMessage Message { get; set; }

        public DocumentumMessageProcessor(IBus messageBus)
        {
            this.messageBus = messageBus;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken)
        {
            Log.Information("Processing message '@message'", Message);

            // TODO:
            var responseContent = string.Format("Response to '{0}'", Message.Content);

            var response = new DocumentumResponseMessage(responseContent);

            await messageBus.PublishAsync(response);
        }
    }
}
