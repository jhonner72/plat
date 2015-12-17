using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BpmJobExtractor
{
    public class BpmJob
    {
        public string ProcessInstanceId { get; set; }
        public string ProcessDefinitionId { get; set; }
        public string ProcessName { get; set; }
        public string BusinessKey { get; set; }
        public DateTime EndTime { get; set; }
        public string ActivityId { get; set; }
        public string ActivityName { get; set; }
        public string JobType { get; set; }
        public string JobConfiguration { get; set; }
        public int Retries { get; set; }
        public string ExceptionMessage { get; set; }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;

            var job = obj as BpmJob;

            var testActivityBusinessKey = job.BusinessKey != null ? job.BusinessKey : "";
            var testThisBusinessKey = this.BusinessKey != null ? this.BusinessKey : "";

            var isEqual = false;
            try
            {
                isEqual = job.ProcessInstanceId.Equals(this.ProcessInstanceId) &&
                    job.ProcessDefinitionId.Equals(this.ProcessDefinitionId) &&
                    testActivityBusinessKey.Equals(testThisBusinessKey) &&
                    job.ActivityId.Equals(this.ActivityId) &&
                    job.ActivityName.Equals(this.ActivityName) &&
                    job.JobType.Equals(this.JobType) &&
                    job.JobConfiguration.Equals(this.JobConfiguration) &&
                    job.Retries.Equals(this.Retries) &&
                    job.ExceptionMessage.Equals(this.ExceptionMessage) &&
                    job.EndTime.Equals(this.EndTime);
            }
            catch (Exception ex)
            {
                Console.WriteLine("[ERROR] BpmJob:Equals: " + ex.ToString());
            }

            return isEqual;
        }
    }
}
