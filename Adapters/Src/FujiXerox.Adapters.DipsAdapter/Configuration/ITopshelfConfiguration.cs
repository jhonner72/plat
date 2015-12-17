namespace FujiXerox.Adapters.DipsAdapter.Configuration
{
    //[AppSettingWrapper]
    //[KeyPrefix("topshelf:")]
    public interface ITopshelfConfiguration
    {
        string ServiceDescription { get; set; }
        string ServiceDisplayName { get; set; }
        string ServiceName { get; set; }
    }
}
