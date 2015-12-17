using System;
using Serilog.Extras.Attributed;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class OutboundVoucher : IImageExchangeVoucher
    {
        public long Id { get; set; }

        public string BsbLedgerFi { get; set; }
        public string BsbCollectingFi { get; set; }
        public string BsbCapturingFi { get; set; }
        public string TransactionCode { get; set; }
        public long Amount { get; set; }
        public string DrawerAccountNumber { get; set; }
        public DateTime TransmissionDate { get; set; }
        public string AuxiliaryDomestic { get; set; }
        public string ExtraAuxiliaryDomestic { get; set; }
        public string TransactionIdentifier { get; set; }
        public string BatchNumber { get; set; }
        public string DipsBatchNumber { get; set; }

        [NotLogged]
        public CoinImage Image { get; set; }

        [NotLogged]
        public string FrontImagePath { get; set; }
        
        [NotLogged]
        public string RearImagePath { get; set; }

        // Backing property for VoucherIndicator below (simplifies mapping our Enumeration class to the DB column value)
        public string VoucherIndicatorValue { get; set; }

        [NotLogged]
        public VoucherIndicator VoucherIndicator
        {
            get { return VoucherIndicator.FromValue(VoucherIndicatorValue); }
            set { VoucherIndicatorValue = value.Value; }
        }

        // Backing property for DebitCreditType below (simplifies mapping our Enumeration class to the DB column value)
        public string DebitCreditTypeValue { get; set; }

        [NotLogged]
        public DebitCreditType DebitCreditType
        {
            get { return DebitCreditType.FromValue(DebitCreditTypeValue); }
            set { DebitCreditTypeValue = value.Value; }
        }
    }
}
