using Autofac;
using Lombard.AdjustmentLetters.Utils;

namespace Lombard.AdjustmentLetters.Modules
{
    public class UtilsModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<FileWriter>()
                .As<IFileWriter>()
                .SingleInstance();

            builder
                .RegisterType<PathHelper>()
                .As<IPathHelper>()
                .SingleInstance();
        }
    }
}