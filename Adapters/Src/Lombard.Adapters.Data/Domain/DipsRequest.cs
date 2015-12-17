using System;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsRequest
    {
        [MaxLength(50)]
        public string guid_name { get; set; }
        [MaxLength(2000)]
        public string payload { get; set; }
        public DateTime request_time { get; set; }
        public bool? RequestCompleted { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
