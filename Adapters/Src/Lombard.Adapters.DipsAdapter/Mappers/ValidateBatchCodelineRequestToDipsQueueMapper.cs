using System.Linq;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class ValidateBatchCodelineRequestToDipsQueueMapper : IMapper<ValidateBatchCodelineRequest, DipsQueue>
    {
        private readonly IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper;

        public ValidateBatchCodelineRequestToDipsQueueMapper(
            IBatchCodelineRequestMapHelper batchCodelineRequestMapHelper)
        {
            this.batchCodelineRequestMapHelper = batchCodelineRequestMapHelper;
        }

        public DipsQueue Map(ValidateBatchCodelineRequest input)
        {
            return batchCodelineRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.CodelineValidation, 
                input.voucherBatch.scannedBatchNumber, 
                input.voucher.First().documentReferenceNumber, 
                input.voucher.First().processingDate, 
                string.Empty, 
                input.voucherBatch.workType.ToString());
        }
    }
}
