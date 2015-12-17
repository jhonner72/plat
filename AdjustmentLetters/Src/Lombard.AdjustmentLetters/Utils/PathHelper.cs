using System.IO.Abstractions;
using Lombard.AdjustmentLetters.Configuration;
using Lombard.Common.FileProcessors;
using Serilog;

namespace Lombard.AdjustmentLetters.Utils
{
    public interface IPathHelper
    {
        ValidatedResponse<string> GetJobPath(string jobIdentifier);
    }

    public class PathHelper : IPathHelper
    {
        private readonly IFileSystem fileSystem;
        private readonly IAdjustmentLettersConfiguration config;

        public PathHelper(IFileSystem fileSystem, IAdjustmentLettersConfiguration config)
        {
            this.fileSystem = fileSystem;
            this.config = config;
        }

        public ValidatedResponse<string> GetJobPath(string jobIdentifier)
        {
            if (string.IsNullOrEmpty(jobIdentifier))
            {
                return ValidatedResponseHelper.Failure<string>("jobIdentifier cannot be null or empty");
            }

            var rootLocation = config.BitLockerLocation;

            if (!fileSystem.Directory.Exists(rootLocation))
            {
                ValidatedResponseHelper.Failure<string>("Cannot find bitlocker folder location {0}", rootLocation);
            }

            var jobLocation = CreateFolderIfNotExists(rootLocation, jobIdentifier);

            return ValidatedResponse<string>.Success(jobLocation);
        }

        private string CreateFolderIfNotExists(string path, string newFolderName)
        {
            var newFolderLocation = fileSystem.Path.Combine(path, newFolderName);

            if (!fileSystem.Directory.Exists(newFolderLocation))
            {
                Log.Information("Creating folder {@newFolderName} at {@path}", newFolderName, path);

                fileSystem.Directory.CreateDirectory(newFolderLocation);
            }

            return newFolderLocation;
        }
    }
}
