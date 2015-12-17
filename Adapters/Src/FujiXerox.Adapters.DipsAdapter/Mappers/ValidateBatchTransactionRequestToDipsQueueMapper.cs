using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
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
