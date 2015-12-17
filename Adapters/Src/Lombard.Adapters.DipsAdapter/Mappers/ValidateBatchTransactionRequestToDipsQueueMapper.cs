using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchTransactionRequestToDipsQueueMapper : IMapper<ValidateBatchTransactionRequest, DipsQueue>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public ValidateBatchTransactionRequestToDipsQueueMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public DipsQueue Map(ValidateBatchTransactionRequest input)
        {
            return batchTransactionRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.TransactionValidation,
                input.voucherBatch.scannedBatchNumber,
                input.voucher.First().voucher.documentReferenceNumber,
                input.voucher.First().voucher.processingDate,
                input.voucherBatch.workType.ToString());
        }
    }
}