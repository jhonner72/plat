using System;
using System.Collections.Generic;
using System.Linq;
using Lombard.Vif.Service.Domain;
using Lombard.Vif.Service.Extensions;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.Service.Utils
{
    public class RequestConverterHelper
    {
        public virtual VoucherInformation GetPrimeCredit(IList<VoucherInformation> vouchers)
        {
            int numberOfCredit = vouchers.Count(v => v.voucher.documentType == DocumentTypeEnum.Cr);

            if (numberOfCredit == 1)
            {
                return vouchers.Single(v => v.voucher.documentType == DocumentTypeEnum.Cr);
            }
            else if (numberOfCredit > 1)
            {
                var creditVouchers = vouchers.Where(v => v.voucher.documentType == DocumentTypeEnum.Cr);

                if (creditVouchers.Any(v => !string.IsNullOrEmpty(v.GetExtraAuxDom())))
                {
                    return creditVouchers.Last(v => !string.IsNullOrEmpty(v.GetExtraAuxDom()));
                }
                else if (creditVouchers.Any(v => !string.IsNullOrEmpty(v.GetAuxDom())))
                {
                    return creditVouchers.Last(v => !string.IsNullOrEmpty(v.GetAuxDom()));
                }
                else
                {
                    return creditVouchers.Last();
                }
            }

            return null;
        }
    }
}
