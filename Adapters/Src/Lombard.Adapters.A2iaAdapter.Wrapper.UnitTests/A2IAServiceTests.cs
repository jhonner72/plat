//using System;
//using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
//using Microsoft.VisualStudio.TestTools.UnitTesting;
//using Moq;

//namespace Lombard.Adapters.A2iaAdapter.Wrapper.UnitTests
//{
//    [TestClass]
//    public class A2IAServiceTests
//    {
//        private Mock<A2iACheckReaderLib.API> a2IaEngine;
//        private IA2IaService ia2IaService;
//        private readonly string imagesFolderPath = AppDomain.CurrentDomain.SetupInformation.ApplicationBase + @"\Test Images\";
//        private readonly string[] cpuCores = new[] { "SMS04855", "SMS04855", "SMS04855", "SMS04855" };
//        //private readonly string paramPath = @"C:\Program Files\A2iA\A2iA CheckReader V6.0 R3\Parms\SoftInt\Parms";
//        private readonly string paramPath = AppDomain.CurrentDomain.SetupInformation.ApplicationBase; 
//        private readonly string tableFile = AppDomain.CurrentDomain.SetupInformation.ApplicationBase + @"\Test Images\AU_Custom.tbl";

//        [TestInitialize]
//        public void TestInitialize()
//        {
//            a2IaEngine = new Mock<A2iACheckReaderLib.API>();
//            ia2IaService = new Ia2IaService(a2IaEngine.Object);
//        }

//        // Use TestCleanup to run code after each test has run
//        [TestCleanup()]
//        public void MyTestCleanup()
//        {
//            ia2IaService.Dispose();
//        }
        

//        [TestMethod]
//        public void Initialise_Success()
//        {
//            // define engine expectations
//            int tableId = 1;
//            int documentId = 1;
//            int channelParamId = 1;
//            int channelId = 1;

//            a2IaEngine.Setup(x => x.ScrOpenDocumentTable(tableFile)).Returns(tableId);
//            a2IaEngine.Setup(x => x.ScrGetDefaultDocument(tableId)).Returns(documentId);
//            a2IaEngine.Setup(x => x.ScrCreateChannelParam()).Returns(channelParamId);
//            a2IaEngine.Setup(x => x.ScrOpenChannelExt2(ref channelId, channelParamId, 10000)).Returns(0); // 0 = channel created successfully
          
//            // Invoke Method
//            ia2IaService.Initialise(paramPath, tableFile, cpuCores, true, false, false);

//            // Assert
//            Assert.IsTrue(ia2IaService.ChannelId > 0);
//            Assert.IsTrue(ia2IaService.DocumentId > 0);
//            Assert.IsTrue(ia2IaService.ExtractAmount);
//            Assert.IsTrue(ia2IaService.ExtractDate == false);
//            Assert.IsTrue(ia2IaService.ExtractCodeline == false);
//        }

//        [TestMethod]
//        public void ProcessChequeOcr_ExtractAmount_Success()
//        {
//            // define engine expectations
//            int requestId = 1;
//            int resultId = 1;
//            float imageRotation = 0;
//            string amount = "10000";
//            string amountConfidence = "999";
//            float x1 = 200;
//            float y1 = 100;
//            float x2 = 250;
//            float y2 = 150;
            
//            a2IaEngine.Setup(x => x.ScrOpenRequest(ia2IaService.ChannelId, ia2IaService.DocumentId)).Returns(requestId);
//            a2IaEngine.Setup(x => x.ScrGetResult(ia2IaService.ChannelId, requestId, 3000)).Returns(resultId);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.OrientationCorrection)).Returns(imageRotation);
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.EngineFields.Amount)).Returns(Constants.Enabled);

//            // Amount fields extraction
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.ResultFields.Amount)).Returns(amount);
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.ResultFields.AmountConfidence)).Returns(amountConfidence);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationX1)).Returns(x1);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationY1)).Returns(y1);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationX2)).Returns(x2);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationY2)).Returns(y2);
            
//            // define request
//            var request = new ChequeOcrRequest();
//            request.CorrelationId = "1";
//            request.FilePath = imagesFolderPath + "CHEQUE009.tif";

