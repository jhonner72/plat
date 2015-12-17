using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class CheckThirdPartyRequestProcessorFactory : IMessageProcessorFactory<CheckThirdPartyBatchRequest>
    {
        private readonly IComponentContext container;

        public CheckThirdPartyRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CheckThirdPartyBatchRequest> CreateMessageProcessor(CheckThirdPartyBatchRequest message)
        {
            var dipsVoucherMapper = container.Resolve<IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<CheckThirdPartyBatchRequest, IEnumerable<DipsDbIndex>>>();
            var imageMergeHelper = container.Resolve<IImageMergeHelper>();
            var dipsQueueMapper = container.Resolve<IMapper<CheckThirdPartyBatchRequest, DipsQueue>>();

            return new CheckThirdPartyRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper, imageMergeHelper)
            {
                Message = message
            };
        }
    }
}
