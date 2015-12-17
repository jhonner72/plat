using System;
using System.Globalization;
using System.Linq;
using System.Collections.Generic;
using Lombard.Common.FileProcessors;
using System.ComponentModel.DataAnnotations;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Serilog;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class OutboundVoucherFileToCuscalFile
    {
        public static CuscalFile Map(OutboundVoucherFile input)
        {
            var cuscalItems = input.Vouchers.Select(voucher => BuildValueDictionary(voucher, input.ProcessingDate));
            
            return new CuscalFile(cuscalItems);
        }
        public static string BuildValueDictionary(OutboundVoucher voucher, DateTime processingDate)
        {
            var drnResponse = GetDRN(voucher.FrontImagePath);
            var drnValue = string.Empty;
            var tranCode = string.Empty;
            if (!drnResponse.IsSuccessful)
            {
                drnResponse = GetDRN(voucher.RearImagePath);// if failed, try getting the DRN from rearImagePath
                if(!drnResponse.IsSuccessful)
                    drnValue = string.Empty;
                else
                    drnValue = drnResponse.Result;
            }
            else
                drnValue = drnResponse.Result;


            if (voucher.DebitCreditTypeValue == DebitCreditType.Credit.Value)
                tranCode = voucher.TransactionCode;

            Log.Information("{OutboundVoucherFileToCuscalFile:BuildValueDictionary} The DRN value is {0}", drnValue);
            
            var cuscalItem = string.Join(",",
                CoinValueConstants.CuscalFormName,
                string.Join(string.Empty, processingDate.Year.ToString(), voucher.TransmissionDate.ToString("MM", CultureInfo.InvariantCulture), voucher.TransmissionDate.ToString("dd", CultureInfo.InvariantCulture)),
                voucher.FrontImagePath.Remove(0, voucher.FrontImagePath.LastIndexOf('\\')+1),
                voucher.RearImagePath.Remove(0, voucher.RearImagePath.LastIndexOf('\\')+1),
                voucher.ExtraAuxiliaryDomestic,    
                voucher.AuxiliaryDomestic.PadLeft(9,'0'),
                voucher.BsbLedgerFi,
                voucher.DrawerAccountNumber,
                tranCode,
                voucher.Amount.ToString().PadLeft(12,'0'),
                drnValue,
                string.Empty,
                string.Join(string.Empty,voucher.DebitCreditTypeValue, "r"));

            return cuscalItem.ToString();
        }

        public static ValidatedResponse<string> GetDRN(string imagePath)
        {
            var deconstructedImagePath = imagePath.Split('_');
            var drn = new List<string>();

            if(deconstructedImagePath.Length <3)
            {
                var errorMessage = "{OutboundVoucherFileToCuscalFile:GetDRN}: Insufficient value from the image filename, blankValue will be used.";
                Log.Error(errorMessage);
                drn.Add(string.Empty);

                return ValidatedResponse<string>.Failure(new List<ValidationResult> { new ValidationResult(errorMessage) });
            }

            if (deconstructedImagePath.Length ==4)
            {
                Log.Warning("(OutboundVoucherFileToCuscalFile:GetDRN): DRN value is {@value}.", deconstructedImagePath[2]);
                drn.Add(deconstructedImagePath[2]);
            }

            return ValidatedResponse<string>.Success(drn.FirstOrDefault());
        }
    }
}
