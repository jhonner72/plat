using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Hosting;
using Lombard.Adapters.MftAdapter.Web.Controllers;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.Queues;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Adapters.MftAdapter.UnitTests.Web.Controllers
{
    [TestClass]
    public class CopyImagesControllerTests
    {
        private Mock<IExchangePublisher<String>> exchange;

        private const string FileName = "NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3";

        [TestInitialize]
        public void TestInitialize()
        {
            exchange = new Mock<IExchangePublisher<String>>();
        }

        [TestMethod]
        public async Task WhenPublishAsync_AndUnexpectedException_ThenReturnInternalServerError()
        {
            var request = new CopyImageRequest
            {
                FileName = FileName
            };

            exchange.Setup(x => x.PublishAsync(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()))
                .Throws(new Exception());

            var sut = CreateController(new HttpRequestMessage());

            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task GivenInvalidModelState_WhenPostFromFile_ThenReturnBadRequest()
        {
            var request = new CopyImageRequest
            {
                FileName = FileName,
                RoutingKey = It.IsAny<string>()
            };

            var sut = CreateController(new HttpRequestMessage());
            sut.ModelState.AddModelError("FileName", "Required");

            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.BadRequest, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenPublishAsync()
        {
            var request = new CopyImageRequest
            {
                FileName = FileName,
                RoutingKey = It.IsAny<string>()
            };

            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            await sut.PostAsync(request);

            exchange.Verify(x => x.PublishAsync(request.FileName, It.IsAny<string>(), It.IsAny<string>()));
        }

        [TestMethod]
        public async Task WhenPostFromFile_AndPublishAsyncFaults_ThenReturnInternalServerError()
        {
            var request = new CopyImageRequest
            {
                FileName = FileName,
                RoutingKey = It.IsAny<string>()
            };

            ExpectExchangeToFaultOnPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenReturnOk()
        {
            var request = new CopyImageRequest
            {
                FileName = FileName,
                RoutingKey = It.IsAny<string>()
            };

            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.OK, result.StatusCode);
        }

        private void ExpectExchangeToFaultOnPublish()
        {
            var task = Task.Run(() => { throw new Exception(); });

            exchange.Setup(x => x.PublishAsync(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()))
                .Returns(task);
        }

        private void ExpectExchangeToPublish()
        {
            exchange.Setup(x => x.PublishAsync(It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>()))
                .Returns(Task.Delay(0));
        }

        private CopyImagesController CreateController(HttpRequestMessage request)
        {
            request.Properties.Add(HttpPropertyKeys.HttpConfigurationKey, new HttpConfiguration());

            return new CopyImagesController(exchange.Object)
            {
                Request = request
            };
        }
    }

}
