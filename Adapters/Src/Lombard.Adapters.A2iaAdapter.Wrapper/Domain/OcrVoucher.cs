using Serilog.Extras.Attributed;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.Domain
{
    public enum VoucherType
    {
        Debit,
        Credit
    }

    public class OcrVoucher
    {
        [NotLogged]
        internal int RequestId { get; set; }
        [NotLogged]
        internal int ResultId { get; set; }
        [NotLogged]
        internal byte[] ImageBuffer { get; set; }
        [NotLogged]
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
