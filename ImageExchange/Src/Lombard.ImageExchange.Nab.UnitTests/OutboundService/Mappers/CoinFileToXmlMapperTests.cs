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
    public class CoinFileToXmlMapperTests
    {
        private CoinFileToXmlMapper mapper;
        private readonly Fixture fixture = new Fixture();
        
        private readonly CoinFile coinFile;
        private readonly CoinHeader header;
        private readonly CoinTrailer trailer;
        private readonly IEnumerable<CoinItem> items; 

        private Mock<IMapper<CoinHeader, XElement>> headerMapper;
        private Mock<IMapper<CoinTrailer, XElement>> trailerMapper;
        private Mock<IMapper<IEnumerable<CoinItem>, IEnumerable<XElement>>> itemsMapper; 

        private XDocument generatedCoin;

        private const string TestHeader = "testheader";
        private const string TestTrailer = "testtrailer";
        private const string TestItem = "testitem";

        public CoinFileToXmlMapperTests()
        {
            header = fixture.Create<CoinHeader>();
            trailer = fixture.Create<CoinTrailer>();
            items = fixture.CreateMany<CoinItem>();

            fixture.Register(() => new XElement(TestItem));

            coinFile = new CoinFile(header, trailer, items);
        }
        
        [TestInitialize]
        public void Setup()
        {
            headerMapper = new Mock<IMapper<CoinHeader, XElement>>();
            trailerMapper = new Mock<IMapper<CoinTrailer, XElement>>();
            itemsMapper = new Mock<IMapper<IEnumerable<CoinItem>, IEnumerable<XElement>>>();

            headerMapper.Setup(x => x.Map(header))
                .Returns(new XElement(TestHeader));
            trailerMapper.Setup(x => x.Map(trailer))
                .Returns(new XElement(TestTrailer));
            itemsMapper.Setup(x => x.Map(items))
                .Returns(fixture.CreateMany<XElement>());

            mapper = new CoinFileToXmlMapper(headerMapper.Object, trailerMapper.Object, itemsMapper.Object);
            generatedCoin = mapper.Map(coinFile);
        }

        [TestMethod]
        public void ItUsesCoinHeaderMapperWithHeader()
        {
            headerMapper.Verify(x => x.Map(header));
        }

        [TestMethod]
        public void ItGeneratesDocument()
        {
            Assert.IsNotNull(generatedCoin);
        }

        [TestMethod]
        public void GeneratedDocumentHasRootAsExpected()
        {
            generatedCoin
                .AssertRootIs(CoinElementConstants.TransactionRoot);
        }

        [TestMethod]
        public void GeneratedDocumentHasHeaderFromHeaderMapper()
        {
            generatedCoin
                .AssertRootHas(TestHeader);
        }

        [TestMethod]
        public void ItUsesCoinTrailerMapperWithTrailer()
        {
            trailerMapper.Verify(x => x.Map(trailer));
        }

        [TestMethod]
        public void GeneratedDocumentHasTrailerFromTrailerMapper()
        {
            generatedCoin
                .AssertRootHas(TestTrailer);
        }

        [TestMethod]
        public void ItUsesCoinItemCollectionMapper()
        {
            itemsMapper.Verify(x => x.Map(items));
        }

        [TestMethod]
        public void GeneratedDocumentReturnsItemsFromMapper()
        {
            generatedCoin
                .AssertRootHas(TestItem, items.Count());
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ItFailsIfCoinFileIsNull()
        {
            mapper.Map(null);
        }
    }
}