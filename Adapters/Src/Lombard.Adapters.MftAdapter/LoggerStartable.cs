using Autofac;
using Serilog;

namespace Lombard.Adapters.MftAdapter
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
