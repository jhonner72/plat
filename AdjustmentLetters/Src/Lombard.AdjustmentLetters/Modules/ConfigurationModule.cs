using System.Configuration;
using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.AdjustmentLetters.Configuration;
using Lombard.Common.Configuration;

namespace Lombard.AdjustmentLetters.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IQueueConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<ITopshelfConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
               .Register(_ => new DictionaryAdapterFactory().GetAdapter<IAdjustmentLettersConfiguration>(ConfigurationManager.AppSettings))
               .SingleInstance();
        }
    }
}