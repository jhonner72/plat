using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsDbIndex
    {
        [MaxLength(5)]
        public string DEL_IND { get; set; }
        [MaxLength(8)]
        public string BATCH { get; set; }
        [MaxLength(9)]
        public string TRACE { get; set; }
        [MaxLength(5)]
        public string SEQUENCE { get; set; }
        [MaxLength(5)]
        public string TABLE_NO { get; set; }
        [MaxLength(10)]
        public string REC_NO { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
