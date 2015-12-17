using System;

namespace BpmHistoryExtractor
{
    class BpmHistory
    {
        public string ProcessInstanceId { get; set; }
        public string ProcessDefinitionId { get; set; }
        public string BusinessKey { get; set; }
        public string Filename { get; set; }
        public DateTime StartTime { get; set; }
        public DateTime? EndTime { get; set; }
        public decimal? Duration { get; set; }
    }
}
