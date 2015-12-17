using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Common.UnitTests.Queues
{
    [TestClass]
    public class ExchangePublisherTests
    {
        private Mock<IAdvancedBus> messageBus;

        [TestInitialize]
        public void TestInitialize()
        {
            messageBus = new Mock<IAdvancedBus>();
        }

        [TestMethod]
        public void WhenDeclare_ThenDeclareDirectPassiveExchange()
        {
            const string ExchangeName = "aaa.test";

            var sut = CreateExchangePublisher();

            sut.Declare(ExchangeName);

            messageBus.Verify(x => x.ExchangeDeclare(ExchangeName, ExchangeType.Direct, true, true, false, It.IsAny<bool>(), It.IsAny<string>(), It.IsAny<bool>()));
        }

        [TestMethod]
        public async Task WhenPublishAsync_ThenPublishToExchange()
        {
            const string Request = "aaa";

            ExpectMessageBusToPublish();

            var sut = CreateExchangePublisher();
            await sut.PublishAsync(Request, "xxx");

            messageBus.Verify(x => x.PublishAsync(It.IsAny<IExchange>(), string.Empty, false, false, It.Is<Message<string>>(m => m.Body == Request)));
        }

        [TestMethod]
        [ExpectedException(typeof(AggregateException))]
        public async Task WhenPublishAsync_AndFault_ThenThrowException()
        {
            const string Request = "aaa";

            ExpectMessageBusToFaultOnPublish();

            var sut = CreateExchangePublisher();
            await sut.PublishAsync(Request, "xxx");
        }

        private void ExpectMessageBusToFaultOnPublish()
        {
            var task = Task.Run(() => { throw new Exception(); });

            messageBus.Setup(x => x.PublishAsync(It.IsAny<IExchange>(), string.Empty, false, false, It.IsAny<Message<string>>()))
                .Returns(task);
        }

        private void ExpectMessageBusToPublish()
        {
            messageBus.Setup(x => x.PublishAsync(It.IsAny<IExchange>(), string.Empty, false, false, It.IsAny<Message<string>>()))
                .Returns(Task.Delay(0));
        }
        private ExchangePublisher<string> CreateExchangePublisher()
        {
            return new ExchangePublisher<string>(messageBus.Object);
        }
    }
}
