using Autofac;
using Lombard.Vif.Service.Mappers;

namespace Lombard.Vif.Service.Modules
{
    public class MappersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<RequestSplitter>()
                .As<IRequestSplitter>();

            builder
                .RegisterType<RequestConverter>()
                .As<IRequestConverter>();
        }
    }
}
