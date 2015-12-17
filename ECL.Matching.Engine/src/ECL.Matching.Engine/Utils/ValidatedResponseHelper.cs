namespace Lombard.ECLMatchingEngine.Service.Utils
{
    using Lombard.Common.FileProcessors;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;

    public static class ValidatedResponseHelper
    {
        public static ValidatedResponse<T> Failure<T>(string failureMessage)
        {
            return ValidatedResponse<T>.Failure(
                new List<ValidationResult>
                {
                    new ValidationResult(failureMessage)
                });
        }

        public static ValidatedResponse<T> Failure<T>(string format, params object[] args)
        {
            var failureMessage = string.Format(format, args);

            return Failure<T>(failureMessage);
        }
    }
}

