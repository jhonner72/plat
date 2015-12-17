using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.AdjustmentLetters.MessageProcessors;
using Lombard.Common.Helper;
using Lombard.Common.Messages;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.AdjustmentLetters.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            var messageBus = MessageBusFactory.CreateBus(connectionString);

            builder
                .RegisterInstance(messageBus)
                .As<IAdvancedBus>()
                .SingleInstance();

            builder
                .RegisterType<AdjustmentLettersRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CreateBatchAdjustmentLettersRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<CreateBatchAdjustmentLettersRequest>>()
                .As<IQueueConsumer<CreateBatchAdjustmentLettersRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CreateBatchAdjustmentLettersResponse>>()
                .As<IExchangePublisher<CreateBatchAdjustmentLettersResponse>>()
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
