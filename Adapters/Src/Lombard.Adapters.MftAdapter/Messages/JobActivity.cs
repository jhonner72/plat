using System;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.MftAdapter.Messages
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "JSON")]
    public class JobActivity : Activity
    {
        public new DateTime? responseDateTime { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
