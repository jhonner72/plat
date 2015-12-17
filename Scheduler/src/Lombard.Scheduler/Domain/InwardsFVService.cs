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
    public interface IInwardsFVService
    {
        [DisplayName("Inward for Value")]
        void ProcessTask();
    }

    public class InwardsFVService : IInwardsFVService
    {
        private IExchangePublisher<Job> publisher;
        private IComponentContext iContainer = null;
        private IQueueConfiguration queueConfiguration;

        public InwardsFVService(IComponentContext container)
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
                Log.Information("InwardsFV: recurring task is started.");
                var scope = iContainer.Resolve<ILifetimeScope>();
                using (var tmpScope = scope.BeginLifetimeScope())
                {
                    // Update recurring interval
                    var schedulerHelper = tmpScope.Resolve<ISchedulerHelper>();
                    schedulerHelper.ScheduleInwardForValueProcess(schedulerHelper.GetSchedulerReference());

                    var job = TaskHelper.GenerateJob("NFVX", Subject.InwardsFV, Predicate.InwardsFV);
                    Log.Information("InwardsFV: Job object created successfully as per the schema.");

                    publisher.PublishAsync(job, Guid.NewGuid().ToString());
                    Log.Information("InwardsFV: Message published successfully to RabbitMQ.");
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "InwardsFV: There was unexpected error occured in InwardsFVService.ProcessTask()");
                throw ex;
            }
        }
    }
}