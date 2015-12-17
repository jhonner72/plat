using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Lombard.Common.FileProcessors;
using Lombard.Ingestion.Data.Domain;
using Lombard.Ingestion.Service.Models;
using Serilog;

namespace Lombard.Ingestion.Service.Mappers
{
    public interface IBatchAuditRecordMapper : IMapper<BatchAuditFile, ValidatedResponse<RefBatchAudit>>
    {
    }

    public class BatchAuditRecordMapper : IBatchAuditRecordMapper
    {
        public ValidatedResponse<RefBatchAudit> Map(BatchAuditFile batchAuditFile)
        {
            try
            {
                RefBatchAudit refBatchAudit = new RefBatchAudit();

                for (int i = 0; i < batchAuditFile.Records.Length; i++)
                {
                    var reconPair = batchAuditFile.Records[i].Split('=');
                    if (reconPair.Length == 2)
                    {
                        if (reconPair[0].Equals("MachineNumber", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.MachineNumber = reconPair[1];
                        }
                        else if (reconPair[0].Equals("BatchNumber", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.BatchNumber = reconPair[1];
                        }
                        else if (reconPair[0].Equals("ProcessingDate", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.ProcessingDate = DateTime.ParseExact(reconPair[1], "yyyyMMdd", null);
                        }
                        else if (reconPair[0].Equals("TimeStamp", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.FileTimeStamp = DateTime.ParseExact(reconPair[1], "yyyy-MM-dd HH:mm:ss", null);
                        }
                        else if (reconPair[0].Equals("WorkType", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.WorkType = reconPair[1];
                        }
                        else if (reconPair[0].Equals("RecordCount", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.RecordCount = int.Parse(reconPair[1]);
                        }
                        else if (reconPair[0].Equals("FirstDRN", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.FirstDrn = reconPair[1];
                        }
                        else if (reconPair[0].Equals("LastDRN", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.LastDrn = reconPair[1];
                        }
                        else if (reconPair[0].Equals("FileName", StringComparison.OrdinalIgnoreCase))
                        {
                            refBatchAudit.Filename = reconPair[1];
                        }
                    }
                }

                return ValidatedResponse<RefBatchAudit>.Success(refBatchAudit);
            }
            catch(Exception ex)
            {
                Log.Error(ex, "Parse error");
                return ValidatedResponse<RefBatchAudit>.Failure(new List<ValidationResult>
                    {
                        new ValidationResult("Parse error. " + ex.Message)
                    });
            }
        }
    }
}
