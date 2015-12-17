using System.Collections.Generic;
using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public abstract class SectionToXmlMapper<TSection> : IMapper<TSection, XElement>
        where TSection : SectionWithFields
    {
        private readonly IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> fieldsMapper;

        protected SectionToXmlMapper(IMapper<IReadOnlyDictionary<string, string>, IEnumerable<XElement>> fieldsMapper)
        {
            this.fieldsMapper = fieldsMapper;
        }

        public abstract string SectionName { get; }

        public virtual XElement Map(TSection input)
        {
            Guard.IsNotNull(input, "input");

            var fields = fieldsMapper.Map(input.Metadata);
            var element = new XElement(SectionName);

            element.Add(fields);
            return element;
        }
    }
}