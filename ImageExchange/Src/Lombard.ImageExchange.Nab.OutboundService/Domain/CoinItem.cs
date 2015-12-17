using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Constants;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class CoinItem : SectionWithFields
    {
        public CoinItem(CoinImage image, IDictionary<string, string> metadata)
            : base(metadata)
        {
            Image = image;
        }

        public CoinImage Image { get; private set; }

        public string BatchNumberRaw
        {
            get { return Metadata[CoinFieldNames.BatchNumber]; }
        }

        public string CollectingBsbRaw
        {
            get { return Metadata[CoinFieldNames.BsbCollectingFi]; }
        }

        public string LedgerBsbRaw
        {
            get { return Metadata[CoinFieldNames.BsbLedgerFi]; }
        }
    }
}