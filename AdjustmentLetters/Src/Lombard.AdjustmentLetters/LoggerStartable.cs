using Autofac;
using Lombard.LogExtensions;
using Serilog;

namespace Lombard.AdjustmentLetters
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