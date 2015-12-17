using Autofac;
using Lombard.Common.Configuration;
using Topshelf;
using Topshelf.Autofac;

namespace Lombard.Ingestion.Service
{
    public class Program
    {
        public static int Main(string[] args)
        {
            return RunService();
        }

        private static int RunService()
        {
            var builder = new ContainerBuilder();

            builder.RegisterAssemblyModules(typeof(Program).Assembly);

            var container = builder.Build();

            var exitCode = HostFactory.Run(
                x =>
                {
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
