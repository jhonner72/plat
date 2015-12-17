using System;
using Serilog.Extras.Attributed;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public interface IImageExchangeVoucher
    {
        long Id { get; set; }

        string BsbLedgerFi { get; set; }
        string BsbCollectingFi { get; set; }
        string BsbCapturingFi { get; set; }
        string TransactionCode { get; set; }
        long Amount { get; set; }
        string DrawerAccountNumber { get; set; }
        DateTime TransmissionDate { get; set; }
        string AuxiliaryDomestic { get; set; }
        string ExtraAuxiliaryDomestic { get; set; }
        string TransactionIdentifier { get; set; }
        string BatchNumber { get; set; }

        [NotLogged]
        CoinImage Image { get; set; }

        [NotLogged]
        VoucherIndicator VoucherIndicator { get; set; }

        [NotLogged]
        DebitCreditType DebitCreditType { get; set; }

        [NotLogged]
        string FrontImagePath { get; set; }

        [NotLogged]
        string RearImagePath { get; set; }
    }
}