using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;

namespace Lombard.Common.Extensions
{
    public static class ValidationResultExt
    {
        public static bool HasErrors(this IEnumerable<ValidationResult> errors)
        {
            Guard.IsNotNull(errors, "errors");

            return errors.Any();
        }

        public static ValidationException ToException(this ValidationResult error)
        {
            Guard.IsNotNull(error, "error");

            return new ValidationException(error.ErrorMessage);
        }

        public static AggregateException ToAggregateException(this IEnumerable<ValidationResult> errors)
        {
            Guard.IsNotNull(errors, "errors");

            return errors.Any() ? new AggregateException(errors.Select(ToException)) : null;
        }
    }
}