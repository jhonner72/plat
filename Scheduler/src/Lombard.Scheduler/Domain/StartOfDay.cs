using Autofac;
using Lombard.Scheduler.EntityFramework;
using Lombard.Scheduler.Utils;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using System;
using System.ComponentModel;

namespace Lombard.Scheduler.Domain
{
    public interface IStartOfDay
    {
        [DisplayName("Start of Day")]
        void ProcessTask();
    }

    public class StartOfDay : IStartOfDay
    {
        private IComponentContext iContainer = null;

        public StartOfDay(IComponentContext container)
        {
            this.iContainer = container;
        }

        public void ProcessTask()
        {
            var scope = iContainer.Resolve<ILifetimeScope>();

            using (var tmpScope = scope.BeginLifetimeScope())
            {
                var schedulerHelper = tmpScope.Resolve<ISchedulerHelper>();
                var entityFramework = tmpScope.Resolve<IEntityFramework>();

                var schedulerReference = schedulerHelper.GetSchedulerReference();

                BusinessCalendar businessCalendar = TaskHelper.isProcessingDay(entityFramework);

                if (businessCalendar.businessDay.Date == DateTime.Today.Date && businessCalendar.inEndOfDay == false)
                {
                    //Remove previous endofday tasks
                    RemovePreviousEndOfDayTask();

                    try
                    {
                        schedulerHelper.ScheduleVifProcess(schedulerReference);

                        schedulerHelper.ScheduleIEProcess(schedulerReference);

                        schedulerHelper.ScheduleInwardForValueProcess(schedulerReference);

                        schedulerHelper.ScheduleAgencyBankProcess(schedulerReference);

                        schedulerHelper.ScheduleInitialEndOfDayProcess(schedulerReference, businessCalendar);

                        schedulerHelper.ScheduleFinalEndOfDayProcess(schedulerReference, businessCalendar);

                        schedulerHelper.ScheduleLockedBoxProcesses(schedulerReference);

                        schedulerHelper.ScheduleCorporateProcesses(schedulerReference);

                        schedulerHelper.ScheduleAusPostProcess(schedulerReference);
                    }
                    catch (Exception ex)
                    {
                        Log.Error("SOD: An error occured in Start Of Day. {Exception}", ex.ToString());
                        throw new InvalidOperationException("Error in converting Start Time");
                    }
                }
                else
                {
                    Log.Information("SOD: Either it is a non processing day or End of Day Process did not run successfully. Business Day {entityFrameWorkValue}, InEndProcessing Day {finishedEndOfDay}, Today's date {today}", businessCalendar.businessDay.ToString(), businessCalendar.inEndOfDay, DateTime.Today.ToString());
                }
            }
        }

        private void RemovePreviousEndOfDayTask()
        {
            if (!TaskHelper.DeleteBackgroundJobs("EndOfDay"))
                Log.Warning("SOD: EndOfDay BackgroundJob could not be deleted, please check the dashboard and delete it manually.");
            else
                Log.Information("SOD: Removed the previous day EODI and EODF scheduled tasks.");
        }
    }
}