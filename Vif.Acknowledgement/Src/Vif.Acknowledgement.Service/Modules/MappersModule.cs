using Autofac;
using Lombard.Vif.Acknowledgement.Service.Mappers;

namespace Lombard.Vif.Acknowledgement.Service.Modules
{
    public class MappersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<RequestProcessor>()
                .As<IRequestSplitter>();

            builder
                .RegisterType<RequestConverter>()
                .As<IRequestConverter>();
        }
    }
}
