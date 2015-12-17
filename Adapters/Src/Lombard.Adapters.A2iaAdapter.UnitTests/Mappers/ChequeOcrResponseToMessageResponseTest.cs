//using System.Drawing;
//using Lombard.Adapters.A2iaAdapter.Mappers;
//using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Moq;

//namespace Lombard.Adapters.A2iaAdapter.UnitTests.Mappers
//{
//    [TestClass]
//    public class ChequeOcrResponseToMessageResponseTest
//    {
//        private OcrBatchToCarResponseMapper mapper;
//        private ChequeOcrResponse inputResponse;


//        [TestInitialize]
//        public void TestInitialize()
//        {
//            mapper = new Mock<OcrBatchToCarResponseMapper>().Object;
//            inputResponse = new ChequeOcrResponse
//            {
//                AmountResult = "201355",
//                AmountScore = "992",
//                AmountLocation = new Rectangle(230, 30, 200, 50),
//                ImageRotation = 0,
//                DocumentReferenceNumber = "123456789",
//            };
//        }
        
//        [TestMethod]
//        public void GiveChequeOcrResponse_WhenMap_ThenMapToRecogniseCourtesyAmountResponse()
//        {
//            var actualResponse = mapper.Map(inputResponse);

//            Assert.AreEqual(inputResponse.DocumentReferenceNumber, actualResponse.documentReferenceNumber);
//            Assert.AreEqual(inputResponse.AmountResult, actualResponse.capturedAmount);
//            Assert.AreEqual(inputResponse.AmountScore, actualResponse.amountConfidenceLevel);
//            Assert.AreEqual(inputResponse.ImageRotation.ToString(), actualResponse.imageRotation);
//            Assert.AreEqual(inputResponse.AmountLocation.Height, actualResponse.amountRegionOfInterest.height);
//            Assert.AreEqual(inputResponse.AmountLocation.Width, actualResponse.amountRegionOfInterest.width);
//            Assert.AreEqual(inputResponse.AmountLocation.Left, actualResponse.amountRegionOfInterest.left);
//            Assert.AreEqual(inputResponse.AmountLocation.Top, actualResponse.amountRegionOfInterest.top);

//        }
//    }
//}
