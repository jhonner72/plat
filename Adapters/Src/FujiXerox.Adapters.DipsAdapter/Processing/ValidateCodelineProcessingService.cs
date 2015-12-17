using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class ValidateCodelineProcessingService : ProcessingService
    {
        private ValidateCodelineRequestSubscriber ValidateCodelineRequest { get; set; }
        private ValidateCodelineResponsePollingJob ValidateCodelineResponse { get; set; }

        public ValidateCodelineProcessingService(DipsConfiguration configuration, ILogger log) 
            : base(configuration, log, configuration.ValidateCodelineQueueName, configuration.ValidateTransactionExchangeName)
        {
            ValidateCodelineRequest = new ValidateCodelineRequestSubscriber(configuration, log, Consumer,
                InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            ValidateCodelineResponse = new ValidateCodelineResponsePollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            ValidateCodelineResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            ValidateCodelineResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            ValidateCodelineRequest.StartConsumer();
        }
    }
}