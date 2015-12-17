namespace Lombard.ImageExchange.Nab.OutboundService.Helpers
{
    /// <summary>
    /// Simple DTO for the CoinFileTotalsCalculator's results
    /// </summary>
    public class CoinFileTrailerTotals
    {
        public int DebitCount { get; set; }
        public int CreditCount { get; set; }
        public int NonValueCount { get; set; }
        public long TotalDebits { get; set; }
        public long TotalCredits { get; set; }

        public CoinFileTrailerTotals()
        {
            DebitCount = 0;
            CreditCount = 0;
            NonValueCount = 0;
            TotalDebits = 0L;
            TotalCredits = 0L;
        }
    }
}
