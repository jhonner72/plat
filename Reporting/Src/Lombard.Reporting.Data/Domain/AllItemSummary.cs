using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Reporting.Data.Domain
{
    public class AllItemSummary
    {
        public int? DrCount { get; set; }
        public int? CrCount { get; set; }
        public decimal? DrTotal { get; set; }
        public decimal? CrTotal { get; set; }
    }
}
