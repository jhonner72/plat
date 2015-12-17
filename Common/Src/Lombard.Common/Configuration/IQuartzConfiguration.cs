using Castle.Components.DictionaryAdapter;

namespace Lombard.Common.Configuration
{
    [KeyPrefix("quartz:")]
    [AppSettingWrapper]
    public interface IQuartzConfiguration
    {
        int PollingIntervalSecs { get; set; }
    }
}
