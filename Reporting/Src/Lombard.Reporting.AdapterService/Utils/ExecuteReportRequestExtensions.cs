namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;

    public static class ExecuteReportRequestExtensions
    {
        public static bool IsAllItemsReport(this ExecuteReportRequest request)
        {
            if (!string.IsNullOrWhiteSpace(request.reportName) &&
                (request.reportName.Equals("NAB_All_Items_Report", StringComparison.OrdinalIgnoreCase) ||
                 request.reportName.Equals("BQL_All_Items_Report", StringComparison.OrdinalIgnoreCase)))
            {
                return true;
            }

            return false;
        }

        public static bool IsLockedBoxCreditCardReport(this ExecuteReportRequest request)
        {
            if (!string.IsNullOrWhiteSpace(request.reportName) && request.reportName.Equals("NAB_Locked_Box_Extract_VR3", StringComparison.OrdinalIgnoreCase))
            {
                return true;
            }

            return false;
        }

        public static ReportType GetReportType(this ExecuteReportRequest request)
        {
            if (request.IsAllItemsReport())
            {
                return ReportType.AllItems;
            }
            else if (request.IsLockedBoxCreditCardReport())
            { 
                return ReportType.LockedBoxCreditCard; 
            }
            else
            { 
                return ReportType.SSRS; 
            }
        }

        public static string GetBankCode(this ExecuteReportRequest reportRequest)
        {
            string bankCode = string.Empty;

            if ("NAB_All_Items_Report".Equals(reportRequest.reportName, StringComparison.OrdinalIgnoreCase))
            {
                bankCode = "NAB";
            }
            else if ("BQL_All_Items_Report".Equals(reportRequest.reportName, StringComparison.OrdinalIgnoreCase))
            {
                bankCode = "BQL";
            }

            return bankCode;
        }

        public static bool IsBql(this ExecuteReportRequest reportRequest)
        {
            if ("BQL_All_Items_Report".Equals(reportRequest.reportName, StringComparison.OrdinalIgnoreCase))
            {
                return true;
            }

            return false;
        }
    }
}
