using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class CheckThirdPartyProcessingService:ProcessingService
    {
        private CheckThirdPartyRequestSubscriber CheckThirdPartyRequest { get; set; }
        private CheckThirdPartyResponsePollingJob CheckThirdPartyResponse { get; set; }

        public CheckThirdPartyProcessingService(DipsConfiguration configuration, ILogger log)
            : base(configuration, log, configuration.CheckThirdPartyQueueName, configuration.CheckThirdPartyExchangeName)
        {
            CheckThirdPartyRequest = new CheckThirdPartyRequestSubscriber(configuration, log, Consumer, InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            CheckThirdPartyResponse = new CheckThirdPartyResponsePollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            CheckThirdPartyResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            CheckThirdPartyResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            CheckThirdPartyRequest.StartConsumer();
        }
    }
}