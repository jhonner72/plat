namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks
{
    public class PublishInformation
    {
        public string CorrelationId { get; set; }
        public string RoutingKey { get; set; }
        public int PublishTimeOutSeconds { get; set; }
    }
}
