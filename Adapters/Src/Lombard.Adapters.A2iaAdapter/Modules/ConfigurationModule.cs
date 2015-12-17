using System.Configuration;
using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Common.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;

namespace Lombard.Adapters.A2iaAdapter.Modules
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
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IDfsConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IA2iaConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
