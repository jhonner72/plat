using System;

namespace BPMProcessDefinitionExtractor
{
    public class BpmProcessDefinition
    {
        public string ProcessInstanceId { get; set; }
        public string Name { get; set; }
        public string BusinessKey { get; set; }
        public int Version { get; set; }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;

            var activity = obj as BpmProcessDefinition;

            return activity.ProcessInstanceId.Equals(this.ProcessInstanceId) &&
                activity.Name.Equals(this.Name) &&
                activity.BusinessKey.Equals(this.BusinessKey) &&
                activity.Version.Equals(this.Version);
        }
    }
}
