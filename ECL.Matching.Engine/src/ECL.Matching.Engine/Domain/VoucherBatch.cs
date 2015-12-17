using System.Collections.Generic;

namespace Lombard.ECLMatchingEngine.Service.Domain
{
    public interface iVoucherInfoBatch
    {
        string JobIdentifier { get; set; }
        List<IECLRecordVoucherInfo> VoucherInformation { get; set; }
    }
    public class VoucherInfoBatch : iVoucherInfoBatch
    {
        public string JobIdentifier { get; set; }
        public List<IECLRecordVoucherInfo> VoucherInformation { get; set; }
    }
}
