namespace Lombard.Reporting.AdapterService.Utils
{
    using Autofac;

    public interface IReportGeneratorFactory
    {
        IReportGenerator GetReportGenerator(ReportType reportType);
    }

    public class ReportGeneratorFactory : IReportGeneratorFactory
    {
        private readonly IComponentContext container;

        public ReportGeneratorFactory(IComponentContext container)
        {
            this.container = container;
        }

        public IReportGenerator GetReportGenerator(ReportType reportType)
        {
            return this.container.ResolveKeyed<IReportGenerator>(reportType);
        }
    }
}