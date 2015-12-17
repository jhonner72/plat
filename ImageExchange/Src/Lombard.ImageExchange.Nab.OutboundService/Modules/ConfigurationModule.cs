using System.Configuration;
using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;
using Lombard.Common.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IOutboundConfiguration>(ConfigurationManager.AppSettings))
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