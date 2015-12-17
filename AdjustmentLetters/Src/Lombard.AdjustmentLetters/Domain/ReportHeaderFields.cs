namespace Lombard.AdjustmentLetters.Domain
{
    public class HeaderRow : TransactionInputReport
    {
        public string WorkstationId { get; set; }

        public string BusinessDate { get; set; }

        public string ReportOn { get; set; }

        public string Page { get; set; }

        public string BorderLine { get; set; }
    }
}
