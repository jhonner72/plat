using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Common.Queues;
using System.Configuration;
using EasyNetQ;

namespace Lombard.Scheduler.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;
            var messageBus = MessageBusFactory.CreateBus(connectionString);

            builder
               .RegisterInstance(messageBus)
               .SingleInstance()
               .As<IAdvancedBus>();

            builder
               .RegisterType<ExchangePublisher<Job>>()
               .As<IExchangePublisher<Job>>()
               .SingleInstance();


            base.Load(builder);
        }
       
    }
}