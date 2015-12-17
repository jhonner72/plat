using System.Configuration;

namespace Lombard.Adapters.MftAdapter.IntegrationTests
{
    public static class ConfigurationHelper
    {
        public static string MftAdapterApiUrl { get { return ConfigurationManager.AppSettings["MftAdapterApiUrl"]; } }
        public static string RabbitMqConnectionString { get { return ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString; } }
        public static string JobsQueueName { get { return ConfigurationManager.AppSettings["JobsQueueName"]; } }
        public static string CopyImagesQueueName { get { return ConfigurationManager.AppSettings["CopyImagesQueueName"]; } }
        public static string IncidentQueueName { get { return ConfigurationManager.AppSettings["IncidentQueueName"]; } }
    }
}
