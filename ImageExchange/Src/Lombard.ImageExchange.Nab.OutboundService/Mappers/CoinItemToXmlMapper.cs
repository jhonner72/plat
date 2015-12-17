using System.Collections.Generic;
using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinItemToXmlMapper : IMapper<CoinItem, XElement>
    {
        private readonly IMapper<CoinImage, XElement> imageMapper;
        private readonly IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> fieldMapper;

        public CoinItemToXmlMapper(IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> fieldMapper, IMapper<CoinImage, XElement> imageMapper)
        {
            this.fieldMapper = fieldMapper;
            this.imageMapper = imageMapper;
        }

        public XElement Map(CoinItem input)
        {
            Guard.IsNotNull(input, "input");

            var element = new XElement(CoinElementConstants.CoinItem);
            var fields = fieldMapper.Map(input.Metadata);
            var image = imageMapper.Map(input.Image);

            element.Add(fields);
            element.Add(image);

            return element;
        }
    }
}