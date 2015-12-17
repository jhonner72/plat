using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Lombard.ECLMatchingEngine.Service.Domain
{
    public static class Get
    {
        public static VoucherInfoBatch MatchVouchers(iVoucherInfoBatch voucherInfoBatch, IEnumerable<IECLRecord> ECLItemsResult)
        {
            VoucherInfoBatch updatedMatchedVoucher = new VoucherInfoBatch();
            updatedMatchedVoucher.JobIdentifier = voucherInfoBatch.JobIdentifier;

            if (voucherInfoBatch.VoucherInformation != null)
            {
                IECLRecordVoucherInfo[] vouchers = voucherInfoBatch.VoucherInformation
                    .Where(a => a.SkippedForNextProcessing == false)
                    .ToArray<IECLRecordVoucherInfo>();

                var MatchedVouchers = vouchers.Where
                     (voucherInfo => ECLItemsResult.Any
                              (eclItems =>
                               eclItems.Amount == voucherInfo.Voucher.voucher.amount.PadLeft(10, '0') &&
                               eclItems.ChequeSerialNumber == voucherInfo.Voucher.voucher.auxDom.PadLeft(9, '0') &&
                               eclItems.DrawerAccountNumber == voucherInfo.Voucher.voucher.accountNumber.PadLeft(10, ' ') &&
                               eclItems.LedgerBSBCode == voucherInfo.Voucher.voucher.bsbNumber)).Distinct().ToArray<IECLRecordVoucherInfo>();

                var updatedMatchedVouchers = MatchedVouchers.Select(f => GetMatchingECLRecord(f.Voucher, ECLItemsResult)).ToArray<IECLRecordVoucherInfo>();

                updatedMatchedVoucher.VoucherInformation = updatedMatchedVouchers.ToList<IECLRecordVoucherInfo>();
               
            }
            return updatedMatchedVoucher;
        }

        public static VoucherInfoBatch UnMatchVouchers(iVoucherInfoBatch voucherInfoBatch, VoucherInformation[] MatchedVouchers)
        {
            var unMatchedVoucher = new VoucherInfoBatch();

            if (voucherInfoBatch.VoucherInformation != null)
            {
                var vouchers = voucherInfoBatch.VoucherInformation.Select(a => a.Voucher).ToArray<VoucherInformation>();
                unMatchedVoucher.JobIdentifier = voucherInfoBatch.JobIdentifier;

                if (vouchers.Length != 0)
                {
                    var newQuery = vouchers.Except(MatchedVouchers).Select(a => new ECLRecordVoucherInfo { Voucher = a })
                                                                    .ToList<IECLRecordVoucherInfo>();

                    unMatchedVoucher.VoucherInformation = newQuery;
                }
            }

            return unMatchedVoucher;
        }
        public static IECLRecordVoucherInfo GetMatchingECLRecord(VoucherInformation voucher, IEnumerable<IECLRecord> ECLItemsResult)
        {
            var ICLRecord = ECLItemsResult.Where
                (eclItems => eclItems.Amount == voucher.voucher.amount.PadLeft(10, '0') &&
                             eclItems.ChequeSerialNumber == voucher.voucher.auxDom.PadLeft(9, '0') &&
                             eclItems.DrawerAccountNumber == voucher.voucher.accountNumber.PadLeft(10, ' ') &&
                             eclItems.LedgerBSBCode == voucher.voucher.bsbNumber).ToArray<IECLRecord>();

            if (ICLRecord.Count<IECLRecord>() == 0)
            {
                Log.Error("[Get]:[UpdateVoucherProcessInformation] NO IECLRecord found.");
                throw new InvalidOperationException();
            }

            if (ICLRecord.Count<IECLRecord>() >= 1)
            {
                if (ICLRecord.Count<IECLRecord>() > 1)
                {
                    Log.Warning(String.Format("[Get]:[UpdateVoucherProcessInformation] Voucher_{0}_{1}_{2}_{3}.JSON",
                        voucher.voucher.processingDate.ToString("ddMMyyyy"),
                        voucher.voucher.documentReferenceNumber,
                        voucher.voucherBatch.scannedBatchNumber,
                        voucher.voucherProcess.transactionLinkNumber));
                    Log.Warning("[Get]:[UpdateVoucherProcessInformation] More than one (1) IECLRecord found, will use the first one.");
                }
                else
                {
                    Log.Information("[Get]:[UpdateVoucherProcessInformation] An exact IECLRecord found.");
                }
                
                APPresentmentTypeEnum exchangeMode = APPresentmentTypeEnum.E;
                var PresentmentTypeEnum = Enum.TryParse<APPresentmentTypeEnum>(ICLRecord.FirstOrDefault().ExchangeModeCode, out exchangeMode);
                if (PresentmentTypeEnum)
                {
                    voucher.voucherProcess.apPresentmentType = exchangeMode;
                }
                else
                {
                    Log.Error("[Get]:[GetCorrespondingECLRecord] there was an error in parsing enum value of {@enumValue}", ICLRecord.FirstOrDefault().ExchangeModeCode);
                    throw new InvalidOperationException();
                }
            }

            var matchedECLVoucher = new ECLRecordVoucherInfo();
            matchedECLVoucher.MatchedECLRecord = ICLRecord.FirstOrDefault();
            matchedECLVoucher.Voucher = voucher;

            return matchedECLVoucher;
        }
    }
}
