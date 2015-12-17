namespace Lombard.NABD2UserLoad.Service
{
    using System;
    using Autofac;
    using Lombard.Common.Configuration;
    using Serilog;
    using Serilog.Extras.Attributed;
    using Lombard.NABD2UserLoad.Data;

    public class Program
    {
        static void Main(string[] args)
        {
            RunService();
        }

        private static void RunService()
        {
            var builder = new ContainerBuilder();

            builder.RegisterAssemblyModules(typeof(Program).Assembly);
            var container = builder.Build();
            container.Resolve<ServiceRunner>().Start();
        }
    }
}
