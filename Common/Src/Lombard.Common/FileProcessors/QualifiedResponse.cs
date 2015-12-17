using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Common.FileProcessors
{
    public class QualifiedResponse<TResult> : ValidatedResponse<TResult>
    {
        public bool HasErrors {get; private set;}

        protected QualifiedResponse(bool isSuccessful, bool hasErrors, TResult result, IEnumerable<ValidationResult> validationResults)
            : base(isSuccessful, result, validationResults)
        {
            this.HasErrors = hasErrors;
        }
        
        public static new QualifiedResponse<TResult> Success(TResult result)
        {
            return new QualifiedResponse<TResult>(true, false, result, new List<ValidationResult>());
        }

        public static new QualifiedResponse<TResult> Failure(IEnumerable<ValidationResult> validationErrors)
        {
            return new QualifiedResponse<TResult>(false, true, default(TResult), validationErrors);
        }

        public static QualifiedResponse<TResult> SuccessWithErrors(TResult result, IEnumerable<ValidationResult> validationErrors)
        {
            return new QualifiedResponse<TResult>(true, true, result, validationErrors);
        }

        public override IEnumerable<ValidationResult> ValidationResults
        {
            get
            {
                if (!HasErrors)
                {
                    throw new InvalidOperationException("Cannot access validation results when parse has no errors");
                }

                return validationResults;
            }
        }
    }
}
