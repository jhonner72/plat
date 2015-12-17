using System.Collections.Generic;
using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchTransactionRequestToDipsNabChqScanPodMapper :
        IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsNabChq>>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public ValidateBatchTransactionRequestToDipsNabChqScanPodMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public IEnumerable<DipsNabChq> Map(ValidateBatchTransactionRequest input)
        {
            // NOTE: when creating a DipsNabChq for a validation request we set all status flags to 'valid'
            return
                input.voucher.Select(
                    voucher => batchTransactionRequestMapHelper.CreateNewDipsNabChqForValidateTransactionRequest(
                        input.voucherBatch.scannedBatchNumber,
                        voucher.voucher.documentReferenceNumber,
                        voucher.voucher.processingDate,
                        string.Empty,
                        string.Empty,
                        voucher.unprocessable,
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
                        voucher.voucher.amount,
                        true,
                        voucher.voucher.documentType.ToString(),
                        input.voucherBatch.workType.ToString(),
                        voucher.rawMICR,
                        voucher.rawOCR,
                        input.voucherBatch.captureBsb,
                        input.voucherBatch.batchAccountNumber,
                        input.voucherBatch.processingState.ToString(),
                        input.voucherBatch.collectingBank,
                        input.voucherBatch.unitID,
                        voucher.forValueIndicator,
                        voucher.dips_override,
                        input.voucherBatch.batchType,
                        voucher.postTransmissionQaAmountFlag,
                        voucher.postTransmissionQaCodelineFlag,
                        input.voucherBatch.subBatchType)).ToList();
        }
    }
}