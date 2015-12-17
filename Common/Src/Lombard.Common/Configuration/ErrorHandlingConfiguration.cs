using System.Configuration;

namespace Lombard.Common.Configuration
{
    public static class ErrorHandlingConfiguration
    {
        public static string ErrorExchangeName { get { return ConfigurationManager.AppSettings["errorhandling:ErrorExchangeName"]; } }
    }
}
