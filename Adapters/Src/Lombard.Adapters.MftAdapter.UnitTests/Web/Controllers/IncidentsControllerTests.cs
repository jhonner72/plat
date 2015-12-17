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
    public class IncidentsControllerTests
    {
        private Mock<IMapper<CreateIncidentRequest, Incident>> incidentRequestMapper;
        private Mock<IExchangePublisher<Incident>> exchange;

        [TestInitialize]
        public void TestInitialize()
        {
            exchange = new Mock<IExchangePublisher<Incident>>();
            incidentRequestMapper = new Mock<IMapper<CreateIncidentRequest, Incident>>();
        }

        [TestMethod]
        public async Task WhenPostFromFile_AndUnexpectedException_ThenReturnInternalServerError()
        {
            incidentRequestMapper.Setup(x => x.Map(It.IsAny<CreateIncidentRequest>()))
                .Throws(new Exception());

            var sut = CreateController(new HttpRequestMessage());

            var request = new CreateIncidentRequest
            {
                JobIdentifier = It.IsAny<string>(),
                JobSubject = It.IsAny<string>(),
                JobPredicate = It.IsAny<string>()
            };

            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task GivenInvalidModelState_WhenPostFromFile_ThenReturnBadRequest()
        {
            var sut = CreateController(new HttpRequestMessage());
            sut.ModelState.AddModelError("FileName", "Required");

            var result = await sut.PostAsync(new CreateIncidentRequest());

            Assert.AreEqual(HttpStatusCode.BadRequest, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenMapRequest()
        {
            var request = new CreateIncidentRequest
            {
                JobIdentifier = It.IsAny<string>(),
                JobSubject = It.IsAny<string>(),
                JobPredicate = It.IsAny<string>()
            };

            var sut = CreateController(new HttpRequestMessage());
            await sut.PostAsync(request);

            incidentRequestMapper.Verify(x => x.Map(request));
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenPublishAsync()
        {
            var request = new CreateIncidentRequest
            {
                JobIdentifier = It.IsAny<string>(),
                JobSubject = It.IsAny<string>(),
                JobPredicate = It.IsAny<string>()
            };

            var incident = new Incident { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectIncidentMapperToMap(incident);
            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            await sut.PostAsync(request);

            exchange.Verify(x => x.PublishAsync(incident, It.IsAny<string>()));
        }

        [TestMethod]
        public async Task WhenPostFromFile_AndPublishAsyncFaults_ThenReturnInternalServerError()
        {
            var request = new CreateIncidentRequest
            {
                JobIdentifier = It.IsAny<string>(),
                JobSubject = It.IsAny<string>(),
                JobPredicate = It.IsAny<string>()
            };

            var incident = new Incident { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectIncidentMapperToMap(incident);
            ExpectExchangeToFaultOnPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.InternalServerError, result.StatusCode);
        }

        [TestMethod]
        public async Task WhenPostFromFile_ThenReturnOk()
        {
            var request = new CreateIncidentRequest
            {
                JobIdentifier = It.IsAny<string>(),
                JobSubject = It.IsAny<string>(),
                JobPredicate = It.IsAny<string>()
            };

            var incident = new Incident { jobIdentifier = Guid.NewGuid().ToString() };

            ExpectIncidentMapperToMap(incident);
            ExpectExchangeToPublish();

            var sut = CreateController(new HttpRequestMessage());
            var result = await sut.PostAsync(request);

            Assert.AreEqual(HttpStatusCode.OK, result.StatusCode);
        }

        private void ExpectExchangeToFaultOnPublish()
        {
            var task = Task.Run(() => { throw new Exception(); });

            exchange.Setup(x => x.PublishAsync(It.IsAny<Incident>(), It.IsAny<string>()))
                .Returns(task);
        }

        private void ExpectExchangeToPublish()
        {
            exchange.Setup(x => x.PublishAsync(It.IsAny<Incident>(), It.IsAny<string>()))
                .Returns(Task.Delay(0));
        }

        private void ExpectIncidentMapperToMap(Incident incidentRequest)
        {
            incidentRequestMapper.Setup(x => x.Map(It.IsAny<CreateIncidentRequest>()))
                .Returns(incidentRequest);
        }


        private IncidentsController CreateController(HttpRequestMessage request)
        {
            request.Properties.Add(HttpPropertyKeys.HttpConfigurationKey, new HttpConfiguration());

            return new IncidentsController(exchange.Object, incidentRequestMapper.Object)
            {
                Request = request
            };
        }
    }

}
