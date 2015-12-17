using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsQueue
    {
        [MaxLength(33)]
        public string S_LOCATION { get; set; }
        [MaxLength(16)]
        public string S_PINDEX { get; set; }
        [MaxLength(10)]
        public string S_LOCK { get; set; }
        [MaxLength(80)]
        public string S_CLIENT { get; set; }
        [MaxLength(128)]
        public string S_JOB_ID { get; set; }
        [MaxLength(5)]
        public string S_MODIFIED { get; set; }
        [MaxLength(5)]
        public string S_COMPLETE { get; set; }
        [MaxLength(8)]
        public string S_BATCH { get; set; }
        [MaxLength(9)]
        public string S_TRACE { get; set; }
        [MaxLength(8)]
        public string S_SDATE { get; set; }
        [MaxLength(8)]
        public string S_STIME { get; set; }
        [MaxLength(10)]
        public string S_UTIME { get; set; }
        [MaxLength(5)]
        public string S_PRIORITY { get; set; }
        [MaxLength(80)]
        public string S_IMG_PATH { get; set; }
        [MaxLength(250)]
        public string S_USERNAME { get; set; }
        [MaxLength(128)]
        public string S_SELNSTRING { get; set; }
        [MaxLength(32)]
        public string S_VERSION { get; set; }
        [MaxLength(17)]
        public string S_LOCKUSER { get; set; }
        [MaxLength(17)]
        public string S_LOCKMODULENAME { get; set; }
        [MaxLength(10)]
        public string S_LOCKUNITID { get; set; }
        [MaxLength(17)]
        public string S_LOCKMACHINENAME { get; set; }
        [MaxLength(10)]
        public string S_LOCKTIME { get; set; }
        [MaxLength(9)]
        public string S_PROCDATE { get; set; }
        [MaxLength(5)]
        public string S_REPORTED { get; set; }
        public string CorrelationId { get; set; }
        public bool ResponseCompleted { get; set; }
        public byte[] ConcurrencyToken { get; set; }
        [MaxLength(100)]
        public string RoutingKey { get; set; }
    }
    // ReSharper restore InconsistentNaming
}
