using System.Configuration;

namespace Lombard.Adapters.A2iaAdapter.IntegrationTests
{
    public static class ConfigurationHelper
    {
        public static string RabbitMqConnectionString { get { return ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString; } }
        public static string InboundExchangeName { get { return ConfigurationManager.AppSettings["InboundExchangeName"]; } }
        public static string OutboundQueueName { get { return ConfigurationManager.AppSettings["OutboundQueueName"]; } }
    }
}
