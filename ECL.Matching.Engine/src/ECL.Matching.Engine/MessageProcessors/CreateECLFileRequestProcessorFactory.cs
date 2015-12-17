
namespace Lombard.ECLMatchingEngine.Service.MessageProcessors
{
    using Autofac;
    using Lombard.Common.Queues;
    using Lombard.ECLMatchingEngine.Service.Data;
    using Lombard.ECLMatchingEngine.Service.Mappers;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;

    public class CreateECLFileRequestProcessorFactory : IMessageProcessorFactory<MatchVoucherRequest>
    {
        private readonly IComponentContext container;

         public CreateECLFileRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
         }
         public IMessageProcessor<MatchVoucherRequest> CreateMessageProcessor(MatchVoucherRequest message)
        {
            //// Construct an instance per request. The dependencies should not be injected to this contructor, 
            //// so that we can request new instances as we construct the MessageProcessor:

            var publisher = this.container.Resolve<IExchangePublisher<MatchVoucherResponse>>();
            var ECLMatching = this.container.Resolve<IMatchVoucherRequestToECLRecordBatch>();
            var VoucherBatch = this.container.Resolve<IMatchVoucherRequestToVoucherInformationBatch>();
            var MatchedVoucherBatch = this.container.Resolve<IMatchedVoucherInformationToECLFileInfo>();
            var fileWriter = this.container.Resolve<IECLRecordFileSystem>();

            return new CreateECLFileRequestProcessor(publisher, ECLMatching, VoucherBatch, MatchedVoucherBatch, fileWriter)
            {
                Message = message
            };
        }
    }
}