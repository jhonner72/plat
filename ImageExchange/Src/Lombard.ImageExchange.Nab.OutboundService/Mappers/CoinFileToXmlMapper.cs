using System.Collections.Generic;
using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinFileToXmlMapper : IMapper<CoinFile, XDocument>
    {
        private readonly IMapper<CoinHeader, XElement> headerMapper;
        private readonly IMapper<CoinTrailer, XElement> trailerMapper;
        private readonly IMapper<IEnumerable<CoinItem>, IEnumerable<XElement>> itemsMapper;

        public CoinFileToXmlMapper(IMapper<CoinHeader, XElement> headerMapper, IMapper<CoinTrailer, XElement> trailerMapper, IMapper<IEnumerable<CoinItem>, IEnumerable<XElement>> itemsMapper)
        {
            this.headerMapper = headerMapper;
            this.trailerMapper = trailerMapper;
            this.itemsMapper = itemsMapper;
        }

        public XDocument Map(CoinFile input)
        {
            Guard.IsNotNull(input, "input");

            var header = headerMapper.Map(input.Header);
            var trailer = trailerMapper.Map(input.Trailer);
            var items = itemsMapper.Map(input.Items);

            var root = new XElement(CoinElementConstants.TransactionRoot);

            root.Add(header);
            root.Add(items);
            root.Add(trailer);

            var coin = new XDocument();
            coin.Add(root);

            return coin;
        }
    }
}