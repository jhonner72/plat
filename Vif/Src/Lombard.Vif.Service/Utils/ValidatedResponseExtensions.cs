using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Lombard.Common.FileProcessors;

namespace Lombard.Vif.Service.Utils
{
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
