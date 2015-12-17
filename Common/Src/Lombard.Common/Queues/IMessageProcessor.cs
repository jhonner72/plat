using System.Threading;
using System.Threading.Tasks;

namespace Lombard.Common.Queues
{
    /// <summary>
    /// A message that can be processed on a seperate thread
    /// </summary>
    /// <typeparam name="TMessage"></typeparam>
    public interface IMessageProcessor<TMessage>
    {
        /// <summary>
        /// The data to pass to the job.
        /// </summary>
        TMessage Message { get; set; }

        /// <summary>
        /// Process the message.
        /// </summary>
        /// <remarks>It is the message processors responsibility to handle the cancellation token requests e.g. cancellationToken.ThrowIfCancellationRequested</remarks>
        /// <param name="cancellationToken">The cancellation token to indicate that the (thread) processing should be forceably terminated.</param>
        Task ProcessAsync(CancellationToken cancellationToken, string correlationId, string routingKey);
    }
}
