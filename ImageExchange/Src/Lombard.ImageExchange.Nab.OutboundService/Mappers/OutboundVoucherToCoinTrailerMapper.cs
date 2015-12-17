using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class OutboundVoucherToCoinTrailerMapper : IMapper<OutboundVoucherFile, CoinTrailer>
    {
        private readonly ICoinFileTotalsCalculator coinFileTotalsCalculator;

        public static IDictionary<string, string> BuildValueDictionary(long totalCreditCents, long totalDebitCents, int nonValueItemCount, int creditItemCount, int debitItemCount, string operationType)
        {
            Dictionary<string, string> dictionary = null;
            switch (operationType)
            {
                case "ImageExchange":
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.TrailerRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.TrailerVersion },
                            {CoinFieldNames.FileCreditTotalAmount, totalCreditCents.ToString("D") }, 
                            {CoinFieldNames.FileDebitTotalAmount, totalDebitCents.ToString("D") }, 
                            {CoinFieldNames.FileCountNonValueItems, nonValueItemCount.ToString("D") }, 
                            {CoinFieldNames.FileCountCreditItems, creditItemCount.ToString("D") }, 
                            {CoinFieldNames.FileCountDebitItems, debitItemCount.ToString("D") }, 
                        };
                        break;
                    }
                case "AgencyXML":
                    {
                        var agencyNonValueItemsCount = creditItemCount + debitItemCount; 
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.OtherAgencyTrailerRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.OtherAgencyTrailerVersion },
                            {CoinFieldNames.FileCreditTotalAmount, CoinValueConstants.OtherAgencyFileCreditTotalAmount }, 
                            {CoinFieldNames.FileDebitTotalAmount, CoinValueConstants.OtherAgencyFileDebitTotalAmount }, 
                            {CoinFieldNames.FileCountNonValueItems, agencyNonValueItemsCount.ToString("D") }, 
                            {CoinFieldNames.FileCountCreditItems, CoinValueConstants.OtherAgencyFileCountCreditItems }, 
                            {CoinFieldNames.FileCountDebitItems, CoinValueConstants.OtherAgencyFileCountDebitItems }, 
                        };
                        break;
                    }

                default: //IE
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.TrailerRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.TrailerVersion },
                            {CoinFieldNames.FileCreditTotalAmount, totalCreditCents.ToString("D") }, 
                            {CoinFieldNames.FileDebitTotalAmount, totalDebitCents.ToString("D") }, 
                            {CoinFieldNames.FileCountNonValueItems, nonValueItemCount.ToString("D") }, 
                            {CoinFieldNames.FileCountCreditItems, creditItemCount.ToString("D") }, 
                            {CoinFieldNames.FileCountDebitItems, debitItemCount.ToString("D") }, 
                        };
                        break;
                    }
            }
            return dictionary;
        }

        public OutboundVoucherToCoinTrailerMapper(ICoinFileTotalsCalculator coinFileTotalsCalculator)
        {
            this.coinFileTotalsCalculator = coinFileTotalsCalculator;
        }

        public CoinTrailer Map(OutboundVoucherFile input)
        {
            var result = coinFileTotalsCalculator.Process(input.Vouchers);
                    
            return new CoinTrailer(BuildValueDictionary(result.TotalCredits, result.TotalDebits, result.NonValueCount, result.CreditCount, result.DebitCount, input.OperationType));
        }
    }
}