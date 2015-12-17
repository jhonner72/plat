using System.IO.Abstractions;
using System.Web.Http;
using Autofac;
using Autofac.Integration.WebApi;
using Lombard.Common.DateAndTime;

namespace Lombard.Adapters.MftAdapter.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>();

            builder
                .RegisterType<ServiceRunner>()
                .SingleInstance();

            builder
                .RegisterType<DateTimeProvider>()
                .As<IDateTimeProvider>()
                .SingleInstance();

            //Web API
            builder.RegisterApiControllers(System.Reflection.Assembly.GetExecutingAssembly());

            builder.RegisterInstance(new HttpConfiguration());

            //General
            builder
                .RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();
        }
    }
}
