using System;
using System.Globalization;
using System.Text;
using System.Web.Mvc;
using System.Linq;
using Microsoft.Owin;
using Owin;
using Hangfire;
using Hangfire.Dashboard;
using Hangfire.SqlServer;
using Lombard.Scheduler.Processor;
using Lombard.Scheduler.Utils;
using Autofac;
using Serilog;
using Lombard.LogExtensions;
using System.Collections.Generic;

[assembly: OwinStartup(typeof(Lombard.Scheduler.Startup))]

namespace Lombard.Scheduler
{
    public class Startup
    {
        private ITaskProcessor taskProcessor;

        public void Configuration(IAppBuilder app)
        {           
            var Container = new ContainerBuilder();
            Container.RegisterAssemblyModules(typeof(MvcApplication).Assembly);
            var iContainer = Container.Build();
          
            app.UseHangfireDashboard();
            app.UseHangfireServer();

            taskProcessor = iContainer.Resolve<ITaskProcessor>();
        }
    }
}
