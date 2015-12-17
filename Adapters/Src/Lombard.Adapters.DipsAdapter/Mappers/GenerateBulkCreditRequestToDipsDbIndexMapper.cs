using System.Collections.Generic;
using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;

namespace Lombard.Adapters.DipsAdapter.Mappers
{
    public class GenerateBulkCreditRequestToDipsDbIndexMapper : IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>>
    {
        private readonly IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper;


        public GenerateBulkCreditRequestToDipsDbIndexMapper(
            IGenerateBulkCreditRequestMapHelper generateBulkCreditRequestMapHelper)
        {
            this.generateBulkCreditRequestMapHelper = generateBulkCreditRequestMapHelper;
        }

        public IEnumerable<DipsDbIndex> Map(VoucherInformation[] input)
        {
            return input.Select(voucher =>
                generateBulkCreditRequestMapHelper.CreateNewDipsDbIndex(
                voucher.voucherBatch.scannedBatchNumber,
                voucher.voucher.documentReferenceNumber
                )).ToList();

        }
    }
}
