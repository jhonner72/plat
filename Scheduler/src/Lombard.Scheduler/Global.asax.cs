using System;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;
using Hangfire.Logging;
using Hangfire.Logging.LogProviders;
using Hangfire;
using Hangfire.SqlServer;
using Lombard.Scheduler.Domain;
using Lombard.Scheduler.Configuration;
using Lombard.Scheduler.EntityFramework;
using Lombard.Scheduler;
using Lombard.Scheduler.Utils;
using Autofac;
using Serilog;
using Hangfire.Dashboard;
using Lombard.LogExtensions;


namespace Lombard.Scheduler
{
    public class MvcApplication : System.Web.HttpApplication
    {
        private IContainer iContainer = null;
        protected void Application_Start()
        {
            AreaRegistration.RegisterAllAreas();
            RouteConfig.RegisterRoutes(RouteTable.Routes);

            Log.Logger = new LoggerConfiguration()
                .Destructure.UsingAttributes()
                .Enrich.WithCallerInformation()
                .ReadFrom.AppSettings()
                .CreateLogger();
            
            GlobalConfiguration.Configuration
                .UseSqlServerStorage(
                "NonProcessingDay",
                new SqlServerStorageOptions { QueuePollInterval = TimeSpan.FromSeconds(1) });

            GlobalConfiguration.Configuration.UseLogProvider(new SerilogLogProvider());
            var Container = new ContainerBuilder();
            Container.RegisterAssemblyModules(typeof(MvcApplication).Assembly);

            
            iContainer = Container.Build();
            GlobalConfiguration.Configuration.UseAutofacActivator(iContainer);
           
        }
    
    }
}
