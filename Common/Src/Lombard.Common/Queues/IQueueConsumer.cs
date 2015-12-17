using System;
using System.Threading.Tasks;
using EasyNetQ;

namespace Lombard.Common.Queues
{
    public interface IQueueConsumer<in TMessage> : IDisposable
        where TMessage : class
    {
        void Subscribe(string queueName);
        Task CompleteAsync(bool forceCancel = false);
        Task HandleMessageAsync(TMessage message, string correlationId, string routingKey, IMessage<TMessage> sourceMesage, MessageReceivedInfo info);
    }
}