namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using System.Collections.Generic;
    public class MatchedECLFileInfo
    {
        public string FileName { get; set; }

        public MatchedECLRecordHeader Header { get; set; }

        public IEnumerable<MatchedECLRecordBody> Body { get; set; }

        public MatchedECLRecordTrailer Trailer { get; set; }
    }
}
