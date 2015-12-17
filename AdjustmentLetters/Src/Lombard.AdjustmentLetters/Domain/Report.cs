using System.Collections.Generic;

namespace Lombard.AdjustmentLetters.Domain
{
    public class Report
    {
        public HeaderRow HeaderInfo { get; set; }

        public List<TableRow> VoucherRows { get; set; }

        public FooterRow FooterInfo { get; set; }

    }
}
