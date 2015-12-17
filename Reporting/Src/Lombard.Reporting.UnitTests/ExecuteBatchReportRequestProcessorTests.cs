using System.Threading;
using Lombard.Common.FileProcessors;
using Lombard.Common.Queues;
using Lombard.Reporting.AdapterService.MessageProcessors;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.AdapterService.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Reporting.UnitTests
{
    [TestClass]
    public class ExecuteBatchReportRequestProcessorTests
    {
        private readonly Mock<IExchangePublisher<ExecuteBatchReportResponse>> publisher;
        private readonly Mock<IReportGeneratorFactory> reportGeneratorFactory;
        private readonly Mock<IReportGenerator> reportGenerator;
        private readonly Mock<IPathHelper> pathHelper;

        public ExecuteBatchReportRequestProcessorTests()
        {
            publisher = new Mock<IExchangePublisher<ExecuteBatchReportResponse>>();
            reportGeneratorFactory = new Mock<IReportGeneratorFactory>();
            reportGenerator = new Mock<IReportGenerator>();
            pathHelper = new Mock<IPathHelper>();
        }

        [TestMethod]
        public void ProcessAsync_HappyPath_ShouldPublishResponse()
        {
            var tempRequestProcessor = GetTempRequestProcessor();

            pathHelper
                .Setup(x => x.GetReportsPath("someJobId"))
                .Returns(ValidatedResponse<string>.Success("someFolderPath"));

            reportGenerator
                .Setup(x => x.RenderReport(It.IsAny<ExecuteReportRequest>(), "someFolderPath"))
                .Returns(ValidatedResponse<string>.Success("someReportOutputFileName"));

            reportGeneratorFactory
                .Setup(x => x.GetReportGenerator(It.IsAny<ReportType>()))
                .Returns(reportGenerator.Object);
            
            tempRequestProcessor.ProcessAsync(new CancellationToken(), "someCorrelationId", "routingKey").Wait();

            publisher.Verify(p =>p.PublishAsync(It.IsAny<ExecuteBatchReportResponse>(), "someCorrelationId", "routingKey"));
        }

        private ExecuteBatchReportRequestProcessor GetTempRequestProcessor()
        {
            return new ExecuteBatchReportRequestProcessor(
                publisher.Object,
                reportGeneratorFactory.Object, 
                pathHelper.Object
            )
            {
                Message = new ExecuteBatchReportRequest
                {
                    jobIdentifier = "someJobId", 
                    reports = new [] { new ExecuteReportRequest() }
                }
            };
        }
    }
}