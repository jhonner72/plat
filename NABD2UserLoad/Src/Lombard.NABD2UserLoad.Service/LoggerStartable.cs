using Autofac;
using Lombard.LogExtensions;
using Serilog;

namespace Lombard.NABD2UserLoad.Service
{
    internal class LoggerStartable : IStartable
    {
        public void Start()
        {
            Log.Logger = new LoggerConfiguration()
                .Destructure.UsingAttributes()
                .Enrich.WithCallerInformation()
                .ReadFrom.AppSettings()
                .CreateLogger();
        }
    }
}
