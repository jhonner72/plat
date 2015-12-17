using Autofac;
using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;
using Lombard.ECLMatchingEngine.Service.Configuration;
using System.Configuration;

namespace Lombard.ECLMatchingEngine.Service.Modules
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
                .Register(_ => new DictionaryAdapterFactory().GetAdapter<IECLRecordConfiguration>(ConfigurationManager.AppSettings))
                .SingleInstance();
        }
    }
}
