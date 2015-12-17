using System;
using System.Collections.Generic;

namespace BpmActivityExtractor
{
    public class RunResult
    {
        public DateTime? PreviousMaxEndTime { get; set; }
        public List<BpmActivity> Activities { get; set; }

        public RunResult()
        {
            Activities = new List<BpmActivity>();
        }
    }
}
