using System;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using Lombard.Common.Extensions;
using Lombard.Extensions;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Common.UnitTests.Extensions
{
    [TestClass]
    public class ValidationResultExtTests
    {
        [TestMethod]
        public void WhenEmptyValidationResultsWeAreClean()
        {
            var results = Enumerable.Empty<ValidationResult>();

            Assert.IsFalse(results.HasErrors());
        }

        [TestMethod]
        public void WhenCollectionHasErrorsThenWeReturnHasErrors()
        {
            var results = new[] {new ValidationResult("error")};

            Assert.IsTrue(results.HasErrors());
        }

        [TestMethod]
        public void WeAreAbleToTransformValidationResultIntoException()
        {
            var error = new ValidationResult("error is fancy");
            var exception = error.ToException();

            Assert.IsNotNull(exception);
            Assert.AreEqual(error.ErrorMessage, exception.Message);
        }

        [TestMethod]
        public void WeAreAbleToTransformManyValidateResultsIntoAggregateException()
        {
            var errors = new[] {new ValidationResult("super error"), new ValidationResult("Fancy error")};
            var exception = errors.ToAggregateException();

            Assert.IsInstanceOfType(exception, typeof(AggregateException));
            Assert.AreEqual(2, exception.InnerExceptions.Count);
        }

        [TestMethod]
        public void WhenNoExceptionWeJustReturnNullInsteadOfAggregateException()
        {
            var errors = Enumerable.Empty<ValidationResult>();
            var exception = errors.ToAggregateException();

            Assert.IsNull(exception);
        }
    }
}