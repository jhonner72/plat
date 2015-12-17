using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    [TestClass]
    public class BytesToBase64ParserFacts
    {
        private const string Expected = "AQIDBAU=";

        private readonly byte[] input = { 1, 2, 3, 4, 5 };

        private IMapper<byte[], string> mapper;

        [TestInitialize]
        public void Setup()
        {
            mapper = new BytesToBase64Parser();
        }

        [TestMethod]
        public void ItShouldConvertFromBase64()
        {
            string actual = mapper.Map(input);
            Assert.AreEqual(Expected, actual);
        }
    }
}