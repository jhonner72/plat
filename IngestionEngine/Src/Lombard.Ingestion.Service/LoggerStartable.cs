using Autofac;
using Destructurama;
using Lombard.LogExtensions;
using Serilog;

namespace Lombard.Ingestion.Service
{
    internal class LoggerStartable : IStartable
    {
        public void Start()
        {
            Log.Logger = new LoggerConfiguration()
                .Destructure.UsingAttributes()
                .Enrich.WithCallerInformation()
                .Enrich.FromLogContext()
                .ReadFrom.AppSettings()
                .CreateLogger();
        }
    }
}
