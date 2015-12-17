
using System.IO.Abstractions;
using System.Threading.Tasks;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Ploeh.AutoFixture;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.Helpers
{
    [TestClass]
    public class CoinFileCreatorTests
    {
        private CoinFile coinFile;

        private Mock<IMapper<OutboundVoucherFile, CoinFile>> mapper;

        private CoinFileCreator coinFileCreator;
        private Mock<IFileWriter<CoinFile>> coinFileWriter;
        private Mock<IFileSystem> fileSystem;
        
        private OutboundVoucherFile outboundVoucherFile;

        [TestInitialize]
        public void TestInitialize()
        {
            mapper = new Mock<IMapper<OutboundVoucherFile, CoinFile>>();
            coinFileWriter = new Mock<IFileWriter<CoinFile>>();
            fileSystem = new Mock<IFileSystem>();

            fileSystem
                .Setup(x => x.Path.Combine(It.IsAny<string>(), It.IsAny<string>()))
                .Returns((string directory, string fileName) => string.Format(@"{0}\{1}", directory, fileName));
        }

        [TestMethod]
        public async Task ProcessAsync_ShouldCreateFile()
        {
            var fixture = new Fixture();
            coinFile = fixture.Create<CoinFile>();

            outboundVoucherFile = fixture.Create<OutboundVoucherFile>();

            outboundVoucherFile.FileLocation = @"c:\someDir";
            outboundVoucherFile.FileName = @"someFileName.xml";

            mapper
                .Setup(m => m.Map(It.IsAny<OutboundVoucherFile>()))
                .Returns(coinFile);

            coinFileWriter
                .Setup(w => w.SaveFile(coinFile, @"c:\someDir\someFileName.xml"))
                .Returns(() => Task.FromResult(string.Empty));

            coinFileCreator = GetCoinFileCreator();

            await coinFileCreator.ProcessAsync(outboundVoucherFile);

            mapper.VerifyAll();
            coinFileWriter.VerifyAll();
        }

        private CoinFileCreator GetCoinFileCreator()
        {
            return new CoinFileCreator(
                mapper.Object,
                coinFileWriter.Object,
                fileSystem.Object);
        }
    }
}