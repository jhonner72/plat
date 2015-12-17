using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.Service.Extensions
{
    public static class VoucherInformationExtension
    {
        public static string GetExtraAuxDom(this VoucherInformation voucherInformation)
        {
            if (voucherInformation.voucherProcess.highValueFlag)
            {
                if (!string.IsNullOrEmpty(voucherInformation.voucherProcess.alternateExAuxDom))
                {
                    return voucherInformation.voucherProcess.alternateExAuxDom;
                }
            }
            return voucherInformation.voucher.extraAuxDom;
        }

        public static string GetAuxDom(this VoucherInformation voucherInformation)
        {
            if (voucherInformation.voucherProcess.highValueFlag)
            {
                if (!string.IsNullOrEmpty(voucherInformation.voucherProcess.alternateAuxDom))
                {
                    return voucherInformation.voucherProcess.alternateAuxDom;
                }
            }
            return voucherInformation.voucher.auxDom;
        }

        public static string GetTransactionCode(this VoucherInformation voucherInformation)
        {
            if (voucherInformation.voucherProcess.highValueFlag)
            {
                if (!string.IsNullOrEmpty(voucherInformation.voucherProcess.alternateTransactionCode))
                {
                    return voucherInformation.voucherProcess.alternateTransactionCode;
                }
            }
            return voucherInformation.voucher.transactionCode;
        }
    }
}
