using System.Collections.Generic;

namespace FujiXerox.Adapters.A2iaAdapter.Model
{
    public class OcrBatch
    {
        public string JobIdentifier { get; set; }
        public IList<OcrVoucher> Vouchers { get; set; }
    }
}
