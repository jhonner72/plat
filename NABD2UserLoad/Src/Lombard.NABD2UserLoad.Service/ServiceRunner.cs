namespace Lombard.NABD2UserLoad.Service
{
    using Lombard.NABD2UserLoad.Data;
    using Lombard.NABD2UserLoad.Service.Configurations;
    using Serilog;
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    public class ServiceRunner : IDisposable
    {
        private readonly INabUserSynchRepository _nabUserSyncRepository;
        public ServiceRunner(INabUserSynchRepository nabUserSyncRepository)
        {
            _nabUserSyncRepository = nabUserSyncRepository;
        }

        public bool Start()
        {
            var reportDate = DateTime.Now.Date.ToString("yyyyMMdd");
            var reportName = "NabUsers_" + reportDate + ".csv";
            var filePath = Path.Combine(UserLoadConfiguration.Settings.UserloadSourcePath, reportName);
            var archiveFilePath = Path.Combine(UserLoadConfiguration.Settings.UserloadArchivePath, reportName);
            if (File.Exists(filePath))
            {
                _nabUserSyncRepository.Execute(filePath, archiveFilePath);
            }
            else
            {
                Log.Information("File {0} does not exist.", filePath);
            }
            return true;
        }

        public bool Stop()
        {
            Log.Information("Service stopping...");

            Log.Information("Service stopped.");

            return true;
        }

        public void Dispose()
        {
        }
    }
}
