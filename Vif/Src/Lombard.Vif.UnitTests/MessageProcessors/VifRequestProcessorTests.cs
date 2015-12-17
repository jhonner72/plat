using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading;
using Lombard.Common.FileProcessors;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Domain;
using Lombard.Vif.Service.Mappers;
using Lombard.Vif.Service.MessageProcessors;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Vif.UnitTests.MessageProcessors
{
    [TestClass]
    public class VifRequestProcessorTests
    {
        private Mock<IExchangePublisher<CreateValueInstructionFileResponse>> publisher;
        private Mock<IRequestSplitter> requestSplitter;
        private Mock<IRequestConverter> requestConverter;
        private Mock<IFileWriter> fileWriter;
        private Mock<IPathHelper> pathHelper;
        private Mock<IVifGenerator> vifGenerator;
        private List<ValidationResult> validationResult;
        
        private readonly CreateValueInstructionFileRequest message;
        private readonly VifFileInfo vifFileInfo;

        public VifRequestProcessorTests()
        {
            message = new CreateValueInstructionFileRequest
            {
                jobIdentifier = "neod-3f94ff54-1993-4676-853f-b7f0befed24h",
                sequenceNumber = 8356,
                state = 3,
                businessDate = new System.DateTime(2015, 05, 20, 00, 00, 00, 000),
                captureBsb = "083029",
                entity = "NAB",
                collectingBsb = "082037",
                recordTypeCode = GetRecordTypeValue()
            };

            vifFileInfo = new VifFileInfo(message, new List<VoucherInformation>());
            publisher = new Mock<IExchangePublisher<CreateValueInstructionFileResponse>>();
            requestSplitter = new Mock<IRequestSplitter>();
            requestConverter = new Mock<IRequestConverter>();
            fileWriter = new Mock<IFileWriter>();
            pathHelper = new Mock<IPathHelper>();
            vifGenerator = new Mock<IVifGenerator>();
            validationResult = new List<ValidationResult>();
        }

        private RecordTypeCode[] GetRecordTypeValue()
        {
            RecordTypeCode[] collection = new RecordTypeCode[] 
            {
                new RecordTypeCode{code="C", transactionCode="111" },
                new RecordTypeCode{code="C", transactionCode="222" },
                new RecordTypeCode{code="P", bsb="377872" },
                new RecordTypeCode{code="P", bsb="377873" },
                new RecordTypeCode{code="P", bsb="377874" },
                new RecordTypeCode{code="P", bsb="377876" },
                new RecordTypeCode{code="P", bsb="377877" },
                new RecordTypeCode{code="P", bsb="377878" },
                new RecordTypeCode{code="P", bsb="401795" },
                new RecordTypeCode{code="P", bsb="430327" },
                new RecordTypeCode{code="P", bsb="430328" },
                new RecordTypeCode{code="P", bsb="430329" },
                new RecordTypeCode{code="P", bsb="430330" },
                new RecordTypeCode{code="P", bsb="433687" },
                new RecordTypeCode{code="P", bsb="455701" },
                new RecordTypeCode{code="P", bsb="455702" },
                new RecordTypeCode{code="P", bsb="455703" },
                new RecordTypeCode{code="P", bsb="455704" },
                new RecordTypeCode{code="P", bsb="471527" },
                new RecordTypeCode{code="P", bsb="490292" },
                new RecordTypeCode{code="P", bsb="490289" },
                new RecordTypeCode{code="P", bsb="531355" },
                new RecordTypeCode{code="P", bsb="531356" },
                new RecordTypeCode{code="P", bsb="531357" },
                new RecordTypeCode{code="P", bsb="531358" },
                new RecordTypeCode{code="P", bsb="531359" },
                new RecordTypeCode{code="P", bsb="552061" },
                new RecordTypeCode{code="P", bsb="555001" },
                new RecordTypeCode{code="P", bsb="556733" },
                new RecordTypeCode{code="P", bsb="558388" }
            };
            return collection;
        }

        [TestMethod]
        public void ProcessAsync_RequestConverterResponseFailed_ThrowsException()
        {
            VifRequestProcessor vifRequestProcessor = GetVifRequestProcessor();

            pathHelper
                .Setup(x => x.GetVifPath(It.IsAny<string>()))
                .Returns(ValidatedResponse<string>.Success("somePath"));

            requestSplitter
                .Setup(r => r.Map(message))
                .Returns(ValidatedResponse<VifFileInfo>.Success(vifFileInfo));

            validationResult.Add(new ValidationResult("SomeErrorMessage"));

            requestConverter
                .Setup(r => r.Map(It.IsAny<VifFileInfo>()))
                .Returns(ValidatedResponse<IVifGenerator>.Failure(validationResult));

            try
            {
                vifRequestProcessor.ProcessAsync(new CancellationToken(), "SomeCorrelationID", "SomeRoutingKey").Wait();

                Assert.Fail("Expected Exception");
            }
            catch (AggregateException ae)
            {
                var invalidOperationException = ae.InnerExceptions.Single();

                Assert.IsInstanceOfType(invalidOperationException, typeof(InvalidOperationException));

                Assert.IsTrue(invalidOperationException.Message.StartsWith("SomeErrorMessage"));
            }
        }

        [TestMethod]
        public void ProcessAsync_RequestConverterThrowException_ThrowsException()
        {
            VifRequestProcessor vifRequestProcessor = GetVifRequestProcessor();

            pathHelper
                .Setup(x => x.GetVifPath(It.IsAny<string>()))
                .Returns(ValidatedResponse<string>.Success("somePath"));

            requestSplitter
                .Setup(r => r.Map(message))
                .Returns(ValidatedResponse<VifFileInfo>.Success(vifFileInfo));

            requestConverter
                .Setup(q => q.Map(It.IsAny<VifFileInfo>()))
                .Throws(new Exception());

            try
            {
                vifRequestProcessor.ProcessAsync(new CancellationToken(), "SomeCorrelationID", "SomeRoutingKey").Wait();

                Assert.Fail("Expected Exception");
            }
            catch (AggregateException ae)
            {
                var exception = ae.InnerExceptions.Single();

                Assert.IsInstanceOfType(exception, typeof(Exception));
            }

            requestConverter.VerifyAll();
        }

        [TestMethod]
        public void ProcessAsync_InvalidSystemPath_ThrowsException()
        {
            VifRequestProcessor vifRequestProcessor = GetVifRequestProcessor();

            validationResult.Add(new ValidationResult("PathDoesNotExist"));

            pathHelper
                .Setup(y => y.GetVifPath(message.jobIdentifier))
                .Returns(ValidatedResponse<string>.Failure(validationResult));

            try
            {
                vifRequestProcessor.ProcessAsync(new CancellationToken(), "SomeCorrelationID", "SomeRoutingKey").Wait();

                Assert.Fail("Expected Exception");
            }
            catch (AggregateException ae)
            {
                var invalidOperationException = ae.InnerExceptions.Single();

                Assert.IsInstanceOfType(invalidOperationException, typeof(InvalidOperationException));

                Assert.IsTrue(invalidOperationException.Message.StartsWith("PathDoesNotExist"));
            }
        }

        [TestMethod]
        public void ProcessAsync_HappyPath_ShouldPublishResponseToRabbitMQ()
        {
            var processor = GetVifRequestProcessor();

            pathHelper
                .Setup(x => x.GetVifPath(It.IsAny<string>()))
                .Returns(ValidatedResponse<string>.Success("somePath"));

            requestSplitter
                .Setup(r => r.Map(message))
                .Returns(ValidatedResponse<VifFileInfo>.Success(vifFileInfo));

            requestConverter
                .Setup(x => x.Map(It.IsAny<VifFileInfo>()))
                .Returns(ValidatedResponse<IVifGenerator>.Success(vifGenerator.Object));

            vifGenerator
                .Setup(x => x.GenerateVif())
                .Returns("fakeContents");

            fileWriter
                .Setup(x => x.WriteToFile("somePath", It.IsAny<string>(), "fakeContents"))
                .Returns("someFilenameWithPath");

            processor.ProcessAsync(new CancellationToken(), "someCorrelationId", It.IsAny<string>()).Wait();

            publisher
                .Verify(x => x.PublishAsync(
                        It.Is<CreateValueInstructionFileResponse>(y =>
                            y.valueInstructionFileFilename == "MO.FXA.VIF.NAB383.D150520.R56"),
                        It.IsAny<string>(),
                        It.IsAny<string>()));

            requestConverter.VerifyAll();
            pathHelper.VerifyAll();
            fileWriter.VerifyAll();
        }

        private VifRequestProcessor GetVifRequestProcessor()
        {
            return new VifRequestProcessor(
                publisher.Object,
                requestSplitter.Object,
                requestConverter.Object,
                fileWriter.Object,
                pathHelper.Object) 
                { Message = message };
        }
    }
}