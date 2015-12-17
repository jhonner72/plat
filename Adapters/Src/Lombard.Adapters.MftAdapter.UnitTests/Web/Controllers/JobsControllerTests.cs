using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Hosting;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Controllers;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.Queues;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Adapters.MftAdapter.UnitTests.Web.Controllers
{
    [TestClass]
    public class JobsControllerTests
    {
        private Mock<IMapper<CreateJobFromFileRequest, JobRequest>> jobRequestMapper;
        private Mock<IExchangePublisher<JobRequest>> exchange;

        [TestInitialize]
        public void TestInitialize()
        {
            exchange = new Mock<IExchangePublisher<JobRequest>>();
            jobRequestMapper = new Mock<IMapper<CreateJobFromFileRequest, JobRequest>>();
        }

        [TestMethod]
        public async Task WhenPostFromFile_AndUnexpectedException_ThenReturnInternalServerError()
        {
            jobRequestMapper.Setup(x => x.Map(It.IsAny<CreateJobFromFileRequest>()))
                .Throws(new Exception());

            var sut = CreateController(new HttpRequestMessage());

            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName
            };

            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task GivenInvalidModelState_WhenPostFromFile_ThenReturnBadRequest()
        {
            var sut = CreateController(new HttpRequestMessage());
            sut.ModelState.AddModelError("FileName", "Required");

            var result = await sut.PostAsync(new CreateJobFromFileRequest());

            Assert.AreEqual(HttpStatusCode.BadRequest, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenMapRequest()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName
            };

            var sut = CreateController(new HttpRequestMessage());
            await sut.PostAsync(request);

            jobRequestMapper.Verify(x => x.Map(request));
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenPublishAsync()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName
            };

            var job = new JobRequest { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectJobRequestMapperToMap(job);
            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            await sut.PostAsync(request);

            exchange.Verify(x => x.PublishAsync(job, It.IsAny<string>()));
        }

        [TestMethod]
        public async Task WhenPostFromFile_AndPublishAsyncFaults_ThenReturnInternalServerError()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName
            };

            var job = new JobRequest { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectJobRequestMapperToMap(job);
            ExpectExchangeToFaultOnPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenReturnOk()
        {
            var request = new CreateJobFromFileRequest
            {
                FileName = Constants.ValidNSBDFileName
            };

            var job = new JobRequest { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectJobRequestMapperToMap(job);
            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.OK, result.StatusCode);
        }

        private void ExpectExchangeToFaultOnPublish()
        {
            var task = Task.Run(() => { throw new Exception(); });

            exchange.Setup(x => x.PublishAsync(It.IsAny<JobRequest>(), It.IsAny<string>()))
                .Returns(task);
        }

        private void ExpectExchangeToPublish()
        {
            exchange.Setup(x => x.PublishAsync(It.IsAny<JobRequest>(), It.IsAny<string>()))
                .Returns(Task.Delay(0));
        }

        private void ExpectJobRequestMapperToMap(JobRequest jobRequest)
        {
            jobRequestMapper.Setup(x => x.Map(It.IsAny<CreateJobFromFileRequest>()))
                .Returns(jobRequest);
        }


        private JobsController CreateController(HttpRequestMessage request)
        {
            request.Properties.Add(HttpPropertyKeys.HttpConfigurationKey, new HttpConfiguration());

            return new JobsController(exchange.Object, jobRequestMapper.Object)
            {
                Request = request
            };
        }
    }

}
