using Autofac;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class ApplicationServicesModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterAssemblyTypes(typeof (FileNameCreator).Assembly)
                .InNamespaceOf<FileNameCreator>()
                .AsImplementedInterfaces()
                .SingleInstance();

            //builder.RegisterType<SystemInfoDataConfigProvider>()
            //    .As<ISystemInfoDataAccess>()
            //    .SingleInstance();
        }
    }
}