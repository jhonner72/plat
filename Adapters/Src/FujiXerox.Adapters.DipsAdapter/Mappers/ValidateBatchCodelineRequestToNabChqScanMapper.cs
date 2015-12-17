using System;
using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchCodelineRequestToDipsNabChqScanPodMapper : IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsNabChq>>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public ValidateBatchCodelineRequestToDipsNabChqScanPodMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(ValidateBatchCodelineRequest input)
        {
            // NOTE: when creating a DipsNabChq for a validation request we set all status flags to 'valid'
            return input.voucher.Select(voucher => batchCodelineRequestMapHelper.CreateNewDipsNabChq(
                input.voucherBatch.scannedBatchNumber, 
                voucher.documentReferenceNumber,
                voucher.processingDate, 
                voucher.extraAuxDom,
                true,
                voucher.auxDom, 
                true,
                voucher.bsbNumber, 
                true,
                voucher.accountNumber, 
                true,
                voucher.transactionCode, 
                true,
                voucher.capturedAmount, 
                voucher.amountConfidenceLevel,
                voucher.documentType.ToString(), 
                input.voucherBatch.workType.ToString(),
                string.Empty, 
                string.Empty,
                true,
                input.voucherBatch.captureBsb,
                input.voucherBatch.batchAccountNumber,
                input.voucherBatch.processingState.ToString(),
                input.voucherBatch.collectingBank,
                input.voucherBatch.unitID,
                input.voucherBatch.batchType,
                string.Empty,
                new DateTime(),
                string.Empty,
                input.voucherBatch.subBatchType)).ToList();
        }
    }
}
