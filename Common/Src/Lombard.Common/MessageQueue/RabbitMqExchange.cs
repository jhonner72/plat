using RabbitMQ.Client.Framing;


namespace Lombard.Common.MessageQueue
{
    /// <summary>
    /// 
    /// </summary>
    public class RabbitMqExchange : RabbitMqBase
    {
        public string ExchangeName { get; private set; }

        public RabbitMqExchange(string exchange, string hostNames, string userName, string password, int timeout = 5000, int heartbeatSeconds = 300, bool automaticRecoveryEnabled = true)
            : base(hostNames, userName, password, timeout, heartbeatSeconds, automaticRecoveryEnabled)
        {
            ExchangeName = exchange;
            while (Connection == null || !Connection.IsOpen)
                if (!WaitingToConnect) TryCreateConnectionFactory(null);
            Model = Connection.CreateModel();
            Model.ExchangeDeclare(ExchangeName, "direct", true);
        }

        public void SendMessage(byte[] message, string routingKey, string correlationId)
        {
            Model.BasicPublish(ExchangeName, routingKey, new BasicProperties
            {
                CorrelationId  = correlationId
            }, message);
        }
    }
}
