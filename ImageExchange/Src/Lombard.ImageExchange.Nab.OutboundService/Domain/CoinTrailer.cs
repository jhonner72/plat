using System.Collections.Generic;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class CoinTrailer : SectionWithFields
    {
        public CoinTrailer(IDictionary<string, string> metadata) 
            : base(metadata)
        {
        }
    }
}