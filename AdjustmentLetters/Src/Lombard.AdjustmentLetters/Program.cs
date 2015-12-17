using System;
using Autofac;
using EasyNetQ;
using Lombard.Common.Configuration;
using Serilog;
using Serilog.Extras.Topshelf;
using Topshelf;
using Topshelf.Autofac;

namespace Lombard.AdjustmentLetters
{
    public class Program
    {
        private static IAdvancedBus _messageBus;

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

                    _messageBus = container.Resolve<IAdvancedBus>();

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

            if (exitCode == TopshelfExitCode.Ok && _messageBus != null)
            {
                try
                {
                    _messageBus.Dispose();
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
