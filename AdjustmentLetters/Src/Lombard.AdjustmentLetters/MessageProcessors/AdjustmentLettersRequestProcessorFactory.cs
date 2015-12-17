using Autofac;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Helper;
using Lombard.AdjustmentLetters.Mappers;
using Lombard.AdjustmentLetters.Utils;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.AdjustmentLetters.MessageProcessors
{
    public class AdjustmentLettersRequestProcessorFactory : IMessageProcessorFactory<CreateBatchAdjustmentLettersRequest>
    {
         private readonly IComponentContext container;

         public AdjustmentLettersRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<CreateBatchAdjustmentLettersRequest> CreateMessageProcessor(CreateBatchAdjustmentLettersRequest message)
        {
            var publisher = container.Resolve<IExchangePublisher<CreateBatchAdjustmentLettersResponse>>();
            var requestSplitter = container.Resolve<IMessageToBatchConverter>();
            var letterGen = container.Resolve<ILetterGenerator>();
            var fileWriter = container.Resolve<IFileWriter>();
            var fileReader = container.Resolve<IFileReader>();
            var pathHelper = container.Resolve<IPathHelper>();
            var aspose = container.Resolve<IAsposeWrapper>();

            return new AdjustmentLettersRequestProcessor((ILifetimeScope)container, publisher, requestSplitter, letterGen, fileWriter, pathHelper, fileReader, aspose)
            {
                Message = message
            };
        }
    }
}
