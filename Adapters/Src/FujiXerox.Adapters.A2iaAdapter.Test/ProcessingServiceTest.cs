//using System;
//using FujiXerox.Adapters.A2iaAdapter.MessageQueue;
//using FujiXerox.Adapters.A2iaAdapter.Serialization;
//using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Moq;
//using Serilog;

//namespace FujiXerox.Adapters.A2iaAdapter.Test
//{
//    [TestClass]
//    [Ignore]
//    public class ProcessingServiceTest
//    {
//        private ILogger Log { get; set; }
//        private Mock<ILogger> LogMoq { get; set; }

//        [TestInitialize]
//        public void Initialize()
//        {
//            LogMoq = new Mock<ILogger>();
//            Log = LogMoq.Object;
//            ProcessingService.Log = Log;
//        }

//        [TestMethod]
//        public void QueueMessageRecieved_Handles_Message_With_No_Vouchers_Acknowledges_Message()
//        {
//            // Arrange
//            var message = CustomJsonSerializer.MessageToBytes(new RecogniseBatchCourtesyAmountRequest
//            {
//                jobIdentifier = string.Format("NAB{0}", Guid.NewGuid())
//            });
//            const string messageId = "Test123";
//            var payloadMoq = new Mock<IBasicGetResult>();
//            payloadMoq.SetupGet(r => r.Body).Returns(message);
//            payloadMoq.SetupGet(r => r.BasicProperties.MessageId).Returns(messageId);

//            var payload = payloadMoq.Object;

//            // Act
//            var actual = ProcessingService.Queue_MessageReceived(payload);

//            // Assert
//            Assert.IsTrue(actual);
//        }
//    }
//}
