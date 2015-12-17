namespace Lombard.Reporting.AdapterService.Modules
{
    using System.Configuration;
    using System.Data.Common;
    using System.Data.SqlClient;
    using System.IO.Abstractions;
    using Autofac;
    using Lombard.Reporting.AdapterService.Utils;
    using Lombard.Reporting.Data;

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

            builder.RegisterType<ReportGeneratorFactory>()
                .As<IReportGeneratorFactory>()
                .SingleInstance();

            builder.RegisterType<AllItemsReportGenerator>()
                .Keyed<IReportGenerator>(ReportType.AllItems);

            builder.RegisterType<SSRSReportGenerator>()
                .Keyed<IReportGenerator>(ReportType.SSRS);

            builder.RegisterType<VR3CreditCardReportGenerator>()
                .Keyed<IReportGenerator>(ReportType.LockedBoxCreditCard);

            builder.RegisterType<ReportingRepository>();

            builder.RegisterType<AllItemsReportHelper>();

            builder.RegisterType<VR3CreditCardReportHelper>();

            var connectionString = ConfigurationManager.ConnectionStrings["TrackingDb"].ConnectionString;

            builder
                .Register(_ => new SqlConnection(connectionString))
                .As<DbConnection>();

            builder.RegisterType<ReportingDbContext>();
        }
    }
}
