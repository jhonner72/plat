using System.Diagnostics.CodeAnalysis;
using Lombard.Common;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
    public class SentFileType : Enumeration<SentFileType, string>
    {
        public static SentFileType Vif = new SentFileType("VIF");

        // In this case, value & displayName are the same...
        public SentFileType(string value)
            : base(value.ToUpper(), value.ToUpper())
        {

        }
    }
}
