using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Serialization;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class SubscriberBase<T>
    {
        private RabbitMqConsumer Consumer { get; set; }
      
        protected RabbitMqExchange InvalidExchange { get; private set; }
        protected string InvalidRoutingKey { get; private set; }
        protected string RecoverableRoutingKey { get; set; }
        protected ILogger Log { get; private set; }
        protected DipsConfiguration Configuration { get; private set; }
        protected bool ContinueProcessing { get; private set; }
        protected T Message { get; private set; }
        protected string CorrelationId { get; private set; }
        protected string RoutingKey { get; private set; }

        protected SubscriberBase(DipsConfiguration configuration, ILogger logger, RabbitMqConsumer consumer, RabbitMqExchange invalidExchange, string invalidRoutingKey, string recoverableRoutingKey)
        {
            Log = logger;
            Configuration = configuration;
            Consumer = consumer;
            InvalidExchange = invalidExchange;
            InvalidRoutingKey = invalidRoutingKey;
            RecoverableRoutingKey = recoverableRoutingKey;
        }

        public void StartConsumer()
        {
            Consumer.ReceiveMessage += Consumer_ReceiveMessage;
            Consumer.StartConsuming();
        }

        public virtual void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            ContinueProcessing = false;
            if (message == null) return; // queue is empty
            Message = CustomJsonSerializer.BytesToMessage<T>(message.Body);
            CorrelationId = message.BasicProperties.CorrelationId;
            RoutingKey = message.RoutingKey;
            if (message.Body.Count() == 0)
                Log.Error(
                    "SubscriberBase: Consumer_ReceiveMessage(message) - message.Body contains no data for message id {0}",
                    message.BasicProperties.MessageId);
            if (Message != null)
            {
                ContinueProcessing = true;
                return;
            }
            if (message.Body.Count() > 0)
                Log.Error("SubscriberBase: Consumer_ReceiveMessage(message) - message.Body contains data which is not compatible with {0} for message id {1}", typeof(T).ToString(), message.BasicProperties.MessageId);
            InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, "");
        }
    }
}
