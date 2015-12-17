using Castle.Components.DictionaryAdapter;

namespace Lombard.Common.Configuration
{
    [KeyPrefix("errorhandling:")]
    [AppSettingWrapper]
    public interface IErrorHandlingConfiguration
    {
        bool Enabled { get; set; }
        string ExchangeName { get; set; }
    }
}
