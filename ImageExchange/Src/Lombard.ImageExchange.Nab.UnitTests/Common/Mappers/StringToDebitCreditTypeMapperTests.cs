using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.Common.Mappers
{

    [TestClass]
    public class StringToDebitCreditTypeMapperTests
    {
        [TestMethod]
        public void ItCanDetectCredits()
        {
            var mapper = GetStringToDebitCreditTypeMapper();

            string[] creditTranCodes = { "50", "75", "99", "950", "999" };

            foreach (var creditTranCode in creditTranCodes)
            {
                var actual = mapper.Map(creditTranCode);
                Assert.AreEqual(DebitCreditType.Credit, actual);
            }
        }

        [TestMethod]
        public void ItCanDetectDebits()
        {
            var mapper = GetStringToDebitCreditTypeMapper();

            string[] debitTranCodes = { string.Empty, "  ", "ab", "00", "20", "100", "949", "1000" };

            foreach (var debitTranCode in debitTranCodes)
            {
                var actual = mapper.Map(debitTranCode);
                Assert.AreEqual(DebitCreditType.Debit, actual);
            }
        }

        private StringToDebitCreditTypeMapper GetStringToDebitCreditTypeMapper()
        {
            return new StringToDebitCreditTypeMapper();
        }
    }
}
