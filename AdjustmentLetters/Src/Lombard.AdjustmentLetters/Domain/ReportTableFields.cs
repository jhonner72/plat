namespace Lombard.AdjustmentLetters.Domain
{
    public class TableRow : TransactionInputReport
    {
        [OutputFormat(FieldName = "Bch", Order = 2, FieldWidth = 4, ColumnWidth = 6)]
        public string Bch { get; set; }

        [OutputFormat(FieldName = "Din", Order = 2, FieldWidth = 10, ColumnWidth = 11)]
        public string Din { get; set; }

        [OutputFormat(FieldName = "NegBSB", Order = 2, FieldWidth = 6, ColumnWidth = 10)]
        public string NegBsb { get; set; }

        [OutputFormat(FieldName = "Ean", Order = 2, FieldWidth = 10, ColumnWidth = 16)]
        public string Ean { get; set; }

        [OutputFormat(FieldName = "Ad", Order = 2, FieldWidth = 9, ColumnWidth = 14)]
        public string Ad { get; set; }

        [OutputFormat(FieldName = "Bsb", Order = 2, FieldWidth = 6, ColumnWidth = 8)]
        public string Bsb { get; set; }

        [OutputFormat(FieldName = "AccountNo", Order = 2, FieldWidth = 10, ColumnWidth = 18)]
        public string AccountNo { get; set; }

        [OutputFormat(FieldName = "TranCode", Order = 2, FieldWidth = 4, ColumnWidth = 6)]
        public string TransCode { get; set; }

        [OutputFormat(FieldName = "Drcr", Order = 2, FieldWidth = 3, ColumnWidth = 5)]
        public string Drcr { get; set; }

        [OutputFormat(FieldName = "Amount", Order = 2, FieldWidth = 15, ColumnWidth = 21)]
        public string Amount { get; set; }
    }
}
