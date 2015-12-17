using System.IO.Abstractions;
using Autofac;

namespace Lombard.Vif.Service.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>();

            builder.RegisterType<ServiceRunner>();

            builder.RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();
        }
    }
}
