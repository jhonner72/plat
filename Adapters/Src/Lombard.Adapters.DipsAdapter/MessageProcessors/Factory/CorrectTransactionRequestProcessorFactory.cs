using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class CorrectTransactionRequestProcessorFactory : IMessageProcessorFactory<CorrectBatchTransactionRequest>
    {
        private readonly IComponentContext container;

        public CorrectTransactionRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CorrectBatchTransactionRequest> CreateMessageProcessor(CorrectBatchTransactionRequest message)
        {
            var dipsQueueMapper = container.Resolve<IMapper<CorrectBatchTransactionRequest, DipsQueue>>();
            var dipsVoucherMapper = container.Resolve<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<CorrectBatchTransactionRequest, IEnumerable<DipsDbIndex>>>();
            var imageMergeHelper = container.Resolve<IImageMergeHelper>();

            return new CorrectTransactionRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper, imageMergeHelper)
            {
                Message = message
            };
        }
    }
}
