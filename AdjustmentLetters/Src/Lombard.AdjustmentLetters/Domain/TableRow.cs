using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.AdjustmentLetters.Service.Domain
{
    public class TableRow : TransactionInputReport
    {
        [OutputFormat(FieldName = "Bch", Order = 2, FieldWidth = 4, ColumnWidth = 6, Pad = "Right")]
        public string Bch { get; set; }

        [OutputFormat(FieldName = "Din", Order = 2, FieldWidth = 10, ColumnWidth = 11, Pad = "Right")]
        public string Din { get; set; }

        [OutputFormat(FieldName = "NegBSB", Order = 2, FieldWidth = 6, ColumnWidth = 10, Pad = "Right")]
        public string NegBSB { get; set; }

        [OutputFormat(FieldName = "Ean", Order = 2, FieldWidth = 10, ColumnWidth = 16, Pad = "Right")]
        public string Ean { get; set; }

        [OutputFormat(FieldName = "Ad", Order = 2, FieldWidth = 9, ColumnWidth = 14, Pad = "Right")]
        public string AD { get; set; }

        [OutputFormat(FieldName = "Bsb", Order = 2, FieldWidth = 6, ColumnWidth = 8, Pad = "Right")]
        public string BSB { get; set; }

        [OutputFormat(FieldName = "AccountNo", Order = 2, FieldWidth = 10, ColumnWidth = 18, Pad = "Right")]
        public string AccountNo { get; set; }

        [OutputFormat(FieldName = "TranCode", Order = 2, FieldWidth = 4, ColumnWidth = 6, Pad = "Right")]
        public string TransCode { get; set; }

        [OutputFormat(FieldName = "Drcr", Order = 2, FieldWidth = 3, ColumnWidth = 5, Pad = "Right")]
        public string DRCR { get; set; }

        [OutputFormat(FieldName = "Amount", Order = 2, FieldWidth = 15, ColumnWidth = 21, Pad = "Left")]
        public string AMOUNT { get; set; }
    }
}
