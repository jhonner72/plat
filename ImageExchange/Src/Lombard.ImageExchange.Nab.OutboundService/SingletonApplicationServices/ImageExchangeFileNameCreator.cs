using System;
using System.Globalization;
using Lombard.Common.DateAndTime;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices
{
    public interface IFileNameCreator
    {
        string Execute(OutboundVoucherFile outboundVoucherFile);
    }

    public class FileNameCreator : IFileNameCreator
    {
        private readonly string sendingOrganisation;

        private readonly IDateTimeProvider dateTimeProvider;

        private readonly string environment;

        public FileNameCreator(IDateTimeProvider dateTimeProvider, IOutboundConfiguration configuration)
        {
            this.dateTimeProvider = dateTimeProvider;

            sendingOrganisation = configuration.CoinSendingOrganisation;

            environment = configuration.Environment;
        }

        public string Execute(OutboundVoucherFile outboundVoucherFile) //CoinFile doc, CoinFileInfo info)
        {
            var filename = string.Empty;

            switch (outboundVoucherFile.OperationType)
            {
                case "ImageExchange":
                    { filename = GetImageExchangeFileName(outboundVoucherFile); 
                      break; 
                    }
                case "Cuscal":
                    {
                        filename = GetCuscalFilename(outboundVoucherFile);
                        break;
                    }
                case "AgencyXML":
                    { filename = GetOtherAgencyFileName(outboundVoucherFile); 
                      break; 
                    }

                default: //IE
                    {
                        filename = GetImageExchangeFileName(outboundVoucherFile);
                        break; 
                    }
            }

            return filename;
        }
        private string GetImageExchangeFileName(OutboundVoucherFile outboundVoucherFile) 
        {
            // Format CCYYMMDD (Date assigned by Image Archive capture date that is assigned to items)
            var dataProcessingDate = outboundVoucherFile.ProcessingDate.ToString(CoinValueConstants.CoinDateFormatString);

            var fileCreationDateTime = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone();

            // Format CCYYMMDD (File creation date assigned by system procedure)
            var fileCreationDate = fileCreationDateTime.ToString(CoinValueConstants.CoinDateFormatString);

            // Format HHMMSS (File creation time assigned by system procedure)
            var fileCreationTime = fileCreationDateTime.ToString(CoinValueConstants.CoinTimeFormatString);

            // Numeric 16 (Exchange Partner derived number. Represents logical reference to source data to assist file resend etc)
            var batchNumber = outboundVoucherFile.BatchNumber.ToString("D16");

            // Alphanumeric	3 (Sending organisation)
            var sourceOrganisation = sendingOrganisation;

            // Alphanumeric 3 (Receiving organisation)
            var destinationOrganisation = outboundVoucherFile.EndpointShortName;

            // Numeric 6 (File sequence number for processing day)
            //     As part of COIN Industry Testing, it was highlighted that this number 
            //     needs to be unique for a day (previously we just used "000000").
            //     However, there is no requirement to guarantee order or starting value.
            //     So for now, just use the last 6 digits of the batch number

            var sequenceNumber = outboundVoucherFile.SequenceNumber.PadLeft(6, '0'); //batchNumber.Substring(10, 6);

            // Generate based on the the spec outlined in the document:
            // [APCA - Industry Test Strategy and Plan - 13 May 2014 Appendix 6.docx]

            var fileName = string.Join(".",
                CoinValueConstants.CoinFilePrefix,
                dataProcessingDate,
                fileCreationDate,
                fileCreationTime,
                batchNumber,
                sourceOrganisation,
                destinationOrganisation,
                sequenceNumber,
                CoinValueConstants.CoinFileExtension);

            return fileName;
        }
        private string GetOtherAgencyFileName(OutboundVoucherFile outboundVoucherFile)
        {
            var fileCreationDateTime = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone();
            var fileCreationDate = fileCreationDateTime.ToString(CoinValueConstants.CoinDateFormatString);
            var fileCreationTime = fileCreationDateTime.ToString(CoinValueConstants.CoinTimeFormatString);

            // Numeric 16 (Exchange Partner derived number. Represents logical reference to source data to assist file resend etc)
            var batchNumber = outboundVoucherFile.BatchNumber.ToString("D16");

            // Alphanumeric	3 (Sending organisation)
            var sourceOrganisation = sendingOrganisation;

            // Alphanumeric 3 (Receiving organisation)
            var destinationOrganisation = outboundVoucherFile.EndpointLongName;

            var fileTypePrefix = string.Join(string.Empty, sourceOrganisation, destinationOrganisation);
            // Numeric 6 (File sequence number for processing day)
            //     As part of COIN Industry Testing, it was highlighted that this number 
            //     needs to be unique for a day (previously we just used "000000").
            //     However, there is no requirement to guarantee order or starting value.
            //     So for now, just use the last 6 digits of the batch number

            var sequenceNumber = "001";

            // Generate based on the the spec outlined in the document:
            // [APCA - Industry Test Strategy and Plan - 13 May 2014 Appendix 6.docx]

            var fileName = string.Join(".",
                fileTypePrefix,
                fileCreationDate,
                fileCreationTime,
                sequenceNumber,
                CoinValueConstants.CoinFileExtension);

            return fileName;
        }
        private string GetCuscalFilename(OutboundVoucherFile outboundVoucherFile)
        {
            var filenames = string.Empty;
            var cuscalFilename = String.Join(".",String.Join
                ("_", "Cuscal",
                 "Cheque",
                 String.Join
                    (string.Empty, outboundVoucherFile.ProcessingDate.ToString("dd", CultureInfo.InvariantCulture),
                     outboundVoucherFile.ProcessingDate.ToString("MM", CultureInfo.InvariantCulture),
                     outboundVoucherFile.ProcessingDate.Year.ToString())), "csv");

            var zipFilename = GetCuscalZipFilename(outboundVoucherFile);

            filenames = String.Join(";", cuscalFilename, zipFilename);
            return filenames;
        }
        private string GetCuscalZipFilename(OutboundVoucherFile outboundVoucherFile)
        {
            var zipFilename = String.Join(".", environment, "NAB", "RPT", "CUSCAL",
                String.Join
                    (string.Empty, outboundVoucherFile.ProcessingDate.Year.ToString(),
                        outboundVoucherFile.ProcessingDate.ToString("MM", CultureInfo.InvariantCulture),
                        outboundVoucherFile.ProcessingDate.ToString("dd", CultureInfo.InvariantCulture)), "zip");

            return zipFilename;
        }
       
    }
}