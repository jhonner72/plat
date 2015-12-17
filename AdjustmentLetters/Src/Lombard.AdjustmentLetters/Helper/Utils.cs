using System;
using System.IO;
using System.IO.Abstractions;
using System.Linq;
using Lombard.AdjustmentLetters.Configuration;

namespace Lombard.AdjustmentLetters.Helper
{
    public interface IFileReader
    {
        Stream LoadImage(string jobId, DateTime processingdate, string drn);
    }

    public class FileReader : IFileReader
    {
        private readonly IFileSystem fileSystem;
        private readonly IAdjustmentLettersConfiguration config;

        public FileReader(IFileSystem fileSystem, IAdjustmentLettersConfiguration config)
        {
            this.fileSystem = fileSystem;
            this.config = config;
        }

        public Stream LoadImage(string jobIdLocation, DateTime processingdate, string drn)
        {
            var file = this.fileSystem.Directory
                .EnumerateFiles(jobIdLocation)
                .FirstOrDefault(x => Path.GetFileName(x) == string.Format(this.config.ImageFileNameTemplate, processingdate, drn));

            if (file == null)
            {
                return null;
            }

            return new FileStream(file, FileMode.Open, FileAccess.Read);
        }
    }
}
