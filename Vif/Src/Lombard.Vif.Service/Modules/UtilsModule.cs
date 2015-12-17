using Autofac;
using Lombard.Vif.Service.Utils;

namespace Lombard.Vif.Service.Modules
{
    public class UtilsModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<FileWriter>()
                .As<IFileWriter>();

            builder
                .RegisterType<PathHelper>()
                .As<IPathHelper>();

            builder
                .RegisterType<RequestConverterHelper>()
                .SingleInstance();
        }
    }
}
