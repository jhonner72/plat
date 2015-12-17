namespace Lombard.Reporting.AdapterService.Utils
{
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.ServiceModel;
using Lombard.Common.FileProcessors;
using Lombard.Reporting.AdapterService.Configuration;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.AdapterService.RE2005;
using Lombard.Reporting.AdapterService.RS2010;
using Serilog;
using ParameterValue = Lombard.Reporting.AdapterService.RE2005.ParameterValue;

    public class SSRSReportGenerator : IReportGenerator
    {
        private readonly EndpointAddress reportService2010Reference;
        private readonly EndpointAddress reportExecution2005Reference;
        private readonly IFileWriter fileWriter;

        private RE2005.TrustedUserHeader trusteduserHeader;
        private RE2005.ServerInfoHeader serviceInfo;
        private ExecutionHeader execHeader;
        private RE2005.ExecutionInfo execInfo;
        
        public SSRSReportGenerator(IReportingConfiguration reportingConfiguration, IFileWriter fileWriter)
        {
            this.trusteduserHeader = new RE2005.TrustedUserHeader();
            this.execHeader = new ExecutionHeader();
            this.serviceInfo = new RE2005.ServerInfoHeader();
            this.execInfo = new ExecutionInfo();
                        
            this.fileWriter = fileWriter;

            this.reportService2010Reference = new EndpointAddress(reportingConfiguration.ReportService2010Reference);
            this.reportExecution2005Reference = new EndpointAddress(reportingConfiguration.ReportExecution2005Reference);
        }

        public ValidatedResponse<string> RenderReport(ExecuteReportRequest reportRequest, string outputFolderPath)
        {
            if (string.IsNullOrEmpty(reportRequest.outputFilename))
            {
                Log.Debug("RenderReport : Report outputFilename must not be null or empty");
                return ValidatedResponseHelper.Failure<string>("Report OutputFilename must not be null or empty");
            }

            Log.Information("RenderReport : [{@outputFilename}] Requesting report object {@reportName}", reportRequest.outputFilename, reportRequest.reportName);

            var reportExecutionWatch = Stopwatch.StartNew();

            var parameters = this.GetParameters(reportRequest);
            var reportObject = this.GetReportObjectFromSSRS(reportRequest.reportName);

            if (!reportObject.IsSuccessful)
            {
                Log.Error("RenderReport : [{@outputFilename}] Error retrieve report {@reportName}", reportRequest.outputFilename, reportRequest.reportName);
                return ValidatedResponseHelper.Failure<string>("Error retrieve report {0} : {1}", reportRequest.reportName, reportObject.ValidationResults.AsString());
            }

            var fileNameAndPath = string.Empty;
            try
            {
                var renderFormat = RenderFormat.FromValue(reportRequest.outputFormatType.ToString()); // also throws ArgumentException if can't be parsed
                
                // Set parameters into SoapClient object.
                var rsExec = new ReportExecutionServiceSoapClient();
                rsExec.Endpoint.Address = this.reportExecution2005Reference;
                rsExec.ClientCredentials.Windows.AllowedImpersonationLevel = System.Security.Principal.TokenImpersonationLevel.Impersonation;
                rsExec.ClientCredentials.Windows.ClientCredential = null;

                Log.Debug("RenderReport : [{@outputFilename}] Loading report {@report}", reportRequest.outputFilename, reportObject.Result.Path);

                rsExec.LoadReport(this.trusteduserHeader, reportObject.Result.Path, null, out this.serviceInfo, out this.execInfo);

                Log.Debug("RenderReport : [{@outputFilename}] Report loaded {@report} with Execution ID {@executionID}", reportRequest.outputFilename, reportObject.Result.Path, this.execInfo.ExecutionID);

                this.execHeader.ExecutionID = this.execInfo.ExecutionID;

                // Set the parameters.
                rsExec.SetExecutionParameters(this.execHeader, this.trusteduserHeader, parameters, "EN-US", out this.execInfo);

                Log.Debug("RenderReport : [{@outputFilename}] Parameters {@param} was set.", reportRequest.outputFilename, parameters);

                byte[] result;
                string extension;
                string encoding;
                string mimeType;
                RE2005.Warning[] warnings;
                string[] streamIDs;

                Log.Debug("RenderReport : [{@outputFilename}] Rendering report...", reportRequest.outputFilename);

                // Render the report.
                rsExec.Render(this.execHeader, null, renderFormat.SSRSValue, null, out result, out extension, out mimeType, out encoding, out warnings, out streamIDs);

                Log.Debug("RenderReport : [{@outputFilename}] Saving report to file...", reportRequest.outputFilename);

                fileNameAndPath = this.fileWriter.WriteToFile(outputFolderPath, reportRequest.outputFilename, result);

                Log.Information("RenderReport : [{@outputFilename}] Report saved in {@filenameandPath}", reportRequest.outputFilename, fileNameAndPath);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RenderReport: Something went wrong {@stackTrace}", ex.ToString());
                return ValidatedResponseHelper.Failure<string>("Error {0}", ex.ToString());
            }
            finally
            {
                reportExecutionWatch.Stop();

                Log.Information("RenderReport : [{@outputFilename}] Finish generating report in {@time} seconds", reportRequest.outputFilename, reportExecutionWatch.ElapsedMilliseconds / 1000.0);
            }

            return ValidatedResponse<string>.Success(fileNameAndPath);
        }

        private ParameterValue[] GetParameters(ExecuteReportRequest reportRequest)
        {
            var parameters = new List<ParameterValue>();
            foreach (var requestParam in reportRequest.parameters)
            {
                parameters.Add(new ParameterValue
                {
                    Name = requestParam.name,
                    Value = requestParam.value
                });
            }

            return parameters.ToArray();
        }

        private ValidatedResponse<CatalogItem> GetReportObjectFromSSRS(string reportName)
        {
            CatalogItem catalogItem;
            var client = new ReportingService2010SoapClient();

            try
            {
                // Get EndPoint Address from AppSettings.
                Log.Information("GetReportObjectFromSSRS : Getting the report object {@reportName}", reportName);
                var trustedUserHeader = new RS2010.TrustedUserHeader();
                client.Endpoint.Address = this.reportService2010Reference;
                client.ClientCredentials.Windows.AllowedImpersonationLevel = System.Security.Principal.TokenImpersonationLevel.Impersonation;
                client.ClientCredentials.Windows.ClientCredential = null;
                client.Open();

                Log.Information("GetReportObjectFromSSRS : Reporting Service 2010 Soap Client Uri -  {@uri}", this.reportService2010Reference.Uri);
                //// I need to list of children of a specified folder.
                CatalogItem[] items;
                client.ListChildren(trustedUserHeader, "/", true, out items);

                catalogItem = (from CatalogItem ci in items
                         where ci.TypeName.Contains("Report") && ci.Name.Equals(reportName, StringComparison.OrdinalIgnoreCase)
                         select ci).FirstOrDefault();

                Log.Information("GetReportObjectFromSSRS : Catalog Item {@catalogItem} was successfully obtained", catalogItem.Name);

                Log.Information("GetReportObjectFromSSRS : Successfully obtained Report Object"); 
                return ValidatedResponse<CatalogItem>.Success(catalogItem);

            }
            catch (Exception ex)
            {
                Log.Error(ex, "GetReportObjectFromSSRS: Something went wrong in getting the SSRS Report Object - {@reportname}", reportName);
                return ValidatedResponseHelper.Failure<CatalogItem>("Error {0}", ex.ToString());
            }
            finally
            {
                client.Close();
            }

        }
    }
}
