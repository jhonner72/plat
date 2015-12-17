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
                .RegisterType<A2IAService>()
                .As<IA2IAService>()
                .SingleInstance();

        }
    }
}
