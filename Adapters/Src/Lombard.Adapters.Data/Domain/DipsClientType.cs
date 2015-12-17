using System.Diagnostics.CodeAnalysis;
using Lombard.Common;

namespace Lombard.Adapters.Data.Domain
{
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
    public class DipsClientType : Enumeration<DipsClientType, string>
    {
        public static DipsClientType NabChq = new DipsClientType("NabChq");

        // In this case, value & displayName are the same...
        public DipsClientType(string value) : base(value, value)
        {

        }
    }
}
