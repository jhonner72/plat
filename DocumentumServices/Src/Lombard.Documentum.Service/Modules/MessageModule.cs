using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Common.Jobs;
using Lombard.Common.Queues;
using Lombard.Documentum.Service.Jobs;
using Lombard.Documentum.Service.Messages;

namespace Lombard.Documentum.Service.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            var messageBus = RabbitHutch.CreateBus(connectionString);

            builder
                .RegisterInstance(messageBus)
                .As<IBus>();

            builder
                .RegisterType<DocumentumMessageProcessorFactory>()
                .As<IMessageProcessorFactory<DocumentumRequestMessage>>();

            builder
                .RegisterType<QueueConsumer<DocumentumRequestMessage>>()
                .As<IQueueConsumer<DocumentumRequestMessage>>()
                .SingleInstance();
        }
    }
}
