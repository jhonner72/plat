using Autofac;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;

namespace Lombard.Adapters.DipsAdapter.MessageProcessors.Factory
{
    public class GetVouchersInformationResponseProcessorFactory : IMessageProcessorFactory<GetVouchersInformationResponse>
    {
        private readonly IComponentContext container;

        public GetVouchersInformationResponseProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<GetVouchersInformationResponse> CreateMessageProcessor(GetVouchersInformationResponse message)
        {
            return new GetVouchersInformationResponseProcessor((ILifetimeScope)container)
            {
                Message = message
            };
        }
    }
}
