using Castle.Components.DictionaryAdapter;

namespace Lombard.Common.Configuration
{
    [KeyPrefix("topshelf:")]
    [AppSettingWrapper]
    public interface ITopshelfConfiguration
    {
        string ServiceDescription { get; set; }
        string ServiceName { get; set; }
        string ServiceDisplayName { get; set; }
    }
}
