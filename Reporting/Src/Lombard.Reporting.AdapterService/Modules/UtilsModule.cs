namespace Lombard.Reporting.AdapterService.Modules
{
    using Autofac;
    using Lombard.Reporting.AdapterService.Utils;

    public class UtilsModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<PathHelper>()
                .As<IPathHelper>();

            builder
                .RegisterType<FileWriter>()
                .As<IFileWriter>();
        }
    }
}
