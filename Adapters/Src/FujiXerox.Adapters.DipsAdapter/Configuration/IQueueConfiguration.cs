namespace FujiXerox.Adapters.DipsAdapter.Configuration
{
    public interface IQueueConfiguration
    {
        string HostName { get; set; }
        string UserName { get; set; }
        string Password { get; set; }
        int Timeout { get; set; }
        int HeartbeatSeconds { get; set; }
        string InvalidExchangeName { get; set; }
        string InvalidRoutingKey { get; set; }
        string RecoverableRoutingKey { get; set; }
    }
}
