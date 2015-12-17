using Lombard.Common.Queues;
using Lombard.Documentum.Service.Messages;
using Serilog;

namespace Lombard.Documentum.Service
{
    public class ServiceRunner
    {
        private readonly IQueueConsumer<DocumentumRequestMessage> documentumRequestQueueConsumer;

        public ServiceRunner(IQueueConsumer<DocumentumRequestMessage> documentumRequestQueueConsumer)
        {
            this.documentumRequestQueueConsumer = documentumRequestQueueConsumer;
        }

        public void Start()
        {
            // TODO: See Leon's I AdapterConfiguration -> probably needs to be moved 
            // into Lombard.Common, with a simple SubscriptionId, and then used here too

            documentumRequestQueueConsumer.Subscribe("DocumentumService");

            Log.Information("Documentum Request Queue Consumer Started");
        }

        public void Stop()
        {
            documentumRequestQueueConsumer.Dispose();

            Log.Information("Documentum Request Queue Consumer Stopped");
        }
    }
}
