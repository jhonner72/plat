using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.Configuration
{
    [KeyPrefix("queue:")]
    [AppSettingWrapper]
    public interface IQueueConfiguration
    {
        string RequestExchangeName { get; set; }
        string ResponseExchangeName { get; set; }
        string ErrorRequestExchangeName { get; set; }
    }
}
