using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using Aspose.Pdf;
using Autofac;
using Lombard.AdjustmentLetters.Data;
using Lombard.AdjustmentLetters.Data.Domain;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Helper;
using Lombard.AdjustmentLetters.Mappers;
using Lombard.AdjustmentLetters.MessageProcessors;
using Lombard.AdjustmentLetters.Utils;
using Lombard.Common.FileProcessors;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.AdjustmentLetters.UnitTests
{
    [TestClass]
    public class AdjLetterRequestProcessorTest
    {
        private Mock<IContainer> container;
        private Mock<IExchangePublisher<CreateBatchAdjustmentLettersResponse>> exchange;
        private Mock<IMessageToBatchConverter> requestSplitter;
        private Mock<ILetterGenerator> letterGenerator;
        private Mock<IFileWriter> fileWriter;
        private Mock<IFileReader> fileReader;
        private Mock<IPathHelper> pathHelper;
        private Mock<IReferenceDbContext> refDb;
        private Mock<IAsposeWrapper> aspose;

        [TestInitialize]
        public void TestInitialize()
        {
            container = new Mock<IContainer>();
            exchange = new Mock<IExchangePublisher<CreateBatchAdjustmentLettersResponse>>();
            requestSplitter = new Mock<IMessageToBatchConverter>();
            letterGenerator = new Mock<ILetterGenerator>();
            fileWriter = new Mock<IFileWriter>();
            pathHelper = new Mock<IPathHelper>();
            refDb = new Mock<IReferenceDbContext>();
            aspose = new Mock<IAsposeWrapper>();
            fileReader = new Mock<IFileReader>();
        }

        [TestMethod]
        public async Task WhenProcessorProcessAsyncIsCalled_ThenSaveDocumentAndPublishIsCalled()
        {
            var processor = new AdjustmentLettersRequestProcessor(container.Object, exchange.Object, requestSplitter.Object, letterGenerator.Object, fileWriter.Object, pathHelper.Object, fileReader.Object, aspose.Object, refDb.Object);
            var cancellationToken = new CancellationToken();
            var message = GetTestData_BatchRequest();

            processor.Message = message;
            requestSplitter.Setup(x => x.Map(It.IsAny<CreateBatchAdjustmentLettersRequest>())).Returns(GetTestData_SuccessfulValidatedRespAdjLetterBatch);
            pathHelper.Setup(x => x.GetJobPath(It.IsAny<string>())).Returns(ValidatedResponse<string>.Success("c:\\ImAFakePath"));
            refDb.Setup(x => x.GetAllBranches()).Returns(new List<Branch>() { GetTestData_Branch() });
            letterGenerator.Setup(x => x.GeneratePdfFromTemplate(It.IsAny<VoucherInformation>(), It.IsAny<string>())).Returns(new Document());

            await processor.ProcessAsync(cancellationToken, "", string.Empty);

            exchange.Verify(x => x.PublishAsync(It.IsAny<CreateBatchAdjustmentLettersResponse>(), It.IsAny<string>(), It.IsAny<string>()), Times.Once);
            letterGenerator.Verify(x => x.GeneratePdfFromTemplate(It.IsAny<VoucherInformation>(), It.IsAny<string>()), Times.Once);
            letterGenerator.Verify(x => x.AddVoucherImage(It.IsAny<string>(), It.IsAny<Document>(), It.IsAny<AdjustmentLetter>(), It.IsAny<IFileReader>()), Times.Once);
            aspose.Verify(x => x.SaveDocument(It.IsAny<Document>(), It.IsAny<string>()), Times.Once);
            aspose.Verify(x => x.AddPageNumbers(It.IsAny<string>()), Times.Once);
        }

        private static Branch GetTestData_Branch()
        {
            var branch = new Branch
            {
                bank_code = "test",
                branch_bsb = "082401",
                branch_name = "test"
            };

            return branch;
        }

        private CreateBatchAdjustmentLettersRequest GetTestData_BatchRequest()
        {
            var request = new CreateBatchAdjustmentLettersRequest
            {
                voucherInformation = new[]
                {
                    GetTestData_VoucherInfo()
                },
                processingDate = new DateTime(2015, 1, 1),
                jobIdentifier = "1234"
            };

            return request;
        }

        private ValidatedResponse<AdjLetterBatch> GetTestData_SuccessfulValidatedRespAdjLetterBatch()
        {
            var batch = new AdjLetterBatch
            {
                JobIdentifier = "1234",
                ProcessingDate = new DateTime(2015, 1, 1),
                JobFolderLocation = "c:\\test"
            };

            batch.Letters.Add(GetTestData_Letter());

            return ValidatedResponse<AdjLetterBatch>.Success(batch);
        }

        private AdjustmentLetter GetTestData_Letter()
        {
            var letter = new AdjustmentLetter();

            var voucherInfo = GetTestData_VoucherInfo();

            letter.PdfFilename = "Hello.pdf";
            letter.AdjustedVoucher = voucherInfo;
            letter.Vouchers.Add(voucherInfo);

            return letter;
        }

        private VoucherInformation GetTestData_VoucherInfo()
        {
            var voucher = new Voucher
            {
                documentReferenceNumber = "04114",
                bsbNumber = "082401",
                auxDom = string.Empty,
                accountNumber = "813208132",
                amount = "419.38",
                documentType = DocumentTypeEnum.Dr,
                transactionCode = "01",
                processingDate = new DateTime(2015, 01, 01)
            };

            var voucherProcess = new VoucherProcess
            {
                adjustedFlag = true,
                adjustmentDescription = "test description",
                adjustmentReasonCode = 00,
                transactionLinkNumber = "9000001"
            };

            var voucherBatch = new VoucherBatch
            {
                scannedBatchNumber = "67500125",
                collectingBank = "082401"
            };

            var voucherInfo = new VoucherInformation
            {
                voucher = voucher,
                voucherProcess = voucherProcess,
                voucherBatch = voucherBatch
            };

            return voucherInfo;

        }
    }
}
