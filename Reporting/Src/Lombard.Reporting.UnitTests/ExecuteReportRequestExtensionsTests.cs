using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.AdapterService.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Reporting.UnitTests
{
    [TestClass]
    public class ExecuteReportRequestExtensionsTests
    {
        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsNabAllItemReport_ThenReturnTrue()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "NAB_All_Items_Report"
            };
            
            bool isAllItemReport = request.IsAllItemsReport();

            Assert.IsTrue(isAllItemReport);
        }

        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsBqlAllItemReport_ThenReturnTrue()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "BQL_All_Items_Report"
            };

            bool isAllItemReport = request.IsAllItemsReport();

            Assert.IsTrue(isAllItemReport);
        }

        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsDailyVifReport_ThenReturnFalse()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "NAB_Daily_VIF_Transmission_Report"
            };

            bool isAllItemReport = request.IsAllItemsReport();

            Assert.IsFalse(isAllItemReport);
        }

        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsNabAllItemReport_ThenBankCodeShouldBeNab()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "NAB_All_Items_Report"
            };

            var bankCode = request.GetBankCode();

            Assert.AreEqual("NAB", bankCode);
        }

        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsBqlAllItemReport_ThenBankCodeShouldBeBql()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "BQL_All_Items_Report"
            };

            var bankCode = request.GetBankCode();

            Assert.AreEqual("BQL", bankCode);
        }

        [TestMethod]
        public void GivenAnExecuteReportRequest_WhenReportNameIsNotNabOrBqlAllItemReport_ThenBankCodeShouldBeEmpty()
        {
            ExecuteReportRequest request = new ExecuteReportRequest()
            {
                reportName = "Some_Other_Report"
            };

            var bankCode = request.GetBankCode();

            Assert.AreEqual(string.Empty, bankCode);
        }
    }
}
