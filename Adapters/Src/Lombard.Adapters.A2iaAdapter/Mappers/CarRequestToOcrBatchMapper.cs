using System.IO;
using System.Linq;
using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Mappers
{
    public class CarRequestToOcrBatchMapper : IMapper<RecogniseBatchCourtesyAmountRequest, OcrBatch>
    {
        private readonly IAdapterConfiguration adapterConfiguration;

        public CarRequestToOcrBatchMapper(IAdapterConfiguration adapterConfiguration)
        {
            this.adapterConfiguration = adapterConfiguration;
        }

        public OcrBatch Map(RecogniseBatchCourtesyAmountRequest message)
        {
            return new OcrBatch
            {
                JobIdentifier = message.jobIdentifier,
                Vouchers = message.voucher.Select(v => new OcrVoucher
                {
                    Id = v.documentReferenceNumber,
                    ImagePath = Path.Combine(adapterConfiguration.ImageFileFolder, message.jobIdentifier, string.Format(adapterConfiguration.ImageFileNameTemplate, v.processingDate, v.documentReferenceNumber)),
                    VoucherType = ParseTransactionCode(v.transactionCode)
                }).ToList()
            };
        }

        private static VoucherType ParseTransactionCode(string transactionCode)
        {
            int transactionCodeInteger;
            if (int.TryParse(transactionCode, out transactionCodeInteger))
            {
                return (transactionCodeInteger >= 50) ? VoucherType.Credit : VoucherType.Debit;
            }

            return VoucherType.Debit;
        }
    }
}
