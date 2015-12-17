using System;
using System.Linq;
using Lombard.Ingestion.Data.Repository;
using Lombard.Ingestion.Service.Configurations;
using Lombard.Ingestion.Service.Helpers;
using Lombard.Ingestion.Service.Mappers;
using Lombard.Ingestion.Service.Models;
using Serilog;
using Serilog.Context;

namespace Lombard.Ingestion.Service.Workers
{
    public class BatchAuditIngestionWorker
    {
        private IIngestionRepository repository;
        private IIngestionServiceConfiguration configuration;
        private FileHelper fileHelper;
        private IBatchAuditRecordMapper batchAuditRecordMapper;

        public BatchAuditIngestionWorker(IIngestionRepository repository, IIngestionServiceConfiguration configuration, 
            FileHelper fileHelper, IBatchAuditRecordMapper batchAuditRecordMapper)
        {
            this.repository = repository;
            this.configuration = configuration;
            this.fileHelper = fileHelper;
            this.batchAuditRecordMapper = batchAuditRecordMapper;
        }

        public void Process()
        {
            Log.Debug("BatchAuditIngestionWorker - Processing...");

            Log.Debug("BatchAuditIngestionWorker - Looking for batch audit files in {0}", configuration.BatchAuditBitLockerLocation);

            var files = fileHelper.GetBatchAuditFiles(configuration.BatchAuditBitLockerLocation);

            if (files.Any())
            {
                Log.Information("BatchAuditIngestionWorker - Found {0} batch audit files", files.Count());

                foreach (var filePath in files)
                {
                    using (LogContext.PushProperty("File", fileHelper.GetFileName(filePath)))
                    {
                        Log.Information("BatchAuditIngestionWorker - Start processing file.");

                        try
                        {
                            var records = fileHelper.ReadAllLines(filePath);

                            var mappingResult = batchAuditRecordMapper.Map(new BatchAuditFile() { Records = records });
                            if (mappingResult.IsSuccessful)
                            {
                                repository.Add(mappingResult.Result);
                                repository.SaveChanges();
                            }
                            else
                            {
                                Log.Information("BatchAuditIngestionWorker - {0}", string.Join(Environment.NewLine, mappingResult.ValidationResults));
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.Error(ex, "BatchAuditIngestionWorker - Unknown error has occurred. {0}", ex.Message);
                            throw;
                        }
                        finally
                        {
                            fileHelper.DeleteFile(filePath);
                        }

                        Log.Information("BatchAuditIngestionWorker - Finish processing file");
                    }
                }
            }

            Log.Debug("BatchAuditIngestionWorker - Processed.");
        }
    }
}