using System;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsResponseDone
    {
        [MaxLength(50)]
        public string guid_name { get; set; }
        public DateTime response_time { get; set; }
        public int number_of_results { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
