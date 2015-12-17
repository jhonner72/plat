using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Constants;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinMetadataFieldToXmlMapper : IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>>
    {
        public IEnumerable<XElement> Map(IReadOnlyDictionary<string, string> input)
        {
            Guard.IsNotNull(input, "input");

            if (input.Any() == false)
            {
                throw new ArgumentOutOfRangeException(
                    "input", "You need to pass more than one field to assemble");
            }

            var fields = from e in input
                select new XElement(CoinElementConstants.Field,
                    new XAttribute(CoinElementConstants.NameAttribute, e.Key),
                    new XAttribute(CoinElementConstants.ValueAttribute, e.Value));

            return fields;
        }
    }
}