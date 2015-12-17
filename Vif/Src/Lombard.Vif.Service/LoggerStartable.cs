﻿using Autofac;
using Lombard.LogExtensions;
using Serilog;

namespace Lombard.Vif.Service
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