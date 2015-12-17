using System.Linq;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Mappers
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
