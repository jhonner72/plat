//using System;
//using System.Drawing;
//using System.IO;
//using System.Linq;
//using Lombard.Adapters.A2iaAdapter.Configuration;
//using Lombard.Adapters.A2iaAdapter.Mappers;
//using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Moq;

//namespace Lombard.Adapters.A2iaAdapter.UnitTests.Mappers
//{
//    [TestClass]
//    public class CourtesyAmountRequestBatchInfoTest
//    {
//        private Mock<IAdapterConfiguration> adapterConfigurationMock;
//        private CarRequestToOcrBatchMapper mapper;
//        private RecogniseBatchCourtesyAmountRequest inputRequest;

//        private RecogniseCourtesyAmountRequest[] vouchers;

//        [TestInitialize]
//        public void TestInitialize()
//        {
//            adapterConfigurationMock = new Mock<IAdapterConfiguration>();
//            mapper = new Mock<CarRequestToOcrBatchMapper>(adapterConfigurationMock.Object).Object;
//            vouchers = new RecogniseCourtesyAmountRequest[2]
//            { 
//                new RecogniseCourtesyAmountRequest{documentReferenceNumber = "123456789", processingDate = new DateTime(2015,4,2)},
//                new RecogniseCourtesyAmountRequest{documentReferenceNumber = "987654321", processingDate = new DateTime(2015,4,2)}
//            };
//            inputRequest = new RecogniseBatchCourtesyAmountRequest
//            {
//                jobIdentifier = "batch1",
//                voucher = vouchers,
//            };

//            adapterConfigurationMock.Setup(c => c.ImageFileNameTemplate).Returns("VOUCHER_{0:ddMMyyyy}_{1}_FRONT.tif");
//            adapterConfigurationMock.Setup(c => c.ImageFileFolder).Returns(@"D:\SMS\Fuji Xerox\A2IAIntegration\Test Images");
//        }

//        [TestMethod]
//        public void GiveRecogniseBatchCourtesyAmountRequest_WhenMap_ThenMapToBatchInfo()
//        {
//            var actualResponse = mapper.Map(inputRequest);

//            Assert.AreEqual(inputRequest.jobIdentifier, actualResponse.JobIdentifier);
//            for (var i = 0; i < inputRequest.voucher.Length; i++)
//            {
//                var i1 = i;
//                var v =
//                    actualResponse.ChequeImageInfos.Where(
//                        c => c.DocumentReferenceNumber == inputRequest.voucher[i1].documentReferenceNumber);
//                Assert.AreEqual(v.Count(), 1);
//                Assert.IsNotNull(actualResponse.ChequeImageInfos[i].Urn);
//                Assert.AreEqual(Path.GetDirectoryName(actualResponse.ChequeImageInfos[i].Urn), @"D:\SMS\Fuji Xerox\A2IAIntegration\Test Images\" + inputRequest.jobIdentifier);
//                Assert.AreEqual(actualResponse.ChequeImageInfos[i].Status, 0);
//                Assert.AreEqual(actualResponse.ChequeImageInfos[i].Succes, true);
//            }

//        }

//    }
//}
