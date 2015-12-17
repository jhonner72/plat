using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Modules
{
    public class MapperModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<ValidateBatchCodelineRequestToDipsDbIndexMapper>()
                .As<IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateBatchCodelineRequestToDipsQueueMapper>()
                .As<IMapper<ValidateBatchCodelineRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateBatchCodelineRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchCodelineRequestToDipsDbIndexMapper>()
                .As<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchCodelineRequestToDipsQueueMapper>()
                .As<IMapper<CorrectBatchCodelineRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchCodelineRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateBatchTransactionRequestToDipsDbIndexMapper>()
                .As<IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateBatchTransactionRequestToDipsQueueMapper>()
                .As<IMapper<ValidateBatchTransactionRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<ValidateBatchTransactionRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchTransactionRequestToDipsDbIndexMapper>()
                .As<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchTransactionRequestToDipsQueueMapper>()
                .As<IMapper<CorrectBatchTransactionRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<CorrectBatchTransactionRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();
            
            builder
                .RegisterType<CheckThirdPartyBatchRequestToDipsDbIndexMapper>()
                .As<IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<CheckThirdPartyBatchRequestToDipsQueueMapper>()
                .As<IMapper<CheckThirdPartyBatchRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<CheckThirdPartyRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateCorrespondingVoucherRequestToDipsDbIndexMapper>()
                .As<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateCorrespondingVoucherRequestToDipsQueueMapper>()
                .As<IMapper<GenerateCorrespondingVoucherRequest, DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateCorrespondingVoucherRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsNabChq>>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateBulkCreditRequestToDipsDbIndexMapper>()
                .As<IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateBulkCreditRequestToDipsQueueMapper>()
                .As<IMapper<VoucherInformation[], DipsQueue>>()
                .SingleInstance();

            builder
                .RegisterType<GenerateBulkCreditRequestToDipsNabChqScanPodMapper>()
                .As<IMapper<VoucherInformation[], IEnumerable<DipsNabChq>>>()
                .SingleInstance();

        }
    }
}
