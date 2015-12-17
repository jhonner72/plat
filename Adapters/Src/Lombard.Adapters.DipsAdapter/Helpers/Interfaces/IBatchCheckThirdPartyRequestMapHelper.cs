using System;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Helpers.Interfaces
{
    public interface IBatchCheckThirdPartyRequestMapHelper
    {
        DipsQueue CreateNewDipsQueue(
            DipsLocationType locationType,
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string jobIdentifier,
            string jobId);

        DipsDbIndex CreateNewDipsDbIndex(
            string batchNumber,
            string documentReferenceNumber);

        DipsNabChq CreateNewDipsNabChq(
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string extraAuxDom,
            bool extraAuxDomStatus,
            string auxDom,
            bool auxDomStatus,
            string bsbNumber,
            bool bsbNumberStatus,
            string accountNumber,
            bool accountNumberStatus,
            string transactionCode,
            bool transactionCodeStatus,
            string capturedAmount,
            string amountConfidenceLevel,
            string documentType,
            string jobId,
            int manualRepair,
            string amount,
            bool amountStatus,
            string captureBsb,
            string batchAccountNumber,
            string processingState,
            string collectingBank,
            string unitId,
            bool tpcRequired,
            string preAdjustmentAmount,
            bool adjustedFlag,
            bool thirdPartyCheckFailed,
            bool thirdPartyPoolFlag,
            bool highValueFlag,
            string voucherDelayedIndicator,
            string batchType,
            bool unprocessable,
            bool unencodedEcdReturnFlag,
            string transactionLinkNumber,
            bool thirdPartyMixedDepositReturnFlag,
            bool suspectFraud,
            bool surplusItemFlag,
            string rawMicr,
            string rawOcr,
            string presentationMode,
            bool isGeneratedVoucher,
            string dipsTraceNumber,
            string dipsSequenceNumber,
            bool adjustmentLetterRequired,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string adjustmentReasonCode,
            string adjustmentDescription,
            string subBatchType,
            string alt_acc_num,
            string alt_bsb_num,
            string alt_ead,
            string alt_ser_num,
            string alt_trancode,
            bool creditNoteFlag,
            string insertedCreditType
            );
    }
}