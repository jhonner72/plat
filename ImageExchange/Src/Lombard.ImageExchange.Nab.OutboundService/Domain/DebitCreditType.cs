using System.Diagnostics.CodeAnalysis;

using Lombard.Common;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class DebitCreditType : Enumeration<DebitCreditType, string>
    {
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static DebitCreditType Debit = new DebitCreditType("D");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static DebitCreditType Credit = new DebitCreditType("C");

        // In this case, value & displayName are the same...
        public DebitCreditType(string value) : base(value, value)
        {

        }
    }
}