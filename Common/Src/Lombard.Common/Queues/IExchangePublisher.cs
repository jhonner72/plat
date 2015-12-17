using System.Threading.Tasks;

namespace Lombard.Common.Queues
{
    public interface IExchangePublisher<in TMessage> where TMessage : class
    {
        void Declare(string exchangeName); 
        Task PublishAsync(TMessage message, string correlationId);
        Task PublishAsync(TMessage message, string correlationId, string routingKey);
    }
}
