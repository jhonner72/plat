using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class GenerateCorrespondingVoucherRequestToDipsQueueMapper : IMapper<GenerateCorrespondingVoucherRequest, DipsQueue>
    {
        private readonly IGenerateCorrespondingVoucherRequestMapHelper generateCorrespondingVoucherRequestMapHelper;

        public GenerateCorrespondingVoucherRequestToDipsQueueMapper(
            IGenerateCorrespondingVoucherRequestMapHelper generateCorrespondingVoucherRequestMapHelper)
        {
            this.generateCorrespondingVoucherRequestMapHelper = generateCorrespondingVoucherRequestMapHelper;
        }

        public DipsQueue Map(GenerateCorrespondingVoucherRequest input)
        {
            return generateCorrespondingVoucherRequestMapHelper.CreateNewDipsQueue(
                DipsLocationType.GenerateCorrespondingVoucher,
                input.generateVoucher.First().voucherBatch.scannedBatchNumber,
                input.generateVoucher.First().voucher.documentReferenceNumber,
                input.generateVoucher.First().voucher.processingDate,
                input.generateVoucher.First().voucherBatch.workType.ToString(),
                string.Empty);
        }
    }
}
