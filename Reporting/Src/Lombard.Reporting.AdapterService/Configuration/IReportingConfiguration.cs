namespace Lombard.Reporting.AdapterService.Configuration
{
    using Castle.Components.DictionaryAdapter;
    using Lombard.Common.Configuration;

    [KeyPrefix("reporting:")]
    [AppSettingWrapper]
    public interface IReportingConfiguration
    {
        string BitLockerLocation { get; set; }

        string ReportExecution2005Reference { get; set; }

        string ReportService2010Reference { get; set; }
    }
}
