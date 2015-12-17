using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using Lombard.Ingestion.Service.Configurations;
using System.Configuration;

namespace Lombard.Ingestion.Service.Modules
{
    public class ConfigurationModule : Module
    {
        protected override void Load(ContainerBuilder builder)
        {
            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IIngestionServiceConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();

            builder
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<ITopshelfConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
