using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "JSON")]
    public class CopyImageRequest
    {
        public string FileName { get; set; }
        public string RoutingKey { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
