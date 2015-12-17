using Autofac;
using System.IO.Abstractions;

namespace Lombard.ECLMatchingEngine.Service.Modules
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


