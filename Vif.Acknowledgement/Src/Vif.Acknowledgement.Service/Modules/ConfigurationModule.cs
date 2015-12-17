using System.Configuration;
using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using Lombard.Vif.Acknowledgement.Service.Configuration;

namespace Lombard.Vif.Acknowledgement.Service.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
               .Register(_ => new DictionaryAdapterFactory().GetAdapter<IAcknowledgmentConfiguration>(ConfigurationManager.AppSettings))
               .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IQueueConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<ITopshelfConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
