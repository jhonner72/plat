using System.IO;
using System.IO.Abstractions;
using System.Threading.Tasks;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.ImageExchange.Nab.UnitTests.OutboundService.SingletonApplicationServices
{
    [TestClass]
    public class CoinFileWriterTests
    {
        private CoinFile file;
        
        private string finalPath;
        
        private Mock<IMapper<CoinFile, XDocument>> mapper;
        private Mock<IFileSystem> fileSystem;     

        private MemoryStream stream;

        [TestInitialize]
        public void Setup()
        {
            stream = new MemoryStream();
            file = new CoinFile(null, null, null);
            
            finalPath = @"c:\coin\NAB\file.xml";
            var xDoc = new XDocument(new XElement("root", new XElement("key", "value")));

            mapper = new Mock<IMapper<CoinFile, XDocument>>();
            
            mapper
                .Setup(m => m.Map(file))
                .Returns(xDoc);

            fileSystem = new Mock<IFileSystem>();

            fileSystem
                .Setup(m => m.File.OpenWrite(finalPath))
                .Returns(stream);
        }

        [TestMethod]
        public async Task ItShouldSaveFileMappedFromItems()
        {

            var fileInfo = new Mock<FileInfoBase>();

            fileSystem.Setup(s => s.FileInfo.FromFileName(It.IsAny<string>())).Returns(fileInfo.Object);
              
              
            var writer = new CoinFileWriter(mapper.Object, fileSystem.Object);

            await writer.SaveFile(file, finalPath);
            
            mapper.VerifyAll();

            fileSystem.VerifyAll();
        }
    }
}