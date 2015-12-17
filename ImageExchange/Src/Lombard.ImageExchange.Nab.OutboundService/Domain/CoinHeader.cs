using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Constants;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class CoinHeader : SectionWithFields
    {
        public CoinHeader(IDictionary<string, string> metadata)
            : base(metadata)
        {
        }

        public string DateCreatedRaw
        {
            get { return Metadata[CoinFieldNames.DateCreated]; }
        }

        public string TimeCreatedRaw
        {
            get { return Metadata[CoinFieldNames.TimeCreated]; }
        }
    }
}