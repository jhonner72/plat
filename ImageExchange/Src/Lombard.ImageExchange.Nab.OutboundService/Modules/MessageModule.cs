using Autofac;
using EasyNetQ;
using Lombard.Common.Helper;
using Lombard.Common.Messages;
using Lombard.Common.Queues;
using Lombard.ImageExchange.Nab.OutboundService.MessageProcessors;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using System.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            var messageBus = MessageBusFactory.CreateBus(connectionString);

            var errorExchangeName = ConfigurationManager.AppSettings["queue:ErrorRequestExchangeName"];

            if (!string.IsNullOrEmpty(errorExchangeName))
            {
                // Instead of using the default error exchange/queue
                messageBus.Container.Resolve<IConventions>().ErrorExchangeNamingConvention = info => errorExchangeName;
            }

            builder
                .RegisterInstance(messageBus)
                .SingleInstance()
                .As<IAdvancedBus>();

            builder
                .RegisterType<CreateImageExchangeFileRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CreateImageExchangeFileRequest>>()
                .SingleInstance();
            
            builder
                .RegisterType<QueueConsumer<CreateImageExchangeFileRequest>>()
                .As<IQueueConsumer<CreateImageExchangeFileRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CreateImageExchangeFileResponse>>()
                .As<IExchangePublisher<CreateImageExchangeFileResponse>>()
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
