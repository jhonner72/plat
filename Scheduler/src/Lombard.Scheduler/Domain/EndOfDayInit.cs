using Autofac;
using Hangfire;
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
    public interface IEndOfDayInit
    {
        [DisplayName("End of Day - Initial")]
        void ProcessTask();
    }

    public class EndOfDayInit : IEndOfDayInit
    {
        private IComponentContext iContainer = null;
        private IExchangePublisher<Job> publisher;
        private IQueueConfiguration queueConfiguration;

        public EndOfDayInit(IComponentContext container)
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
                Log.Information("EODI: Initial EOD Process was started.");

                RecurringJob.RemoveIfExists(SchedulerProcess.ImageExchange);
                Log.Information("EODI: Removed {serviceName}", SchedulerProcess.ImageExchange);

                //Send RabbitMQ message to trigger EOD BPM process
                var job = TaskHelper.GenerateJob("NEDI", Subject.EndOfDayInit, Predicate.EndOfDay);
                Log.Information("EODI: Job object created successfully as per the schema.");

                publisher.PublishAsync(job, Guid.NewGuid().ToString());
                Log.Information("EODI: Message published successfully to RabbitMQ.");
            }
            catch(Exception ex)
            {
                 Log.Error("EODI: There was unexpected error occured in EndOfDayInit.ProcessTask() with the stack trace information as {stackTrace}", ex.ToString());
                 throw new InvalidOperationException();
            }
        }
    }
}