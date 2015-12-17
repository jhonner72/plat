namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Lombard.ECLMatchingEngine.Service.Domain;

    [TestClass]
    public class UT_MatchedECLRecordTrailer
    {
        [TestMethod]
        public void MatchedECLRecordTrailer_GivenAValidInput_ShouldReturnTrailerInStringFormat()
        {
            var actualObject = this.MockECLRecordTrailer().ToString();
            var expectedObject = "Z000001                                                                                                         ";

            Assert.AreEqual(actualObject, expectedObject);
        }

        private MatchedECLRecordTrailer MockECLRecordTrailer()
        {
            return new MatchedECLRecordTrailer
            {
                Record_Type_Code = "Z",
                TransactionNumberTotal = "1",
                Filler = "".PadLeft(105, ' ')
            };
        }
    }
}
