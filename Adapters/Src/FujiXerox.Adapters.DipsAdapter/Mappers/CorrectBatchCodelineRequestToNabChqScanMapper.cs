using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchCodelineRequestToDipsNabChqScanPodMapper : IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public CorrectBatchCodelineRequestToDipsNabChqScanPodMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(CorrectBatchCodelineRequest input)
        {
            return input.voucher.Select(voucher => batchCodelineRequestMapHelper.CreateNewDipsNabChq(
                input.voucherBatch.scannedBatchNumber, 
                voucher.documentReferenceNumber, 
                voucher.processingDate,
                voucher.extraAuxDom,
                voucher.extraAuxDomStatus,
                voucher.auxDom,
                voucher.auxDomStatus,
                voucher.bsbNumber,
                voucher.bsbNumberStatus,
                voucher.accountNumber,
                voucher.accountNumberStatus,
                voucher.transactionCode, 
                voucher.transactionCodeStatus, 
                voucher.capturedAmount, 
                voucher.amountConfidenceLevel, 
                voucher.documentType.ToString(), 
                input.voucherBatch.workType.ToString(), 
                string.Empty, 
                voucher.amount,
                voucher.amountStatus,
                input.voucherBatch.captureBsb,
                input.voucherBatch.batchAccountNumber,
                input.voucherBatch.processingState.ToString(),
                voucher.collectingBank,
                input.voucherBatch.unitID,
                input.voucherBatch.batchType,
                voucher.repostFromDRN,
                voucher.repostFromProcessingDate,
                input.voucherBatch.collectingBank,
                input.voucherBatch.subBatchType)).ToList();
        }
    }
}
