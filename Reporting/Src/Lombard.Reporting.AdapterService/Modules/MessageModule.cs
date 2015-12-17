namespace Lombard.Reporting.AdapterService.Modules
{
using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Common.Helper;
using Lombard.Common.Messages;
using Lombard.Common.Queues;
using Lombard.Reporting.AdapterService.MessageProcessors;
using Lombard.Reporting.AdapterService.Messages.XsdImports;

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
                .RegisterType<ExecuteBatchReportRequestProcessorFactory>()
                .As<IMessageProcessorFactory<ExecuteBatchReportRequest>>()
                .SingleInstance();
            
            builder
                .RegisterType<QueueConsumer<ExecuteBatchReportRequest>>()
                .As<IQueueConsumer<ExecuteBatchReportRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<ExecuteBatchReportResponse>>()
                .As<IExchangePublisher<ExecuteBatchReportResponse>>()
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
