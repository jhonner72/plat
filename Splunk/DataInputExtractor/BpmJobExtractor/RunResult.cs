using System;
using System.Collections.Generic;

namespace BpmJobExtractor
{
    public class RunResult
    {
        public DateTime? PreviousMaxEndTime { get; set; }
        public List<BpmJob> Activities { get; set; }

        public RunResult()
        {
            Activities = new List<BpmJob>();
        }
    }
}
