using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class GenerateCorrespondingVoucherProcessingService : ProcessingService
    {
        private GenerateCorrespondingVoucherRequestSubscriber GenerateCorrespondingVoucherRequest { get; set; }
        private GenerateCorrespondingVoucherResponsePollingJob GenerateCorrespondingVoucherResponse { get; set; }

        public GenerateCorrespondingVoucherProcessingService(DipsConfiguration configuration, ILogger log) 
            : base(configuration, log, configuration.GenerateCorrespondingVoucherQueueName, configuration.GenerateCorrespondingVoucherExchangeName)
        {
            GenerateCorrespondingVoucherRequest = new GenerateCorrespondingVoucherRequestSubscriber(configuration, log, Consumer, InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            GenerateCorrespondingVoucherResponse=new GenerateCorrespondingVoucherResponsePollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            GenerateCorrespondingVoucherResponse.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            GenerateCorrespondingVoucherResponse.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            GenerateCorrespondingVoucherRequest.StartConsumer();
        }
    }
}