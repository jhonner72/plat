using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.A2iaAdapter.Wrapper.UnitTests
{
    [SuppressMessage("ReSharper", "InconsistentNaming")]
    public class A2iaTestData
    {
        public string CorrelationId { get; set; }
        public string ImageFileName { get; set; }

        public string ImageFilePath { get; set; }
        public string Amount { get; set; }
        public string Date { get; set; }
        public string Codeline { get; set; }

        public int ImageRotation { get; set; }
    }
}
