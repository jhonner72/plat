using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Vif.Acknowledgement.Service.Configuration
{
    [KeyPrefix("queue:")]
    [AppSettingWrapper]
    public interface IQueueConfiguration
    {
        string RequestExchangeName { get; set; }
        string ResponseExchangeName { get; set; }
    }
}
