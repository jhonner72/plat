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
    public interface IEndOfDayFinal
    {
        [DisplayName("End of Day - Final")]
        void ProcessTask();
    }

    public class EndOfDayFinal : IEndOfDayFinal
    {
        private IComponentContext iContainer = null;
        private IExchangePublisher<Job> publisher;
        private IQueueConfiguration queueConfiguration;

        public EndOfDayFinal(IComponentContext container)
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
                Log.Information("EODF: EOD Final Process was started.");

                var scope = iContainer.Resolve<ILifetimeScope>();
                using (var tmpScope = scope.BeginLifetimeScope())
                {
                    RecurringJob.RemoveIfExists(SchedulerProcess.InwardForValue);
                    Log.Information("EODF: Removed {serviceName}", SchedulerProcess.InwardForValue);

                    RecurringJob.RemoveIfExists(SchedulerProcess.ValueInformationFile);
                    Log.Information("EODF: Removed {serviceName}", SchedulerProcess.ValueInformationFile);

                    //Send RabbitMQ message to trigger EOD BPM process
                    var job = TaskHelper.GenerateJob("NEDF", Subject.EndOfDayFinal, Predicate.EndOfDay);
                    Log.Information("EODF: Job object created successfully as per the schema.");

                    publisher.PublishAsync(job, Guid.NewGuid().ToString());
                    Log.Information("EODF: Message published successfully to RabbitMQ.");

                    var schedulerHelper = tmpScope.Resolve<ISchedulerHelper>();
                    schedulerHelper.ScheduleStartOfDayProcess(schedulerHelper.GetSchedulerReference());
                }
            }
            catch(Exception ex)
            {
                Log.Error("EODF: There was unexpected error occured in EndOfDayFinal.ProcessTask() with the stack trace information as {stackTrace}", ex.ToString());
                 throw new InvalidOperationException();
            }
              
           
        }        
    }
}