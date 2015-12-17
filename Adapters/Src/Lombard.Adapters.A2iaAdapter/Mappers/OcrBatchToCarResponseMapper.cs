using System.Globalization;
using System.Linq;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Mappers
{
    public class OcrBatchToCarResponseMapper : IMapper<OcrBatch, RecogniseBatchCourtesyAmountResponse>
    {
        public RecogniseBatchCourtesyAmountResponse Map(OcrBatch message)
        {
            return new RecogniseBatchCourtesyAmountResponse
            {
                jobIdentifier = message.JobIdentifier,
                voucher = message.Vouchers
                .OrderBy(v => v.Id)
                .Select(v => new RecogniseCourtesyAmountResponse
                {
                    documentReferenceNumber = v.Id,
                    imageRotation = v.ImageRotation.ToString(CultureInfo.InvariantCulture),
                    capturedAmount = v.AmountResult.Result,
                    amountConfidenceLevel = v.AmountResult.Score,
                    amountRegionOfInterest = new RegionOfInterest
                    {
                        height = v.AmountResult.Location.Height,
                        left = v.AmountResult.Location.Left,
                        top = v.AmountResult.Location.Top,
                        width = v.AmountResult.Location.Width
                    }
                }).ToArray()
            };
        }
    }
}
