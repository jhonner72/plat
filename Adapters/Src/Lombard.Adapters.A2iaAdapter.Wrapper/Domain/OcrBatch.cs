using System.Collections.Generic;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.Domain
{
    public class OcrBatch
    {
        public string JobIdentifier { get; set; }
        public IList<OcrVoucher> Vouchers { get; set; }
    }
}
