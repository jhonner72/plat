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
    public class CoinItemToXmlMapperTests
    {
        private CoinItemToXmlMapper mapper;
        private Mock<IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>>> fieldMapper; 
        private Mock<IMapper<CoinImage, XElement>> imageMapper ;
        private readonly Fixture fixture = new Fixture();
        private readonly CoinItem item;
        private const string RFieldsTag = "testitem";
        private const string ImageTag = "testimage";

        public CoinItemToXmlMapperTests()
        {
            fixture.Register(() => new XElement(RFieldsTag));
            item = fixture.Create<CoinItem>();
        }

        [TestInitialize]
        public void Setup()
        {
            fieldMapper = new Mock<IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>>>();
            fieldMapper.Setup(x => x.Map(item.Metadata))
                .Returns(fixture.CreateMany<XElement>());
            imageMapper = new Mock<IMapper<CoinImage, XElement>>();
            imageMapper.Setup(x => x.Map(item.Image))
                .Returns(new XElement(ImageTag));

            mapper = new CoinItemToXmlMapper(fieldMapper.Object, imageMapper.Object);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ItShouldThrowWhenNullInput()
        {
            mapper.Map(null);
        }

        [TestMethod]
        public void WhenGoodInputItShouldReturnItemElement()
        {
            var result = mapper.Map(item);
            Assert.IsNotNull(result);
            Assert.AreEqual(CoinElementConstants.CoinItem, result.Name);
        }

        [TestMethod]
        public void ItUsesFieldMapper()
        {
// ReSharper disable once UnusedVariable
            var result = mapper.Map(item);
            fieldMapper.Verify(x => x.Map(item.Metadata));
        }

        [TestMethod]
        public void WhenGoodInputFieldsArePresent()
        {
            var result = mapper.Map(item);
            var rfields = result.Elements(RFieldsTag);

            Assert.IsTrue(rfields.Any());
            Assert.IsTrue(rfields.All(x => x.Name == RFieldsTag));
        }

        [TestMethod]
        public void ItUsesImageMapper()
        {
            mapper.Map(item);

            imageMapper.Verify(x => x.Map(item.Image), Times.Once);
        }

        [TestMethod]
        public void ItReturnsOnlyOneImageTagFromMapper()
        {
            var result = mapper.Map(item);
            var image = result.Element(ImageTag);

            Assert.IsNotNull(image);
            Assert.AreEqual(ImageTag, image.Name);
        }
    }
}