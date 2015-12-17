using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public abstract class SectionWithFields
    {
        public IReadOnlyDictionary<string, string> Metadata { get; protected set; }

        protected SectionWithFields(IDictionary<string, string> metadata)
        {
            Metadata = new ReadOnlyDictionary<string, string>(metadata);
        }
    }
}