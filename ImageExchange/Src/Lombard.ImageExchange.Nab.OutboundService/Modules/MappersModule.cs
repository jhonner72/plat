using Autofac;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class MappersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            // Outbound Service mappers
            builder.RegisterAssemblyTypes(typeof(OutboundVoucherToCoinFileMapper).Assembly)
                .InNamespaceOf<OutboundVoucherToCoinFileMapper>()
                .AsImplementedInterfaces()
                .SingleInstance();

            // Common mappers
            builder
                .RegisterAssemblyTypes(typeof(StringToDebitCreditTypeMapper).Assembly)
                .InNamespaceOf<StringToDebitCreditTypeMapper>()
                .AsImplementedInterfaces()
                .SingleInstance();
        }
    }
}