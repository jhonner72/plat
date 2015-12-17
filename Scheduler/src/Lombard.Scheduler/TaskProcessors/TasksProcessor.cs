using System;
using Hangfire;
using Hangfire.Logging;
using Lombard.Scheduler.Domain;
using Lombard.Scheduler.EntityFramework;
using Lombard.Scheduler.Utils;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using Autofac;

namespace Lombard.Scheduler.Processor
{
    public interface ITaskProcessor
    {
    }

    public class TaskProcessor : ITaskProcessor
    {
        private IEntityFramework entityFramework;
   
        public TaskProcessor(IComponentContext container)
        {
            Log.Information("Task Processor is started.");
            
            entityFramework = container.Resolve<IEntityFramework>();

            BusinessCalendar currentValue = TaskHelper.isProcessingDay(entityFramework);

            if (currentValue == null)
                throw new InvalidOperationException();

            if (currentValue.businessDay.Date != DateTime.Today.Date || currentValue.inEndOfDay == true)
            {
                Log.Information("TaskProcessor: Either it is a non processing day or End of Day Process did not run. Business Day {entityFrameWorkValue}, InEndProcessing Day {finishedEndOfDay}, Today's date {today}", currentValue.businessDay.ToString(), currentValue.inEndOfDay, DateTime.Today.ToString());
            }
            else
            {
                var schedulerHelper = container.Resolve<ISchedulerHelper>();
                schedulerHelper.ScheduleStartOfDayProcess(schedulerHelper.GetSchedulerReference());
            }
        }
    }
}