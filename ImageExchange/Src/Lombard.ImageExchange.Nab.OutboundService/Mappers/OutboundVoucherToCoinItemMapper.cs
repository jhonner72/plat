using System;
using System.Linq;
using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class OutboundVoucherToCoinItemMapper : IMapper<OutboundVoucherFile, IEnumerable<CoinItem>>
    {

        public IEnumerable<CoinItem> Map(OutboundVoucherFile input)
        {
            return input.Vouchers.Select(voucher => new CoinItem (voucher.Image, BuildValueDictionary(input.OperationType, voucher)));
        }

        public static IDictionary<string, string> BuildValueDictionary(string operationType, OutboundVoucher voucher)
        {
            var extraAuxiliaryDomestic = voucher.ExtraAuxiliaryDomestic;
            var ead = extraAuxiliaryDomestic != null ? extraAuxiliaryDomestic.PadLeft(11) : string.Empty;
            Dictionary<string, string> dictionary = null;
            switch (operationType)
            {
                case "ImageExchange":
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.ItemRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.ItemVersion},
                            {CoinFieldNames.BsbLedgerFi, voucher.BsbLedgerFi},
                            {CoinFieldNames.BsbCollectingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransactionCode, voucher.TransactionCode},
                            {CoinFieldNames.Amount, voucher.Amount.ToString("D")}, 
                            {CoinFieldNames.BsbDepositorNominatedFi, CoinValueConstants.ItemBsbDepositorNominatedFi},
                            {CoinFieldNames.DrawerAccountNumber, voucher.DrawerAccountNumber.PadLeft(21).Trim()},
                            {CoinFieldNames.DepositorsNominatedAccount, CoinValueConstants.ItemDepositorsNominatedAccount},
                            {CoinFieldNames.AuxiliaryDomestic, voucher.AuxiliaryDomestic.PadLeft(9).Trim()},
                            {CoinFieldNames.ExtraAuxiliaryDomestic, ead.Trim()},
                            {CoinFieldNames.BsbCapturingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransmissionDate, voucher.TransmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.CaptureDeviceIdentifier, CoinValueConstants.ItemCaptureDeviceIdentifier},
                            {CoinFieldNames.TransactionIdentifier, voucher.TransactionIdentifier},
                            {CoinFieldNames.VoucherIndicator, voucher.VoucherIndicator.DisplayName.Trim()},
                            {CoinFieldNames.ManualRepair, CoinValueConstants.ItemManualRepair},
                            {CoinFieldNames.BatchNumber, voucher.BatchNumber.PadLeft(16)}
                        };
                        break;
                    }
                case "AgencyXML":
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.OtherAgencyItemRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.OtherAgencyItemVersion},
                            {CoinFieldNames.BsbLedgerFi, voucher.BsbLedgerFi},
                            {CoinFieldNames.BsbCollectingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransactionCode, voucher.TransactionCode},
                            {CoinFieldNames.Amount, voucher.Amount.ToString("D")}, 
                            {CoinFieldNames.BsbDepositorNominatedFi, CoinValueConstants.ItemBsbDepositorNominatedFi},
                            {CoinFieldNames.DrawerAccountNumber, voucher.DrawerAccountNumber.PadLeft(21).Trim()},
                            {CoinFieldNames.DepositorsNominatedAccount, CoinValueConstants.ItemDepositorsNominatedAccount},
                            {CoinFieldNames.AuxiliaryDomestic, voucher.AuxiliaryDomestic.PadLeft(9).Trim()},
                            {CoinFieldNames.ExtraAuxiliaryDomestic, ead.Trim()},
                            {CoinFieldNames.BsbCapturingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransmissionDate, voucher.TransmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.CaptureDeviceIdentifier, CoinValueConstants.ItemCaptureDeviceIdentifier},
                            {CoinFieldNames.TransactionIdentifier, voucher.TransactionIdentifier},
                            {CoinFieldNames.VoucherIndicator, voucher.VoucherIndicator.DisplayName.Trim()},
                            {CoinFieldNames.ManualRepair, CoinValueConstants.ItemManualRepair},
                            {CoinFieldNames.BatchNumber, voucher.BatchNumber}
                        };
                        break;
                    }
                default: //IE
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.ItemRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.ItemVersion},
                            {CoinFieldNames.BsbLedgerFi, voucher.BsbLedgerFi},
                            {CoinFieldNames.BsbCollectingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransactionCode, voucher.TransactionCode},
                            {CoinFieldNames.Amount, voucher.Amount.ToString("D")}, 
                            {CoinFieldNames.BsbDepositorNominatedFi, CoinValueConstants.ItemBsbDepositorNominatedFi},
                            {CoinFieldNames.DrawerAccountNumber, voucher.DrawerAccountNumber.PadLeft(21).Trim()},
                            {CoinFieldNames.DepositorsNominatedAccount, CoinValueConstants.ItemDepositorsNominatedAccount},
                            {CoinFieldNames.AuxiliaryDomestic, voucher.AuxiliaryDomestic.PadLeft(9).Trim()},
                            {CoinFieldNames.ExtraAuxiliaryDomestic, ead.Trim()},
                            {CoinFieldNames.BsbCapturingFi, voucher.BsbCollectingFi},
                            {CoinFieldNames.TransmissionDate, voucher.TransmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.CaptureDeviceIdentifier, CoinValueConstants.ItemCaptureDeviceIdentifier},
                            {CoinFieldNames.TransactionIdentifier, voucher.TransactionIdentifier},
                            {CoinFieldNames.VoucherIndicator, voucher.VoucherIndicator.DisplayName.Trim()},
                            {CoinFieldNames.ManualRepair, CoinValueConstants.ItemManualRepair},
                            {CoinFieldNames.BatchNumber, voucher.BatchNumber.PadLeft(16)}
                        };
                        break;
                    }
            }

            return dictionary;
            // the spec also mentions Customer Field 1 through to 17 for any custom data, but we're not supporting that
        }
    }
}