using System.Collections.Generic;
using System.Xml.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinTrailerToXmlMapper : SectionToXmlMapper<CoinTrailer>
    {
        public CoinTrailerToXmlMapper(IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> fieldsMapper) 
            : base(fieldsMapper)
        {
        }

        public override string SectionName
        {
            get { return CoinElementConstants.TransactionTrailer; }
        }
    }
}