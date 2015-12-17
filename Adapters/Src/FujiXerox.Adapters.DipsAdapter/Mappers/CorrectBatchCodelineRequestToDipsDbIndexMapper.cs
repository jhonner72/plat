using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchCodelineRequestToDipsDbIndexMapper : IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public CorrectBatchCodelineRequestToDipsDbIndexMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(CorrectBatchCodelineRequest input)
        {
            return input.voucher.Select(voucher => batchCodelineRequestMapHelper.CreateNewDipsDbIndex(input.voucherBatch.scannedBatchNumber, voucher.documentReferenceNumber)).ToList();
        }
    }
}
