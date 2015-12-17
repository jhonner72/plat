using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Common.Queues;
using Lombard.Vif.Acknowledgement.Service.MessageProcessors;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Common.Helper;
using Lombard.Common.Messages;

namespace Lombard.Vif.Acknowledgement.Service.Modules
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
                .RegisterType<VifAckRequestProcessorFactory>()
                .As<IMessageProcessorFactory<ProcessValueInstructionFileAcknowledgmentRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<ProcessValueInstructionFileAcknowledgmentRequest>>()
                .As<IQueueConsumer<ProcessValueInstructionFileAcknowledgmentRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse>>()
                .As<IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse>>()
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
