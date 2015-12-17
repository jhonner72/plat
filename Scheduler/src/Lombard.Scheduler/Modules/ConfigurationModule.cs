using Autofac;
using Lombard.Scheduler.EntityFramework;
using Castle.Components.DictionaryAdapter;
using Lombard.Scheduler.Configuration;
using Lombard.Scheduler.Domain;
using System.Configuration;

namespace Lombard.Scheduler.Modules
{
    public class ScheduleModules : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
               .Register(_ => new DictionaryAdapterFactory().GetAdapter<IQueueConfiguration>(ConfigurationManager.AppSettings))
               .InstancePerLifetimeScope();
        }
    }
}