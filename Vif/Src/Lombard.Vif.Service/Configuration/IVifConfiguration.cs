using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Vif.Service.Configuration
{
    [KeyPrefix("vif:")]
    [AppSettingWrapper]
    public interface IVifConfiguration
    {
        string BitLockerLocation { get; set; }
    }
}
