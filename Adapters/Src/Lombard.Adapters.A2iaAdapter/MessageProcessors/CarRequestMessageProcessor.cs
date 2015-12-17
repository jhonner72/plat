using System;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using EasyNetQ;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Adapters.A2iaAdapter.Wrapper;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;
using Lombard.Common.Queues;
using Serilog;

namespace Lombard.Adapters.A2iaAdapter.MessageProcessors
{
    public class CarRequestMessageProcessor : IMessageProcessor<RecogniseBatchCourtesyAmountRequest>
    {
        private readonly IOcrProcessingService carService;
        private readonly IMapper<RecogniseBatchCourtesyAmountRequest, OcrBatch> requestMapper;
        private readonly IExchangePublisher<RecogniseBatchCourtesyAmountResponse> exchangePublisher;
        private readonly IMapper<OcrBatch, RecogniseBatchCourtesyAmountResponse> responseMapper;

        public CarRequestMessageProcessor(
            IOcrProcessingService carService,
            IMapper<RecogniseBatchCourtesyAmountRequest, OcrBatch> requestMapper,
            IExchangePublisher<RecogniseBatchCourtesyAmountResponse> exchangePublisher,
            IMapper<OcrBatch, RecogniseBatchCourtesyAmountResponse> responseMapper)
        {
            this.carService = carService;
            this.requestMapper = requestMapper;
            this.exchangePublisher = exchangePublisher;
            this.responseMapper = responseMapper;
        }

        public RecogniseBatchCourtesyAmountRequest Message { get; set; }
        
        public async Task ProcessAsync(CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;
            try
            {
                Log.Information("Processing RecogniseBatchCourtesyAmountRequest '{@request}', '{@correlationId}'",
                    request, correlationId);

                var batch = requestMapper.Map(Message);

                // Validate the file path
                ValidateImageFiles(batch);

                // Process           
                carService.ProcessBatch(batch);

                var response = responseMapper.Map(batch);

                await exchangePublisher.PublishAsync(response, correlationId);
                Log.Information("Successfully processed RecogniseBatchCourtesyAmountRequest {@CorrelationId}",
                    correlationId);

            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing RecogniseBatchCourtesyAmountRequest {@request}", request);
                throw new EasyNetQException("Error processing RecogniseBatchCourtesyAmountRequest", ex);
            }
        }

        public void ValidateImageFiles(OcrBatch batch)
        {
            var missingImages = batch.Vouchers
                .Where(v => !File.Exists(v.ImagePath))
                .Select(v => v.Id)
                .ToList();

            if (missingImages.Any())
            {
                throw new FileNotFoundException(string.Format("Could not find image files for the following vouchers : {0}", string.Join(", ", missingImages)));
            }
        }
    }
}
