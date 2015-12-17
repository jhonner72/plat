using Autofac;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Mappers;

namespace Lombard.AdjustmentLetters.Modules
{
    public class MappersModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .RegisterType<MessageToBatchConverter>()
                .As<IMessageToBatchConverter>()
                .SingleInstance();

            builder
                .RegisterType<LetterGenerator>()
                .As<ILetterGenerator>()
                .SingleInstance();
        }
    }
}
