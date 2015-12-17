using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO.Abstractions;
using Lombard.Reporting.AdapterService.Configuration;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.AdapterService.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;
using Lombard.Reporting.IntegrationTests.Hooks;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Reporting.IntegrationTests.Steps
{
    [Binding]
    public class GenerateReportSteps
    {
        [When(@"a request is added to the queue with jobIdentifier (.*) and the following info")]
        public void WhenATempRequestIsAddedToTheQueueWithJobIdentifier(string jobIdentifier, Table reports)
        {
            Assert.IsTrue(!string.IsNullOrEmpty(jobIdentifier));

            var requestHelper = reports.CreateSet<RequestHelper>();

            var allReportRequests = ExtractReportRequests(requestHelper);

            var requestMessage = new ExecuteBatchReportRequest
            {
                jobIdentifier = jobIdentifier,
                reports = allReportRequests
            };

            DeleteAnyExistingReportsForThisJobIdentifier(jobIdentifier);

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            OutboundServiceBus.Publish(requestMessage);
        }

        [Then(@"a response will be added to the response queue")]
        public void ThenATempResponseWillBeAddedToTheResponseQueue()
        {
            const int timeoutInSeconds = 120;

            var task = OutboundServiceBus.GetSingleResponseAsync(timeoutInSeconds);
            
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");
        }

        private static ExecuteReportRequest[] ExtractReportRequests(IEnumerable<RequestHelper> requestHelper)
        {
            var allReportRequests = new List<ExecuteReportRequest>();

            foreach (var requestInfo in requestHelper)
            {
                var parameters = ExtractParameters(requestInfo);

                FormatType formatType;
                if (!Enum.TryParse(requestInfo.OutputFormatType, out formatType))
                {
                    Assert.Fail("OutputFormatType not recognized");
                }

                var executeReportRequest = new ExecuteReportRequest
                {
                    reportName = requestInfo.ReportName,
                    outputFilename = requestInfo.OutputFilename,
                    outputFormatType = formatType,
                    parameters = parameters
                };

                allReportRequests.Add(executeReportRequest);
            }

            return allReportRequests.ToArray();
        }

        private static Parameter[] ExtractParameters(RequestHelper requestInfo)
        {
            var parameters = new List<Parameter>();
            if (!string.IsNullOrEmpty(requestInfo.Parameter1Key))
            {
                parameters.Add(new Parameter
                {
                    name = requestInfo.Parameter1Key,
                    value = requestInfo.Parameter1Value
                });
            }

            if (!string.IsNullOrEmpty(requestInfo.Parameter2Key))
            {
                parameters.Add(new Parameter
                {
                    name = requestInfo.Parameter2Key,
                    value = requestInfo.Parameter2Value
                });
            }
            return parameters.ToArray();
        }

        private static void DeleteAnyExistingReportsForThisJobIdentifier(string jobIdentifier)
        {
            IFileSystem fileSystem = new FileSystem();

            IPathHelper pathHelper = new PathHelper(fileSystem, new TestReportConfiguration());

            var reportsPathResponse = pathHelper.GetReportsPath(jobIdentifier);

            Assert.IsTrue(reportsPathResponse.IsSuccessful, "Cannot determine output path for reports");

            var folder = reportsPathResponse.Result;

            foreach (var existingReportFile in fileSystem.Directory.EnumerateFiles(folder))
            {
                Console.WriteLine("Deleting existing report file before running test: {0}", existingReportFile);
                fileSystem.File.Delete(existingReportFile);
            }
        }
    }

    public class TestReportConfiguration : IReportingConfiguration
    {
        public string BitLockerLocation 
        {
            get { return ConfigurationManager.AppSettings["reporting:BitLockerLocation"]; }
            set { throw new InvalidOperationException(); }
        }

        public string ReportExecution2005Reference
        {
            get { throw new InvalidOperationException(); }
            set { throw new InvalidOperationException(); }
        }

        public string ReportService2010Reference
        {
            get { throw new InvalidOperationException(); }
            set { throw new InvalidOperationException(); }
        }
    }

    public class RequestHelper
    {
        public string ReportName { get; set; }
        public string OutputFilename { get; set; }
        public string OutputFormatType { get; set; }
        public string Parameter1Key { get; set; }
        public string Parameter1Value { get; set; }
        public string Parameter2Key { get; set; }
        public string Parameter2Value { get; set; }
    }
}
