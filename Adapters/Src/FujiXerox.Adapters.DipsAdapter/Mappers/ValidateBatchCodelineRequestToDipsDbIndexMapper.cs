using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchCodelineRequestToDipsDbIndexMapper : IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public ValidateBatchCodelineRequestToDipsDbIndexMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(ValidateBatchCodelineRequest input)
        {
            return input.voucher.Select(voucher => batchCodelineRequestMapHelper.CreateNewDipsDbIndex(input.voucherBatch.scannedBatchNumber, voucher.documentReferenceNumber)).ToList();
        }
    }
}
