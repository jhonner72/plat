namespace Lombard.Reporting.AdapterService.Utils
{
    using Lombard.Common.FileProcessors;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;

    public interface IReportGenerator
    {
        ValidatedResponse<string> RenderReport(ExecuteReportRequest request, string outputFolderPath);
    }
}
