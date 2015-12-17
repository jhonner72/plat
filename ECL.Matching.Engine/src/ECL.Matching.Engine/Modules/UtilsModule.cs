namespace Lombard.ECLMatchingEngine.Service.Modules
{
    using Autofac;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.Utils;


    public class UtilssModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<ECLRecordFileSystem>()
                .As<IECLRecordFileSystem>();

            builder
               .RegisterType<VoucherInfoBatch>()
               .As<iVoucherInfoBatch>();

        }
    }
}

