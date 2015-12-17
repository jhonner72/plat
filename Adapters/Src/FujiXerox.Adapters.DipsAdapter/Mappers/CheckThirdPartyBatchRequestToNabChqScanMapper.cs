using System;
using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class CheckThirdPartyRequestToDipsNabChqScanPodMapper : IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsNabChq>>
    {
        private readonly IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper;

        public CheckThirdPartyRequestToDipsNabChqScanPodMapper(
            IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper)
        {
            this.batchCheckThirdPartyRequestMapHelper = batchCheckThirdPartyRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(CheckThirdPartyBatchRequest input)
        {
            // NOTE: Per US #12295, DIPS should show blank if there is no explicit balancing
            // reason supplied. Here, we convert the reason code to String.Empty explicitly

            return input.voucher.Select(voucher => batchCheckThirdPartyRequestMapHelper.CreateNewDipsNabChq(
                input.voucherBatch.scannedBatchNumber,
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
                input.voucherBatch.workType.ToString(),
                string.Empty,
                voucher.voucher.amount,
                true,
                input.voucherBatch.captureBsb,
                input.voucherBatch.batchAccountNumber,
                input.voucherBatch.processingState.ToString(),
                input.voucherBatch.collectingBank,
                input.voucherBatch.unitID,
                voucher.thirdPartyCheckRequired,
                
                voucher.voucherProcess.preAdjustmentAmount,
                voucher.voucherProcess.adjustedFlag,
                voucher.voucherProcess.thirdPartyCheckFailed,
                voucher.voucherProcess.thirdPartyPoolFlag,
                voucher.voucherProcess.highValueFlag,
                voucher.voucherProcess.voucherDelayedIndicator,
                input.voucherBatch.batchType,
                
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
                voucher.voucherProcess.adjustmentsOnHold,

                voucher.dipsTraceNumber,
                voucher.dipsSequenceNumber,
                voucher.voucherProcess.adjustmentLetterRequired,

                voucher.voucherProcess.postTransmissionQaAmountFlag,
                voucher.voucherProcess.postTransmissionQaCodelineFlag,
                voucher.voucherProcess.adjustmentReasonCode.ToString(),
                voucher.voucherProcess.adjustmentDescription,
                input.voucherBatch.subBatchType                
                )).ToList();
        }
    }
}

