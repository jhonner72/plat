using Ionic.Zip;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Serilog;
using System;
using System.IO.Abstractions;
using System.Linq;
using System.Threading.Tasks;

namespace Lombard.ImageExchange.Nab.OutboundService.Helpers
{
    public interface ICoinFileCreator
    {
        Task ProcessAsync(OutboundVoucherFile outboundVoucherFile);
    }

    public class CoinFileCreator : ICoinFileCreator
    {
        private readonly IMapper<OutboundVoucherFile, CoinFile> documentMapper;
        private readonly IFileWriter<CoinFile> writer;
        private readonly IFileSystem fileSystem;
        
        public CoinFileCreator(
            IMapper<OutboundVoucherFile, CoinFile> documentMapper,
            IFileWriter<CoinFile> writer, 
            IFileSystem fileSystem)
        {
            this.documentMapper = documentMapper;
            this.writer = writer;
            this.fileSystem = fileSystem;
        }

        public async Task ProcessAsync(OutboundVoucherFile outboundVoucherFile)
        {
            Guard.IsNotNull(outboundVoucherFile, "OutboundVoucherFile");
            Guard.IsNotNull(outboundVoucherFile.FileName, "OutboundVoucherFile.FileName");
            Guard.IsNotNull(outboundVoucherFile.FileLocation, "OutboundVoucherFile.FileLocation");

            CoinFile imageExchangeFile = null;
            CuscalFile cuscalFile = null;

            if (outboundVoucherFile.OperationType == ImageExchangeType.Cuscal.ToString())
            {
                cuscalFile = OutboundVoucherFileToCuscalFile.Map(outboundVoucherFile);
                CreateImageExchangeFile(outboundVoucherFile, cuscalFile);
            }
            else
            {
                imageExchangeFile = documentMapper.Map(outboundVoucherFile);
                await CreateImageExchangeFile(outboundVoucherFile, imageExchangeFile);
            }
        }

        private async Task<string> CreateImageExchangeFile(OutboundVoucherFile outboundVoucherFile, CoinFile imageExchangeFile)
        {
            var fileFullPath = fileSystem.Path.Combine(outboundVoucherFile.FileLocation, outboundVoucherFile.FileName);

            await writer.SaveFile(imageExchangeFile, fileFullPath);

            Log.Debug("Image exchange file has been created: {file}", fileFullPath);

            if (outboundVoucherFile.OperationType == "ImageExchange")
            {
                var zipFileFullPath = fileFullPath.Replace(".xml", ".zip");
                using (ZipFile zip = new ZipFile())
                {
                    zip.AddFile(fileFullPath, @"\");
                    zip.Save(zipFileFullPath);
                }
            }
            return fileFullPath;
        }

        private string CreateImageExchangeFile(OutboundVoucherFile outboundVoucherFile, CuscalFile cuscalFile)
        {
            var xmlFullPath = String.Join("\\", outboundVoucherFile.FileLocation, outboundVoucherFile.FileName);
            var zipFullPath = String.Join("\\", outboundVoucherFile.FileLocation, outboundVoucherFile.ZipFileName);

            fileSystem.File.AppendAllLines(xmlFullPath, cuscalFile.VoucherItems);

            Log.Information("Cuscal file has been created: {@file}", xmlFullPath);

            Log.Information("Zipping file");
            using (ZipFile zip = new ZipFile())
            {
                zip.Password = outboundVoucherFile.ZipPassword;
                zip.AddFile(xmlFullPath, @"\");

                Log.Information("Job ID location is : {@JobIDLoc}", outboundVoucherFile.FileLocation);
                var allImages = fileSystem.Directory.EnumerateFiles(outboundVoucherFile.FileLocation)
                    .Where(t => t.EndsWith(".JPG", StringComparison.OrdinalIgnoreCase))
                    .Select(file => zip.AddFile(fileSystem.Path.Combine(outboundVoucherFile.FileLocation, file), @"\"));

                Log.Information("Job ID location is - after : {@JobIDLoc}", outboundVoucherFile.FileLocation);
                Log.Debug("Added {@numberOfImages} image JPG file(s)", allImages.Count().ToString());

                zip.Save(zipFullPath);
            }

            Log.Information("Deleting the original xml file");
            fileSystem.File.Delete(xmlFullPath);

            return xmlFullPath;
        }
    }
}