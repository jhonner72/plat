using A2iACheckReaderLib;
using Autofac;
using Lombard.Adapters.A2iaAdapter.Wrapper;

namespace Lombard.Adapters.A2iaAdapter.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
            .As<IStartable>();

            builder.RegisterType<ServiceRunner>();

            builder
                .RegisterType<APIClass>()
                .As<API>();

            builder
                .RegisterType<A2iACombinedTableService>()
                .As<IOcrProcessingService>()
                .SingleInstance();
        }
    }
}
