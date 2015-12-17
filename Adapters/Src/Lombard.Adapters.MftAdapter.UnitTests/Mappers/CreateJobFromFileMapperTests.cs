using System;
using Lombard.Adapters.MftAdapter.Mappers;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.DateAndTime;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Adapters.MftAdapter.UnitTests.Mappers
{
    [TestClass]
    public class CreateJobFromFileMapperTests
    {
        private Mock<IDateTimeProvider> dateTimeProvider;

        [TestInitialize]
        public void TestInitialize()
        {
            dateTimeProvider = new Mock<IDateTimeProvider>();
        }

        [TestMethod]
        public void GiveCreateJobFromNSBDFileRequest_WhenMap_ThenMapToJobRequest()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName,
                JobSubject = "voucher",
                JobPredicate = "outclearings",
                JobIdentifier = Constants.ValidNSBDJobId
            };

            var sut = CreateMapper();

            var result = sut.Map(request);

            Assert.AreEqual(Constants.ValidNSBDJobId, result.jobIdentifier);
            Assert.AreEqual(request.JobSubject, result.subject);
            Assert.AreEqual(request.JobPredicate, result.predicate);
        }

        [TestMethod]
        public void GiveCreateJobFromNSBDFileRequest_WhenMap_ThenUseDateTimeProvider()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName,
                JobSubject = "voucher",
                JobPredicate = "outclearings",
                JobIdentifier = Constants.ValidNSBDFileName.Substring(Constants.ValidNSBDFileName.IndexOf("_", StringComparison.Ordinal) + 1, Constants.ValidNSBDFileName.IndexOf(".", StringComparison.Ordinal) - Constants.ValidNSBDFileName.IndexOf("_", StringComparison.Ordinal) - 1)
            };

            var sut = CreateMapper();

            sut.Map(request);

            dateTimeProvider.Verify(x => x.CurrentTimeInAustralianEasternTimeZone());
        }

        [TestMethod]
        public void GiveCreateJobFromNIEIFileRequest_WhenMap_ThenMapToJobRequest()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNIEIFileName,
                JobSubject = "inwardimageexchange",
                JobPredicate = "inclearings",
                JobIdentifier = "NIEI-2cbabea0-43f9-4554-b118-c80c487d97c3"
            };

            var sut = CreateMapper();

            var result = sut.Map(request);

            Assert.AreEqual(Constants.ValidNIEIJobId, result.jobIdentifier);
            Assert.AreEqual(request.JobSubject, result.subject);
            Assert.AreEqual(request.JobPredicate, result.predicate);
        }

        [TestMethod]
        public void GiveCreateJobFromNIEIFileRequest_WhenMap_ThenUseDateTimeProvider()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNIEIFileName,
                JobSubject = "inwardimageexchange",
                JobPredicate = "inclearings",
                JobIdentifier = "NIEI-2cbabea0-43f9-4554-b118-c80c487d97c3"
            };

            var sut = CreateMapper();

            sut.Map(request);

            dateTimeProvider.Verify(x => x.CurrentTimeInAustralianEasternTimeZone());
        }

        private CreateJobFromFileMapper CreateMapper()
        {
            return new CreateJobFromFileMapper(dateTimeProvider.Object);
        }
    }
}
