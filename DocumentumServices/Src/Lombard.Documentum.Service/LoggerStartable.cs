using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Autofac;
using Serilog;

namespace Lombard.Documentum.Service
{
    internal class LoggerStartable : IStartable
    {
        public void Start()
        {
            Log.Logger = new LoggerConfiguration()
                .ReadAppSettings()
                .CreateLogger();
        }
    }
}
