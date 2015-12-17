using System;
using System.IO.Abstractions;
using System.Linq;
using Ionic.Zip;

namespace Lombard.AdjustmentLetters.Utils
{
    public interface IFileWriter
    {
        void SaveZipFile(string folderPath, string fileName);
    }

    public class FileWriter : IFileWriter
    {
        private readonly IFileSystem fileSystem;

        public FileWriter(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
        }

        public void SaveZipFile(string folderPath, string fileName)
        {
            using (var zip = new ZipFile())
            {
                string zippedFilePath = this.fileSystem.Path.Combine(folderPath, fileName);

                var pdfFiles = this.fileSystem.Directory.GetFiles(folderPath, "*.pdf");
                if (pdfFiles.Any())
                {
                    zip.AddFiles(pdfFiles, @"\");
                    zip.Save(zippedFilePath);
                }
                else
                {
                    throw new Exception(string.Format("There were no PDF files that were present in directory {0}", folderPath));
                }
            }
        }
    }
}
