using System;
using System.IO;
using RabbitMQ.Client;
using RabbitMQ.Client.Exceptions;
using Serilog;

namespace FujiXerox.Adapters.A2iaAdapter.MessageQueue
{
    /// <summary>
    /// 
    /// </summary>
    public class RabbitMqConsumer : RabbitMqBase
    {
        private readonly string queueName;

        private bool isConsuming;
        private string consumerTag;

        public delegate void OnReceiveMessageDelegate(IBasicGetResult message);
        public event OnReceiveMessageDelegate ReceiveMessage;

        public event EventHandler ConsumerFailed;

        private delegate void ConsumeDelegate();

        public RabbitMqConsumer(string queueName, string hostNames, string userName, string password, int timeout = 5000, int heartbeatSeconds = 300, bool automaticRecoverEnabled = true)
            : base(hostNames, userName, password, timeout, heartbeatSeconds, automaticRecoverEnabled)
        {
            this.queueName = queueName;
            while (Connection == null || !Connection.IsOpen)
                if (!WaitingToConnect) TryCreateConnectionFactory(null);
            Model = Connection.CreateModel();
            Model.QueueDeclare(this.queueName, true, false, false, null);
        }

        public void StartConsuming()
        {
            isConsuming = true;
            var c = new ConsumeDelegate(Consume);
            c.BeginInvoke(null, null);
        }

        public void StopConsuming()
        {
            Model.BasicCancel(consumerTag);
            isConsuming = false;
        }

        public void Consume()
        {
            var consumer = new QueueingBasicConsumer(Model);
            consumerTag = Model.BasicConsume(queueName, false, consumer);
            while (isConsuming)
            {
                try
                {
                    var e = consumer.Queue.Dequeue();
                    OnReceiveMessage(new BasicDeliveryEventArgs(e));
                    Model.BasicAck(e.DeliveryTag, false);
                }
                catch (OperationInterruptedException oiex)
                {
                    Log.Error(oiex, "RabbitMqConsumer: Consume - the connection to the queue {0} has been lost", queueName);
                    isConsuming = false;
                    OnConsumerFailed();
                    break;
                }
                catch (EndOfStreamException eose)
                {
                    Log.Error(eose, "RabbitMqConsumer: Consume - the connection to the queue {0} has been closed", queueName);
                    isConsuming = false;
                    OnConsumerFailed();
                    break;
                }
            }
        }

        protected void OnConsumerFailed()
        {
            var consumerFailed = ConsumerFailed;
            if(consumerFailed!=null) consumerFailed(this, new EventArgs());
        }

        protected void OnReceiveMessage(IBasicGetResult message)
        {
            var receiveMessage = ReceiveMessage;
            if (receiveMessage != null) receiveMessage(message);
        }

        protected override void Dispose(bool disposing)
        {
            isConsuming = false;
            base.Dispose(disposing);
        }
    }
}
