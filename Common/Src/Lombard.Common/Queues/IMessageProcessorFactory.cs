namespace Lombard.Common.Queues
{
    /// <summary>
    /// A simple factory to spawn new job instances for threaded processing
    /// </summary>
    /// <typeparam name="TMessage">The type of message that will be consimed by the processor</typeparam>
    public interface IMessageProcessorFactory<TMessage>
    {
        /// <summary>
        /// Create a new instance of a message processor
        /// </summary>
        /// <param name="message">The message to process</param>
        /// <returns>A new instance of a message processor</returns>
        IMessageProcessor<TMessage> CreateMessageProcessor(TMessage message);
    }
}
