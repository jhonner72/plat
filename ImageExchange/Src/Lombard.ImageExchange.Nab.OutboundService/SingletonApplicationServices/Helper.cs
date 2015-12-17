using System;
using Serilog;
using System.IO.Abstractions;

namespace Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices
{
    public static class Helper
    {
       
        public static string GetImageFullPath(IFileSystem fileSystem, string jobLocation, string jsonFileName, string frontOrBackSuffix)
        {
            var baseFileName = fileSystem.Path.GetFileNameWithoutExtension(jsonFileName);

            //Get the number of underscore char. 
            var fileNameComponent = baseFileName.Split('_');

            if (fileNameComponent.Length != 5)
            {
                Log.Warning("JSON filename may not be following the naming convention. The process to truncate the filename will continue and throw exception when it can't construct the file accordingly.");
            }
            else if (fileNameComponent.Length < 3)
            {
                Log.Error("JSON filename is too short for next processing.");
                throw new InvalidOperationException();
            }

            var truncatedFilename = String.Join("_", fileNameComponent[0], fileNameComponent[1], fileNameComponent[2]);
            var fileName = string.Format("{0}_{1}.jpg", truncatedFilename, frontOrBackSuffix);

            var imageFullPath = fileSystem.Path.Combine(jobLocation, fileName);

            return imageFullPath;
        }
    }
}
