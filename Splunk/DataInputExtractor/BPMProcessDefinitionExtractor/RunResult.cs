using System;
using System.Collections.Generic;

namespace BPMProcessDefinitionExtractor
{
    public class RunResult
    {
        public List<BpmProcessDefinition> Processes { get; set; }

        public RunResult()
        {
            Processes = new List<BpmProcessDefinition>();
        }
    }
}
