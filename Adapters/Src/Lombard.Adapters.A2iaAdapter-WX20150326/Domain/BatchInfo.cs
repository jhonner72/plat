namespace Lombard.Adapters.A2iaAdapter.Domain
{
    /// <summary>
    /// Intermediary data carrier for a batch
    /// </summary>
    public class BatchInfo
    {
        public string CorrelationId { get; set; }
        public ChequeImageInfo[] ChequeImageInfos { get; set; }
    }
}
