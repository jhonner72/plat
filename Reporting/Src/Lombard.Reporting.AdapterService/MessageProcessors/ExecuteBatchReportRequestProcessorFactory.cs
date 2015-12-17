namespace Lombard.Reporting.AdapterService.MessageProcessors
{
    using Autofac;
    using Lombard.Common.Queues;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;
    using Lombard.Reporting.AdapterService.Utils;

    public class ExecuteBatchReportRequestProcessorFactory : IMessageProcessorFactory<ExecuteBatchReportRequest>
    {
        private readonly IComponentContext container;

        public ExecuteBatchReportRequestProcessorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IMessageProcessor<ExecuteBatchReportRequest> CreateMessageProcessor(ExecuteBatchReportRequest message)
        {
            // Construct an instance per request. The dependencies should not be injected to this contructor, 
            // so that we can request new instances as we construct the MessageProcessor:
            var publisher = this.container.Resolve<IExchangePublisher<ExecuteBatchReportResponse>>();
            var reportGeneratorFactory = this.container.Resolve<IReportGeneratorFactory>();
            var pathHelper = this.container.Resolve<IPathHelper>();

            return new ExecuteBatchReportRequestProcessor(publisher, reportGeneratorFactory, pathHelper)
            {
                Message = message
            };
        }
    }
}
