namespace Lombard.Reporting.AdapterService
{
    using Autofac;
    using Lombard.LogExtensions;
    using Serilog;

    internal class LoggerStartable : IStartable
    {
        public void Start()
        {
            Log.Logger = new LoggerConfiguration()
                .Destructure.UsingAttributes()
                .Enrich.WithCallerInformation()
                .Enrich.FromLogContext()
                .ReadAppSettings()
                .CreateLogger();
        }
    }
}