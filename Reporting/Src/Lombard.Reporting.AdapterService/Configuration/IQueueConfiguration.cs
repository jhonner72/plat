namespace Lombard.Reporting.AdapterService.Configuration
{
    using Castle.Components.DictionaryAdapter;
    using Lombard.Common.Configuration;

    [KeyPrefix("queue:")]
    [AppSettingWrapper]
    public interface IQueueConfiguration
    {
        string RequestExchangeName { get; set; }

        string ResponseExchangeName { get; set; }
    }
}
