using System.Collections.Generic;
using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchTransactionRequestToDipsDbIndexMapper : IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public CorrectBatchTransactionRequestToDipsDbIndexMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(CorrectBatchTransactionRequest input)
        {
            return input.voucher.Select(voucher => batchTransactionRequestMapHelper.CreateNewDipsDbIndex(input.voucherBatch.scannedBatchNumber, voucher.voucher.documentReferenceNumber)).ToList();
        }
    }
}
