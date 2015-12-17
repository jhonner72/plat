using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Adapters.MftAdapter.Configuration
{
    [KeyPrefix("adapter:")]
    [AppSettingWrapper]
    public interface IAdapterConfiguration
    {
        string ApiUrl { get; set; }

        bool HandleJobRequests { get; set; }
        string JobsExchangeName { get; set; }

        bool HandleCopyImageRequests { get; set; }
        string CopyImagesExchangeName { get; set; }

        bool HandleIncidentRequests { get; set; }
        string IncidentExchangeName { get; set; }
    }
}
