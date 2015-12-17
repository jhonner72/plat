using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinItemCollectionToXmlMapper : IMapper<IEnumerable<CoinItem>, IEnumerable<XElement>>
    {
        private readonly IMapper<CoinItem, XElement> itemMapper;

        public CoinItemCollectionToXmlMapper(IMapper<CoinItem, XElement> itemMapper)
        {
            Guard.IsNotNull(itemMapper, "itemMapper");

            this.itemMapper = itemMapper;
        }

        public IEnumerable<XElement> Map(IEnumerable<CoinItem> input)
        {
            Guard.IsNotNull(input, "input");

            if (input.Any() == false)
                throw new ArgumentOutOfRangeException("input", "Empty item collections are not allowed");

            return input.Select(x => itemMapper.Map(x)).ToArray();
        }
    }
}