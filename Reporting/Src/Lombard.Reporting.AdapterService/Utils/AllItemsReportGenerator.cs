namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using System.Diagnostics;
    using System.IO;
    using System.IO.Abstractions;
    using System.Linq;
    using Lombard.Common.FileProcessors;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;
    using Lombard.Reporting.Data;
    using Serilog;

    public class AllItemsReportGenerator : IReportGenerator
    {
        private readonly IFileSystem fileSystem;
        private readonly ReportingRepository repository;
        private readonly AllItemsReportHelper reportHelper;

        public AllItemsReportGenerator(IFileSystem fileSystem, ReportingRepository repository, AllItemsReportHelper reportHelper)
        {
            this.fileSystem = fileSystem;
            this.repository = repository;
            this.reportHelper = reportHelper;
        }

        public ValidatedResponse<string> RenderReport(ExecuteReportRequest reportRequest, string outputFolderPath)
        {
            if (string.IsNullOrEmpty(reportRequest.outputFilename))
            {
                Log.Debug("RenderReport : Report outputFilename must not be null or empty");
                return ValidatedResponseHelper.Failure<string>("Report OutputFilename must not be null or empty");
            }

            var reportExecutionWatch = Stopwatch.StartNew();

            Log.Debug("{@ReportRequest}", reportRequest);

            var fileNameAndPath = Path.Combine(outputFolderPath, reportRequest.outputFilename);
            try
            {
                string bankCode = reportRequest.GetBankCode();
                bool isBql = reportRequest.IsBql();

                var processDateParam = reportRequest.parameters.GetValue("processdate");
                var processStateParam = reportRequest.parameters.GetValue("processingstate", !isBql);

                DateTime processDate;
                if (!DateTime.TryParse(processDateParam.value, out processDate))
                {
                    Log.Debug("RenderReport : Invalid DateTime format '{0}'", processDateParam.value);
                    return ValidatedResponseHelper.Failure<string>("Invalid DateTime format '{0}'", processDateParam.value);
                }

                if (isBql)
                {
                    processStateParam.value = "QLD";
                }

                var allItems = this.repository.GetAllItems(processDate, processStateParam.value, bankCode);

                DateTime runDate = DateTime.Now;

                using (var streamWriter = this.fileSystem.File.CreateText(fileNameAndPath))
                {
                    if (allItems.Any())
                    {
                        Log.Information("Found {0} all items for processing.", allItems.Count);

                        string previousBatchNumber = allItems.First().BatchNumber;
                        string previousTransactionNumber = allItems.First().TransactionNumber;
                        int pageRowCount = 0;
                        int pageNumber = 1;

                        for (int i = 0; i < allItems.Count; i++)
                        {
                            string currentBatchNumber = allItems[i].BatchNumber;
                            string currentTransactionNumber = allItems[i].TransactionNumber;

                            if (this.reportHelper.IsNewBatchNumberGroup(previousBatchNumber, currentBatchNumber) ||
                                this.reportHelper.IsNewTransactionGroup(previousTransactionNumber, currentTransactionNumber))
                            {
                                previousBatchNumber = currentBatchNumber;
                                previousTransactionNumber = currentTransactionNumber;

                                if (!this.reportHelper.IsStartNewPage(pageRowCount))
                                {
                                    streamWriter.WriteLine();
                                    pageRowCount++;
                                }
                            }

                            if (this.reportHelper.IsStartNewPage(pageRowCount))
                            {
                                Log.Debug("Starting new page: {0}.", pageNumber);

                                this.reportHelper.PrintPageHeader(streamWriter, processDate, runDate, bankCode, processStateParam.value, pageNumber);

                                pageNumber++;
                                pageRowCount = 0;
                            }

                            this.reportHelper.PrintReportRow(streamWriter, allItems[i]);

                            pageRowCount++;
                        }

                        this.reportHelper.PrintReportFooter(streamWriter, allItems);
                    }
                    else
                    {
                        Log.Information("Found 0 all items for processing.", allItems.Count);

                        this.reportHelper.PrintEmptyReport(streamWriter, processDate, bankCode);
                    }
                }

                Log.Information("RenderReport : Created report at {0}", fileNameAndPath);
            }
            catch (Exception ex)
            {
                Log.Error("RenderReport: Something went wrong {@stackTrace}", ex.ToString());
                return ValidatedResponseHelper.Failure<string>("Error {0}", ex);
            }
            finally
            {
                reportExecutionWatch.Stop();

                Log.Information("RenderReport : [{@outputFilename}] Finish generating report in {@time} seconds", reportRequest.outputFilename, reportExecutionWatch.ElapsedMilliseconds / 1000.0);
            }

            return ValidatedResponse<string>.Success(fileNameAndPath);
        }
    }
}
