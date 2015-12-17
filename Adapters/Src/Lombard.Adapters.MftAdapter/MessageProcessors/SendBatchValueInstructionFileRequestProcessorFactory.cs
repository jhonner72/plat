using System.Collections.Generic;
using System.IO.Abstractions;
using Autofac;
using Lombard.Adapters.MftAdapter.Collections;
using Lombard.Adapters.MftAdapter.Configuration;
using Lombard.Adapters.MftAdapter.Messages.XsdImports;
using Lombard.Common.Queues;

namespace Lombard.Adapters.MftAdapter.MessageProcessors
{
    public class SendBatchValueInstructionFileRequestProcessorFactory : IMessageProcessorFactory<SendBatchValueInstructionFileRequest>
    {
        private readonly IComponentContext container;

        public SendBatchValueInstructionFileRequestProcessorFactory(
            IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<SendBatchValueInstructionFileRequest> CreateMessageProcessor(SendBatchValueInstructionFileRequest message)
        {
            var fileSystem = container.Resolve<IFileSystem>();
            var pendingVifFiles = container.Resolve<VifDictionary>();
            var adapterConfig = container.Resolve<IAdapterConfiguration>();

            return new SendBatchValueInstructionFileRequestProcessor(fileSystem, pendingVifFiles, adapterConfig)
            {
                Message = message
            };
        }
    }
}
