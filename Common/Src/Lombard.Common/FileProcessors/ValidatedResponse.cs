using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace Lombard.Common.FileProcessors
{
    public class ValidatedResponse<TResult>
    {
        public bool IsSuccessful { get; private set; }

        protected readonly TResult result;

        protected readonly IEnumerable<ValidationResult> validationResults;

        public static ValidatedResponse<TResult> Success(TResult result)
        {
            return new ValidatedResponse<TResult>(true, result, new List<ValidationResult>());
        }

        public static ValidatedResponse<TResult> Failure(IEnumerable<ValidationResult> validationErrors)
        {
            return new ValidatedResponse<TResult>(false, default(TResult), validationErrors);
        }

        protected ValidatedResponse(bool isSuccessful, TResult result, IEnumerable<ValidationResult> validationResults)
        {
            IsSuccessful = isSuccessful;
            this.result = result;
            this.validationResults = validationResults;
        }

        /// <summary>
        /// If successful, contains parsed file name with properties of metadata
        /// </summary>
        public virtual TResult Result
        {
            get
            {
                if (!IsSuccessful)
                {
                    throw new InvalidOperationException("Cannot access result when parse was unsuccessful");
                }

                return result;
            }
        }

        /// <summary>
        /// If successful, contains parsed file name with properties of metadata
        /// </summary>
        public virtual IEnumerable<ValidationResult> ValidationResults
        {
            get
            {
                if (IsSuccessful)
                {
                    throw new InvalidOperationException("Cannot access validation results when parse was successful");
                }

                return validationResults;
            }
        }
    }
}
