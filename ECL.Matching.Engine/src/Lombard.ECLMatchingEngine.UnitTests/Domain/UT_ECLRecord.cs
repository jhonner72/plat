using System;
using Lombard.ECLMatchingEngine.Service.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    [TestClass]
    public class UT_ECLRecord
    {
        [TestMethod]
        public void ICLRecord_GivenAValidECLRecord_ReturnIECLObject()
        {
            var actualObj = this.MockECLRecordItem();
            var expectedObj = new ECLRecord("D080999 461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999")
            {
                LedgerBSBCode = "080999",
                Amount = "0000100000",
                ExchangeModeCode = "M",
                ChequeSerialNumber = "000233531",
                DrawerAccountNumber = " 461087460",
                ECLInput = "D080999 461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999"
            };
            Assert.AreEqual(expectedObj.Amount, actualObj.Amount);
            Assert.AreEqual(expectedObj.ChequeSerialNumber, actualObj.ChequeSerialNumber);
            Assert.AreEqual(expectedObj.DrawerAccountNumber, actualObj.DrawerAccountNumber);
            Assert.AreEqual(expectedObj.ExchangeModeCode, actualObj.ExchangeModeCode);
            Assert.AreEqual(expectedObj.LedgerBSBCode, actualObj.LedgerBSBCode);
            Assert.AreEqual(expectedObj.ECLInput, actualObj.ECLInput);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void ICLRecord_GivenAnInValidECLRecord_ShouldReturnIECLObjectWithAllNullValues()
        {
            var expectedObj = new ECLRecord("D0809990461087460   000233531000000000000000010000041M19/08/5 083029 083309 020211200 41    Z  083894 999999999")
            {
                LedgerBSBCode = "080999",
                Amount = "100000",
                ExchangeModeCode = "M",
                ChequeSerialNumber = "233531",
                DrawerAccountNumber = "461087460",
                ECLInput = "D0809990461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999"
            };
        }


        private ECLRecord MockECLRecordItem()
        {
            var target = "D080999 461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999";
            return new ECLRecord(target.ToString());
        }
    }
}
