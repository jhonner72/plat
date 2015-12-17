using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel.DataAnnotations;

namespace Lombard.Scheduler.Models
{
    public class EodTaskItem
    {
        public string Id { get; set; }
        public string Type { get; set; }
        public string DisplayName { get; set; }
        public string ScheduledTime { get; set; }
        public bool Checked { get; set; }
        public int JobDelayCount { get; set; }
    }

    public class EndOfDayModel
    {
        public List<EodTaskItem> Tasks { get; set; }

        [Required(ErrorMessage = "Delayed By is required"), Editable(false)]
        public string DelayedBy { get; set; }

        public string AuthorisedBy { get; set; }

        public EndOfDayModel()
        {
            Tasks = new List<EodTaskItem>();
        }
    }
}