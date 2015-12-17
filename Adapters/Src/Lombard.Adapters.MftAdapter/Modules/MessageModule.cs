using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.MftAdapter.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            //Message Bus

            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            var messageBus = MessageBusFactory.CreateBus(connectionString);

            builder
                .RegisterInstance(messageBus)
                .As<IAdvancedBus>()
                .SingleInstance();

            //Queues

            //Exchanges

            builder
                .RegisterType<ExchangePublisher<JobRequest>>()
                .As<IExchangePublisher<JobRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<Incident>>()
                .As<IExchangePublisher<Incident>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<string>>()
                .As<IExchangePublisher<string>>()
                .SingleInstance();

            //Misc
        }
    }
}
