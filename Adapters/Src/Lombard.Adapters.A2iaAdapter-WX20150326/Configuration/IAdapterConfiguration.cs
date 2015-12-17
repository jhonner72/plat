using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Adapters.A2iaAdapter.Configuration
{
    [KeyPrefix("adapter:")]
    [AppSettingWrapper]
    public interface IAdapterConfiguration
    {
        string ExchangeName { get; set; }
        string InboundQueueName { get; set; }
        string OutboundQueueName { get; set; }
        //public string ManualProcessQueueName { get; set; }
        //public string ErrorQueueName { get; set; }

        string ParameterPath { get; set; }
        string TablePath { get; set; }
        string CpuNames { get; set; }
        //string A2IAUserName { get; set; }
        //string A2IAPassword { get; set; }
        int CarConfidenceLevelThreshold { get; set; }
        string ImageFileFolder { get; set; }
    }
}
