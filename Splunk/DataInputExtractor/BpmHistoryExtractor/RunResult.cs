using System;
using System.Collections.Generic;

namespace BpmHistoryExtractor
{
    class RunResult
    {
        public DateTime? PreviousMaxStartTime { get; set; }
        public List<string> ProcessInstanceIds { get; set; }

        public RunResult()
        {
            ProcessInstanceIds = new List<string>();
        }
    }
}
