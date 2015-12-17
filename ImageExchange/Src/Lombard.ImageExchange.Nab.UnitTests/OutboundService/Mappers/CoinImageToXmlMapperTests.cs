using System;
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
    public class CoinImageToXmlMapperTests
    {
        private IMapper<CoinImage, XElement> mapper;
        private Mock<IMapper<byte[], string>> imageMapper; 
        private readonly Fixture fixture = new Fixture();

        [TestInitialize]
        public void Setup()
        {
            imageMapper = new Mock<IMapper<byte[], string>>();

            mapper = new CoinImageToXmlMapper(imageMapper.Object);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void ItWillThrowIfInputIsNull()
        {
            mapper.Map(null);
        }

        [TestMethod]
        public void ItShouldReturnNotNullElementWhenNotNullInput()
        {
            var result = mapper.Map(fixture.Create<CoinImage>());
            Assert.IsNotNull(result);
        }

        [TestMethod]
        public void ReturnedElementShouldBeNameAsCoinImage()
        {
            var result = mapper.Map(fixture.Create<CoinImage>());
            Assert.AreEqual(CoinElementConstants.ImageTag, result.Name);
        }

        [TestMethod]
        public void IfBothImagesAreNotNullItShouldUseImageMapper()
        {
            var image = fixture.Create<CoinImage>();

// ReSharper disable once UnusedVariable
            var result = mapper.Map(image);

            imageMapper.Verify(x => x.Map(image.FrontImage));
            imageMapper.Verify(x => x.Map(image.RearImage));
        }

        [TestMethod]
        public void IfBothImagesAreNotNullItContainsFrontAndBackElements()
        {
            var image = fixture.Create<CoinImage>();
            var result = mapper.Map(image);

            Assert.IsNotNull(result.Element(CoinElementConstants.FrontImage));
            Assert.IsNotNull(result.Element(CoinElementConstants.RearImage));
        }

        [TestMethod]
        public void IfBothImagesAreNotNullFrontImageElementValueIsFromFrontProperty()
        {
            var image = fixture.Create<CoinImage>();
            imageMapper.Setup(x => x.Map(image.FrontImage))
                .Returns("this_is_front_image");

            var result = mapper.Map(image);

            var front = result.Element(CoinElementConstants.FrontImage).Value;
            Assert.AreEqual("this_is_front_image", front);
        }

        [TestMethod]
        public void IfBothImagesAreNotNullBackImageElementValueIsFromRearProperty()
        {
            var image = fixture.Create<CoinImage>();
            imageMapper.Setup(x => x.Map(image.RearImage))
                .Returns("this_is_back_image");

            var result = mapper.Map(image);

            var back = result.Element(CoinElementConstants.RearImage).Value;
            Assert.AreEqual("this_is_back_image", back);
        }

        [TestMethod]
        public void IfBothImagesAreEmptyWeDontCallImageMapper()
        {
            var image = new CoinImage{FrontImage = new byte[0], RearImage = new byte[0]};

// ReSharper disable once UnusedVariable
            var result = mapper.Map(image);
            imageMapper.Verify(x => x.Map(It.IsAny<byte[]>()), Times.Never);
        }

        [TestMethod]
        public void IfOnlyOneOfTheImagesIsEmptyWeDontCallImageMapper()
        {
            var image = new CoinImage { FrontImage = new byte[0], RearImage = new byte[]{1, 2, 3} };

// ReSharper disable once UnusedVariable
            var result = mapper.Map(image);
            imageMapper.Verify(x => x.Map(It.IsAny<byte[]>()), Times.Never);
        }

        [TestMethod]
        public void IfBothImagesAreEmptyWeStillHaveImageElement()
        {
            var image = new CoinImage{FrontImage = new byte[0], RearImage = new byte[0]};

            var result = mapper.Map(image);
            Assert.IsNotNull(result);
            Assert.AreEqual(CoinElementConstants.ImageTag, result.Name);
        }

        [TestMethod]
        public void IfBothImagesAreEmptyWeDontHaveBackOrFrontTags()
        {
            var image = new CoinImage{FrontImage = new byte[0], RearImage = new byte[0]};

            var result = mapper.Map(image);

            Assert.IsNull(result.Element(CoinElementConstants.FrontImage));
            Assert.IsNull(result.Element(CoinElementConstants.RearImage));
        }
    }
}