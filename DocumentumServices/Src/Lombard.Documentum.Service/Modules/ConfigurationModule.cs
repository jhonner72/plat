using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using System.Configuration;

namespace Lombard.Documentum.Service.Modules
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
