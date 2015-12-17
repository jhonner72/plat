using System;
using System.Collections.Generic;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.AdjustmentLetters.Domain
{
    public class AdjustmentLetter
    {
        public string PdfFilename { get; set; }

        public string JobIdentifier { get; set; }

        public DateTime ProcessingDate { get; set; }

        public VoucherInformation AdjustedVoucher { get; set; }

        public List<VoucherInformation> Vouchers { get; set; }

        public AdjustmentLetter()
        {
            this.Vouchers = new List<VoucherInformation>();
        }
    }
}
