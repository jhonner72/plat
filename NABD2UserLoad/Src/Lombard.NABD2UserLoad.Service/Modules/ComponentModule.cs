using Autofac;
using Lombard.NABD2UserLoad.Data;
using Microsoft.Practices.EnterpriseLibrary.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.NABD2UserLoad.Service.Modules
{
    public class ComponentModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<LoggerStartable>()
                .As<IStartable>();

            DatabaseFactory.SetDatabaseProviderFactory(new DatabaseProviderFactory());
            Database database = DatabaseFactory.CreateDatabase();

            builder
                .RegisterInstance(database)
                .SingleInstance()
                .As<Database>();

            builder.RegisterType<NabUserSynchRepository>()
               .As<INabUserSynchRepository>()
               .SingleInstance();

            builder.RegisterType<ServiceRunner>();
        }
    }
}
