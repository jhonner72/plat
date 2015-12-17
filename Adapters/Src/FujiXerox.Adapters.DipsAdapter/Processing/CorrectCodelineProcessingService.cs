using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class CorrectCodelineProcessingService : ProcessingService
    {
        private CorrectCodelineRequestSubscriber CorrectCodelineRequest { get; set; }
        private CorrectCodelineResponsePollingJob CorrectCodelineResponse { get; set; }

        public CorrectCodelineProcessingService(DipsConfiguration configuration, ILogger log) 
            : base(configuration, log, configuration.CorrectCodelineQueueName, configuration.CorrectCodelineExchangeName)
        {
            CorrectCodelineRequest = new CorrectCodelineRequestSubscriber(configuration, log, Consumer, InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            CorrectCodelineResponse = new CorrectCodelineResponsePollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            CorrectCodelineResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            CorrectCodelineResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            CorrectCodelineRequest.StartConsumer();
        }
    }
}