using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class CheckThirdPartyBatchRequestToDipsDbIndexMapper : IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper;
        

        public CheckThirdPartyBatchRequestToDipsDbIndexMapper(
            IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper)
        {
            this.batchCheckThirdPartyRequestMapHelper = batchCheckThirdPartyRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(CheckThirdPartyBatchRequest input)
        {
            return input.voucher.Select(voucher => batchCheckThirdPartyRequestMapHelper.CreateNewDipsDbIndex(input.voucherBatch.scannedBatchNumber, voucher.voucher.documentReferenceNumber)).ToList();
        }
    }
}
