using Autofac;
using Lombard.Scheduler.EntityFramework;
using Castle.Components.DictionaryAdapter;
using Lombard.Scheduler.Configuration;
using Lombard.Scheduler.Domain;
using System.Configuration;
using Lombard.Scheduler.Processor;
using Lombard.Scheduler.Utils;

namespace Lombard.Scheduler.Modules
{
    public class TaskModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<ProcessingDay>()
                 .As<IEntityFramework>()
                 .InstancePerLifetimeScope();

            builder.RegisterType<InwardsFVService>()
               .As<IInwardsFVService>()
               .InstancePerLifetimeScope();

            builder.RegisterType<Lombard.Scheduler.Domain.Vif>()
                .As<IVif>()
                .InstancePerLifetimeScope();

            builder.RegisterType<StartOfDay>()
                .As<IStartOfDay>()
                .InstancePerLifetimeScope();

            builder.RegisterType<ImageExchangeService>()
              .As<IImageExchange>()
              .InstancePerLifetimeScope();

            builder.RegisterType<AgencyBanks>()
              .As<IAgencyBanks>()
              .InstancePerLifetimeScope();

            builder.RegisterType<EndOfDayInit>()
              .As<IEndOfDayInit>()
              .InstancePerLifetimeScope();

            builder.RegisterType<EndOfDayFinal>()
              .As<IEndOfDayFinal>()
              .InstancePerLifetimeScope();

            builder.RegisterType<TaskProcessor>()
              .As<ITaskProcessor>()
              .InstancePerLifetimeScope();

            builder.RegisterType<SchedulerHelper>()
              .As<ISchedulerHelper>()
              .InstancePerLifetimeScope();

            builder.RegisterType<LockedBoxCorp>()
              .As<ILockedBoxCorp>()
              .InstancePerLifetimeScope();

            builder.RegisterType<AusPostECL>()
                .As<IAusPostECL>()
                .InstancePerLifetimeScope();
        }
    }
}