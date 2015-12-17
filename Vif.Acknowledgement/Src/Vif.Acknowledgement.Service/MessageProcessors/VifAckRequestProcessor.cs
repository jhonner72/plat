using Lombard.Common.Queues;
using Lombard.Vif.Acknowledgement.Service.Mappers;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using Serilog.Context;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Threading.Tasks;

namespace Lombard.Vif.Acknowledgement.Service.MessageProcessors
{
    public class VifAckRequestProcessor : IMessageProcessor<ProcessValueInstructionFileAcknowledgmentRequest>
    {
        private readonly IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse> publisher;
        private readonly IRequestSplitter requestSplitter;
        private readonly IRequestConverter requestConverter;

        public ProcessValueInstructionFileAcknowledgmentRequest Message { get; set; }

        public VifAckRequestProcessor(
            IExchangePublisher<ProcessValueInstructionFileAcknowledgmentResponse> publisher,
            IRequestSplitter requestSplitter,
            IRequestConverter requestConverter)
        {
            this.publisher = publisher;
            this.requestSplitter = requestSplitter;
            this.requestConverter = requestConverter;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;
            
            try
            {
                using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
                {
                    var requestSplitterResponse = requestSplitter.Map(request);

                    if (!requestSplitterResponse.IsSuccessful)
                    {
                        ExitWithError(requestSplitterResponse.ValidationResults);
                    }

                    var ackCode = requestSplitterResponse.Result;

                    Log.Information(string.Format("Acknowledgement has been obtained : @ProcessCode @StatusCode", ackCode.ProcessCode, ackCode.StatusCode));
                    var vifItemResponse = new ProcessValueInstructionFileAcknowledgmentResponse();
                    var requestConverterResponse = requestConverter.Map(ackCode);

                    if (requestConverterResponse.IsSuccessful)
                    {
                        await publisher.PublishAsync(requestConverterResponse.Result, correlationId, routingKey);

                        Log.Debug("Responded with {@response} to the response queue", vifItemResponse);

                        Log.Information("Processed request {@request}", request);
                    }
                    else
                    {
                        throw new InvalidOperationException(requestConverterResponse.ValidationResults.ToString());
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing request {@request}", request);
                throw;
            }
        }

        private void ExitWithError(IEnumerable<ValidationResult> validationResults)
        {
            foreach (var validationResult in validationResults)
            {
                Log.Warning(validationResult.ErrorMessage);
            }

            throw new InvalidOperationException(string.Join(Environment.NewLine, validationResults));
        }
    }
}
