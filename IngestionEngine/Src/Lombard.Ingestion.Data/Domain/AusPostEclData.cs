using System;

namespace Lombard.Ingestion.Data.Domain
{
    public class AusPostEclData
    {
        public string file_state { get; set; }
        public DateTime file_date { get; set; }
        public int record_sequence { get; set; }
        public string record_content { get; set; }
        public string record_type { get; set; }
    }
}
