namespace Lombard.Vif.Acknowledgement.Service.Mappers
{
    using System;
    using System.Globalization;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.Linq;
    using Lombard.Common.FileProcessors;
    using Lombard.Vif.Acknowledgement.Service.Domain;
    using Lombard.Vif.Service.Messages.XsdImports;

    public interface IRequestConverter : IMapper<IAcknowledgmentCode, ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse>>
    {

    }

    public class RequestConverter : IRequestConverter
    {
        public ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse> Map(IAcknowledgmentCode request)
        {
            ProcessValueInstructionFileAcknowledgmentResponse result = new ProcessValueInstructionFileAcknowledgmentResponse();
            var errorCode = request.StatusCode + request.ProcessCode;

            switch (errorCode)
            {
                case "VALRDY":
                case "VALEMP":
                    {
                        result.ackStatus = true;
                        break;
                    }
                default:
                    {
                        result.ackStatus = false;
                        break;
                    }
            }

            result.errorCode = errorCode;

            return ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse>.Success(result);

        }

        private ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse> Failure(string failureMessage)
        {
            return ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }
    }
}
