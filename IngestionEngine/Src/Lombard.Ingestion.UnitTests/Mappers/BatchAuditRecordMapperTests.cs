using Microsoft.VisualStudio.TestTools.UnitTesting;
using Lombard.Ingestion.Service.Mappers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO.Abstractions;
using Moq;
using Lombard.Ingestion.Service.Models;

namespace Lombard.Ingestion.Service.Mappers.Tests
{
    [TestClass()]
    public class BatchAuditRecordMapperTests
    {
        [TestMethod()]
        public void Map_WhenAValidRecordIsFound_ThenResultIsSuccessful()
        {
            var batchAuditFile = new BatchAuditFile()
            {
                Records = new string[]
                {
                    "[Reconciliation]",
                    "MachineNumber=830",
                    "BatchNumber=68300305",
                    "ProcessingDate=20151211",
                    "TimeStamp=2015-12-16 14:11:08",
                    "WorkType=NabPodChq",
                    "RecordCount=11",
                    "FirstDRN=183000005",
                    "LastDRN=183000015",
                    "FileName=OUTCLEARINGSPKG_11122015_NSBD68300305"
                }
            };

            var mapper = new BatchAuditRecordMapper();
            var result = mapper.Map(batchAuditFile);

            Assert.IsTrue(result.IsSuccessful);
            Assert.AreEqual("830", result.Result.MachineNumber);
            Assert.AreEqual("68300305", result.Result.BatchNumber);
            Assert.AreEqual(new DateTime(2015, 12, 11), result.Result.ProcessingDate);
            Assert.AreEqual(new DateTime(2015, 12, 16, 14, 11, 08), result.Result.FileTimeStamp);
            Assert.AreEqual("NabPodChq", result.Result.WorkType);
            Assert.AreEqual(11, result.Result.RecordCount);
            Assert.AreEqual("183000005", result.Result.FirstDrn);
            Assert.AreEqual("183000015", result.Result.LastDrn);
            Assert.AreEqual("OUTCLEARINGSPKG_11122015_NSBD68300305", result.Result.Filename);
        }

        [TestMethod()]
        public void Map_WhenRecordProcessingDateIsEmpty_ThenResultIsFailure()
        {
            var batchAuditFile = new BatchAuditFile()
            {
                Records = new string[]
                {
                    "ProcessingDate="
                }
            };

            var mapper = new BatchAuditRecordMapper();
            var result = mapper.Map(batchAuditFile);

            Assert.IsFalse(result.IsSuccessful);
        }

        [TestMethod()]
        public void Map_WhenRecordProcessingTimeIsEmpty_ThenResultIsFailure()
        {
            var batchAuditFile = new BatchAuditFile()
            {
                Records = new string[]
                {
                    "TimeStamp="
                }
            };

            var mapper = new BatchAuditRecordMapper();
            var result = mapper.Map(batchAuditFile);

            Assert.IsFalse(result.IsSuccessful);
        }

        [TestMethod()]
        public void Map_WhenRecordCountIsEmpty_ThenResultIsFailure()
        {
            var batchAuditFile = new BatchAuditFile()
            {
                Records = new string[]
                {
                    "RecordCount="
                }
            };

            var mapper = new BatchAuditRecordMapper();
            var result = mapper.Map(batchAuditFile);

            Assert.IsFalse(result.IsSuccessful);
        }
    }
}