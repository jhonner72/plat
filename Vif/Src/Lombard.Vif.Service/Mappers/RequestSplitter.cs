using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.IO;
using System.IO.Abstractions;
using System.Linq;
using Lombard.Common.FileProcessors;
using Lombard.Vif.Service.Configuration;
using Lombard.Vif.Service.Domain;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;
using Serilog;

namespace Lombard.Vif.Service.Mappers
{
    public interface IRequestSplitter : IMapper<CreateValueInstructionFileRequest, ValidatedResponse<VifFileInfo>>
    {
    }

    public class RequestSplitter : IRequestSplitter
    {
        private readonly IFileSystem fileSystem;

        private readonly string bitLockerLocation;

        public RequestSplitter(IVifConfiguration vifConfiguration, IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;

            bitLockerLocation = vifConfiguration.BitLockerLocation;
        }

        public ValidatedResponse<VifFileInfo> Map(CreateValueInstructionFileRequest request)
        {
            try
            {
                if (string.IsNullOrEmpty(request.jobIdentifier))
                {
                    return Failure("Request does not contain a jobIdentifier");
                }

                var jobLocation = fileSystem.Path.Combine(bitLockerLocation, request.jobIdentifier);

                if (!fileSystem.Directory.Exists(jobLocation))
                {
                    return Failure(string.Format("Cannot find bitlocker job location {0}", jobLocation));
                }

                var metadataFiles = fileSystem.Directory.EnumerateFiles(jobLocation, "VOUCHER_*.json");
                var jsonFiles = metadataFiles as IList<string> ?? metadataFiles.ToList();

                int vTotalCreditCount = 0;
                int vTotalDebitCount = 0;
                double vTotalCreditAmount = 0;
                double vTotalDebitAmount = 0;
                var vouchers = new List<VoucherInformation>();
                if (!jsonFiles.Any())
                {
                    Log.Warning(string.Format("Cannot find any VoucherInformation json files in the job location {0}", jobLocation));
                }
                else
                {
                    var serializer = JsonSerializerFactory.Get();

                    foreach (var jsonFile in jsonFiles)
                    {
                        // deserialize JSON directly from a file
                        VoucherInformation voucherInformation;
                        using (var streamReader = fileSystem.File.OpenText(jsonFile))
                        {
                            voucherInformation = (VoucherInformation)serializer.Deserialize(streamReader, typeof(VoucherInformation));

                            streamReader.Close();

                            switch (voucherInformation.voucher.documentType.ToString())
                            {
                                case "Cr":
                                    {
                                        vTotalCreditCount++;
                                        vTotalCreditAmount += Double.Parse(voucherInformation.voucher.amount);

                                        break;
                                    }
                                case "Dr":
                                    {
                                        vTotalDebitCount++;
                                        vTotalDebitAmount += Double.Parse(voucherInformation.voucher.amount);
                                        break;
                                    }
                            }
                        }

                        //--------------- START WORK AROUND
                        //Workaround as per email from Julia C, 2nd July 2015. This piece of code is meant to be removed once permanent solution has been implemented. 
                        if (string.IsNullOrEmpty(voucherInformation.voucherBatch.batchAccountNumber))
                            voucherInformation.voucherBatch.batchAccountNumber = "0012";

                        //--------------- END OF WORKAROUND
                        vouchers.Add(voucherInformation);
                    }
                }

                var vifFileInfo = new VifFileInfo(request, vouchers);

                vifFileInfo.TotalCreditCount = vTotalCreditCount;
                vifFileInfo.TotalCreditAmount = vTotalCreditAmount;
                vifFileInfo.TotalDebitAmount = vTotalDebitAmount;
                vifFileInfo.TotalDebitCount = vTotalDebitCount;
                vifFileInfo.RecordTypeCode = request.recordTypeCode;

                return ValidatedResponse<VifFileInfo>.Success(vifFileInfo);
            }
            catch (IOException)
            {
                throw;
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RequestSplitter could not process request");
                return ValidatedResponseHelper.Failure<VifFileInfo>(ex.ToString());
            }
        }

        private ValidatedResponse<VifFileInfo> Failure(string failureMessage)
        {
            return ValidatedResponse<VifFileInfo>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }
    }
}
