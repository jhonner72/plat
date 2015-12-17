namespace Lombard.ECLMatchingEngine.Service.Mappers
{
    using Lombard.Common.FileProcessors;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.Data;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.IO.Abstractions;
    using System.Linq;

    public interface IMatchVoucherRequestToECLRecordBatch : IMapper<MatchVoucherRequest, ValidatedResponse<IEnumerable<ECLRecord>>>
    {

    }

    public class MatchVoucherRequestToECLRecordBatch : IMatchVoucherRequestToECLRecordBatch
    {
        private readonly IFileSystem fileSystem;
        
        private readonly List<ref_aus_post_ecl_data> ECLRecordInfo;

        public MatchVoucherRequestToECLRecordBatch(IECLInfoDataEntityFrameworks dbContext, IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;

            this.ECLRecordInfo = dbContext.GetRecordInfo;

        }

        public ValidatedResponse<IEnumerable<ECLRecord>> Map(MatchVoucherRequest request)
        {
            try
            {
                var ECLMatching = new List<ECLRecord>();

                if (this.ECLRecordInfo.Count() == 0)
                {
                    Log.Warning("{MatchVoucherRequestToECLRecordBatch}:{Map}: Cannot find any ECL Records from the database.");
                }

                ECLMatching = this.ECLRecordInfo.Select(t => new ECLRecord(t.record_content))
                                        .Where(h => h.Amount != null && h.ChequeSerialNumber != null &&
                                            h.DrawerAccountNumber != null &&
                                            h.ExchangeModeCode != null && h.ECLInput != null).ToList();

                return ValidatedResponse<IEnumerable<ECLRecord>>.Success(ECLMatching);
            }
            catch (Exception ex)
            {
                return ValidatedResponseHelper.Failure<IEnumerable<ECLRecord>>("{MatchVoucherRequestToECLRecordBatch}:{Map}: Error occurred while creating ECLRecord Objects Collection");
            }
        }
    }
}
