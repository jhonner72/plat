using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace FujiXerox.Adapters.A2iaAdapter.MessageQueue
{
    /// <summary>
    /// 
    /// </summary>
    public class BasicDeliveryEventArgs : IBasicGetResult
    {
        public BasicDeliveryEventArgs(BasicDeliverEventArgs args)
        {
            BasicProperties = args.BasicProperties;
            Body = args.Body;
            DeliveryTag = args.DeliveryTag;
            Exchange = args.Exchange;
            Redelivered = args.Redelivered;
            RoutingKey = args.RoutingKey;
        }

        public IBasicProperties BasicProperties { get; private set; }
        public byte[] Body { get; private set; }
        public ulong DeliveryTag { get; private set; }
        public string Exchange { get; private set; }
        public uint MessageCount { get; private set; }
        public bool Redelivered { get; private set; }
        public string RoutingKey { get; private set; }
    }
}
