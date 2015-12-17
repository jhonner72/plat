using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Common.Helper;
using Lombard.Common.Messages;
using Lombard.Common.Queues;
using Lombard.Vif.Service.MessageProcessors;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.Service.Modules
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
                .RegisterType<VifRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CreateValueInstructionFileRequest>>()
                .SingleInstance();
            
            builder
                .RegisterType<QueueConsumer<CreateValueInstructionFileRequest>>()
                .As<IQueueConsumer<CreateValueInstructionFileRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CreateValueInstructionFileResponse>>()
                .As<IExchangePublisher<CreateValueInstructionFileResponse>>()
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
