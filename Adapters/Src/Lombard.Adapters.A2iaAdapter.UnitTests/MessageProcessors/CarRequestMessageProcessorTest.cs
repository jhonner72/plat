//using System.Collections.Concurrent;
//using System.Drawing;
//using System.IO.Abstractions;
//using Lombard.Adapters.A2iaAdapter.Domain;
//using Lombard.Adapters.A2iaAdapter.Mappers;
//using Lombard.Adapters.A2iaAdapter.MessageProcessors;
//using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
//using Lombard.Adapters.A2iaAdapter.Wrapper;
//using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
//using Lombard.Common.Queues;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Moq;

//namespace Lombard.Adapters.A2iaAdapter.UnitTests.MessageProcessors
//{
//    [TestClass]
//    public class CarRequestMessageProcessorTest
//    { 
//        private Mock<CarRequestMessageProcessor> processorMock;

//        private BatchInfo batchInfo;
//        private Mock<IA2IaService> a2IaServiceMock;
//        private Mock<IFileSystem> fileSystemMock;
//        private Mock<ICourtesyAmountRequestBatchInfoMapper> infoMapperMock;
//        private Mock<IExchangePublisher<RecogniseBatchCourtesyAmountResponse>> exchangeMock;
//        private Mock<IChequeOcrResponseToMessageResponseMapper> responseMapperMock;

//        [TestInitialize]
//        public void TestInitialize()
//        {
//            fileSystemMock = new Mock<IFileSystem>();
//            a2IaServiceMock = new Mock<IA2IaService>();
//            infoMapperMock = new Mock<ICourtesyAmountRequestBatchInfoMapper>();
//            exchangeMock = new Mock<IExchangePublisher<RecogniseBatchCourtesyAmountResponse>>();
//            responseMapperMock = new Mock<IChequeOcrResponseToMessageResponseMapper>();

//            //var context = new Mock<IComponentContext>();
//            //context.Setup(c => c.Resolve<IA2IAService>()).Returns(a2IaServiceMock.Object);
//            //context.Setup(c => c.Resolve<IFileSystem>()).Returns(fileSystemMock.Object);
//            //context.Setup(c => c.Resolve<ICourtesyAmountRequestBatchInfoMapper>()).Returns(infoMapperMock.Object);
//            //context.Setup(c => c.Resolve<IExchangePublisher<RecogniseBatchCourtesyAmountResponse>>()).Returns(exchangeMock.Object);
//            //context.Setup(c => c.Resolve<IChequeOcrResponseToMessageResponseMapper>()).Returns(responseMapperMock.Object);
//            //context.Setup(c => c.Resolve<IAdapterConfiguration>()).Returns(configureMock.Object);

//            processorMock = new Mock<CarRequestMessageProcessor>(a2IaServiceMock.Object, fileSystemMock.Object,
//                infoMapperMock.Object, exchangeMock.Object, responseMapperMock.Object);
//            //var vouchers = new []
//            //{
//            //    new RecogniseCourtesyAmountRequest{ documentReferenceNumber = "000025962", processingDate = new DateTime(2015, 3, 30)},
//            //    new RecogniseCourtesyAmountRequest{ documentReferenceNumber = "000206869", processingDate = new DateTime(2015, 3, 30)},
//            //};
//            //request = new RecogniseBatchCourtesyAmountRequest
//            //{
//            //    jobIdentifier = "batch1",
//            //    voucher = vouchers
//            //};

//            var chequeImageInfos = new []
//            {
//                new ChequeImageInfo
//                {
//                    DocumentReferenceNumber = "000025962",
//                    Status = 0,
//                    Succes = true,
//                    Urn = @"D:\SMS\Fuji Xerox\A2IAIntegration\Test Images\Batch1\VOUCHER_30032015_000025962_FRONT.tif"
//                },
//                new ChequeImageInfo
//                {
//                    DocumentReferenceNumber = "000206869",
//                    Status = 0,
//                    Succes = true,
//                    Urn = @"D:\SMS\Fuji Xerox\A2IAIntegration\Test Images\Batch1\VOUCHER_30032015_000206869_FRONT.tif"
//                },
//            };

//            batchInfo = new BatchInfo
//            {
//                JobIdentifier = "batch1",
//                ChequeImageInfos = chequeImageInfos
//            };

//        }

//        [TestMethod]
//        public void GiveBatchInfo_WhenValidateImageFiles_ThenBatchInfoUpdated()
//        {
//            fileSystemMock.Setup(f => f.File.Exists(It.IsAny<string>())).Returns(true);

//            processorMock.Object.ValidateImageFiles(batchInfo);
//            foreach (var b in batchInfo.ChequeImageInfos)
//            {
//                Assert.AreEqual(b.Status, 0);
//                Assert.AreEqual(b.Succes, true);
//            }

//            fileSystemMock.Verify(f=>f.File.Exists(It.IsAny<string>()));
//        }


//        [TestMethod]
//        public void GiveBatchInfo_WhenSendIcrRequestForImageFiles_ThenConcurrentBagUpdated()
//        {
//            var chequeOcrInitiateResponse = new ChequeOcrInitiateResponse
//            {
//                DocumentReferenceNumber = "123456789",
//                Success = true
//            };
//            a2IaServiceMock.Setup(s => s.ProcessChequeOcrInitiateRequest(It.IsAny<ChequeOcrRequest>())).Returns(chequeOcrInitiateResponse);

//            var bagResponses = new ConcurrentBag<RecogniseCourtesyAmountResponse>();
//            processorMock.Object.RequestForIcr(batchInfo, bagResponses);

//            Assert.AreEqual(bagResponses.Count, 0);
//            a2IaServiceMock.Verify(a => a.ProcessChequeOcrInitiateRequest(It.IsAny<ChequeOcrRequest>()));
//        }

//        [TestMethod]
//        public void GiveBatchInfo_WhenGetIcrResultForImageFiles_ThenConcurrentBagUpdated()
//        {
//            var chequeOcrResponse = new ChequeOcrResponse
//            {
//                DocumentReferenceNumber = "123456789",
//                AmountResult = "12355",
//                AmountScore = "935",
//                AmountLocation = new Rectangle(155, 28, 235, 45),
//                ImageRotation = 0, 
//                Success = true
//            };
//            a2IaServiceMock.Setup(s => s.GetLastChequeOcrResponse()).Returns(chequeOcrResponse);

//            var bagResponses = new ConcurrentBag<RecogniseCourtesyAmountResponse>();
//            processorMock.Object.FetchIcrResult(batchInfo, bagResponses);

//            Assert.AreEqual(bagResponses.Count, batchInfo.ChequeImageInfos.Length);
//            a2IaServiceMock.Verify(a=>a.GetLastChequeOcrResponse());
//        }
//    }
//}
