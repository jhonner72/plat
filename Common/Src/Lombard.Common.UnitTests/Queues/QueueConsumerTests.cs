using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Serilog;
using Lombard.Common.Helper;
using Lombard.Common.Messages;

namespace Lombard.Common.UnitTests.Queues
{
    [TestClass]
    public class QueueConsumerTests
    {
        private Mock<IMessageProcessorFactory<string>> processorFactory;
        private Mock<IAdvancedBus> messageBus;
        private Mock<IMessageProcessor<string>> processor;
        private Mock<ILogger> logger;
        private Mock<IDisposable> consumer;
        private Mock<IQueue> queue;
        private Mock<ICustomErrorHandling> errorHandler;
        private Mock<IExchangePublisher<Error>> errorExchange;

        [TestInitialize]
        public void TestInitialize()
        {
            processorFactory = new Mock<IMessageProcessorFactory<string>>();
            messageBus = new Mock<IAdvancedBus>();
            processor = new Mock<IMessageProcessor<string>>();
            logger = new Mock<ILogger>();
            consumer = new Mock<IDisposable>();
            queue = new Mock<IQueue>();
            errorHandler = new Mock<ICustomErrorHandling>();
            errorExchange = new Mock<IExchangePublisher<Error>>();
            Log.Logger = logger.Object;
        }

        [TestMethod]
        public void WhenSubscribe_ThenDeclareQueue()
        {
            const string QueueName = "testQueue";

            ExpectSubscribeToCreateConsumer();

            var sut = CreateQueueConsumer();

            sut.Subscribe(QueueName);

            messageBus.Verify(x => x.QueueDeclare(QueueName, It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<int?>(), It.IsAny<int?>(), It.IsAny<int?>(), It.IsAny<string>(), It.IsAny<string>(), It.IsAny<int?>(), It.IsAny<int?>()));
        }

        [TestMethod]
        public void WhenSubscribe_ThenSubscribeToQueue()
        {
            const string QueueName = "testQueue";

            ExpectSubscribeToCreateConsumer(); 

            var sut = CreateQueueConsumer();

            sut.Subscribe(QueueName);

            messageBus.Verify(x => x.Consume(It.IsAny<IQueue>(), It.IsAny<Func<IMessage<string>, MessageReceivedInfo, Task>>()));
        }

        [TestMethod]
        public async Task WhenHandleMessage_ThenCreateProcessor()
        {
            const string Message = "TestMessage";

            ExpectMessageProcessorFactoryToCreateMessageProcessor(Message);

            var sut = CreateQueueConsumer();

            await sut.HandleMessageAsync(Message, "xxx","yyy");

            processorFactory.Verify(x => x.CreateMessageProcessor(Message));
        }

        [TestMethod]
        public async Task WhenHandleMessage_ThenProcessMessage()
        {
            const string Message = "TestMessage";

            ExpectMessageProcessorFactoryToCreateMessageProcessor(Message);

            var sut = CreateQueueConsumer();

            await sut.HandleMessageAsync(Message, "xxx", "yyy");

            processor.Verify(x => x.ProcessAsync(It.IsAny<CancellationToken>(), "xxx", "yyy"));
        }

        [TestMethod]
        public async Task WhenHandleMessage_AndProcessingError_ThenLogError()
        {
            const string Message = "TestMessage";
            var ex = new InvalidOperationException();

            ExpectMessageProcessorToThrow(ex);

            var sut = CreateQueueConsumer();

            try
            {
                await sut.HandleMessageAsync(Message, "xxx", "yyy");
            }
            catch
            {
                logger.Verify(x => x.Error(It.Is<AggregateException>(y => y.InnerExceptions.Single() == ex), It.IsAny<string>()));
            }
        }

        [TestMethod]
        [ExpectedException(typeof(EasyNetQException))]
        public async Task WhenHandleMessage_AndProcessingError_ThenThrowEasyNetQException()
        {
            const string Message = "TestMessage";
            var ex = new InvalidOperationException();

            ExpectMessageProcessorToThrow(ex);
            ExpectCustomErrorHandlingToThrowException(ex);

            var sut = CreateQueueConsumer();

            await sut.HandleMessageAsync(Message, "xxx", "yyy");
        }

        [TestMethod]
        public async Task WhenCompleteAsync_ThenDisposeConsumer()
        {
            const string SubscriptionId = "testId";
            var sut = CreateQueueConsumer();

            ExpectProcessorFactoryToCreate(new WaitingMessageProcessorStub());
            ExpectSubscribeToCreateConsumer();

            sut.Subscribe(SubscriptionId);

            await sut.CompleteAsync(true);

            consumer.Verify(x => x.Dispose());
        }

        private void ExpectSubscribeToCreateConsumer()
        {
            messageBus.Setup(x => x.QueueDeclare(It.IsAny<string>(), It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<bool>(), It.IsAny<int?>(), It.IsAny<int?>(), It.IsAny<int?>(), It.IsAny<string>(), It.IsAny<string>(), It.IsAny<int?>(), It.IsAny<int?>()))
                .Returns(queue.Object);

            messageBus.Setup(x => x.Consume(It.IsAny<IQueue>(), It.IsAny<Func<IMessage<string>, MessageReceivedInfo, Task>>()))
                .Returns(consumer.Object);
        }


        private void ExpectMessageProcessorFactoryToCreateMessageProcessor(string message)
        {
            processor.Setup(x => x.Message)
                .Returns(message);

            processorFactory
                .Setup(x => x.CreateMessageProcessor(message))
                .Returns(processor.Object);
        }

        private void ExpectMessageProcessorToThrow(Exception ex)
        {
            processor.Setup(x => x.ProcessAsync(It.IsAny<CancellationToken>(), "xxx", "yyy"))
                .Throws(ex);

            processorFactory
                .Setup(x => x.CreateMessageProcessor(It.IsAny<string>()))
                .Returns(processor.Object);
        }

        private void ExpectProcessorFactoryToCreate(IMessageProcessor<string> processorStub)
        {
            processorFactory
                .Setup(x => x.CreateMessageProcessor(It.IsAny<string>()))
                .Returns(processorStub);
        }

        private void ExpectCustomErrorHandlingToThrowException(Exception ex)
        {
            errorHandler
                .Setup(x => x.MapMessageToErrorSchema(It.IsAny<AggregateException>(), It.IsAny<string>(), It.IsAny<string>(), It.IsAny<string>(), It.IsAny<MessageProperties>(), null, null))
                .Throws(ex);
        }

        private QueueConsumer<string> CreateQueueConsumer()
        {
            return new QueueConsumer<string>(processorFactory.Object, messageBus.Object, errorHandler.Object, errorExchange.Object);
        }
    }
}