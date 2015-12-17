using System.IO.Abstractions;
using Autofac;
using Lombard.Common.DateAndTime;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<LoggerStartable>()
                .As<IStartable>();

            builder
                .RegisterType<ServiceRunner>();

            builder.RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();

            builder.RegisterType<DateTimeProvider>()
                .As<IDateTimeProvider>()
                .SingleInstance();
        }
    }
}