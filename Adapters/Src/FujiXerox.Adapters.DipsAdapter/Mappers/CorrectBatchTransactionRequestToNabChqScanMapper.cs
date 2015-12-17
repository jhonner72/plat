using System;
using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchTransactionRequestToDipsNabChqScanPodMapper : IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsNabChq>>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public CorrectBatchTransactionRequestToDipsNabChqScanPodMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(CorrectBatchTransactionRequest input)
        {
            // NOTE: Per US #12295, DIPS should show blank if there is no explicit balancing
            // reason supplied. Here, we convert the reason code to String.Empty explicitly
            
            return input.voucher.Select(voucher => batchTransactionRequestMapHelper.CreateNewDipsNabChqForCorrectTransactionRequest(
                input.voucherBatch.scannedBatchNumber,
                voucher.voucher.documentReferenceNumber,
                voucher.voucher.processingDate,
                voucher.reasonCode == ExpertBalanceReason.None ? String.Empty : voucher.reasonCode.ToString(),
                voucher.transactionLinkNumber,
                voucher.unprocessable,
                voucher.voucher.extraAuxDom,
                voucher.codelineFieldsStatus.extraAuxDomStatus,
                voucher.voucher.auxDom,
                voucher.codelineFieldsStatus.auxDomStatus,
                voucher.voucher.bsbNumber,
                voucher.codelineFieldsStatus.bsbNumberStatus,
                voucher.voucher.accountNumber,
                voucher.codelineFieldsStatus.accountNumberStatus,
                voucher.voucher.transactionCode,
                voucher.codelineFieldsStatus.transactionCodeStatus,
                voucher.voucher.amount,
                voucher.codelineFieldsStatus.amountStatus,
                voucher.voucher.documentType.ToString(),
                input.voucherBatch.workType.ToString(),
                voucher.presentationMode,
                voucher.rawMICR,
                voucher.rawOCR,
                input.voucherBatch.captureBsb,
                input.voucherBatch.batchAccountNumber,
                voucher.surplusItemFlag,
                voucher.suspectFraudFlag,
                input.voucherBatch.processingState.ToString(),
                input.voucherBatch.collectingBank,
                input.voucherBatch.unitID,
                voucher.isGeneratedVoucher,
                voucher.targetEndPoint,
                voucher.forValueIndicator,
                voucher.dips_override,
                voucher.thirdPartyCheckRequired,
                voucher.unencodedECDReturnFlag,
                voucher.thirdPartyMixedDepositReturnFlag,
                input.voucherBatch.batchType,
                voucher.postTransmissionQaAmountFlag,
                voucher.postTransmissionQaCodelineFlag,
                input.voucherBatch.subBatchType)).ToList();
        }
    }
}

