using System.Collections.Generic;
using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class GenerateBulkCreditRequestToDipsNabChqScanPodMapper : IMapper<VoucherInformation[], IEnumerable<DipsNabChq>>
    {
        private readonly IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper;

        public GenerateBulkCreditRequestToDipsNabChqScanPodMapper(
            IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper)
        {
            this.generateBulkCreditRequestMapHelper = generateBulkCreditRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(VoucherInformation[] input)
        {
            // NOTE: Per US #12295, DIPS should show blank if there is no explicit balancing
            // reason supplied. Here, we convert the reason code to String.Empty explicitly

             var test =
                input.Select(
                    voucher => generateBulkCreditRequestMapHelper.CreateNewDipsNabChq(
                        voucher.voucherBatch.scannedBatchNumber,
                        voucher.voucher.documentReferenceNumber,
                        voucher.voucher.processingDate,
                        voucher.voucher.extraAuxDom,
                        true,
                        voucher.voucher.auxDom,
                        true,
                        voucher.voucher.bsbNumber,
                        true,
                        voucher.voucher.accountNumber,
                        true,
                        voucher.voucher.transactionCode,
                        true,
                        string.Empty,
                        string.Empty,
                        voucher.voucher.documentType.ToString(),
                        voucher.voucherBatch.workType.ToString(),
                        voucher.voucherProcess.manualRepair,
                        voucher.voucher.amount,
                        true,
                        voucher.voucherBatch.captureBsb,
                        voucher.voucherBatch.batchAccountNumber,
                        voucher.voucherBatch.processingState.ToString(),
                        voucher.voucherBatch.collectingBank,
                        voucher.voucherBatch.unitID,

                        voucher.voucherProcess.preAdjustmentAmount,
                        voucher.voucherProcess.adjustedFlag,
                        voucher.voucherProcess.thirdPartyCheckFailed,
                        voucher.voucherProcess.thirdPartyPoolFlag,
                        voucher.voucherProcess.highValueFlag,
                        voucher.voucherProcess.voucherDelayedIndicator,
                        voucher.voucherBatch.batchType,

                        voucher.voucherProcess.unprocessable,
                        voucher.voucherProcess.unencodedECDReturnFlag,
                        voucher.voucherProcess.transactionLinkNumber,
                        voucher.voucherProcess.thirdPartyMixedDepositReturnFlag,
                        voucher.voucherProcess.suspectFraud,
                        voucher.voucherProcess.surplusItemFlag,
                        voucher.voucherProcess.rawMICR,
                        voucher.voucherProcess.rawOCR,
                        voucher.voucherProcess.presentationMode,
                        voucher.voucherProcess.isGeneratedVoucher,

                        voucher.voucherProcess.adjustmentLetterRequired,

                        voucher.voucherProcess.postTransmissionQaAmountFlag,
                        voucher.voucherProcess.postTransmissionQaCodelineFlag,
                        voucher.voucherProcess.adjustmentReasonCode.ToString(),
                        voucher.voucherProcess.adjustmentDescription,

                        voucher.voucherBatch.subBatchType,
                        voucher.voucherProcess.alternateAccountNumber,
                        voucher.voucherProcess.alternateBsbNumber,
                        voucher.voucherProcess.alternateExAuxDom,
                        voucher.voucherProcess.alternateAuxDom,
                        voucher.voucherProcess.alternateTransactionCode,
                        voucher.voucherProcess.creditNoteFlag,
                        0,
                        voucher.voucherProcess.customerLinkNumber,
                        voucher.voucherProcess.isGeneratedBulkCredit,
                        voucher.voucherProcess.insertedCreditType.ToString()
                        )).ToList();
            return test;
        }
    }
}

