﻿using Autofac;
using Lombard.LogExtensions;
using Serilog;

namespace Lombard.Vif.Acknowledgement.Service
{
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