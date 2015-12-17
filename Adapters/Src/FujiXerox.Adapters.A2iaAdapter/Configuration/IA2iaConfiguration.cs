using FujiXerox.Adapters.A2iaAdapter.Enums;

namespace FujiXerox.Adapters.A2iaAdapter.Configuration
{
    //[KeyPrefix("a2ia:")]
    //[AppSettingWrapper]
    // ReSharper disable once InconsistentNaming
    public interface IA2iaConfiguration
    {
        string ParameterPath { get; set; }
        string TablePath { get; set; }
        LoadMethod LoadMethod { get; set; }
        string FileType { get; set; }
        int ChannelTimeout { get; set; }
        
        int MaxProcessorCount { get; set; }
    }
}
