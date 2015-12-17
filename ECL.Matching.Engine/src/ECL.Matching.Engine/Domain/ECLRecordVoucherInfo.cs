namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using Lombard.Vif.Service.Messages.XsdImports;

    public interface IECLRecordVoucherInfo
    {
        VoucherInformation Voucher { get; set; }
        IECLRecord MatchedECLRecord { get; set; }
        bool SkippedForNextProcessing { get; set; }
    }
    public class ECLRecordVoucherInfo :IECLRecordVoucherInfo
    {
        public VoucherInformation Voucher { get; set; }

        public IECLRecord MatchedECLRecord { get; set; }

        public bool SkippedForNextProcessing { get; set; }
        
    }
}
