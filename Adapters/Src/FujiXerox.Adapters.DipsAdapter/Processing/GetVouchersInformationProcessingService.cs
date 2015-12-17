using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using FujiXerox.Adapters.DipsAdapter.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public class GetVouchersInformationProcessingService : ProcessingService
    {
        private GetVouchersInformationResponseSubscriber GetVoucherInformationResponse { get; set; }
        private GetVouchersInformationRequestPollingJob GetVoucherInformationRequest { get; set; }

        public GetVouchersInformationProcessingService(DipsConfiguration configuration, ILogger log) 
            : base(configuration, log, configuration.GetPoolVouchersQueueName, configuration.GetPoolVouchersExchangeName)
        {
            GetVoucherInformationResponse = new GetVouchersInformationResponseSubscriber(configuration, log, Consumer, InvalidExchange, InvalidRoutingKey, RecoverableRoutingKey);
            GetVoucherInformationRequest = new GetVouchersInformationRequestPollingJob(configuration, log, Exchange);
        }

        public override void Start()
        {
            base.Start();
            StartConsuming();
            GetVoucherInformationRequest.Start(TimeSpan.FromSeconds(PollingIntervalSecs));
        }

        public override void Stop()
        {
            base.Stop();
            GetVoucherInformationRequest.ShutdownJob = true;
        }

        protected override void StartConsuming()
        {
            GetVoucherInformationResponse.StartConsumer();
        }
    }
}