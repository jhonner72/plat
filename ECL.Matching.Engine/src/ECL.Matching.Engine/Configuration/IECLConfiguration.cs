using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.ECLMatchingEngine.Service.Configuration
{

    [KeyPrefix("ecl:")]
    [AppSettingWrapper]
    public interface IECLRecordConfiguration
    {
        string BitLockerLocation { get; set; }
    }
    
}
