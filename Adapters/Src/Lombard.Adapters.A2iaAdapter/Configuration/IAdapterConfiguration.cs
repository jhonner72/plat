using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Adapters.A2iaAdapter.Configuration
{
    [KeyPrefix("adapter:")]
    [AppSettingWrapper]
    public interface IAdapterConfiguration
    {
        string InboundQueueName { get; set; }
        string OutboundExchangeName { get; set; }

        string ImageFileFolder { get; set; }
        string ImageFileNameTemplate { get; set; }
    }
}
