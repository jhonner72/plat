using RabbitMQ.Client;

namespace FujiXerox.Adapters.A2iaAdapter.MessageQueue
{
    /// <summary>
    /// 
    /// </summary>
    public interface IBasicGetResult
    {
        // Summary:
        //     Retrieves the Basic-class content header properties for this message.
        IBasicProperties BasicProperties { get; }
        //
        // Summary:
        //     Retrieves the body of this message.
        byte[] Body { get; }
        //
        // Summary:
        //     Retrieve the delivery tag for this message. See also IModel.BasicAck.
        ulong DeliveryTag { get; }
        //
        // Summary:
        //     Retrieve the exchange this message was published to.
        string Exchange { get; }
        //
        // Summary:
        //     Retrieve the number of messages pending on the queue, excluding the message
        //     being delivered.
        //
        // Remarks:
        //     Note that this figure is indicative, not reliable, and can change arbitrarily
        //     as messages are added to the queue and removed by other clients.
        uint MessageCount { get; }
        //
        // Summary:
        //     Retrieve the redelivered flag for this message.
        bool Redelivered { get; }
        //
        // Summary:
        //     Retrieve the routing key with which this message was published.
        string RoutingKey { get; }
    }
}
