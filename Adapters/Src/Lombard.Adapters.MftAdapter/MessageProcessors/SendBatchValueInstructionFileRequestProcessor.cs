using System;
using System.IO;
using System.IO.Abstractions;
using System.Threading;
using System.Threading.Tasks;
using EasyNetQ;
using Lombard.Adapters.MftAdapter.Collections;
using Lombard.Adapters.MftAdapter.Configuration;
using Lombard.Adapters.MftAdapter.Messages.XsdImports;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.MftAdapter.MessageProcessors
{
    public class SendBatchValueInstructionFileRequestProcessor : IMessageProcessor<SendBatchValueInstructionFileRequest>
    {
        private readonly IFileSystem fileSystem;
        private readonly VifDictionary pendingVifs;
        private readonly IAdapterConfiguration adapterConfig;

        public SendBatchValueInstructionFileRequestProcessor(
            IFileSystem fileSystem,
            VifDictionary pendingVifs, 
            IAdapterConfiguration adapterConfig)
        {
            this.fileSystem = fileSystem;
            this.pendingVifs = pendingVifs;
            this.adapterConfig = adapterConfig;
        }

        public SendBatchValueInstructionFileRequest Message { get; set; }

        public async Task ProcessAsync(CancellationToken cancellationToken, string correlationId)
        {
            var request = Message;

            Log.Information("Processing SendBatchValueInstructionRequest '{@SendBatchValueInstructionRequest}', '{@correlationId}'", request, correlationId);

            try
            {
                foreach (var file in request.valueInstructionFile)
                {
                    cancellationToken.ThrowIfCancellationRequested();

                    var filename = file.valueInstructionFileBatchFilename;

                    if (!fileSystem.File.Exists(filename))
                    {
                        throw new FileNotFoundException(string.Format("VIF file '{0}' does not exist", filename));
                    }

                    var targetFilename = string.Format(@"{0}\{1}.{2}", adapterConfig.VifDestinationDirectory, fileSystem.Path.GetFileNameWithoutExtension(filename), adapterConfig.VifReadyExtension);

                    if (!pendingVifs.TryAdd(targetFilename, correlationId))
                    {
                        throw new InvalidOperationException(string.Format("Could not add '{0}' to pending transfers", filename));
                    }

                    await Task.Run(() => fileSystem.File.Move(filename, targetFilename), cancellationToken);

                    Log.Information("Successfully processed SendBatchValueInstructionRequest {@correlationId}", correlationId);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing SendBatchValueInstructionRequest {@SendBatchValueInstructionRequest}", request);
                throw new EasyNetQException("Error processing SendBatchValueInstructionRequest", ex);
            }
        }
    }
}
