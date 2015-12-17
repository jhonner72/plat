using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web.Http.ModelBinding;

namespace Lombard.Extensions
{
    public static class ModelStateExt
    {
        public static IEnumerable<string> SerializeForLog(this ModelStateDictionary modelState)
        {
            return modelState.Select(x =>
                        string.Format("{0} : {1}", 
                            x.Key, 
                            string.Join("; ", x.Value.Errors.Select(y => y.ErrorMessage))));
        }

        public static void AddValidationResults(this ModelStateDictionary modelState,
            IEnumerable<ValidationResult> validationResults)
        {
            var results = validationResults as ValidationResult[] ?? validationResults.ToArray();

            if (!results.Any())
            {
                return;
            }

            foreach (var result in results)
            {
                modelState.AddModelError(string.Join(",", result.MemberNames), result.ErrorMessage);
            }
        }
    }
}
