using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.Ingestion.Service.Configurations
{
    [KeyPrefix("ingestion:")]
    [AppSettingWrapper]
    public interface IIngestionServiceConfiguration
    {
        string AusPostECLBitLockerLocation { get; set; }
        int EclIngestionPollingInSeconds { get; set; }
        int BatchAuditIngestionPollingInSeconds { get; set; }
        string BatchAuditBitLockerLocation { get; set; }
    }
}
