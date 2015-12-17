using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchTransactionRequestToDipsDbIndexMapper : IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper;

        public ValidateBatchTransactionRequestToDipsDbIndexMapper(
            IBatchTransactionRequestMapHelper batchTransactionRequestMapHelper)
        {
            this.batchTransactionRequestMapHelper = batchTransactionRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(ValidateBatchTransactionRequest input)
        {
            return input.voucher.Select(voucher => batchTransactionRequestMapHelper.CreateNewDipsDbIndex(input.voucherBatch.scannedBatchNumber, voucher.voucher.documentReferenceNumber)).ToList();
        }
    }
}
