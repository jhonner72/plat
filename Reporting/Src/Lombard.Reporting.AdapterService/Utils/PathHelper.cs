namespace Lombard.Reporting.AdapterService.Utils
{
using System.IO.Abstractions;
using Lombard.Common.FileProcessors;
using Lombard.Reporting.AdapterService.Configuration;
using Serilog;

    public interface IPathHelper
    {
        ValidatedResponse<string> GetReportsPath(string jobIdentifier);
    }

    public class PathHelper : IPathHelper
    {
        private readonly IFileSystem fileSystem;
        private readonly IReportingConfiguration reportingConfiguration;

        public PathHelper(IFileSystem fileSystem, IReportingConfiguration reportingConfiguration)
        {
            this.fileSystem = fileSystem;
            this.reportingConfiguration = reportingConfiguration;
        }

        public ValidatedResponse<string> GetReportsPath(string jobIdentifier)
        {
            if (string.IsNullOrEmpty(jobIdentifier))
            {
                return ValidatedResponseHelper.Failure<string>("jobIdentifier cannot be null or empty");
            }

            var rootLocation = this.reportingConfiguration.BitLockerLocation;

            if (!this.fileSystem.Directory.Exists(rootLocation))
            {
                return ValidatedResponseHelper.Failure<string>("Cannot find bitlocker folder location {0}", rootLocation);
            }

            var jobLocation = this.CreateFolderIfNotExists(rootLocation, jobIdentifier);

            return ValidatedResponse<string>.Success(jobLocation);
        }

        private string CreateFolderIfNotExists(string path, string newFolderName)
        {
            var newFolderLocation = this.fileSystem.Path.Combine(path, newFolderName);

            if (!this.fileSystem.Directory.Exists(newFolderLocation))
            {
                Log.Information("Creating folder {@newFolderName} at {@path}", newFolderName, path);
                
                this.fileSystem.Directory.CreateDirectory(newFolderLocation);
            }

            return newFolderLocation;
        }
    }
}
