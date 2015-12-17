using System;
using System.Collections.Generic;
using Lombard.Common.DateAndTime;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class OutboundVoucherToCoinHeaderMapper : IMapper<OutboundVoucherFile, CoinHeader>
    {
        private readonly IDateTimeProvider dateTime;

        public OutboundVoucherToCoinHeaderMapper(IDateTimeProvider dateTime)
        {
            this.dateTime = dateTime;
        }

        public CoinHeader Map(OutboundVoucherFile input)
        {
            // TODO: think this needs to match the value used in the filename too
            var createdDate = dateTime.CurrentTimeInAustralianEasternTimeZone();
            return new CoinHeader(BuildValueDictionary(createdDate, input.ProcessingDate, input.OperationType));
        }

        public static IDictionary<string, string> BuildValueDictionary(
            DateTime createdDateTime,
            DateTime transmissionDate, 
            string operationType)
        {
            Dictionary<string, string> dictionary = null;
            switch (operationType)
            {
                case "ImageExchange":
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.HeaderRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.HeaderVersion},
                            {CoinFieldNames.TransmissionDate, transmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.SendingBsb, CoinValueConstants.HeaderBsbSendingFi},
                            {CoinFieldNames.ReceivingBsb, CoinValueConstants.HeaderBsbReceivingFi},
                            {CoinFieldNames.DateCreated, createdDateTime.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.TimeCreated, createdDateTime.ToString(CoinValueConstants.CoinTimeFormatString)},
                            {CoinFieldNames.SequenceNumber, CoinValueConstants.HeaderFileSequenceNumber},
                            {CoinFieldNames.RecordIdentifier, CoinValueConstants.HeaderRecordIdentifier}
                        };
                        break;
                    }
                case "AgencyXML":
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.OtherAgencyHeaderRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.OtherAgencyHeaderVersion},
                            {CoinFieldNames.TransmissionDate, transmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.SendingBsb, CoinValueConstants.HeaderBsbSendingFi},
                            {CoinFieldNames.ReceivingBsb, CoinValueConstants.HeaderBsbReceivingFi},
                            {CoinFieldNames.DateCreated, createdDateTime.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.TimeCreated, createdDateTime.ToString(CoinValueConstants.CoinTimeFormatString)},
                            {CoinFieldNames.SequenceNumber, CoinValueConstants.OtherAgencyHeaderFileSequenceNumber}
                        };
                        break;
                    }
                default: //IE
                    {
                        dictionary = new Dictionary<string, string>
                        {
                            {CoinFieldNames.RecordTypeIdentifier, CoinValueConstants.HeaderRecordType},
                            {CoinFieldNames.Version, CoinValueConstants.HeaderVersion},
                            {CoinFieldNames.TransmissionDate, transmissionDate.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.SendingBsb, CoinValueConstants.HeaderBsbSendingFi},
                            {CoinFieldNames.ReceivingBsb, CoinValueConstants.HeaderBsbReceivingFi},
                            {CoinFieldNames.DateCreated, createdDateTime.ToString(CoinValueConstants.CoinDateFormatString)},
                            {CoinFieldNames.TimeCreated, createdDateTime.ToString(CoinValueConstants.CoinTimeFormatString)},
                            {CoinFieldNames.SequenceNumber, CoinValueConstants.HeaderFileSequenceNumber},
                            {CoinFieldNames.RecordIdentifier, CoinValueConstants.HeaderRecordIdentifier}
                        };
                        break; 
                    }
            }

            return dictionary;
        }

    }
}