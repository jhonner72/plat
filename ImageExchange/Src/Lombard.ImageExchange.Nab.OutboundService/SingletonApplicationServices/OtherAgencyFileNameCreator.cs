using Lombard.Common.DateAndTime;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices
{
    public interface IOtherAgencyFileNameCreator
    {
        string Execute(OutboundVoucherFile outboundVoucherFile);
    }
    public class OtherAgencyFileNameCreator : IOtherAgencyFileNameCreator
    {
        private readonly string sendingOrganisation;
        private readonly IDateTimeProvider dateTimeProvider;

        public OtherAgencyFileNameCreator(IDateTimeProvider dateTimeProvider, IOutboundConfiguration configuration)
        {
            this.dateTimeProvider = dateTimeProvider;

            this.sendingOrganisation = configuration.CoinSendingOrganisation;
        }
        public string Execute(OutboundVoucherFile outboundVoucherFile) //CoinFile doc, CoinFileInfo info)
        {

            var fileCreationDateTime = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone();
            var fileCreationDate = fileCreationDateTime.ToString(CoinValueConstants.CoinDateFormatString);
            var fileCreationTime = fileCreationDateTime.ToString(CoinValueConstants.CoinTimeFormatString);

            // Numeric 16 (Exchange Partner derived number. Represents logical reference to source data to assist file resend etc)
            var batchNumber = outboundVoucherFile.BatchNumber.ToString("D16");

            // Alphanumeric	3 (Sending organisation)
            var sourceOrganisation = sendingOrganisation;

            // Alphanumeric 3 (Receiving organisation)
            var destinationOrganisation = outboundVoucherFile.EndpointShortName;

            var fileTypePrefix = string.Join(sourceOrganisation, destinationOrganisation);
            // Numeric 6 (File sequence number for processing day)
            //     As part of COIN Industry Testing, it was highlighted that this number 
            //     needs to be unique for a day (previously we just used "000000").
            //     However, there is no requirement to guarantee order or starting value.
            //     So for now, just use the last 6 digits of the batch number

            var sequenceNumber = 001;

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
    }
}
