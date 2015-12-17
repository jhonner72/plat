namespace Lombard.ECLMatchingEngine.Service.Modules
{
    using Autofac;
    using Lombard.ECLMatchingEngine.Service.Data;
    using Lombard.ECLMatchingEngine.Service.Mappers;

    public class MappersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<MatchVoucherRequestToECLRecordBatch>()
                .As<IMatchVoucherRequestToECLRecordBatch>();

            builder
                .RegisterType<MatchVoucherRequestToVoucherInformationBatch>()
                .As<IMatchVoucherRequestToVoucherInformationBatch>();

            builder
               .RegisterType<MatchedVoucherInformationToECLFileInfo>()
               .As<IMatchedVoucherInformationToECLFileInfo>();

            builder.RegisterType<ECLRecordDbContext>()
                .As<IECLInfoDataEntityFrameworks>()
                .InstancePerLifetimeScope();
        }
    }
}

