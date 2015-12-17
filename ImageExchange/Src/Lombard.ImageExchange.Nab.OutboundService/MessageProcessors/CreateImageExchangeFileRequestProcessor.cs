using Lombard.Common.FileProcessors;
using Lombard.Common.Queues;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices;
using Serilog;
using Serilog.Context;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace Lombard.ImageExchange.Nab.OutboundService.MessageProcessors
{
    public class CreateImageExchangeFileRequestProcessor : IMessageProcessor<CreateImageExchangeFileRequest>
    {
        private readonly IExchangePublisher<CreateImageExchangeFileResponse> publisher;
        private readonly IMessageToBatchConverter messageToBatchConverter;
        private readonly IBatchCreator batchCreator;
        private readonly ICoinFileCreator coinFileCreator;

        public CreateImageExchangeFileRequest Message { get; set; }

        public CreateImageExchangeFileRequestProcessor(
            IExchangePublisher<CreateImageExchangeFileResponse> publisher, 
            IMessageToBatchConverter messageToBatchConverter,
            IBatchCreator batchCreator,
            ICoinFileCreator coinFileCreator)
        {
            this.publisher = publisher;
            this.messageToBatchConverter = messageToBatchConverter;
            this.batchCreator = batchCreator;
            this.coinFileCreator = coinFileCreator;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            try
            {
                using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
                {
                    Log.Information("Processing CreateImageExchangeFileRequest {@request}", request);
                
                    var messageConverterResponse = messageToBatchConverter.Map(request);

                    if (!messageConverterResponse.IsSuccessful)
                    {
                        ExitEwithError(messageConverterResponse.ValidationResults, correlationId);  // throws an exception to be caught below
                    }

                    CreateImageExchangeFileResponse successfulResponse = await CreateBatchImageExchangeFile(messageConverterResponse);

                    await publisher.PublishAsync(successfulResponse, correlationId, routingKey);

                    Log.Debug("Responded with {@CreateImageExchangeFileResponse} to the response queue", successfulResponse);

                    Log.Information("Processed CreateImageExchangeFileRequest {@CreateImageExchangeFileRequest}", request);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing CreateImageExchangeFileRequest {@CreateImageExchangeFileRequest}", request);
                throw;
            }
        }

        private async Task<CreateImageExchangeFileResponse> CreateBatchImageExchangeFile(ValidatedResponse<Batch> messageConverterResponse)
        {
            var vouchers = messageConverterResponse.Result.OutboundVouchers.ToList();

            var outboundVoucherFile = batchCreator.Execute(messageConverterResponse.Result);
            outboundVoucherFile.Vouchers = vouchers;

            await coinFileCreator.ProcessAsync(outboundVoucherFile);

            if (messageConverterResponse.Result.OperationType == "Cuscal")
            {
                return new CreateImageExchangeFileResponse
                {
                    imageExchangeFilename = outboundVoucherFile.ZipFileName
                };
            }
            else if (messageConverterResponse.Result.OperationType == "ImageExchange")
            {
                return new CreateImageExchangeFileResponse
                {
                    imageExchangeFilename = outboundVoucherFile.ZipFileName
                };
            }
            else
            {
                return new CreateImageExchangeFileResponse
                {
                    imageExchangeFilename = outboundVoucherFile.FileName
                };
            }
           
        }

        private void ExitEwithError(IEnumerable<ValidationResult> validationResults, string correlationID)
        {
            foreach (var validationResult in validationResults)
            {
                Log.Error(validationResult.ErrorMessage);
            }

            throw new InvalidOperationException(validationResults.AsString());
        }
    }
}
