using System.Linq;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchTransactionRequestToDipsQueueMapper : IMapper<CorrectBatchTransactionRequest, DipsQueue>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public CorrectBatchTransactionRequestToDipsQueueMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public DipsQueue Map(CorrectBatchTransactionRequest input)
        {
            return batchTransactionRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.TransactionCorrection,
                input.voucherBatch.scannedBatchNumber,
                input.voucher.First().voucher.documentReferenceNumber,
                input.voucher.First().voucher.processingDate,
                input.voucherBatch.workType.ToString());
        }
    }
}
