using Autofac;
using Lombard.Common.Configuration;
using Serilog.Extras.Topshelf;
using Topshelf;
using Topshelf.Autofac;

namespace Lombard.Documentum.Service
{
    public class Program
    {
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

            return (int)exitCode;
        }
    }
}
