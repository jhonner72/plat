using System;
using System.Collections.Generic;

namespace BpmInstanceExtractor
{
    public class RunResult
    {
        public DateTime? PreviousMaxEndTime { get; set; }
        public List<BpmInstance> Instances { get; set; }

        public RunResult()
        {
            Instances = new List<BpmInstance>();
        }
    }
}
