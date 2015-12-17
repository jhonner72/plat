using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class CorrectCodelineRequestProcessorFactory : IMessageProcessorFactory<CorrectBatchCodelineRequest>
    {
        private readonly IComponentContext container;

        public CorrectCodelineRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CorrectBatchCodelineRequest> CreateMessageProcessor(CorrectBatchCodelineRequest message)
        {
            var dipsQueueMapper = container.Resolve<IMapper<CorrectBatchCodelineRequest, DipsQueue>>();
            var dipsVoucherMapper = container.Resolve<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<CorrectBatchCodelineRequest, IEnumerable<DipsDbIndex>>>();
            var imageMergeHelper = container.Resolve<IImageMergeHelper>();
            var adapterConfiguration = container.Resolve<IAdapterConfiguration>();

            return new CorrectCodelineRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper, imageMergeHelper, adapterConfiguration)
            {
                Message = message
            };
        }
    }
}
