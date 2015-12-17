using Lombard.Ingestion.Service.Helpers;
using Lombard.Ingestion.Service.Models;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;

namespace Lombard.Ingestion.UnitTests
{
    [TestClass]
    public class EclIngestionHelperTests
    {
        [TestMethod]
        public void GetRecordType_WhenRecordStartsWithA_ThenReturnHeader()
        {
            string expectedRecordType = RecordType.HEADER;

            var helper = new EclIngestionHelper();

            string actualRecordType = helper.GetRecordType("A");

            Assert.AreEqual(expectedRecordType, actualRecordType);
        }

        [TestMethod]
        public void GetRecordType_WhenRecordStartsWithD_ThenReturnData()
        {
            string expectedRecordType = RecordType.DATA;

            var helper = new EclIngestionHelper();

            string actualRecordType = helper.GetRecordType("D");

            Assert.AreEqual(expectedRecordType, actualRecordType);
        }

        [TestMethod]
        public void GetRecordType_WhenRecordStartsWithZ_ThenReturnFooter()
        {
            string expectedRecordType = RecordType.FOOTER;

            var helper = new EclIngestionHelper();

            string actualRecordType = helper.GetRecordType("Z");

            Assert.AreEqual(expectedRecordType, actualRecordType);
        }

        [TestMethod]
        public void GetRecordType_WhenRecordIsEmpty_ThenReturnInvalid()
        {
            string expectedRecordType = RecordType.INVALID;

            var helper = new EclIngestionHelper();

            string actualRecordType = helper.GetRecordType("");

            Assert.AreEqual(expectedRecordType, actualRecordType);
        }

        [TestMethod]
        public void GetRecordType_WhenRecordStartsWithC_ThenReturnInvalid()
        {
            string expectedRecordType = RecordType.INVALID;

            var helper = new EclIngestionHelper();

            string actualRecordType = helper.GetRecordType("C");

            Assert.AreEqual(expectedRecordType, actualRecordType);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotVIC_ThenReturnVIC()
        {
            string expected = State.VIC;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.VIC");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotNSW_ThenReturnNSW()
        {
            string expected = State.NSW;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.NSW");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotWA_ThenReturnWA()
        {
            string expected = State.WA;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.WA");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotSA_ThenReturnSA()
        {
            string expected = State.SA;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.SA");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotQLD_ThenReturnQLD()
        {
            string expected = State.QLD;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.QLD");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileIsEmpty_ThenReturnINVALID()
        {
            string expected = State.INVALID;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileState_WhenFileEndsWithDotNT_ThenReturnINVALID()
        {
            string expected = State.INVALID;

            var helper = new EclIngestionHelper();

            string actual = helper.GetFileState("MO.something.NT");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileDate_WhenHeaderHasAValidDateInYYYYMMDDFormat_ThenReturnDateTime()
        {
            DateTime expected = new DateTime(2015, 12, 25);

            var helper = new EclIngestionHelper();

            var actual = helper.GetFileDate("A220151225");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileDate_WhenHeaderHasAnInvalidDateInDDMMYYYYFormat_ThenReturnNull()
        {
            DateTime? expected = null;

            var helper = new EclIngestionHelper();

            var actual = helper.GetFileDate("A225122015");

            Assert.AreEqual(expected, actual);
        }

        [TestMethod]
        public void GetFileDate_WhenHeaderFormatIsInvalid_ThenReturnNull()
        {
            DateTime? expected = null;

            var helper = new EclIngestionHelper();

            var actual = helper.GetFileDate("A");

            Assert.AreEqual(expected, actual);
        }
    }
}
