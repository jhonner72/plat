using Autofac;
using Lombard.Ingestion.Service.Mappers;

namespace Lombard.Ingestion.Service.Modules
{
    public class MapperModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder.RegisterType<BatchAuditRecordMapper>().As<IBatchAuditRecordMapper>();
        }
    }
}
