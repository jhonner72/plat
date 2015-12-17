namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using System.IO.Abstractions;
    using System.Linq;
    using Lombard.Common.FileProcessors;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;
    using Lombard.Reporting.Data;
    using Serilog;

    public class VR3CreditCardReportGenerator : IReportGenerator
    {
        private readonly IFileSystem fileSystem;
        private readonly ReportingRepository repository;
        private readonly VR3CreditCardReportHelper reportHelper;

        private IList<LockedBoxConfig> lockedBoxConfigs = new List<LockedBoxConfig>()
        {
            new LockedBoxConfig { CustomerId = "LML2", DpNumber = "47", WorkSourceCode = "5", WorkSourceName = "(3202) MLC LTD - CARD", IsCreditCard = true }, // MLC Ltd Cards
            new LockedBoxConfig { CustomerId = "LNC1", DpNumber = "66", WorkSourceCode = "2", WorkSourceName = "(3002) NATIONAL CARD SE", IsCreditCard = false }, // National Credit Card Payments
            new LockedBoxConfig { CustomerId = "LRD3", DpNumber = "66", WorkSourceCode = "5", WorkSourceName = "(3302) RDNS HOMECARE", IsCreditCard = true }, // RDNS Homecare - Cards
            new LockedBoxConfig { CustomerId = "LRD4", DpNumber = "66", WorkSourceCode = "5", WorkSourceName = "(3317) RDN OCR - CARD", IsCreditCard = true } // Royal District Nursing Services (RDNS) Limited - Cards
        };

        public VR3CreditCardReportGenerator(IFileSystem fileSystem, ReportingRepository repository, VR3CreditCardReportHelper reportHelper)
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
                var processDateParam = reportRequest.parameters.GetValue("businessdate");
                var lockedBoxParam = reportRequest.parameters.GetValue("lockedbox");

                var lockedBoxConfig = this.lockedBoxConfigs.Single(c => c.CustomerId.Equals(lockedBoxParam.value, StringComparison.OrdinalIgnoreCase));

                DateTime processDate;
                if (!DateTime.TryParse(processDateParam.value, out processDate))
                {
                    Log.Debug("RenderReport : Invalid DateTime format '{0}'", processDateParam.value);
                    return ValidatedResponseHelper.Failure<string>("Invalid DateTime format '{0}'", processDateParam.value);
                }

                var items = this.repository.GetLockedBoxCreditCardItems(processDate, lockedBoxParam.value, lockedBoxConfig.IsCreditCard);

                DateTime runDate = DateTime.Now;

                using (var streamWriter = this.fileSystem.File.CreateText(fileNameAndPath))
                {
                    Log.Information("Found {0} items for processing.", items.Count);

                    if (items.Any())
                    {
                        string previousBatchNumber = items.First().BatchNumber;
                        int pageNumber = 1;

                        int debitCount = 0;
                        int creditCount = 0;
                        int debitRejectCount = 0;
                        int creditRejectCount = 0;
                        decimal batchHeaderAmount = 0m;
                        decimal debitAmount = 0m;
                        decimal creditAmount = 0m;

                        for (int i = 0; i < items.Count; i++)
                        {
                            string currentBatchNumber = items[i].BatchNumber;

                            if (this.reportHelper.IsNewBatchNumberGroup(previousBatchNumber, currentBatchNumber) ||
                                (i == 0))
                            {
                                if (i != 0)
                                {
                                    this.reportHelper.PrintPageFooter(streamWriter, previousBatchNumber, debitCount, creditCount, batchHeaderAmount, debitAmount, creditAmount, debitRejectCount, creditRejectCount);
                                }

                                previousBatchNumber = currentBatchNumber;
                                debitCount = 0;
                                creditCount = 0;
                                batchHeaderAmount = 0m;
                                debitAmount = 0m;
                                creditAmount = 0m;

                                Log.Debug("Starting new page: {0}.", pageNumber);

                                this.reportHelper.PrintPageHeader(streamWriter, processDate, runDate, currentBatchNumber, lockedBoxConfig.WorkSourceCode, lockedBoxConfig.WorkSourceName, items[i].PaymentType, lockedBoxConfig.DpNumber, pageNumber, lockedBoxConfig.IsCreditCard);

                                pageNumber++;
                            }

                            this.reportHelper.PrintReportRow(streamWriter, items[i], lockedBoxConfig.IsCreditCard);

                            this.reportHelper.CalculateReportSummary(items[i], ref debitCount, ref creditCount, ref batchHeaderAmount, ref debitAmount, ref creditAmount);

                            if (i == (items.Count - 1))
                            {
                                // print footer for last item
                                this.reportHelper.PrintPageFooter(streamWriter, previousBatchNumber, debitCount, creditCount, batchHeaderAmount, debitAmount, creditAmount, debitRejectCount, creditRejectCount);
                            }
                        }

                        this.reportHelper.PrintReportFooter(streamWriter);
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