using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Adapters.A2iaAdapter.MessageProcessors;
using Lombard.Adapters.A2iaAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.A2iaAdapter.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            //message bus
            var messageBus = MessageBusFactory.CreateBus(connectionString);

            builder
                .RegisterInstance(messageBus)
                .As<IAdvancedBus>();

            //queue consumers

            builder.RegisterType<CarRequestMessageProcessorFactory>()
                .As<IMessageProcessorFactory<RecogniseBatchCourtesyAmountRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<RecogniseBatchCourtesyAmountRequest>>()
                .As<IQueueConsumer<RecogniseBatchCourtesyAmountRequest>>()
                .SingleInstance();

            //exchange publishers

            builder.RegisterType<ExchangePublisher<RecogniseBatchCourtesyAmountResponse>>()
                .As<IExchangePublisher<RecogniseCourtesyAmountResponse>>();

        }
    }
}
