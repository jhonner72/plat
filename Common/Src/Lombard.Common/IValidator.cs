using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace Lombard
{
    public interface IValidator<in T>
    {
        IEnumerable<ValidationResult> Validate(T entity);
    }
}