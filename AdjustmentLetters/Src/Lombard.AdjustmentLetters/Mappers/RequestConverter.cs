namespace Lombard.AdjustmentLetters.Service.Mappers
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.Globalization;
    using System.Linq;
    using Lombard.AdjustmentLetters.Service.Domain;
    using Lombard.Common.FileProcessors;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;

    public interface IRequestConverter : IMapper<IAdjustmentLetters, ValidatedResponse<ILetterGenerator>>
    {
    }

    public class RequestConverter : IRequestConverter
    {
        public ValidatedResponse<ILetterGenerator> Map(IAdjustmentLetters vifFileInfo)
        {
            var result = new LetterGenerator();
            return ValidatedResponse<ILetterGenerator>.Success(result);
        }

        private ValidatedResponse<ILetterGenerator> Failure(string failureMessage)
        {
            return ValidatedResponse<ILetterGenerator>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }
    }
}
