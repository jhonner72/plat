using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Vif.Acknowledgement.Service.Configuration
{
    [KeyPrefix("vif:")]
    [AppSettingWrapper]
    public interface IAcknowledgmentConfiguration
    {
        string BitLockerLocation { get; set; }
    }
}
