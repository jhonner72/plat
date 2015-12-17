using System.Collections.Generic;
using Autofac;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class ValidateCodelineRequestProcessorFactory : IMessageProcessorFactory<ValidateBatchCodelineRequest>
    {
        private readonly IComponentContext container;

        public ValidateCodelineRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<ValidateBatchCodelineRequest> CreateMessageProcessor(ValidateBatchCodelineRequest message)
        {
            var dipsQueueMapper = container.Resolve<IMapper<ValidateBatchCodelineRequest, DipsQueue>>();
            var dipsVoucherMapper = container.Resolve<IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsNabChq>>>();
            var dipsDbIndexMapper = container.Resolve<IMapper<ValidateBatchCodelineRequest, IEnumerable<DipsDbIndex>>>();

            return new ValidateCodelineRequestProcessor((ILifetimeScope)container, dipsQueueMapper, dipsVoucherMapper, dipsDbIndexMapper)
            {
                Message = message
            };

        }
    }
}
