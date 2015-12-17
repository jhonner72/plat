using Hangfire;
using Lombard.Scheduler.Constants;
using Lombard.Scheduler.Domain;
using Lombard.Scheduler.EntityFramework;
using Lombard.Vif.Service.Messages.XsdImports;
using Newtonsoft.Json;
using Serilog;
using System;
using System.IO;

namespace Lombard.Scheduler.Utils
{
    public interface ISchedulerHelper
    {
        SchedulerReference GetSchedulerReference();
        void ScheduleStartOfDayProcess(SchedulerReference schedulerReference);
        void ScheduleVifProcess(SchedulerReference schedulerReference);
        void ScheduleInwardForValueProcess(SchedulerReference schedulerReference);
        void ScheduleIEProcess(SchedulerReference schedulerReference);
        void ScheduleInitialEndOfDayProcess(SchedulerReference schedulerReference, BusinessCalendar businessCalendar);
        void ScheduleFinalEndOfDayProcess(SchedulerReference schedulerReference, BusinessCalendar businessCalendar);
        void ScheduleAgencyBankProcess(SchedulerReference schedulerReference);
        void ScheduleLockedBoxProcesses(SchedulerReference schedulerReference);
        void ScheduleCorporateProcesses(SchedulerReference schedulerReference);
        void ScheduleAusPostProcess(SchedulerReference schedulerReference);
    }

    public class SchedulerHelper : ISchedulerHelper 
    {
        private readonly IEntityFramework entityFramework;

        public SchedulerHelper(IEntityFramework entityFramework)
        {
            this.entityFramework = entityFramework;
        }

        public SchedulerReference GetSchedulerReference()
        {
            SchedulerReference reference = null;

            var json = this.entityFramework.GetMetadataByName("SchedulerReference");
            if (json != null)
            {
                var serializer = JsonSerializerFactory.Get();
                var reader = new JsonTextReader(new StringReader(json.ref_value));
                reference = serializer.Deserialize<SchedulerReference>(reader);
            }

            return reference;
        }

        public void ScheduleVifProcess(SchedulerReference schedulerReference)
        {
            var vif = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.ValueInformationFile);

            RecurringJob.AddOrUpdate<IVif>(vif.processName,
               x => x.ProcessTask(),
               TaskHelper.UpdateRecurringTaskSchedule(vif.recurringInterval),
               TimeZoneInfo.Local);

            Log.Information("AddOrUpdate {serviceName} RecurringJob.", vif.processName);
        }

        public void ScheduleInwardForValueProcess(SchedulerReference schedulerReference)
        {
            var inwardsFV = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.InwardForValue);

            RecurringJob.AddOrUpdate<IInwardsFVService>(inwardsFV.processName,
               x => x.ProcessTask(),
               TaskHelper.UpdateRecurringTaskSchedule(inwardsFV.recurringInterval),
               TimeZoneInfo.Local);

