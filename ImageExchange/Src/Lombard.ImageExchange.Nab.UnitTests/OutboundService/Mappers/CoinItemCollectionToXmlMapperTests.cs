using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Ploeh.AutoFixture;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Mappers
{
    [TestClass]
    public class CoinItemCollectionToXmlMapperTests
    {
        private readonly Fixture fixture = new Fixture();
        private readonly IEnumerable<CoinItem> items;

        private Mock<IMapper<CoinItem, XElement>> itemMapper; 
        private CoinItemCollectionToXmlMapper mapper;

        public CoinItemCollectionToXmlMapperTests()
        {
            items = fixture.CreateMany<CoinItem>();
        }

        [TestInitialize]
        public void Setup()
        {
            itemMapper = new Mock<IMapper<CoinItem, XElement>>();

            mapper = new CoinItemCollectionToXmlMapper(itemMapper.Object);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ItShouldThrowWhenInputIsEmpty()
        {
            mapper.Map(null);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void ItShouldThrowIfWeAreEmpty()
        {
            mapper.Map(Enumerable.Empty<CoinItem>());
        }

        [TestMethod]
        public void ItShouldUseCoinItemMapperAsMuchAsCoinItems()
        {
            mapper.Map(items);
            itemMapper.Verify(x => x.Map(It.IsAny<CoinItem>()), 
                Times.Exactly(items.Count()));
        }
    }
}