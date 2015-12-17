using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class CorrectTransactionProcessingService : ProcessingService
    {
        private CorrectTransactionRequestSubscriber CorrectTransactionRequest { get; set; }
        private CorrectTransactionResponsePollingJob CorrectTransactionResponse { get; set; }

        public CorrectTransactionProcessingService(DipsConfiguration configuration, ILogger log) 
            : base(configuration, log, configuration.CorrectTransactionQueueName, configuration.CorrectTransactionExchangeName)
        {
            CorrectTransactionRequest = new CorrectTransactionRequestSubscriber(configuration, log, Consumer, InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            CorrectTransactionResponse = new CorrectTransactionResponsePollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            CorrectTransactionResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            CorrectTransactionResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            CorrectTransactionRequest.StartConsumer();
        }
    }
}