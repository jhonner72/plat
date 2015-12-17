using System;
using System.Collections.Generic;
using Lombard.Ingestion.Data.Domain;
using Lombard.Ingestion.Data.Repository;
using Lombard.Ingestion.Service.Configurations;
using Lombard.Ingestion.Service.Helpers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Serilog.Context;

namespace Lombard.Ingestion.Service.Workers.Tests
{
    [TestClass]
    public class EclIngestionWorkerTests
    {
        [TestMethod]
        public void Process_WhenContains1File_Then1FileIsBulkImported()
        {
            LogContext.PermitCrossAppDomainCalls = true;

            var repository = new Mock<BulkIngestionRepository>("connectionString");
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var ingestionHelper = new EclIngestionHelper();
            var fileHelper = new Mock<FileHelper>();

            fileHelper.Setup(f => f.GetFileName(It.IsAny<string>())).Returns("file1.VIC");
            fileHelper.Setup(f => f.GetEclFiles(It.IsAny<string>())).Returns(new string[] { "file1.VIC" });
            fileHelper.Setup(f => f.ReadAllLines(It.IsAny<string>())).Returns(new string[] {
                "A220151110                                                                                                     A",
                "D032820    190475   000402864000000000000000003850006M10/11/15 082037 082265 149000044 06    Z  082265 020388500",
                "Z000001                                                                                                        Z"
            });

            var worker = new EclIngestionWorker(repository.Object, configuration.Object, ingestionHelper, fileHelper.Object);

            worker.Process();

            repository.Verify(v => v.BulkImportEclData(It.IsAny<IList<AusPostEclData>>(), It.IsAny<string>()), Times.Once);
        }

        [ExpectedException(typeof(Exception))]
        [TestMethod]
        public void Process_WhenFileStateIsInvalid_ThenExpectException()
        {
            LogContext.PermitCrossAppDomainCalls = true;

            var repository = new Mock<BulkIngestionRepository>("connectionString");
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var ingestionHelper = new EclIngestionHelper();
            var fileHelper = new Mock<FileHelper>();

            fileHelper.Setup(f => f.GetFileName(It.IsAny<string>())).Returns("file1.NT");
            fileHelper.Setup(f => f.GetEclFiles(It.IsAny<string>())).Returns(new string[] { "file1.NT" });
            fileHelper.Setup(f => f.ReadAllLines(It.IsAny<string>())).Returns(new string[] {
                "A220151110                                                                                                     A",
                "D032820    190475   000402864000000000000000003850006M10/11/15 082037 082265 149000044 06    Z  082265 020388500",
                "Z000001                                                                                                        Z"
            });

            var worker = new EclIngestionWorker(repository.Object, configuration.Object, ingestionHelper, fileHelper.Object);

            worker.Process();
        }

        [ExpectedException(typeof(Exception))]
        [TestMethod]
        public void Process_WhenHeaderIsInvalid_ThenExpectException()
        {
            LogContext.PermitCrossAppDomainCalls = true;

            var repository = new Mock<BulkIngestionRepository>("connectionString");
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var ingestionHelper = new EclIngestionHelper();
            var fileHelper = new Mock<FileHelper>();

            fileHelper.Setup(f => f.GetFileName(It.IsAny<string>())).Returns("file1.VIC");
            fileHelper.Setup(f => f.GetEclFiles(It.IsAny<string>())).Returns(new string[] { "file1.VIC" });
            fileHelper.Setup(f => f.ReadAllLines(It.IsAny<string>())).Returns(new string[] {
                "A2                                                                                                             A",
                "D032820    190475   000402864000000000000000003850006M10/11/15 082037 082265 149000044 06    Z  082265 020388500",
                "Z000001                                                                                                        Z"
            });

            var worker = new EclIngestionWorker(repository.Object, configuration.Object, ingestionHelper, fileHelper.Object);

            worker.Process();
        }

        [ExpectedException(typeof(Exception))]
        [TestMethod]
        public void Process_WhenHeaderNotOnFirstLine_ThenExpectException()
        {
            LogContext.PermitCrossAppDomainCalls = true;

            var repository = new Mock<BulkIngestionRepository>("connectionString");
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var ingestionHelper = new EclIngestionHelper();
            var fileHelper = new Mock<FileHelper>();

            fileHelper.Setup(f => f.GetFileName(It.IsAny<string>())).Returns("file1.VIC");
            fileHelper.Setup(f => f.GetEclFiles(It.IsAny<string>())).Returns(new string[] { "file1.VIC" });
            fileHelper.Setup(f => f.ReadAllLines(It.IsAny<string>())).Returns(new string[] {
                "A220151110                                                                                                     A",
                "D032820    190475   000402864000000000000000003850006M10/11/15 082037 082265 149000044 06    Z  082265 020388500",
                "A220151110                                                                                                     A",
                "Z000001                                                                                                        Z"
            });

            var worker = new EclIngestionWorker(repository.Object, configuration.Object, ingestionHelper, fileHelper.Object);

            worker.Process();
        }

        [ExpectedException(typeof(Exception))]
        [TestMethod]
        public void Process_WhenFirstLineIsNotHeader_ThenExpectException()
        {
            LogContext.PermitCrossAppDomainCalls = true;

            var repository = new Mock<BulkIngestionRepository>("connectionString");
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var ingestionHelper = new EclIngestionHelper();
            var fileHelper = new Mock<FileHelper>();

            fileHelper.Setup(f => f.GetFileName(It.IsAny<string>())).Returns("file1.VIC");
            fileHelper.Setup(f => f.GetEclFiles(It.IsAny<string>())).Returns(new string[] { "file1.VIC" });
            fileHelper.Setup(f => f.ReadAllLines(It.IsAny<string>())).Returns(new string[] {
                "D032820    190475   000402864000000000000000003850006M10/11/15 082037 082265 149000044 06    Z  082265 020388500",
                "A220151110                                                                                                     A",
                "Z000001                                                                                                        Z"
            });

            var worker = new EclIngestionWorker(repository.Object, configuration.Object, ingestionHelper, fileHelper.Object);

            worker.Process();
        }
    }
}
