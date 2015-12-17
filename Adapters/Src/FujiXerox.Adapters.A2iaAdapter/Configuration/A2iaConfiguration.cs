using FujiXerox.Adapters.A2iaAdapter.Enums;

namespace FujiXerox.Adapters.A2iaAdapter.Configuration
{
    public class A2iaConfiguration:IA2iaConfiguration
    {
        public string ParameterPath { get; set; }
        public string TablePath { get; set; }
        public LoadMethod LoadMethod { get; set; }
        public string FileType { get; set; }
        public int ChannelTimeout { get; set; }
        public int MaxProcessorCount { get; set; }
    }
}
