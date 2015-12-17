using System.Configuration;
using System.Data.Common;
using System.Data.SqlClient;
using System.IO.Abstractions;
using Autofac;
using Autofac.Extras.Quartz;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Common.DateAndTime;

namespace Lombard.Adapters.DipsAdapter.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>()
                .SingleInstance();

            builder
                .RegisterType<ServiceRunner>()
                .SingleInstance();

            builder
                .RegisterType<DateTimeProvider>()
                .As<IDateTimeProvider>()
                .SingleInstance();

            builder
                .RegisterType<ImageMergeHelper>()
                .As<IImageMergeHelper>()
                .SingleInstance();

            builder
                .RegisterType<ScannedBatchHelper>()
                .As<IScannedBatchHelper>()
                .SingleInstance();

            //General
            builder
                .RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();


            //Quartz

            builder.RegisterModule(new QuartzAutofacFactoryModule());
            builder.RegisterModule(new QuartzAutofacJobsModule(GetType().Assembly));

            //DbContext

            var connectionString = ConfigurationManager.ConnectionStrings["dips"].ConnectionString;

            builder
                .Register(_ => new SqlConnection(connectionString))
                .As<DbConnection>();

            builder.RegisterType<DipsDbContext>()
                .As<IDipsDbContext>();
        }
    }
}
