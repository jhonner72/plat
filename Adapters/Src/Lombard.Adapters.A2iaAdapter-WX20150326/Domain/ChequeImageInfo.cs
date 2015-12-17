namespace Lombard.Adapters.A2iaAdapter.Domain
{
    /// <summary>
    /// Intermediary data carrier for an image
    /// </summary>
    public class ChequeImageInfo
    {
        public string CorrelationId { get; set; }
        public string DocumentReferenceNumber { get; set; }
        public string Urn { get; set; }
        //public ImageFormat Type { get; set; }
        public string Type { get; set; }
        public int Status { get; set; }
        public decimal Amount { get; set; }
        public byte[] FileContent { get; set; }
        public bool Succes { get; set; }
        public string ErrorMessage { get; set; }
    }
}
