namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Lombard.ECLMatchingEngine.Service.Domain;

    [TestClass]
    public class UT_MatchedECLRecordHeader
    {
        [TestMethod]
        public void MatchedECLRecordHeader_GivenAValidInput_ShouldReturnHeaderInStringFormat()
        {
            var actualObject = MockECLRecordHeader().ToString();
            var expectedObject = "A320150923                                                                                                      ";

            Assert.AreEqual(actualObject, expectedObject);
        }

        private MatchedECLRecordHeader MockECLRecordHeader()
        {
            string MockDate = "23-09-2015";
            return new MatchedECLRecordHeader
            {
                Filler = "".PadLeft(102, ' '),
                Record_Type_Code = "A",
                StateId = "3",
                ProcessingDate = Convert.ToDateTime(MockDate).ToString("yyyyMMdd")
            };
        }
    }
}
