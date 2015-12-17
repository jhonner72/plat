using System.Configuration;

namespace Lombard.AdjustmentLetters.IntegrationTests
{
    public static class ConfigurationHelper
    {
        public static string RabbitMqConnectionString { get { return ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString; } }
        public static string RequestExchangeName { get { return ConfigurationManager.AppSettings["RequestExchangeName"]; } }
        public static string ResponseQueueName { get { return ConfigurationManager.AppSettings["ResponseQueueName"]; } }
    }
}
