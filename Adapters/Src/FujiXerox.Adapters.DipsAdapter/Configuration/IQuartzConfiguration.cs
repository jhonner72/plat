namespace FujiXerox.Adapters.DipsAdapter.Configuration
{
    //[AppSettingWrapper]
    //[KeyPrefix("quartz:")]
    public interface IQuartzConfiguration
    {
        int PollingIntervalSecs { get; set; }
    }
}
