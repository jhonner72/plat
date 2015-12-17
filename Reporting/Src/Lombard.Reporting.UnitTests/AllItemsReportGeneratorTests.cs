using Lombard.Reporting.AdapterService.Utils;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Moq;
using System.IO;
using System.IO.Abstractions;
using Lombard.Reporting.Data;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using Lombard.Reporting.Data.Domain;

namespace Lombard.Reporting.UnitTests
{
    [TestClass]
    public class AllItemsReportGeneratorTests
    {
        private Mock<IFileSystem> fileSystem;
        private Mock<AllItemsReportHelper> reportHelper;
        private Mock<ReportingRepository> repository;
        private ExecuteReportRequest reportRequest;

        [TestInitialize]
        public void Initialize()
        {
            fileSystem = new Mock<IFileSystem>();
            reportHelper = new Mock<AllItemsReportHelper>(fileSystem.Object);
            repository = new Mock<ReportingRepository>(null);
            reportRequest = new ExecuteReportRequest()
            {
                outputFilename = "outputFilename",
                parameters = new[] { 
                    new Parameter { name = "processdate", value = "2018-05-07" }, 
                    new Parameter { name = "processingState", value = "VIC" } }
            };
        }

        [TestMethod]
        public void RenderReport_WhenVouchersAreEmpty_PrintEmptyReportIsCalledOnce()
        {
            repository.Setup(s => s.GetAllItems(It.IsAny<DateTime>(), It.IsAny<string>(), It.IsAny<string>()))
                .Returns(new List<AllItem>());
            fileSystem.Setup(s => s.File.ReadAllText(It.IsAny<string>()));

            var reportGenerator = new AllItemsReportGenerator(fileSystem.Object, repository.Object, reportHelper.Object);

            var response = reportGenerator.RenderReport(reportRequest, "outputFolderPath");

            Assert.IsTrue(response.IsSuccessful);

            reportHelper.Verify(p => p.PrintEmptyReport(
                It.IsAny<StreamWriter>(), It.IsAny<DateTime>(), It.IsAny<string>()), Times.Once);
        }

        [TestMethod]
        public void RenderReport_WhenContain19VouchersInSameTransaction_Print2HeaderAnd19Row()
        {
            // Arrange
            var items = new List<AllItem>();
            for (int i = 0; i < 19; i++)
            {
                items.Add(new AllItem() { TransactionNumber = "001" });
            }

            repository.Setup(s => s.GetAllItems(It.IsAny<DateTime>(), It.IsAny<string>(), It.IsAny<string>()))
                .Returns(items);
            fileSystem.Setup(s => s.File.ReadAllText(It.IsAny<string>()));

            reportHelper.Setup(s => s.IsStartNewPage(0)).Returns(true);
            reportHelper.Setup(s => s.IsStartNewPage(18)).Returns(true);

            var reportGenerator = new AllItemsReportGenerator(fileSystem.Object, repository.Object, reportHelper.Object);

            // Act
            var response = reportGenerator.RenderReport(reportRequest, "outputFolderPath");

            // Assert
            Assert.IsTrue(response.IsSuccessful);

            reportHelper.Verify(p => p.PrintPageHeader(It.IsAny<StreamWriter>(), It.IsAny<DateTime>(), It.IsAny<DateTime>(),
                It.IsAny<string>(), It.IsAny<string>(), It.IsAny<int>()), Times.Exactly(2));

            reportHelper.Verify(p => p.PrintReportRow(It.IsAny<StreamWriter>(), It.IsAny<AllItem>()), Times.Exactly(19));
        }
    }
}
