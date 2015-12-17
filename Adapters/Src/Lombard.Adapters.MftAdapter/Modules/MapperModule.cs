using Autofac;
using Lombard.Adapters.MftAdapter.Mappers;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Messages;

namespace Lombard.Adapters.MftAdapter.Modules
{
    public class MapperModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<CreateJobFromFileMapper>()
                .As<IMapper<CreateJobFromFileRequest, JobRequest>>()
                .SingleInstance();

            builder
                .RegisterType<CreateIncidentMapper>()
                .As<IMapper<CreateIncidentRequest, Incident>>()
                .SingleInstance();
        }
    }
}
