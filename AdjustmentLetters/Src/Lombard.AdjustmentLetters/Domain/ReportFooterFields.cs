namespace Lombard.AdjustmentLetters.Domain
{
    public class FooterRow : TransactionInputReport
    {
        [OutputFormat(FieldName = "TotalCreditAmount", Order = 1, FieldWidth = 15, ColumnWidth = 41, Pad = "Right")]
        public string TotalCreditAmount { get; set; }

        [OutputFormat(FieldName = "TotalDebitAmount", Order = 2, FieldWidth = 15, ColumnWidth = 41, Pad = "Right")]
        public string TotalDebitAmount { get; set; }

        [OutputFormat(FieldName = "TotalCreditCount", Order = 3, FieldWidth = 4, ColumnWidth = 25, Pad = "Right")]
        public string CreditCount { get; set; }

        [OutputFormat(FieldName = "TotalDebitCount", Order = 4, FieldWidth = 4, ColumnWidth = 25, Pad = "Right")]
        public string DebitCount { get; set; }
    }
}