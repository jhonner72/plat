using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Ploeh.AutoFixture;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    [TestClass]
    public class CoinTrailerToXmlMapperTests
    {
        private Mock<IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>>> fieldMapper;
        private IMapper<CoinTrailer, XElement> mapper;
 
        private readonly Fixture fixture = new Fixture();
        private readonly CoinTrailer input;

        private XElement result;
        private IEnumerable<XElement> expectedFields;

        private const string TestRField = "testrfield";

        public CoinTrailerToXmlMapperTests()
        {
            fixture.Register(() => new XElement(TestRField));
            input = fixture.Create<CoinTrailer>();
        }

        [TestInitialize]
        public void Setup()
        {
            fieldMapper = new Mock<IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>>>();

            expectedFields = fixture.CreateMany<XElement>();

            fieldMapper.Setup(x => x.Map(It.IsAny<IReadOnlyDictionary<string, string>>()))
                .Returns(expectedFields);

            mapper = new CoinTrailerToXmlMapper(fieldMapper.Object);

            result = mapper.Map(input);
        }

        [TestMethod]
        public void ItUsesFieldMapper()
        {
            fieldMapper.Verify(x => x.Map(It.IsAny<IReadOnlyDictionary<string, string>>()));
        }

        [TestMethod]
        public void ItReturnsNonNullElement()
        {
            Assert.IsNotNull(result);
        }

        [TestMethod]
        public void ReturnedElementIsCoinTrailer()
        {
            Assert.AreEqual(
                CoinElementConstants.TransactionTrailer, result.Name);
        }

        [TestMethod]
        public void ReturnedElementContainsFieldElements()
        {
            var rfields = result.Elements(TestRField);

            Assert.IsNotNull(rfields);
            Assert.AreEqual(expectedFields.Count(), rfields.Count());
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ItShouldThrowIfIsNull()
        {
            mapper.Map(null);
        }
    }
}