using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class ValidateTransctionRequestProcessingService : ProcessingService
    {
        private ValidateTransactionRequestSubscriber ValidateTransactionRequest { get; set; }
        private ValidateTransactionResponsePollingJob ValidateTransactionResponse { get; set; }

        public ValidateTransctionRequestProcessingService(DipsConfiguration configuration, ILogger log)
            : base(configuration, log, null, configuration.ValidateTransactionExchangeName)
        {
            ValidateTransactionRequest = new ValidateTransactionRequestSubscriber(configuration, log, Consumer, InvalidExchange,
                InvalidRoutingKey, RecoverableRoutingKey);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            ValidateTransactionResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            ValidateTransactionResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            ValidateTransactionRequest.StartConsumer();
        }
    }
}