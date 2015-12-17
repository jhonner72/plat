using Lombard.Adapters.A2iaAdapter.Messages;
using Lombard.Adapters.A2iaAdapter.Wrapper.Dtos;

namespace Lombard.Adapters.A2iaAdapter.Mappers
{
    public interface IChequeOcrResponseToMessageResponseMapper : IMapper<ChequeOcrResponse, RecogniseCourtesyAmountResponse>
    {

    }

    public class ChequeOcrResponseToMessageResponseMapper : IChequeOcrResponseToMessageResponseMapper
    {
        public RecogniseCourtesyAmountResponse Map(ChequeOcrResponse message)
        {
            //Log.Debug("File name in the message was {0}", message.FilePath);
            //Log.Debug("ICR response error message was {0}", message.ErrorMessage);
            return new RecogniseCourtesyAmountResponse()
            {
                DocumentReferenceNumber = message.DocumentReferenceNumber,
                //frontImageIdentifier = message.FilePath,
                Success = message.Success,
                ErrorMessage = message.ErrorMessage,

                CapturedAmount = message.AmountResult,
                ConfidenceLevel = message.AmountScore//,
                //CodelineResult = message.CodelineResult,
                //CodelineScore = message.CodelineScore,
                //DateResult = message.DateResult,
                //DateScore = message.DateScore
            };
        }
    }
}
