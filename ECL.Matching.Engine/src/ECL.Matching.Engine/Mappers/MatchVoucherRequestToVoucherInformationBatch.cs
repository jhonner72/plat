using System.Runtime.Serialization;
using Newtonsoft.Json;

namespace Lombard.ECLMatchingEngine.Service.Mappers
{
    using Lombard.Common.FileProcessors;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.IO;
    using System.IO.Abstractions;
    using System.Linq;

    public interface IMatchVoucherRequestToVoucherInformationBatch : IMapper<MatchVoucherRequest, ValidatedResponse<iVoucherInfoBatch>>
    {

    }
    public class MatchVoucherRequestToVoucherInformationBatch : IMatchVoucherRequestToVoucherInformationBatch
    {
        private readonly IFileSystem fileSystem;
        private readonly string bitLockerLocation;

        public MatchVoucherRequestToVoucherInformationBatch(IECLRecordConfiguration vifConfiguration, IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;

            bitLockerLocation = vifConfiguration.BitLockerLocation;
        }

        public ValidatedResponse<iVoucherInfoBatch> Map(MatchVoucherRequest request)
        {
            var vouchers = new List<IECLRecordVoucherInfo>();
            var voucherBatch = new VoucherInfoBatch();
            
            try
            {

                if (string.IsNullOrEmpty(request.jobIdentifier))
                {
                    throw new InvalidOperationException("JobIdentifier is found null or empty!");
                }
                var JobIDPath = this.fileSystem.Path.Combine(bitLockerLocation, request.jobIdentifier); 

                if (!fileSystem.Directory.Exists(JobIDPath))
                {
                    Log.Warning(string.Format("Cannot find bitlocker job location {0}. Creating one...", JobIDPath));
                    this.fileSystem.Directory.CreateDirectory(JobIDPath);
                }

                var metadataFiles = fileSystem.Directory.EnumerateFiles(JobIDPath, "VOUCHER_*.json");

                var jsonFiles = metadataFiles as IList<string> ?? metadataFiles.ToList();

                if (!jsonFiles.Any())
                {
                    Log.Warning(string.Format("Cannot find any VoucherInformation json files in the job location {0}", bitLockerLocation));
                    voucherBatch.JobIdentifier = request.jobIdentifier;
                }
                else
                {
                    var serializer = JsonSerializerFactory.Get();

                    foreach (var jsonFile in jsonFiles)
                    {
                        Log.Information(String.Format("Processing file {0}", jsonFile));
                        // deserialize JSON directly from a file
                        VoucherInformation voucherInformation;
                        var stringContents = fileSystem.File.ReadAllText(jsonFile);

                        voucherInformation = (VoucherInformation)serializer.Deserialize(new StringReader(stringContents), typeof(VoucherInformation));

                        var ECLRecordVoucherInfo = new ECLRecordVoucherInfo();
                        ECLRecordVoucherInfo.Voucher = voucherInformation;
                        ECLRecordVoucherInfo.SkippedForNextProcessing = IsVoucherExist(voucherInformation, vouchers) ? ECLRecordVoucherInfo.SkippedForNextProcessing = true : ECLRecordVoucherInfo.SkippedForNextProcessing = false;

                        vouchers.Add(ECLRecordVoucherInfo);
                    }

                    voucherBatch.JobIdentifier = request.jobIdentifier;
                    voucherBatch.VoucherInformation = vouchers;
                }
            }
            catch (FileLoadException fle)
            {
                throw new FileLoadException(string.Format("Failed to Load/Read the JSON file. Exception was {0} ", fle.ToString()));
            }
            catch (JsonSerializationException jse)
            {
                throw new JsonSerializationException(string.Format("Deserialization from JSon Contents to voucher Information Object failed. Exception was {0} ", jse.ToString()));
            }
            catch (Exception ex)
            {
                return ValidatedResponseHelper.Failure<iVoucherInfoBatch>("MatchVoucherRequestToVoucherInformationBatch:Map: Error: {0}", ex.ToString());
            }

            return ValidatedResponse<iVoucherInfoBatch>.Success(voucherBatch);

        }

        private ValidatedResponse<iVoucherInfoBatch> Failure(string failureMessage)
        {
            return ValidatedResponse<iVoucherInfoBatch>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }

        private bool IsVoucherExist(VoucherInformation voucher, List<IECLRecordVoucherInfo> voucherCollection)
        {
            bool isVoucherMatched = false;
            var exist = voucherCollection.Select(
                    a => a.Voucher.voucher.amount == voucher.voucher.amount &&
                         a.Voucher.voucher.auxDom == voucher.voucher.auxDom &&
                         a.Voucher.voucher.accountNumber == voucher.voucher.accountNumber &&
                         a.Voucher.voucher.bsbNumber == voucher.voucher.bsbNumber);
            
            if (exist.Contains(true))
                isVoucherMatched = true;

            return isVoucherMatched;
        }

    }
}
