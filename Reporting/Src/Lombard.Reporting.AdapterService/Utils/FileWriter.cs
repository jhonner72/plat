namespace Lombard.Reporting.AdapterService.Utils
{
    using System.IO.Abstractions;

    public interface IFileWriter
    {
        string WriteToFile(string folderPath, string fileName, byte[] result);
    }

    public class FileWriter : IFileWriter
    {
        private readonly IFileSystem fileSystem;

        public FileWriter(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
        }

        public string WriteToFile(string folderPath, string fileName, byte[] result)
        {
            var fileNameAndPath = this.fileSystem.Path.Combine(folderPath, fileName);

            this.fileSystem.File.WriteAllBytes(fileNameAndPath, result);
            
            return fileNameAndPath;
        }
    }
}
