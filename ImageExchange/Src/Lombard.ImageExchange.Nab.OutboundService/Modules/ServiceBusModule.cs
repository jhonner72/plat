using Autofac;
using Lombard.ImageExchange.Nab.OutboundService.MessageBus;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class ServiceBusModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<InProcessBus>()
                .As<IInProcessBus>()
                .SingleInstance();
        }
    }
}