using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Hangfire;
using Lombard.Scheduler.Models;
using Lombard.Scheduler.Domain;
using Serilog;
using System.Web.Security;

namespace Lombard.Scheduler.Controllers
{
    public class EndOFDayController : Controller
    {
        [HttpGet]
        public ActionResult Index()
        {
            var model = new EndOfDayModel();

            var monitorApi = JobStorage.Current.GetMonitoringApi();
            var jobConnection = JobStorage.Current.GetConnection();

            var jobList = monitorApi.ScheduledJobs(0, 100);

            foreach (var job in jobList)
            {
                var eodTaskItem = new EodTaskItem();

                var jobCount = GetJobCountById(job.Key);

                eodTaskItem.Id = job.Key;
                eodTaskItem.Type = job.Value.Job.Type.Name;
                eodTaskItem.DisplayName = GetDisplayType(job.Value.Job.Type.Name);
                eodTaskItem.ScheduledTime = job.Value.EnqueueAt.ToLocalTime().ToString();
                eodTaskItem.Checked = false;
                eodTaskItem.JobDelayCount = jobCount;
                model.DelayedBy = HttpContext.Request.LogonUserIdentity.Name;

                model.Tasks.Add(eodTaskItem);
            }
            return View(model);
        }
       
        [HttpPost]
        public ActionResult Index(EndOfDayModel model)
        {
            if (!ModelState.IsValid)
                return View(model);

            var monitorApi = JobStorage.Current.GetMonitoringApi();
            var jobConnection = JobStorage.Current.GetConnection();

            var jobs = monitorApi.ScheduledJobs(0, 100);

            foreach (var task in model.Tasks)
            {
                if(task.Checked)
                {
                    var jobId = task.Id;
                    var jobDetails = jobs.SingleOrDefault(x => x.Key == jobId);
                   
                    if (jobDetails.Value != null)
                    {
                        var jobCount = GetJobCountById(jobId);
   
                        if (jobCount ==1 && String.IsNullOrEmpty(model.AuthorisedBy))
                        {
                            ModelState.AddModelError("Authorised by is required", "Authorised By is required.");
                            Log.Information("Attempted to delay the process but Authorised by value was empty. Attempted by {delayby}", model.DelayedBy);

                            return View(model);
                        }
                        if (jobCount >= 2 && task.Type != "IAgencyBanks")
                        {
                            ModelState.AddModelError("AlreadyDelayed", "Task already delayed maximum allowed times.");
                            Log.Information("End of Day delay failed due to task being delayed the maximum allowed times already.");
                            
                            return View(model);
                        }

                        var isDeleted = BackgroundJob.Delete(jobId, "Scheduled");
                        if (!isDeleted)
                        {
                            Log.Information("End of Day could not be delayed due to Exception");
                        }

                        var oldDate = jobDetails.Value.EnqueueAt;
                        var newDate = oldDate.AddMinutes(15);
                        var newDateOffset = new DateTimeOffset(newDate);

                        jobCount++;
                        var result = "";

                        if (task.Type == "IEndOfDayInit")
                            result = BackgroundJob.Schedule<IEndOfDayInit>(x => x.ProcessTask(), newDate);
                        else if (task.Type == "IEndOfDayFinal")
                            result = BackgroundJob.Schedule<IEndOfDayFinal>(x => x.ProcessTask(), newDate);
                        else
                            result = BackgroundJob.Schedule<IAgencyBanks>(x => x.ProcessTask(), newDate);

                        jobConnection.SetJobParameter(result, "JobCount", jobCount.ToString());
                        jobConnection.SetJobParameter(result, "DelayedBy", model.DelayedBy);
                        jobConnection.SetJobParameter(result, "AuthorisedBy", model.AuthorisedBy);

                        Log.Information("End of Day delayed with manual intervention. DelayedBy: {delayedBy}, AuthorisedBy: {authorisedBy}", model.DelayedBy, model.AuthorisedBy);
                        
                    }                   
                }                                
            }

            return RedirectToAction("Index");
        }

        private int GetJobCountById(string jobId)
        {
            var jobConnection = JobStorage.Current.GetConnection();

            var jobCountString = jobConnection.GetJobParameter(jobId, "JobCount");

            var count = 0;

            var tryParseInt = Int32.TryParse(jobCountString, out count);

            return count;

        }

        private string GetDisplayType(string typeName)
        {
            switch (typeName)
            {
                case "IEndOfDayInit": return "End of Day Initial";

                case "IEndOfDayFinal": return "End of Day Final";

                case "IAgencyBanks": return "Agency Banks";

                default: return "Error";

            }
        }
        
    }
}