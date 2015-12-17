namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Lombard.ECLMatchingEngine.Service.Domain;
    [TestClass]
    public class UT_MatchedECLRecordBody
    {
        [TestMethod]
        public void MatchedECLRecordBody_GivenAValidInput_ShouldReturnECLBodyInStringFormat()
        {
            var actualObject = this.MockECLRecordBody().ToString();
            var expectedObject = "D080999 461087460   000233531000000000000000010000041";

            Assert.AreEqual(actualObject, expectedObject);
        }

        private MatchedECLRecordBody MockECLRecordBody()
        {
            return new MatchedECLRecordBody
            {
                ChequeSerialNumber = "233531",
                DrawerAccountNumber = "461087460",
                ExtraAuxDom = "000000000000",
                LedgerBsbCode = "080999",
                Machine_Olp_Distribution_Number = "41",
                Record_Type_Code = "D",
                TransactionAmount = "100000",
                TransactionTypeCode = "".PadLeft(3, ' ')
            };
        
        }
    }
}
