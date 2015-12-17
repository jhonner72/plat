using System;
using System.IO.Abstractions;
using System.Threading.Tasks;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Serilog;

namespace Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices
{
    public interface IFileWriter<in T>
    {
        Task SaveFile(T file, string filePath);
    }

    public class CoinFileWriter : IFileWriter<CoinFile>
    {
        private readonly IMapper<CoinFile, XDocument> mapper;
        private readonly IFileSystem fileSystem;

        public CoinFileWriter(IMapper<CoinFile, XDocument> mapper, IFileSystem fileSystem)
        {
            this.mapper = mapper;
            this.fileSystem = fileSystem;
        }

        public Task SaveFile(CoinFile file, string filePath)
        {
            var xml = mapper.Map(file);
            
            return Task.Run(() => InnerSave(filePath, xml));
        }

        private void InnerSave(string filePath, XDocument document)
        {
            using (var file = fileSystem.File.OpenWrite(filePath))
            {
                document.Save(file);

                var fileInfo = this.fileSystem.FileInfo.FromFileName(filePath);
                var isFileCreated = fileInfo.Exists;
                if (isFileCreated)
                    Log.Information("File {@filename} is saved.", filePath);
                else
                    Log.Information("File {@filename} did not exist yet, file creation took longer than usual. Further execution may fail", filePath);
            }
        }
    }
}