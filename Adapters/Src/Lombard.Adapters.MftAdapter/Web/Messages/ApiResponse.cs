using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    public class ApiResponse
    {
        [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "JSON")]
        // ReSharper disable once InconsistentNaming
        public string message { get; set; }
    }
}
