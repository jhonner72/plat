using System;
using System.Collections.Generic;
using System.Linq;
using Lombard.Ingestion.Data.Domain;
using Lombard.Ingestion.Data.Repository;
using Lombard.Ingestion.Service.Configurations;
using Lombard.Ingestion.Service.Helpers;
using Lombard.Ingestion.Service.Models;
using Serilog;
using Serilog.Context;

namespace Lombard.Ingestion.Service.Workers
{
    public class EclIngestionWorker
    {
        private const int VALID_RECORD_LENGTH = 112;

        private BulkIngestionRepository repository;
        private IIngestionServiceConfiguration configuration;
        private EclIngestionHelper ingestionHelper;
        private FileHelper fileHelper;

        public EclIngestionWorker(BulkIngestionRepository repository, IIngestionServiceConfiguration configuration, EclIngestionHelper helper, FileHelper fileHelper)
        {
            this.repository = repository;
            this.configuration = configuration;
            this.ingestionHelper = helper;
            this.fileHelper = fileHelper;
        }

        public void Process()
        {
            Log.Debug("EclIngestionWorker - Processing...");

            Log.Debug("EclIngestionWorker - Looking for ECL files in {0}", configuration.AusPostECLBitLockerLocation);

            var files = fileHelper.GetEclFiles(configuration.AusPostECLBitLockerLocation);

            if (files.Any())
            {
                Log.Information("EclIngestionWorker - Found {0} ECL files", files.Count());
            }

            foreach (var filePath in files)
            {
                using (LogContext.PushProperty("File", fileHelper.GetFileName(filePath)))
                {
                    Log.Information("EclIngestionWorker - Start processing file. {0}", filePath);

                    DateTime fileDate = DateTime.Today;

                    try
                    {
                        string fileState = ingestionHelper.GetFileState(filePath);

                        if (fileState == State.INVALID)
                        {
                            throw new Exception(string.Format("Invalid fileState. '{0}'", filePath));
                        }

                        var entities = new List<AusPostEclData>();

                        var records = fileHelper.ReadAllLines(filePath);

                        for (int i = 0; i < records.Length; i++)
                        {
                            string record = records[i];

                            if (string.IsNullOrWhiteSpace(record))
                            {
                                Log.Information("EclIngestionWorker - Line {0}: Empty", (i + 1));
                            }
                            else if (record.Length != VALID_RECORD_LENGTH)
                            {
                                Log.Information("EclIngestionWorker - Line {0}: Invalid record length of {1}. '{2}'", (i + 1), record.Length, record);
                            }
                            else
                            {
                                string recordType = ingestionHelper.GetRecordType(record);

                                if (recordType == RecordType.INVALID)
                                {
                                    Log.Error("EclIngestionWorker - Line {0}: Invalid recordType. '{1}'", (i + 1), record);
                                }
                                else if ((i == 0 && recordType != RecordType.HEADER) || (i != 0 && recordType == RecordType.HEADER))
                                {
                                    throw new Exception(string.Format("EclIngestionWorker - Line {0}: Invalid recordType. '{1}'", (i + 1), record));
                                }

                                if (recordType == RecordType.HEADER)
                                {
                                    var tmpFileDate = ingestionHelper.GetFileDate(record);
                                    if (!tmpFileDate.HasValue)
                                    {
                                        throw new Exception(string.Format("EclIngestionWorker - Line {0}: Invalid fileDate. '{1}'", (i + 1), record));
                                    }

                                    fileDate = tmpFileDate.Value;
                                }

                                entities.Add(new AusPostEclData()
                                {
                                    file_date = fileDate,
                                    file_state = fileState,
                                    record_content = record,
                                    record_sequence = (i + 1),
                                    record_type = recordType
                                });
                            }
                        }

                        if (entities.Any())
                        {
                            repository.BulkImportEclData(entities, fileState);
                        }
                    }
                    catch(Exception ex)
                    {
                        Log.Error(ex, "EclIngestionWorker - Unknown error has occurred. {0}", ex.Message);
                        throw;
                    }
                    finally
                    {
                        fileHelper.DeleteFile(filePath);
                    }

                    Log.Information("EclIngestionWorker - Finish processing file. {0}", filePath);
                }
            }

            Log.Debug("EclIngestionWorker - Processed.");
        }
    }
}
