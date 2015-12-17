using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.UnitTests.Builders
{
    public class VoucherInformationBuilder
    {
        private Voucher voucher;
        private VoucherProcess voucherProcess;

        public VoucherInformationBuilder()
        {
            this.voucher = new VoucherBuilder().Build();
            this.voucherProcess = new VoucherProcessBuilder().Build();
        }

        public VoucherInformationBuilder WithVoucher(Voucher voucher)
        {
            this.voucher = voucher;
            return this;
        }

        public VoucherInformationBuilder WithVoucherProcess(VoucherProcess voucherProcess)
        {
            this.voucherProcess = voucherProcess;
            return this;
        }

        public VoucherInformation Build()
        {
            return new VoucherInformation()
            {
                voucher = this.voucher,
                voucherProcess = this.voucherProcess
            };
        }
    }
}
