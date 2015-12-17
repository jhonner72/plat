using System;
using System.Threading;
using System.Threading.Tasks;
using Lombard.Common.Queues;

namespace Lombard.Common.UnitTests.Queues
{
    public class WaitingMessageProcessorStub : IMessageProcessor<string>
    {
        public string Message { get; set; }
       
        public virtual async Task ProcessAsync(CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            await Task.Delay(5000, cancellationToken);

            cancellationToken.ThrowIfCancellationRequested();
        }
    }
}