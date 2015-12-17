using System.IO;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Common;
using Serilog;
using System;
using System.Collections.Generic;
using System.IO.Abstractions;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Newtonsoft.Json;

namespace Lombard.Adapters.DipsAdapter.Helpers
{
    public class ScannedBatchHelper : IScannedBatchHelper
    {
        private readonly IFileSystem fileSystem;
        private readonly IAdapterConfiguration adapterConfiguration;

        public ScannedBatchHelper(
            IFileSystem fileSystem,
            IAdapterConfiguration adapterConfiguration)
        {
            this.fileSystem = fileSystem;
            this.adapterConfiguration = adapterConfiguration;
        }

        public VoucherInformation[] ReadScannedBatch(GenerateBatchBulkCreditRequest request, string jobIdentifier, DateTime processingDate)
        {
            Guard.IsNotNull(jobIdentifier, "jobIdentifier");
            List<VoucherInformation> vouchers = new List<VoucherInformation>();
            Log.Debug("Reading from scanned batch jobId: {@jobIdentifier}, processingDate: {@processingDate}", jobIdentifier, processingDate);

            try
            {
                var jobImageDirectory = GetJobDirectory(jobIdentifier);

                foreach(VoucherGroupCriteria vg in request.vouchers)
                {
                    string fileSearch = string.Format("VOUCHER_{0}_{1}_*.json", vg.processingDate.ToString("ddMMyyyy"), vg.documentReferenceNumber);
                    Log.Information("Searching for JSON files {@fileSearch} in job directory {@jobImageDirectory}", fileSearch, jobImageDirectory);

                    var metadataFiles = fileSystem.Directory.EnumerateFiles(jobImageDirectory, fileSearch);

                    foreach (var metadataFile in metadataFiles)
                    {
                        vouchers.Add(ReadFromJsonFile<VoucherInformation>(metadataFile));
                    }
                }

                Log.Information("Read {0} files for job '{@jobIdentifier}'", vouchers.Count, jobIdentifier);
                return vouchers.ToArray();
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Could not read data from JSON files for job '{@jobIdentifier}'", jobIdentifier);
                throw;
            }
        }

        private string GetJobDirectory(string jobIdentifier)
        {
            var directory = string.Format(adapterConfiguration.PackageSourceDirectory, jobIdentifier.Replace("/", @"\"));

            if (!fileSystem.Directory.Exists(directory))
            {
                throw new DirectoryNotFoundException(string.Format("Could not find the folder '{0}'", directory));
            }

            return directory;
        }

        private T ReadFromJsonFile<T>(string filePath) where T : new()
        {
            var serializer = new JsonSerializer();

            using (var reader = fileSystem.File.OpenText(filePath))
            using (var jsonTextReader = new JsonTextReader(reader))
            {
                return (T)serializer.Deserialize(jsonTextReader, typeof(T));
            }
        }
    }
}