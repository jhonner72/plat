namespace FujiXerox.Adapters.A2iaAdapter.Model
{
    public enum VoucherType
    {
        Debit,
        Credit
    }

    public class OcrVoucher
    {
        public int RequestId { get; set; }
        internal int ResultId { get; set; }
        internal byte[] ImageBuffer { get; set; }
        internal string ImageFormat { get; set; }

        public string Id { get; set; }
        public string ImagePath { get; set; }
        public VoucherType VoucherType { get; set; }

        public string BatchId { get; set; }

        public OcrResult AmountResult { get; set; }
        public OcrResult CodelineResult { get; set; }
        public OcrResult DateResult { get; set; }

        public int ImageRotation { get; set; }

        public OcrVoucher()
        {
            AmountResult = new OcrResult();
            CodelineResult = new OcrResult();
            DateResult = new OcrResult();
        }
    }
}
