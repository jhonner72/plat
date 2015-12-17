using System;
using System.Threading;
using Moq;
using Lombard.Common.Queues;
using Lombard.Common.FileProcessors;
using System.Collections.Generic;
using Lombard.Vif.Acknowledgement.Service.MessageProcessors;
using Lombard.Vif.Acknowledgement.Service.Domain;
using Lombard.Vif.Acknowledgement.Service.Mappers;
using Lombard.Vif.Service.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Vif.Acknowledgement.Service.UnitTests
{
    [TestClass]
    public class VifAckRequestProcessorUnitTest
    {
        private Mock<IRequestSplitter> requestSplitter;
        private Mock<IRequestConverter> requestConverter;
        private Mock<IAcknowledgmentCode> AckCode;
        private Mock<IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse>> publisher;
        private readonly ProcessValueInstructionFileAcknowledgmentRequest message;
        private readonly ProcessValueInstructionFileAcknowledgmentResponse response;

        public VifAckRequestProcessorUnitTest()
        {
            message = new ProcessValueInstructionFileAcknowledgmentRequest
            {
                jobIdentifier = "neod-3f94ff54-1993-4676-853f-b7f0befed24h"
            };

            publisher = new Mock<IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse>>();
            requestSplitter = new Mock<IRequestSplitter>();
            AckCode = new Mock<IAcknowledgmentCode>();
            requestConverter = new Mock<IRequestConverter>();
        }

        [TestMethod]
        public void ProcessAsync_Acknowledgement()
        {
            
            VifAckRequestProcessor ackProcessor = this.GetVifRequestProcessor();
            requestSplitter
                .Setup(x => x.Map(message))
                .Returns(ValidatedResponse<IAcknowledgmentCode>.Success(AckCode.Object));

            
            requestConverter
                .Setup(y => y.Map(AckCode.Object))
                .Returns(ValidatedResponse<ProcessValueInstructionFileAcknowledgmentResponse>.Success(response));

            ackProcessor.ProcessAsync(new CancellationToken(), "SomeCorrelationID", "routingKey").Wait();

            publisher.Verify(p => p.PublishAsync(It.IsAny<ProcessValueInstructionFileAcknowledgmentResponse>(), "SomeCorrelationID", "routingKey"));
                
        }

        private VifAckRequestProcessor GetVifRequestProcessor()
        {
            return new VifAckRequestProcessor(
                publisher.Object,
                requestSplitter.Object,
                requestConverter.Object) 
                { Message = message };
        }
    }
}
