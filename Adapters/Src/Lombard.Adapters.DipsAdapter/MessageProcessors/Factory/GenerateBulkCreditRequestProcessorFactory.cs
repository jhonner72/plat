using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class GenerateBulkCreditRequestProcessorFactory : IMessageProcessorFactory<GenerateBatchBulkCreditRequest>
    {
        private readonly IComponentContext container;

        public GenerateBulkCreditRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<GenerateBatchBulkCreditRequest> CreateMessageProcessor(GenerateBatchBulkCreditRequest message)
        {
            var dipsVoucherMapper = container.Resolve<IMapper<VoucherInformation[], IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<VoucherInformation[], IEnumerable<DipsDbIndex>>>();
            var dipsQueueMapper = container.Resolve<IMapper<VoucherInformation[], DipsQueue>>();
            var dipsScannedBatchHelper = container.Resolve<IScannedBatchHelper>();
            var adapterConfiguration = container.Resolve<IAdapterConfiguration>();

            return new GenerateBulkCreditRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper, dipsScannedBatchHelper, adapterConfiguration)
            {
                Message = message
            };
        }
    }
}
