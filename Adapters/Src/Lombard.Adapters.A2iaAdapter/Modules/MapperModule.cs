using Autofac;
using Lombard.Adapters.A2iaAdapter.Mappers;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Modules
{
    public class MapperModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<CarRequestToOcrBatchMapper>()
                .As<IMapper<RecogniseBatchCourtesyAmountRequest, OcrBatch>>();

            builder
                .RegisterType<OcrBatchToCarResponseMapper>()
                .As<IMapper<OcrBatch, RecogniseBatchCourtesyAmountResponse>>();

        }

    }
}
