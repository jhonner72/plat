using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Hangfire;
using Lombard.Scheduler.EntityFramework;
using Lombard.Vif.Service.Messages.XsdImports;
using Newtonsoft.Json;
using Serilog;

namespace Lombard.Scheduler.Utils
{
    public static class TaskHelper
    {
        public static DateTime? GetDateTimeObject(string time)
        {
            //Expecting HH:mm format; 
            DateTime? thisTime = null;
            var hour = int.Parse(time.Substring(0, 2));
            var min = int.Parse(time.Substring(3, 2));

            TimeSpan ts = new TimeSpan(hour, min, 0);
            thisTime = DateTime.Today + ts;

            return thisTime;
        }

        public static String UpdateRecurringTaskSchedule(int minute)
        {
            return String.Format("*/{0} * * * *", minute);
        }

        public static BusinessCalendar isProcessingDay(IEntityFramework entityFramework)
        {
            JsonTextReader reader =null;
            BusinessCalendar current = null;
            try
            {
                var currentDate = DateTime.Today;
                var jSon = entityFramework.GetBusinessCalendar.FirstOrDefault();

                var serializer = JsonSerializerFactory.Get();

                
                if (jSon != null)
                {
                    reader = new JsonTextReader(new StringReader(jSon.ref_value));
                    current = (BusinessCalendar)serializer.Deserialize(reader, typeof(BusinessCalendar));
                    Log.Debug("[TaskHelper]:[isProcessingDay]: BusinessCalendar Object is obtained.");
                }
            }
            catch(Exception ex)
            {
                Log.Error("[TaskHelper]:[isProcessingDay]: An error occurred in deserialising value from {entityFramework}.",reader.ToString());
                throw new InvalidOperationException(ex.ToString());
            }
            
            return current;
        }

        public static Job GenerateJob(string prefix, string subject, string predicate, Parameter[] parameters = null)
        {
            return new Job()
            {
                jobIdentifier = String.Concat(prefix, "-", Guid.NewGuid().ToString()),
                initiatingJobIdentifier = null,
                processIdentifier = null,
                subject = subject,
                predicate = predicate,
                status = 0,
                activity = null,
                parameters = parameters
            };
        }

        public static bool DeleteBackgroundJobs(string JobId)
        {

           bool deletedSuccesfully = false;
           try
           {
               var monitoringApi = JobStorage.Current.GetMonitoringApi();
               var allScheduledJobs = monitoringApi.ScheduledJobs(0, 10000);

               var result = allScheduledJobs.Where(job => job.Value.Job.Type.Name.ToString().Contains(JobId))
                                                .Select(job => BackgroundJob.Delete(job.Key)).ToList();

               if (result.Contains(false))
               {
                   deletedSuccesfully = false;
                   Log.Warning("[TaskHelper]:[DeleteBackgroundJobs]: Deleted {taskName} background task process failed.", JobId);
               }
               else
               {
                   deletedSuccesfully = true;
                   Log.Information("[TaskHelper]:[DeleteBackgroundJobs]: Deleted {taskName} background task process succesfull.", JobId);
               }
           }
           catch(Exception ex)
           {
               Log.Information("[TaskHelper]:[DeleteBackgroundJobs]: Deleted {taskName} process was errored. Exception was {exception}", JobId, ex.ToString());

           }

           return deletedSuccesfully;
        }

        public static SchedulerDetails GetSchedulerDetailByProcessName(this SchedulerReference reference, string processName)
        {
            return reference.scheduler.Single(s => s.processName == processName);
        }

        public static List<SchedulerDetails> GetSchedulerDetailsByWorkType(this SchedulerReference reference, string workType)
        {
            return reference.scheduler.Where(s => s.workType == workType).ToList();
        }
    }
}