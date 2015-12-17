using System.Configuration;
using System.IO.Abstractions;
using Autofac;
using Lombard.Ingestion.Data.Repository;
using Lombard.Ingestion.Service.Helpers;
using Lombard.Ingestion.Service.Workers;

namespace Lombard.Ingestion.Service.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>();

            builder.RegisterType<ServiceRunner>();

            builder.RegisterType<EclIngestionWorker>();

            builder.RegisterType<BatchAuditIngestionWorker>();

            builder.RegisterType<EclIngestionHelper>()
                .SingleInstance();

            builder.RegisterType<FileHelper>()
                .SingleInstance();

            builder.RegisterType<BulkIngestionRepository>()
                .WithParameter("connectionString", ConfigurationManager.ConnectionStrings["TrackingDb"].ConnectionString);

            builder.RegisterType<IngestionRepository>().As<IIngestionRepository>();

            builder.RegisterType<TrackingDbContext>().InstancePerLifetimeScope();

            builder.RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();
        }
    }
}
