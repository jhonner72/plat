using Castle.Components.DictionaryAdapter;
using Lombard.Adapters.A2iaAdapter.Wrapper.Enums;
using Lombard.Common.Configuration;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.Configuration
{
    [KeyPrefix("a2ia:")]
    [AppSettingWrapper]
    // ReSharper disable once InconsistentNaming
    public interface IA2iaConfiguration
    {
        string ParameterPath { get; set; }
        string TablePath { get; set; }
        LoadMethod LoadMethod { get; set; }
        string FileType { get; set; }
        int ChannelTimeout { get; set; }
        int StickyChannelTimeout { get; set; }

        int MaxProcessorCount { get; set; }
    }
}
