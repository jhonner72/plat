using Autofac;
using Lombard.Adapters.A2iaAdapter.Domain;
using Lombard.Adapters.A2iaAdapter.Mappers;
using Lombard.Common.Jobs;
//using Lombard.Adapters.A2iaAdapter.Jobs;

namespace Lombard.Adapters.A2iaAdapter.Modules
{
    public class JobModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            //builder
            //    .RegisterType<ProcessChequeImageJob>()
            //    .As<IJob<BatchInfo>>();

            //builder
            //    .RegisterType<ProcessChequeImageJobFactory>()
            //    .As<IJobFactory<BatchInfo>>();

            builder
                .RegisterType<JobManager<BatchInfo>>()
                .As<IJobManager<BatchInfo>>()
                .SingleInstance();

            builder
                .RegisterType<CourtesyAmountRequestBatchInfoMapper>()
                .As<ICourtesyAmountRequestBatchInfoMapper>();

            builder
                .RegisterType<ChequeOcrResponseToMessageResponseMapper>()
                .As<IChequeOcrResponseToMessageResponseMapper>();

        }

    }
}
