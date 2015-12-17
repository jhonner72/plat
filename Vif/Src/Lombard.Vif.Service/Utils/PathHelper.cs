using System.IO.Abstractions;
using Lombard.Common.FileProcessors;
using Lombard.Vif.Service.Configuration;
using Serilog;

namespace Lombard.Vif.Service.Utils
{

    public interface IPathHelper
    {
        ValidatedResponse<string> GetVifPath(string jobIdentifier);
    }

    public class PathHelper : IPathHelper
    {
        private readonly IFileSystem fileSystem;
        private readonly IVifConfiguration vifConfiguration;

        public PathHelper(IFileSystem fileSystem, IVifConfiguration vifConfiguration)
        {
            this.fileSystem = fileSystem;
            this.vifConfiguration = vifConfiguration;
        }

        public ValidatedResponse<string> GetVifPath(string jobIdentifier)
        {
            if (string.IsNullOrEmpty(jobIdentifier))
            {
                return ValidatedResponseHelper.Failure<string>("jobIdentifier cannot be null or empty");
            }

            var rootLocation = vifConfiguration.BitLockerLocation;

            if (!fileSystem.Directory.Exists(rootLocation))
            {
                return ValidatedResponseHelper.Failure<string>("Cannot find bitlocker folder location {0}", rootLocation);
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
