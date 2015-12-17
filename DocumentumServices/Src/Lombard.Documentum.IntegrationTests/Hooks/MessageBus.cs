using System.Configuration;
using EasyNetQ;

namespace Lombard.Documentum.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    public class MessageBus
    {
        private static readonly IBus Bus = InitializeMessageBus();

        // Explicit static constructor to tell C# compiler not to mark type as beforefieldinit
        static MessageBus()
        {
        }

        private MessageBus()
        {
        }

        public static IBus Instance
        {
            get
            {
                return Bus;
            }
        }

        public static IBus InitializeMessageBus()
        {
            var rabbitConnection = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;
            return RabbitHutch.CreateBus(rabbitConnection);
        }
    }
}
