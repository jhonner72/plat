using System;

namespace BpmInstanceExtractor
{
    public class BpmInstance
    {
        public string ProcessInstanceId { get; set; }
        public string ProcessDefinitionId { get; set; }
        public string BusinessKey { get; set; }
        
        public DateTime StartTime { get; set; }
        public DateTime? EndTime { get; set; }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;

            var activity = obj as BpmInstance;

            bool isEndTimeEqual = false;
            if ((!this.EndTime.HasValue && !activity.EndTime.HasValue) || 
                (this.EndTime.HasValue && activity.EndTime.HasValue && this.EndTime.Value.Equals(activity.EndTime.Value)))
            {
                isEndTimeEqual = true;
            }
            
            var testActivityBusinessKey = activity.BusinessKey != null ? activity.BusinessKey : "";
            var testThisBusinessKey = this.BusinessKey != null ? this.BusinessKey : "";

            var isEqual = false;
            try
            {
                isEqual = activity.ProcessInstanceId.Equals(this.ProcessInstanceId) &&
                    activity.ProcessDefinitionId.Equals(this.ProcessDefinitionId) &&
                    testActivityBusinessKey.Equals(testThisBusinessKey) &&
                    activity.StartTime.Equals(this.StartTime) &&
                    isEndTimeEqual;
            }
            catch (Exception ex)
            {
                Console.WriteLine("[ERROR] BpmInstance:Equals: " + ex.ToString());
            }

            return isEqual;
        }
    }
}
