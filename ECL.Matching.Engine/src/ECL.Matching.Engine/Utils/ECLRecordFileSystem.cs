namespace Lombard.ECLMatchingEngine.Service.Utils
{
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using System.IO.Abstractions;
    using System.Linq;
    using System.Text;

    public interface IECLRecordFileSystem
    {
        string WriteToFile(MatchedECLFileInfo contents);
    }

    public class ECLRecordFileSystem : IECLRecordFileSystem
    {
        private readonly IFileSystem fileSystem;
        private readonly IECLRecordConfiguration eclConfiguration;

        public ECLRecordFileSystem(IFileSystem fileSystem, IECLRecordConfiguration eclConfiguration)
        {
            this.fileSystem = fileSystem;
            this.eclConfiguration = eclConfiguration;
        }

        public string WriteToFile(MatchedECLFileInfo contents)
        {
            var fileNameAndPath = contents.FileName;
            var fileContents = new StringBuilder();
            fileContents.AppendLine(contents.Header.ToString());
            contents.Body.ToList().ForEach(a => fileContents.AppendLine(a.ToString()));
            fileContents.AppendLine(contents.Trailer.ToString());

            fileSystem.File.WriteAllText(fileNameAndPath, fileContents.ToString());

            return fileNameAndPath;
        }

        
        
    }
}
