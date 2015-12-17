using System.ComponentModel.DataAnnotations;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Adapters.MftAdapter.UnitTests.Web.Messages
{
    [TestClass]
    public class CreateJobFromFileRequestTests
    {
        [TestMethod]
        public void GivenValidOutClearingFileName_WhenValidate_ThenPass()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName,
                JobPredicate = "test",
                JobSubject = "test"
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsTrue(result);
        }

        [TestMethod]
        public void GivenValidInClearingFileName_WhenValidate_ThenPass()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNIEIFileName,
                JobPredicate = "test",
                JobSubject = "test"
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsTrue(result);
        }

        [TestMethod]
        public void GivenInvalidFileName_WhenValidate_ThenFail()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = "So Obviously Broken",
                JobPredicate = "test",
                JobSubject = "test"
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsFalse(result);
        }

        [TestMethod]
        public void GivenNoFileName_WhenValidate_ThenFail()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = null,
                JobPredicate = "test",
                JobSubject = "test"
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsFalse(result);
        }

        [TestMethod]
        public void GivenNoPredicate_WhenValidate_ThenFail()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName,
                JobPredicate = null,
                JobSubject = "test"
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsFalse(result);
        }

        [TestMethod]
        public void GivenNoSubject_WhenValidate_ThenFail()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName,
                JobPredicate = "test",
                JobSubject = null
            };

            var result = Validator.TryValidateObject(request, new ValidationContext(request, null, null), null, true);

            Assert.IsFalse(result);
        }
    }
}
