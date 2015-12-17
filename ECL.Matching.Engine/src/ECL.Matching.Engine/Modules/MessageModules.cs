using Autofac;
using EasyNetQ;
using Lombard.Common.Helper;
using Lombard.Common.Messages;
using Lombard.Common.Queues;
using Lombard.ECLMatchingEngine.Service.MessageProcessors;
using Lombard.Vif.Service.Messages.XsdImports;
using System.Configuration;

namespace Lombard.ECLMatchingEngine.Service.Modules
{
    public class MessageModules : Module
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
              .RegisterType<CreateECLFileRequestProcessorFactory>()
              .As<IMessageProcessorFactory<MatchVoucherRequest>>()
              .SingleInstance();

            builder
                .RegisterType<QueueConsumer<MatchVoucherRequest>>()
                .As<IQueueConsumer<MatchVoucherRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<MatchVoucherResponse>>()
                .As<IExchangePublisher<MatchVoucherResponse>>()
                .SingleInstance();

            builder
                .RegisterType<CustomErrorHandling>()
                .As<ICustomErrorHandling>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<Error>>()
                .As<IExchangePublisher<Error>>()
                .SingleInstance();
        }
    }
}
