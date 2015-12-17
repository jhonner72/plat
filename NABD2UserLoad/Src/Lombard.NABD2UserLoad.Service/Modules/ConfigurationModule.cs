using System.Configuration;
using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.NABD2UserLoad.Service.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<ITopshelfConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
