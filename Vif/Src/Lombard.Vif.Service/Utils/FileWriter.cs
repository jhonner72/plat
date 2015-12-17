using System.IO.Abstractions;

namespace Lombard.Vif.Service.Utils
{
    public interface IFileWriter
    {
        string WriteToFile(string folderPath, string fileName, string contents);
    }

    public class FileWriter : IFileWriter
    {
        private readonly IFileSystem fileSystem;
        
        public FileWriter(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
        }

        public string WriteToFile(string folderPath, string fileName, string contents)
        {
            var fileNameAndPath = fileSystem.Path.Combine(folderPath, fileName);

            fileSystem.File.WriteAllText(fileNameAndPath, contents);

            return fileNameAndPath;
        }
    }
}
