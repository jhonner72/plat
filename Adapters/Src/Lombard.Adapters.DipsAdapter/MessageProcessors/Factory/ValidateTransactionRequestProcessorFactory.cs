using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class ValidateTransactionRequestProcessorFactory : IMessageProcessorFactory<ValidateBatchTransactionRequest>
    {
        private readonly IComponentContext container;

        public ValidateTransactionRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<ValidateBatchTransactionRequest> CreateMessageProcessor(ValidateBatchTransactionRequest message)
        {
            var dipsQueueMapper = container.Resolve<IMapper<ValidateBatchTransactionRequest, DipsQueue>>();
            var dipsVoucherMapper = container.Resolve<IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<ValidateBatchTransactionRequest, IEnumerable<DipsDbIndex>>>();

            return new ValidateTransactionRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper)
            {
                Message = message
            };
        }
    }
}
