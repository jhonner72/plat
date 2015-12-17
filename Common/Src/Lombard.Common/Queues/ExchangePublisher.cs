using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;

namespace Lombard.Common.Queues
{
    public class ExchangePublisher<TMessage> : IExchangePublisher<TMessage> where TMessage : class
    {
        private readonly IAdvancedBus messageBus;

        private IExchange exchange;

        public ExchangePublisher(IAdvancedBus messageBus)
        {
            this.messageBus = messageBus;
        }

        public void Declare(string exchangeName)
        {
            exchange = messageBus.ExchangeDeclare(exchangeName, ExchangeType.Direct, true);
        }

        public async Task PublishAsync(TMessage message, string correlationId)
        {
            await PublishAsync(message, correlationId, string.Empty);
        }
        public async Task PublishAsync(TMessage message, string correlationId, string routingKey)
        {
            var busMessage = new Message<TMessage>(message);

            if (!string.IsNullOrWhiteSpace(correlationId))
            {
                busMessage.Properties.CorrelationId = correlationId;
            }

            await messageBus
                .PublishAsync(exchange, routingKey, false, false, busMessage)
                .ContinueWith(task =>
                {
                    if (task.IsFaulted)
                    {
                        // ReSharper disable once PossibleNullReferenceException
                        throw task.Exception;
                    }
                });
        }
    }
}
