using System;
using System.Collections.Concurrent;
using System.IO.Abstractions;
using System.Threading;
using System.Threading.Tasks;
using Lombard.Adapters.A2iaAdapter.Domain;
using Lombard.Adapters.A2iaAdapter.Mappers;
using Lombard.Adapters.A2iaAdapter.Messages;
using Lombard.Adapters.A2iaAdapter.Wrapper;
using Lombard.Adapters.A2iaAdapter.Wrapper.Dtos;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter.MessageProcessors
{
    public class CarRequestMessageProcessor : IMessageProcessor<RecogniseBatchCourtesyAmountRequest>
    {
        // ReSharper disable once InconsistentNaming
        private readonly IA2IAService a2iaService;
        private readonly ICourtesyAmountRequestBatchInfoMapper courtesyAmountRequestBatchInfoMapper;
        private readonly IFileSystem fileSystem;
        private readonly IExchangePublisher<RecogniseBatchCourtesyAmountResponse> exchangePublisher;
        private readonly IChequeOcrResponseToMessageResponseMapper chequeOcrResponseToMessageResponseMapper;

        public CarRequestMessageProcessor(
            // ReSharper disable once InconsistentNaming
            IA2IAService a2iaService, 
            IFileSystem fileSystem, 
            ICourtesyAmountRequestBatchInfoMapper courtesyAmountRequestBatchInfoMapper, 
            IExchangePublisher<RecogniseBatchCourtesyAmountResponse> exchangePublisher, 
            IChequeOcrResponseToMessageResponseMapper chequeOcrResponseToMessageResponseMapper)
        {
            this.a2iaService = a2iaService;
            this.fileSystem = fileSystem;
            this.courtesyAmountRequestBatchInfoMapper = courtesyAmountRequestBatchInfoMapper;
            this.exchangePublisher = exchangePublisher;
            this.chequeOcrResponseToMessageResponseMapper = chequeOcrResponseToMessageResponseMapper;
        }

        public RecogniseBatchCourtesyAmountRequest Message { get; set; }
        
        public async Task ProcessAsync(CancellationToken cancellationToken)
        {
            try
            {
                //messageBus.AcknowledgeMessageDelivered(request);
                var batchInfo = courtesyAmountRequestBatchInfoMapper.Map(Message);
                // TODO: Validate info
                //Log.Error("Queue message {@message} does not contain correct information. Cannot be processed.", message);

                #region Verify the files in BatchInfo

                //Log.Information("Start processing batch with correlation Id '{0}'.", batchInfo.CorrelationId);

                // Validate the file path
                Parallel.ForEach(batchInfo.ChequeImageInfos, item =>
                {
                    if (!fileSystem.File.Exists(item.Urn))
                    {
                        item.Status = 1;
                        item.Succes = false;
                        item.ErrorMessage = string.Format("Image file {0} does not exists.", item.Urn);
                        // TODO: Notify anyone?
                        Log.Information(item.ErrorMessage);
                    }
                });

                #endregion

                var listResponses = new ConcurrentBag<RecogniseCourtesyAmountResponse>();

                Parallel.ForEach(batchInfo.ChequeImageInfos, item =>
                {
                    //Log.Information("Start processing Cheque Image at '{0}'.", item.URN);
                    if (item.Status == 0)
                    {
                        var result = ProcessChequeIcrRequest(item).Result;
                        //Log.Debug("File '{0}' has been queued.", item.URN);
                        if (!result.Success)
                        {
                            listResponses.Add(result);
                        }
                    }
                    else
                    {
                        listResponses.Add(new RecogniseCourtesyAmountResponse
                        {
                            DocumentReferenceNumber = item.DocumentReferenceNumber,
                            Success = false,
                            ErrorMessage = item.ErrorMessage
                        });

                    }
                });

                Parallel.ForEach(batchInfo.ChequeImageInfos, item =>
                {
                    var result = GetLastChequeOcrResponse().Result;
                    if (result != null)
                    {
                        //Log.Debug("CAR for File '{0}' was {1}, score was {2}.", result.documentReferenceNumber, result.capturedAmount, result.confidenceLevel);
                        listResponses.Add(result);
                    }
                });

                var remaining = GetLastChequeOcrResponse().Result;
                while (remaining != null)
                {
                    Log.Debug("CAR for remaining File was {0}.", remaining.CapturedAmount);
                    listResponses.Add(remaining);
                    remaining = GetLastChequeOcrResponse().Result;
                }

                // publish to outbound queue
                var batchResponse = new RecogniseBatchCourtesyAmountResponse();
                batchResponse.Voucher = listResponses.ToArray();

                await exchangePublisher.PublishAsync(batchResponse);
                //.PopulateOutboundQueue(batchResponse, inboundMessage.BasicProperties.CorrelationId);
            }
            catch (Exception ex) // Specific exceptions
            {
                Log.Error(ex, "Error occurred while processing a message.");
                throw;
            }
        }

        private async Task<RecogniseCourtesyAmountResponse> ProcessChequeIcrRequest(ChequeImageInfo item)
        {
            var response = new RecogniseCourtesyAmountResponse();

            Log.Debug("Starting ICR for Image: " + item.Urn + " at " + DateTime.Now.ToString("dd/MM/yyyy HH:mm:ss.fff"));

            // Invoke A2IA
            var chequeOcrRequest = new ChequeOcrRequest
            {
                FilePath = item.Urn,
                DocumentReferenceNumber = item.DocumentReferenceNumber,
                CorrelationId = item.CorrelationId
            };

            // Response
            response.DocumentReferenceNumber = chequeOcrRequest.DocumentReferenceNumber;

            var chequeOcrResponse = a2iaService.ProcessChequeOcrInitiateRequest(chequeOcrRequest);
            if (chequeOcrResponse == null || !chequeOcrResponse.Success)
            {
                response.Success = false;
                response.ErrorMessage = chequeOcrResponse != null ? chequeOcrResponse.ErrorMessage : "chequeOcrResponse is null";
                Console.WriteLine(response.ErrorMessage);
            }
            else
            {
                response.Success = true;
            }

            return response;
        }

        private async Task<RecogniseCourtesyAmountResponse> GetLastChequeOcrResponse()
        {
            RecogniseCourtesyAmountResponse response = null;

            var chequeOcrResponse = a2iaService.GetLastChequeOcrResponse();

            if (chequeOcrResponse != null)
            {
                if (chequeOcrResponse.Success)
                {
                    response = chequeOcrResponseToMessageResponseMapper.Map(chequeOcrResponse);
                    Log.Debug("CAR for File '{0}' was {1}, score was {2}.",
                        chequeOcrResponse.DocumentReferenceNumber,
                        chequeOcrResponse.AmountResult,
                        chequeOcrResponse.AmountScore);

                    return response;
                }
                else
                {
                    response = new RecogniseCourtesyAmountResponse();
                    response.DocumentReferenceNumber = chequeOcrResponse.DocumentReferenceNumber;
                    response.Success = chequeOcrResponse.Success;
                    response.ErrorMessage = chequeOcrResponse.ErrorMessage;
                    Log.Information(response.ErrorMessage);
                    Log.Debug("Finished ICR for Image: " + chequeOcrResponse.FilePath + " at " + DateTime.Now.ToString("dd/MM/yyyy HH:mm:ss.fff"));

                    // publish to error queue
                    //rabbitService.PopulateOutboundQueue(response);
                }
            }

            return response;
        }

    }
}