            Log.Information("AddOrUpdate {serviceName} RecurringJob.", inwardsFV.processName);
        }

        public void ScheduleIEProcess(SchedulerReference schedulerReference)
        {
            var imageExchange = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.ImageExchange);

            RecurringJob.AddOrUpdate<IImageExchange>(imageExchange.processName,
               x => x.ProcessTask(),
               TaskHelper.UpdateRecurringTaskSchedule(imageExchange.recurringInterval),
               TimeZoneInfo.Local);

            Log.Information("AddOrUpdate {serviceName} RecurringJob.", imageExchange.processName);
        }

        public void ScheduleInitialEndOfDayProcess(SchedulerReference schedulerReference, BusinessCalendar businessCalendar)
        {
            var initialEodConfig = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.InitialEndOfDay);

            var initialEodStartTime = TaskHelper.GetDateTimeObject(initialEodConfig.startTime);
            if (businessCalendar.isEndOfWeek == true)
            {
                var initEndOfWeekDelayValue = initialEodConfig.endOfWeekDelay;
                var changedInitialEodStartTime = (DateTime?)initialEodStartTime.Value.AddMinutes(initialEodConfig.endOfWeekDelay);

                Log.Information("EODI: Last Processing day of the week. Start Time for {ServiceName} has been pushed for another {delayValue} minute(s). Normal Start Time is {normalStartTime}, EODI Start time {lastProcessingDayStartTime}",
                    initialEodConfig.processName, initialEodConfig.endOfWeekDelay.ToString(), initialEodStartTime.ToString(), changedInitialEodStartTime.ToString());

                initialEodStartTime = changedInitialEodStartTime;
            }

            BackgroundJob.Schedule<IEndOfDayInit>(x => x.ProcessTask(), initialEodStartTime.Value);

            Log.Information("Schedule {serviceName} BackgroundJob.", initialEodConfig.processName);
        }

        public void ScheduleFinalEndOfDayProcess(SchedulerReference schedulerReference, BusinessCalendar businessCalendar)
        {
            var finalEodConfig = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.FinalEndOfDay);

            var finalEodStartTime = TaskHelper.GetDateTimeObject(finalEodConfig.startTime);
            if (businessCalendar.isEndOfWeek == true)
            {
                var changedFinalEodStartTime = (DateTime?)finalEodStartTime.Value.AddMinutes(finalEodConfig.endOfWeekDelay);

                Log.Information("EODF: Last Processing day of the week. Start Time for {ServiceName} has been pushed for another {delayValue} minute(s). Normal Start Time is {normalStartTime}, EODF Start time {lastProcessingDayStartTime}",
                  finalEodConfig.processName, finalEodConfig.endOfWeekDelay.ToString(), finalEodStartTime.ToString(), changedFinalEodStartTime.ToString());

                finalEodStartTime = changedFinalEodStartTime;
            }

            BackgroundJob.Schedule<IEndOfDayFinal>(x => x.ProcessTask(), finalEodStartTime.Value);

            Log.Information("Schedule {serviceName} BackgroundJob.", finalEodConfig.processName);
        }

        public void ScheduleStartOfDayProcess(SchedulerReference schedulerReference)
        {
            var sodConfig = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.StartOfDay);

            var startTime = TaskHelper.GetDateTimeObject(sodConfig.startTime);

            //Start Of Day
            RecurringJob.AddOrUpdate<IStartOfDay>(sodConfig.processName,
              y => y.ProcessTask(),
              Cron.Daily(startTime.Value.Hour, startTime.Value.Minute),
              TimeZoneInfo.Local);

            Log.Information("AddOrUpdate {serviceName} RecurringJob.", sodConfig.processName);
        }

        public void ScheduleAgencyBankProcess(SchedulerReference schedulerReference)
        {
            var agencyBankConfig = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.AgencyBank);

            var agencyBankDate = TaskHelper.GetDateTimeObject(agencyBankConfig.startTime);
            
            BackgroundJob.Schedule<IAgencyBanks>(x => x.ProcessTask(), agencyBankDate.Value);

            Log.Information("Schedule {serviceName} BackgroundJob.", agencyBankConfig.processName);
        }

        public void ScheduleLockedBoxProcesses(SchedulerReference schedulerReference)
        {
            var lockedBoxProcesses = schedulerReference.GetSchedulerDetailsByWorkType(SchedulerWorkType.LockedBox);

            foreach (var schedulerDetail in lockedBoxProcesses)
            {
                var startTime = TaskHelper.GetDateTimeObject(schedulerDetail.startTime);

                BackgroundJob.Schedule<ILockedBoxCorp>(x => x.ProcessTask(schedulerDetail.processName, schedulerDetail.batchType, schedulerDetail.workType),
                    startTime.Value);

                Log.Information("Schedule {serviceName} BackgroundJob.", schedulerDetail.processName);
            }
        }

        public void ScheduleCorporateProcesses(SchedulerReference schedulerReference)
        {
            var schedulerDetails = schedulerReference.GetSchedulerDetailsByWorkType(SchedulerWorkType.Corporate);

            foreach (var schedulerDetail in schedulerDetails)
            {
                var startTime = TaskHelper.GetDateTimeObject(schedulerDetail.startTime);

                BackgroundJob.Schedule<ILockedBoxCorp>(x => x.ProcessTask(schedulerDetail.processName, schedulerDetail.batchType, schedulerDetail.workType),
                    startTime.Value);

                Log.Information("Schedule {serviceName} BackgroundJob.", schedulerDetail.processName);
            }
        }

        public void ScheduleAusPostProcess(SchedulerReference schedulerReference)
        {
            var ausPostEclConfig = schedulerReference.GetSchedulerDetailByProcessName(SchedulerProcess.AusPostECL);

            var ausPostEclDate = TaskHelper.GetDateTimeObject(ausPostEclConfig.startTime);

            BackgroundJob.Schedule<IAusPostECL>(x => x.ProcessTask(ausPostEclConfig.workType), ausPostEclDate.Value);

            Log.Information("Schedule {serviceName} BackgroundJob.", ausPostEclConfig.processName);
        }
    }
}