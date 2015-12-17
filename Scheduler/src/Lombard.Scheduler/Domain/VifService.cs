using Autofac;
using Lombard.Common.Queues;
using Lombard.Scheduler.Configuration;
using Lombard.Scheduler.Constants;
using Lombard.Scheduler.Utils;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using System;
using System.ComponentModel;

namespace Lombard.Scheduler.Domain
{
    public interface IVif 
    {
        [DisplayName("Value Information File")]
        void ProcessTask();
    }

    public class Vif : IVif
    {
        private IExchangePublisher<Job> publisher;
        private IComponentContext iContainer = null;
        private IQueueConfiguration queueConfiguration;

        public Vif(IComponentContext container)
        {
            this.iContainer = container;

            queueConfiguration = iContainer.Resolve<IQueueConfiguration>();
            publisher = iContainer.Resolve<IExchangePublisher<Job>>();
            publisher.Declare(queueConfiguration.ResponseExchangeName);
        }

        public void ProcessTask()
        {
            try
            {
                Log.Information("VIF: recurring task is started.");
                var scope = iContainer.Resolve<ILifetimeScope>();
                using (var tmpScope = scope.BeginLifetimeScope())
                {
                    // Update recurring interval
                    var schedulerHelper = tmpScope.Resolve<ISchedulerHelper>();
                    schedulerHelper.ScheduleVifProcess(schedulerHelper.GetSchedulerReference());

                    var job = TaskHelper.GenerateJob("NVIF", Subject.Vif, Predicate.Vif);
                    Log.Information("VIF: Job object created successfully as per the schema.");

                    publisher.PublishAsync(job, Guid.NewGuid().ToString());
                    Log.Information("VIF: Message published successfully to RabbitMQ.");
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "VIF: There was unexpected error occured in VifService.ProcessTask()");
                throw ex;
            }
        }
    }
}
