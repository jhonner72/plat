using System;

namespace BpmActivityExtractor
{
    public class BpmActivity
    {
        public string ProcessInstanceId { get; set; }
        public string ProcessDefinitionId { get; set; }
        public string BusinessKey { get; set; }
        
        public DateTime StartTime { get; set; }
        public DateTime? EndTime { get; set; }
        public string Duration {get;set;}
        public string Id { get; set; }
        public string Name { get; set; }
        public byte State { get; set; }
        

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;

            var activity = obj as BpmActivity;

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
                    activity.Id.Equals(this.Id) &&
                    activity.Name.Equals(this.Name) &&
                    activity.State.Equals(this.State) &&
                    activity.StartTime.Equals(this.StartTime) &&
                    isEndTimeEqual ;
            }
            catch(Exception ex)
            {
                Console.WriteLine("[ERROR] BpmActivity:Equals: " + ex.ToString());
            }

            return isEqual;
        }
    }
}
