using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Globalization;
using System.IO.Abstractions;
using System.Linq;
using Lombard.Common.FileProcessors;
using Lombard.Vif.Acknowledgement.Service.Configuration;
using Lombard.Vif.Acknowledgement.Service.Domain;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Acknowledgement.Service.Utils;
using Serilog;

namespace Lombard.Vif.Acknowledgement.Service.Mappers
{
    public interface IRequestSplitter : IMapper<ProcessValueInstructionFileAcknowledgmentRequest, ValidatedResponse<IAcknowledgmentCode>>
    {
    }

    public class RequestProcessor : IRequestSplitter
    {
        private readonly IFileSystem fileSystem;
        private readonly string bitLockerLocation;

        public RequestProcessor(IAcknowledgmentConfiguration vifAckConfiguration, IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
            bitLockerLocation = vifAckConfiguration.BitLockerLocation;
        }

        public ValidatedResponse<IAcknowledgmentCode> Map(ProcessValueInstructionFileAcknowledgmentRequest request)
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

                var metadataFiles = fileSystem.Directory.EnumerateFiles(jobLocation, "MO.FXA.VIF*.ACK");

                var jsonFiles = metadataFiles as IList<string> ?? metadataFiles.ToList();
                if (!jsonFiles.Any())
                {
                    return Failure(string.Format("Cannot find any VoucherInformation json files in the job location {0}", jobLocation));
                }

                var acknowledgmentCodeFromFile = string.Empty;

                //NAB3802015050401  VALRDY
                using (var streamReader = fileSystem.File.OpenText(jsonFiles.FirstOrDefault()))
                    acknowledgmentCodeFromFile = streamReader.ReadToEnd();

                var statusCode = acknowledgmentCodeFromFile.Substring(18, 3);
                var processCode = acknowledgmentCodeFromFile.Substring(21, 3);

                var ackCode = new AcknowledgmentCode(statusCode, processCode);

                return ValidatedResponse<IAcknowledgmentCode>.Success(ackCode);
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RequestSplitter could not process request");
                return ValidatedResponseHelper.Failure<IAcknowledgmentCode>(ex.ToString());
            }
        }
        private ValidatedResponse<IAcknowledgmentCode> Failure(string failureMessage)
        {
            return ValidatedResponse<IAcknowledgmentCode>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }
    }
}
