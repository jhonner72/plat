
using Autofac;

namespace Lombard.Documentum.Service.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<ServiceRunner>();

            builder.RegisterType<LoggerStartable>()
                .As<IStartable>();
        }
    }
}
