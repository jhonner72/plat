using System.Linq;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class GenerateBulkCreditRequestToDipsQueueMapper : IMapper<VoucherInformation[], DipsQueue>
    {
        private readonly IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper;

        public GenerateBulkCreditRequestToDipsQueueMapper(
            IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper)
        {
            this.generateBulkCreditRequestMapHelper = generateBulkCreditRequestMapHelper;
        }

        public DipsQueue Map(VoucherInformation[] input)
        {
            return generateBulkCreditRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.GenerateBulkCreditVoucher,
                input.First().voucherBatch.scannedBatchNumber,
                input.First().voucher.documentReferenceNumber,
                input.First().voucher.processingDate,
                input.First().voucherBatch.workType.ToString(),
                input.First().voucherBatch.workType.ToString());
        }
    }
}
