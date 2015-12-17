using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using Lombard.Adapters.MftAdapter.Configuration;
using System.Configuration;

namespace Lombard.Adapters.MftAdapter.Modules
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
        }
    }
}
