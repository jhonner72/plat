using System;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsResponseData
    {
        [MaxLength(50)]
        public string guid_name { get; set; }
        [MaxLength(60)]
        public string doc_ref_number { get; set; }
        public string payload { get; set; }
        public string front_image { get; set; }
        public string rear_image { get; set; }

        
    }
    // ReSharper restore InconsistentNaming
}
