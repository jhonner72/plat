using System.Configuration;
using System.Data.Common;
using System.Data.SqlClient;
using System.IO.Abstractions;
using Autofac;
using Lombard.AdjustmentLetters.Data;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Helper;

namespace Lombard.AdjustmentLetters.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>()
                .SingleInstance();

            builder.RegisterType<ServiceRunner>()
                .SingleInstance();

            builder.RegisterType<FileSystem>()
                .As<IFileSystem>()
                .SingleInstance();

            builder.RegisterType<FileReader>()
               .As<IFileReader>()
               .SingleInstance();

            builder.RegisterType<AsposeWrapper>()
                .As<IAsposeWrapper>()
                .SingleInstance();

            //DbContext

            var connectionString = ConfigurationManager.ConnectionStrings["referenceDb"].ConnectionString;

            builder
                .Register(_ => new SqlConnection(connectionString))
                .As<DbConnection>();

            builder.RegisterType<ReferenceDbContext>()
                .As<IReferenceDbContext>();
        }
    }
}