//            // Invoke Method
//            var response = ia2IaService.ProcessChequeOcr(request);

//            // Assert
//            Assert.IsTrue(response.Success);
//            Assert.IsTrue(request.CorrelationId == response.CorrelationId);
//            Assert.IsTrue(request.FilePath == response.FilePath);
//            Assert.IsTrue(response.AmountResult == amount);
//            Assert.IsTrue(response.AmountScore == amountConfidence);
//            Assert.IsTrue(response.AmountLocation == new System.Drawing.Rectangle((int)x1, (int)y1, (int)x2 - (int)x1, (int)y2 - (int)y1));
//            Assert.IsTrue(response.ImageRotation == (int)imageRotation);
//        }

//        [TestMethod]
//        public void ProcessChequeOcrInitiateRequest_Success()
//        {
//            // define engine expectations
//            int requestId = 1;
//            a2IaEngine.Setup(x => x.ScrOpenRequest(ia2IaService.ChannelId, ia2IaService.DocumentId)).Returns(requestId);
            
//            // define request
//            var request = new ChequeOcrRequest();
//            request.CorrelationId = "1";
//            request.FilePath = imagesFolderPath + "CHEQUE009.tif";

//            // Invoke Method
//            var response = ia2IaService.ProcessChequeOcrInitiateRequest(request);

//            // Assert
//            Assert.IsTrue(response.Success);
//            Assert.IsTrue(request.CorrelationId == response.CorrelationId);
//            Assert.IsTrue(request.FilePath == response.FilePath);
//            Assert.IsTrue(response.RequestId == requestId);
//        }

//        [TestMethod]
//        public void GetLastChequeOcrResponse_Success()
//        {
//            int resultId = 2;

//            // Enqueue
//            var chequeOcrInitiateResponse = new ChequeOcrInitiateResponse();
//            chequeOcrInitiateResponse.CorrelationId = "1";
//            chequeOcrInitiateResponse.RequestId = resultId;
//            chequeOcrInitiateResponse.FilePath = imagesFolderPath + "CHEQUE009.tif";
//            chequeOcrInitiateResponse.Success = true;
//            ia2IaService.CurrentRequests.Enqueue(chequeOcrInitiateResponse);

//            // define engine expectations
//            float imageRotation = 0;
//            string amount = "10000";
//            string amountConfidence = "999";
//            float x1 = 200;
//            float y1 = 100;
//            float x2 = 250;
//            float y2 = 150;

//            a2IaEngine.Setup(x => x.ScrGetResult(ia2IaService.ChannelId, chequeOcrInitiateResponse.RequestId, 3000)).Returns(resultId);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.OrientationCorrection)).Returns(imageRotation);
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.EngineFields.Amount)).Returns(Constants.Enabled);

//            // Amount fields extraction
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.ResultFields.Amount)).Returns(amount);
//            a2IaEngine.Setup(x => x.GetStringProperty(resultId, Constants.ResultFields.AmountConfidence)).Returns(amountConfidence);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationX1)).Returns(x1);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationY1)).Returns(y1);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationX2)).Returns(x2);
//            a2IaEngine.Setup(x => x.get_ObjectProperty(resultId, Constants.ResultFields.AmountLocationY2)).Returns(y2);
            
//            // Invoke Method
//            var response = ia2IaService.GetLastChequeOcrResponse();

//            // Assert
//            Assert.IsTrue(response.Success);
//            Assert.IsTrue(response.FilePath == chequeOcrInitiateResponse.FilePath);
//            Assert.IsTrue(response.CorrelationId == chequeOcrInitiateResponse.CorrelationId);
//            Assert.IsTrue(response.RequestId == chequeOcrInitiateResponse.RequestId);
//            Assert.IsTrue(response.ResultId == chequeOcrInitiateResponse.RequestId);
//            Assert.IsTrue(response.AmountResult == amount);
//            Assert.IsTrue(response.AmountScore == amountConfidence);
//            Assert.IsTrue(response.AmountLocation == new System.Drawing.Rectangle((int)x1, (int)y1, (int)x2 - (int)x1, (int)y2 - (int)y1));
//            Assert.IsTrue(response.ImageRotation == (int)imageRotation);
//        }
//    }
//}
