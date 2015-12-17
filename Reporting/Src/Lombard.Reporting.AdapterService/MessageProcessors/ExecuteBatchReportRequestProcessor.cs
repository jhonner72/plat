namespace Lombard.Reporting.AdapterService.MessageProcessors
{
using System;
using System.Linq;
using System.Threading.Tasks;
using Lombard.Common.FileProcessors;
using Lombard.Common.Queues;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.AdapterService.Utils;
using Serilog;
using Serilog.Context;

    public class ExecuteBatchReportRequestProcessor : IMessageProcessor<ExecuteBatchReportRequest>
    {
        private readonly IExchangePublisher<ExecuteBatchReportResponse> publisher;
        private readonly IReportGeneratorFactory reportGeneratorFactory;
        private readonly IPathHelper pathHelper;

        public ExecuteBatchReportRequestProcessor(IExchangePublisher<ExecuteBatchReportResponse> publisher, IReportGeneratorFactory reportGeneratorFactory, IPathHelper pathHelper)
        {
            this.publisher = publisher;
            this.reportGeneratorFactory = reportGeneratorFactory;
            this.pathHelper = pathHelper;
        }

        public ExecuteBatchReportRequest Message { get; set; }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = this.Message;

            using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
            {
                Log.Information("ProcessAsync : Processing request for jobIdentifier {@jobIdentifier}", request.jobIdentifier);

                try
                {
                    var outputFolderPath = this.GetOutputFolderPath(request);
                    var errorCount = 0;
                    foreach (var reportRequest in request.reports)
                    {
                        Log.Information("ProcessAsync : Render Report Begin {@outputFilename}", reportRequest.outputFilename);

                        ReportType reportType = reportRequest.GetReportType();

                        var reportGenerator = this.reportGeneratorFactory.GetReportGenerator(reportType);
                        var renderReportResponse = reportGenerator.RenderReport(reportRequest, outputFolderPath);

                        if (!renderReportResponse.IsSuccessful)
                        {
                            Log.Information("ProcessAsync : Render Report Failed {@outputFilename}", reportRequest.outputFilename);
                            errorCount++;
                        }
                        else
                        {
                            Log.Information("ProcessAsync : Render Report Success - {@outputFilename}", reportRequest.outputFilename);
                        }

                        Log.Information("ProcessAsync : Render Report End {@outputFilename}", reportRequest.outputFilename);
                    }
                    Log.Information("ProcessAsync : Processing {@totalRequest}, Errored {@totalError}", request.reports.Count().ToString(), errorCount.ToString());
                    if (errorCount != 0)
                    {
                        Log.Information("ProcessAsync : There were some errors in processing the reports.");
                    }
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "ProcessAsync : Error processing request {@request}", request);
                }

                var response = new ExecuteBatchReportResponse();
                    await publisher.PublishAsync(response, correlationId, routingKey);
                Log.Information("ProcessAsync : Responded with {@response} to the response queue", response);
                Log.Information("ProcessAsync : Processed request for jobIdentifier {@jobIdentifier}", request.jobIdentifier);
            }
        }

        private static void ThrowException(ValidatedResponse<string> renderReportResponse)
        {
            var errorMessage = renderReportResponse.ValidationResults.AsString();

            throw new InvalidOperationException(errorMessage);
        }

        private string GetOutputFolderPath(ExecuteBatchReportRequest request)
        {
            var pathHelperResponse = this.pathHelper.GetReportsPath(request.jobIdentifier);

            if (!pathHelperResponse.IsSuccessful)
            {
                var errorMessage = string.Format(
                    "{0} : {1}",
                    "Error retrieving reports path",
                    pathHelperResponse.ValidationResults.AsString());

                throw new Exception(errorMessage);
            }

            return pathHelperResponse.Result;
        }
    }
}
