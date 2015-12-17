using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Lombard.Common.FileProcessors;
using Lombard.Ingestion.Data.Domain;
using Lombard.Ingestion.Data.Repository;
using Lombard.Ingestion.Service.Configurations;
using Lombard.Ingestion.Service.Helpers;
using Lombard.Ingestion.Service.Mappers;
using Lombard.Ingestion.Service.Models;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Ingestion.Service.Workers.Tests
{
    [TestClass()]
    public class BatchAuditIngestionWorkerTests
    {
        [TestMethod()]
        public void Process_WhenContainsNoFile_ThenNoFileIsImported()
        {
            var repository = new Mock<IIngestionRepository>();
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var fileHelper = new Mock<FileHelper>();
            var batchAuditRecordMapper = new Mock<IBatchAuditRecordMapper>();

            fileHelper.Setup(f => f.GetBatchAuditFiles(It.IsAny<string>())).Returns(new string[] { });

            var worker = new BatchAuditIngestionWorker(repository.Object, configuration.Object, fileHelper.Object, batchAuditRecordMapper.Object);

            worker.Process();

            repository.VerifyAll();
            repository.Verify(x => x.Add(It.IsAny<RefBatchAudit>()), Times.Never);
            repository.Verify(x => x.SaveChanges(), Times.Never);
        }

        [TestMethod()]
        public void Process_WhenContains1File_Then1RecordIsAddedAndSaved()
        {
            var repository = new Mock<IIngestionRepository>();
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var fileHelper = new Mock<FileHelper>();
            var batchAuditRecordMapper = new Mock<IBatchAuditRecordMapper>();

            fileHelper.Setup(f => f.GetBatchAuditFiles(It.IsAny<string>())).Returns(new string[] { "test" });
            batchAuditRecordMapper.Setup(x => x.Map(It.IsAny<BatchAuditFile>())).Returns(
                ValidatedResponse<RefBatchAudit>.Success(new RefBatchAudit()));

            var worker = new BatchAuditIngestionWorker(repository.Object, configuration.Object, fileHelper.Object, batchAuditRecordMapper.Object);

            worker.Process();

            repository.VerifyAll();
            repository.Verify(x => x.Add(It.IsAny<RefBatchAudit>()), Times.Once);
            repository.Verify(x => x.SaveChanges(), Times.Once);
        }

        [TestMethod()]
        public void Process_WhenContains1FileWithNoRecords_ThenALogIsCreatedAndNoRecordsIsAdded()
        {
            var repository = new Mock<IIngestionRepository>();
            var configuration = new Mock<IIngestionServiceConfiguration>();
            var fileHelper = new Mock<FileHelper>();
            var batchAuditRecordMapper = new Mock<IBatchAuditRecordMapper>();

            fileHelper.Setup(f => f.GetBatchAuditFiles(It.IsAny<string>())).Returns(new string[] { "test" });
            batchAuditRecordMapper.Setup(x => x.Map(It.IsAny<BatchAuditFile>()))
                .Returns(ValidatedResponse<RefBatchAudit>.Failure(
                    new List<ValidationResult>()));

            var worker = new BatchAuditIngestionWorker(repository.Object, configuration.Object, fileHelper.Object, batchAuditRecordMapper.Object);

            worker.Process();

            repository.VerifyAll();
            repository.Verify(x => x.Add(It.IsAny<RefBatchAudit>()), Times.Never);
            repository.Verify(x => x.SaveChanges(), Times.Never);
        }
    }
}