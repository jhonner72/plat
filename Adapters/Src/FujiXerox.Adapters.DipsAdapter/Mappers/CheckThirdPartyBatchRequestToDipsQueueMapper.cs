using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class CheckThirdPartyBatchRequestToDipsQueueMapper : IMapper<CheckThirdPartyBatchRequest, DipsQueue>
    {
        private readonly IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper;

        public CheckThirdPartyBatchRequestToDipsQueueMapper(
            IBatchCheckThirdPartyRequestMapHelper batchCheckThirdPartyRequestMapHelper)
        {
            this.batchCheckThirdPartyRequestMapHelper = batchCheckThirdPartyRequestMapHelper;
        }

        public DipsQueue Map(CheckThirdPartyBatchRequest input)
        {
            return batchCheckThirdPartyRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.CheckThirdParty,
                input.voucherBatch.scannedBatchNumber,
                input.voucher.First().voucher.documentReferenceNumber,
                input.voucher.First().voucher.processingDate,
                input.voucherBatch.workType.ToString(),
                string.Empty);
        }
    }
}
