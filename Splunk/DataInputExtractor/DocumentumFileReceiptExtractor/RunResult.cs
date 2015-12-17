using System;
using System.Collections.Generic;

namespace DocumentumFileReceiptExtractor
{
    class RunResult
    {
        public DateTime? PreviousMaxReceivedDateTime { get; set; }
        public List<string> FileIds { get; set; }

        public RunResult()
        {
            FileIds = new List<string>();
        }
    }
}
