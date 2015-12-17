using RabbitMQ.Client;

namespace Lombard.Common.MessageQueue
{
    /// <summary>
    /// 
    /// </summary>
    public class BasicGetResult : IBasicGetResult
    {
        // Summary:
        //     Sets the new instance's properties from the arguments passed in.
        public BasicGetResult(RabbitMQ.Client.BasicGetResult result)
        {
            BasicProperties = result.BasicProperties;
            Body = result.Body;
            DeliveryTag = result.DeliveryTag;
            Exchange = result.Exchange;
            MessageCount = result.MessageCount;
            Redelivered = result.Redelivered;
            RoutingKey = result.RoutingKey;
        }

        // Summary:
        //     Retrieves the Basic-class content header properties for this message.
        public IBasicProperties BasicProperties { get; private set; }
        //
        // Summary:
        //     Retrieves the body of this message.
        public byte[] Body { get; private set; }
        //
        // Summary:
        //     Retrieve the delivery tag for this message. See also IModel.BasicAck.
        public ulong DeliveryTag { get; private set; }
        //
        // Summary:
        //     Retrieve the exchange this message was published to.
        public string Exchange { get; private set; }
        //
        // Summary:
        //     Retrieve the number of messages pending on the queue, excluding the message
        //     being delivered.
        //
        // Remarks:
        //     Note that this figure is indicative, not reliable, and can change arbitrarily
        //     as messages are added to the queue and removed by other clients.
        public uint MessageCount { get; private set; }
        //
        // Summary:
        //     Retrieve the redelivered flag for this message.
        public bool Redelivered { get; private set; }
        //
        // Summary:
        //     Retrieve the routing key with which this message was published.
        public string RoutingKey { get; private set; }
    }
}