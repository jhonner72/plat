using System;
using Autofac;
using EasyNetQ;
using Lombard.Common.Configuration;
using Serilog;
using Topshelf;
using Topshelf.Autofac;
using Serilog.Extras.Topshelf;

namespace Lombard.ImageExchange.Nab.OutboundService
{
    public class Program
    {
        private static IAdvancedBus messageBus;

        public static int Main(string[] args)
        {
            var exitCode = RunService();

            return exitCode;
        }

        private static int RunService()
        {
            var builder = new ContainerBuilder();

            builder.RegisterAssemblyModules(typeof(Program).Assembly);

            var exitCode = HostFactory.Run(
                x =>
                {
                    var container = builder.Build();

                    messageBus = container.Resolve<IAdvancedBus>();

                    x.UseAutofacContainer(container);

                    x.UseSerilog();

                    var serviceConfiguration = container.Resolve<ITopshelfConfiguration>();

                    x.SetServiceName(serviceConfiguration.ServiceName);
                    x.SetDescription(serviceConfiguration.ServiceDescription);
                    x.SetDisplayName(serviceConfiguration.ServiceDisplayName);

                    x.Service<ServiceRunner>(
                        s =>
                        {
                            s.ConstructUsingAutofacContainer();
                            s.WhenStarted(i => i.Start());
                            s.WhenStopped(i => i.Stop());
                        });
                });

            if (exitCode == TopshelfExitCode.Ok && messageBus != null)
            {
                try
                {
                    messageBus.Dispose();
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "An error occurred while shutting down the service.");
                    exitCode = TopshelfExitCode.AbnormalExit;
                }
            }

            return (int)exitCode;
        }
    }
}
