using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using Lombard.Adapters.DipsAdapter.Configuration;
using System.Configuration;

namespace Lombard.Adapters.DipsAdapter.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<ITopshelfConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IAdapterConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IQuartzConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
