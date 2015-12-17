namespace Lombard.Reporting.AdapterService
{
    using Lombard.Common.Queues;
    using Lombard.Reporting.AdapterService.Configuration;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;
    using Serilog;

    public class ServiceRunner
    {
        private readonly IQueueConfiguration queueConfiguration;
        private readonly IQueueConsumer<ExecuteBatchReportRequest> requestConsumer;
        private readonly IExchangePublisher<ExecuteBatchReportResponse> responsePublisher;
        
        public ServiceRunner(
            IQueueConfiguration queueConfiguration,
            IQueueConsumer<ExecuteBatchReportRequest> requestConsumer,
            IExchangePublisher<ExecuteBatchReportResponse> responsePublisher)
        {
            this.queueConfiguration = queueConfiguration;
            this.requestConsumer = requestConsumer;
            this.responsePublisher = responsePublisher;
        }

        public void Start()
        {
            this.StartListeningForInputMessages();

            Log.Information("Reporting Adapter Service Started");
        }

        public void Stop()
        {
            this.requestConsumer.Dispose();

            Log.Information("Reporting Adapter Service Stopped");
        }

        private void StartListeningForInputMessages()
        {
            this.requestConsumer.Subscribe(this.queueConfiguration.RequestExchangeName + ".queue");
            this.responsePublisher.Declare(this.queueConfiguration.ResponseExchangeName);
        }
    }
}
