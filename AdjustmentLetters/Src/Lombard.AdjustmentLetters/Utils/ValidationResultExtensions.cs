using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Text;

namespace Lombard.AdjustmentLetters.Utils
{
    public static class ValidationResultExtensions
    {
        public static string AsString(this IEnumerable<ValidationResult> validationErrors)
        {
            var stringBuilder = new StringBuilder();

            foreach (var validationResult in validationErrors)
            {
                stringBuilder.AppendLine(validationResult.ToString());
            }

            return stringBuilder.ToString();
        }
    }
}
