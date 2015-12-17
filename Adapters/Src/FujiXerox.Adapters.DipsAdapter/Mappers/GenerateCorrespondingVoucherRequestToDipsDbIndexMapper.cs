using System.Collections.Generic;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class GenerateCorrespondingVoucherRequestToDipsDbIndexMapper : IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsDbIndex>>
    {
        private readonly IGenerateCorrespondingVoucherRequestMapHelper generateCorrespondingVoucherRequestMapHelper;


        public GenerateCorrespondingVoucherRequestToDipsDbIndexMapper(
            IGenerateCorrespondingVoucherRequestMapHelper generateCorrespondingVoucherRequestMapHelper)
        {
            this.generateCorrespondingVoucherRequestMapHelper = generateCorrespondingVoucherRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(GenerateCorrespondingVoucherRequest input)
        {
            return input.generateVoucher.Select(voucher =>
                generateCorrespondingVoucherRequestMapHelper.CreateNewDipsDbIndex(
                voucher.voucherBatch.scannedBatchNumber,
                voucher.voucher.documentReferenceNumber
                )).ToList();
        }
    }
}
