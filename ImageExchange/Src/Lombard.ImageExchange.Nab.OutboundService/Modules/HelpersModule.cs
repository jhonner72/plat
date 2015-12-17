using Autofac;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class HelpersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterAssemblyTypes(typeof(CoinFileTotalsCalculator).Assembly)
                .InNamespaceOf<CoinFileTotalsCalculator>()
                .AsImplementedInterfaces();
        }
    }
}