using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Lombard.Adapters.DipsAdapter.Configuration;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class GenerateCorrespondingVoucherRequestProcessorFactory : IMessageProcessorFactory<GenerateCorrespondingVoucherRequest>
    {
        private readonly IComponentContext container;

        public GenerateCorrespondingVoucherRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<GenerateCorrespondingVoucherRequest> CreateMessageProcessor(GenerateCorrespondingVoucherRequest message)
        {
            var dipsVoucherMapper = container.Resolve<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<GenerateCorrespondingVoucherRequest, IEnumerable<DipsDbIndex>>>();
            var dipsQueueMapper = container.Resolve<IMapper<GenerateCorrespondingVoucherRequest, DipsQueue>>();
            var adapterConfiguration = container.Resolve<IAdapterConfiguration>();

            return new GenerateCorrespondingVoucherRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper, adapterConfiguration)
            {
                Message = message
            };
        }
    }
}
