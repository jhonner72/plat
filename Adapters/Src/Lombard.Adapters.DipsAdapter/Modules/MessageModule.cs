using System.Configuration;
using Autofac;
using EasyNetQ;
using Lombard.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.MessageProcessors.Factory;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Lombard.Common.Helper;
using Lombard.Common.Messages;

namespace Lombard.Adapters.DipsAdapter.Modules
{
    public class MessageModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            //MessageBus

            var messageBus = MessageBusFactory.CreateBus(connectionString);

            builder
                .RegisterInstance(messageBus)
                .As<IAdvancedBus>()
                .SingleInstance();

            //Error Handling

            builder
                .RegisterType<CustomErrorHandling>()
                .As<ICustomErrorHandling>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<Error>>()
                .As<IExchangePublisher<Error>>()
                .SingleInstance();

            //Mapper

            builder
                .RegisterType<BatchCodelineRequestMapHelper>()
                .As<IBatchCodelineRequestMapHelper>()
                .SingleInstance();

            builder
                .RegisterType<BatchTransactionRequestMapHelper>()
                .As<IBatchTransactionRequestMapHelper>()
                .SingleInstance();

            builder
                .RegisterType<BatchCheckThirdPartyRequestMapHelper>()
                .As<IBatchCheckThirdPartyRequestMapHelper>()
                .SingleInstance();

            builder
                .RegisterType<GenerateCorrespondingVoucherRequestMapHelper>()
                .As<IGenerateCorrespondingVoucherRequestMapHelper>()
                .SingleInstance();

            builder
                .RegisterType<GenerateBulkCreditRequestMapHelper>()
                .As<IGenerateBulkCreditRequestMapHelper>()
                .SingleInstance();

            //Queues

            builder
                .RegisterType<ValidateCodelineRequestProcessorFactory>()
                .As<IMessageProcessorFactory<ValidateBatchCodelineRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<ValidateBatchCodelineRequest>>()
                .As<IQueueConsumer<ValidateBatchCodelineRequest>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectCodelineRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CorrectBatchCodelineRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<CorrectBatchCodelineRequest>>()
                .As<IQueueConsumer<CorrectBatchCodelineRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateTransactionRequestProcessorFactory>()
                .As<IMessageProcessorFactory<ValidateBatchTransactionRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<ValidateBatchTransactionRequest>>()
                .As<IQueueConsumer<ValidateBatchTransactionRequest>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectTransactionRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CorrectBatchTransactionRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<CorrectBatchTransactionRequest>>()
                .As<IQueueConsumer<CorrectBatchTransactionRequest>>()
                .SingleInstance();

            builder
                .RegisterType<CheckThirdPartyRequestProcessorFactory>()
                .As<IMessageProcessorFactory<CheckThirdPartyBatchRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<CheckThirdPartyBatchRequest>>()
                .As<IQueueConsumer<CheckThirdPartyBatchRequest>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateCorrespondingVoucherRequestProcessorFactory>()
                .As<IMessageProcessorFactory<GenerateCorrespondingVoucherRequest>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<GenerateCorrespondingVoucherRequest>>()
                .As<IQueueConsumer<GenerateCorrespondingVoucherRequest>>()
                .SingleInstance();

            builder
                .RegisterType<GetVouchersInformationResponseProcessorFactory>()
                .As<IMessageProcessorFactory<GetVouchersInformationResponse>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<GetVouchersInformationResponse>>()
                .As<IQueueConsumer<GetVouchersInformationResponse>>()
                .SingleInstance();

            builder
                .RegisterType<QueueConsumer<GenerateBatchBulkCreditRequest>>()
                .As<IQueueConsumer<GenerateBatchBulkCreditRequest>>()
                .SingleInstance();

            builder
               .RegisterType<GenerateBulkCreditRequestProcessorFactory>()
               .As<IMessageProcessorFactory<GenerateBatchBulkCreditRequest>>()
               .SingleInstance();

            //Exchanges

            builder
                .RegisterType<ExchangePublisher<ValidateBatchCodelineResponse>>()
                .As<IExchangePublisher<ValidateBatchCodelineResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CorrectBatchCodelineResponse>>()
                .As<IExchangePublisher<CorrectBatchCodelineResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<ValidateBatchTransactionResponse>>()
                .As<IExchangePublisher<ValidateBatchTransactionResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CorrectBatchTransactionResponse>>()
                .As<IExchangePublisher<CorrectBatchTransactionResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<CheckThirdPartyBatchResponse>>()
                .As<IExchangePublisher<CheckThirdPartyBatchResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<GenerateCorrespondingVoucherResponse>>()
                .As<IExchangePublisher<GenerateCorrespondingVoucherResponse>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<GetVouchersInformationRequest>>()
                .As<IExchangePublisher<GetVouchersInformationRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<GetVouchersInformationRequest>>()
                .As<IExchangePublisher<GetVouchersInformationRequest>>()
                .SingleInstance();

            builder
                .RegisterType<ExchangePublisher<GenerateBatchBulkCreditResponse>>()
                .As<IExchangePublisher<GenerateBatchBulkCreditResponse>>()
                .SingleInstance();
        }
    }
}
