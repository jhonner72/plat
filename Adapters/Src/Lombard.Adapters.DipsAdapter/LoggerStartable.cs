using Autofac;
using Serilog;

namespace Lombard.Adapters.DipsAdapter
{
    internal class LoggerStartable : IStartable
    {
        public void Start()
        {
            Log.Logger = new LoggerConfiguration()
                .Destructure.UsingAttributes()
                .ReadAppSettings()
                .CreateLogger();
        }
    }
}
