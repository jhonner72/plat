using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace FujiXerox.Adapters.DipsAdapter.Mappers
{
    public class CorrectBatchCodelineRequestToDipsQueueMapper : IMapper<CorrectBatchCodelineRequest, DipsQueue>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public CorrectBatchCodelineRequestToDipsQueueMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public DipsQueue Map(CorrectBatchCodelineRequest input)
        {
            return batchCodelineRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.CodelineCorrection, 
                input.voucherBatch.scannedBatchNumber, 
                input.voucher.First().documentReferenceNumber, 
                input.voucher.First().processingDate, 
                input.jobIdentifier, 
                input.voucherBatch.workType.ToString());
        }
    }
}
