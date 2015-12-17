using System.Configuration;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests
{
    public static class ConfigurationHelper
    {
        public static string RabbitMqConnectionString { get { return ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString; } }
        public static string DipsConnectionString { get { return ConfigurationManager.ConnectionStrings["dips"].ConnectionString; } }
        public static string ValidateCodelineRequestExchangeName { get { return ConfigurationManager.AppSettings["ValidateCodelineRequestExchangeName"]; } }
        public static string ValidateCodelineResponseQueueName { get { return ConfigurationManager.AppSettings["ValidateCodelineResponseQueueName"]; } }
        public static string CorrectCodelineRequestExchangeName { get { return ConfigurationManager.AppSettings["CorrectCodelineRequestExchangeName"]; } }
        public static string CorrectCodelineResponseQueueName { get { return ConfigurationManager.AppSettings["CorrectCodelineResponseQueueName"]; } }
        public static string ValidateTransactionRequestExchangeName { get { return ConfigurationManager.AppSettings["ValidateTransactionRequestExchangeName"]; } }
        public static string ValidateTransactionResponseQueueName { get { return ConfigurationManager.AppSettings["ValidateTransactionResponseQueueName"]; } }
        public static string CorrectTransactionRequestExchangeName { get { return ConfigurationManager.AppSettings["CorrectTransactionRequestExchangeName"]; } }
        public static string CorrectTransactionResponseQueueName { get { return ConfigurationManager.AppSettings["CorrectTransactionResponseQueueName"]; } }
        public static string CheckThirdPartyRequestExchangeName { get { return ConfigurationManager.AppSettings["CheckThirdPartyRequestExchangeName"]; } }
        public static string CheckThirdPartyResponseQueueName { get { return ConfigurationManager.AppSettings["CheckThirdPartyResponseQueueName"]; } }
        public static string GenerateCorrespondingVoucherRequestExchangeName { get { return ConfigurationManager.AppSettings["GenerateCorrespondingVoucherRequestExchangeName"]; } }
        public static string GenerateCorrespondingVoucherResponseQueueName { get { return ConfigurationManager.AppSettings["GenerateCorrespondingVoucherResponseQueueName"]; } }
        public static string GetPoolVouchersExchangeName { get { return ConfigurationManager.AppSettings["GetPoolVouchersExchangeName"]; } }
        public static string GetPoolVouchersQueueName { get { return ConfigurationManager.AppSettings["GetPoolVouchersQueueName"]; } }
        public static string GenerateBulkCreditRequestExchangeName { get { return ConfigurationManager.AppSettings["GenerateBulkCreditExchangeName"]; } }
        public static string GenerateBulkCreditResponseQueueName { get { return ConfigurationManager.AppSettings["GenerateBulkCreditQueueName"]; } }
    }
}
