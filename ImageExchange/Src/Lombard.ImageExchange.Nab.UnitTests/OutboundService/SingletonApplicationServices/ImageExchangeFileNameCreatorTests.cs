using System;
using Lombard.Common.DateAndTime;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.SingletonApplicationServices
{
    [TestClass]
    public class ImageExchangeFileNameCreatorTests
    {
        private readonly Mock<IDateTimeProvider> dateTimeProvider;
        private readonly Mock<IOutboundConfiguration> outboundConfiguration;

        public ImageExchangeFileNameCreatorTests()
        {
            dateTimeProvider = new Mock<IDateTimeProvider>();
            outboundConfiguration = new Mock<IOutboundConfiguration>();

            outboundConfiguration
                .Setup(x => x.CoinSendingOrganisation)
                .Returns("SRC");
        }

        [TestMethod]
        public void Execute_GivenValidInputs_ReturnsCorrectFileName()
        {
            var imageExchangeFileNameCreator = GetImageExchangeFileNameCreator();

            var outboundVoucherFile = GetOutboundVoucherFile();

            dateTimeProvider
                .Setup(x => x.CurrentTimeInAustralianEasternTimeZone())
                .Returns(new DateTime(2015, 02, 20, 10, 11, 12));
            
            var fileName = imageExchangeFileNameCreator.Execute(outboundVoucherFile);

            Assert.IsNotNull(fileName);
            // IMGEXCH.20150514.20150622.155534.2015051400000001.NAB.RBA.000001
            Assert.AreEqual
            (
                "IMGEXCH.20150319.20150220.101112.0000000012345678.SRC.FSV.000001.xml", 
                fileName);
        }

        private OutboundVoucherFile GetOutboundVoucherFile()
        {
            return new OutboundVoucherFile
            {
                BatchNumber = 12345678, // intentionally using more than 6 digits 
                ProcessingDate = new DateTime(2015, 03, 19),
                EndpointShortName = "FSV",
                 SequenceNumber = "000001",
                  OperationType = ImageExchangeType.ImageExchange.ToString()
            };
        }

        private FileNameCreator GetImageExchangeFileNameCreator()
        {
            return new FileNameCreator(
                dateTimeProvider.Object, 
                outboundConfiguration.Object);
        }
    }
}
