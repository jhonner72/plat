using System.Diagnostics.CodeAnalysis;
using Lombard.Common;

namespace Lombard.Adapters.Data.Domain
{
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
    public class DipsJobIdType : Enumeration<DipsJobIdType, string>
    {
        public static DipsJobIdType NabChqPod = new DipsJobIdType("NabChqPod");
        public static DipsJobIdType NabChqLBox = new DipsJobIdType("NabChqLBox");
        // In this case, value & displayName are the same...
        public DipsJobIdType(string value)
            : base(value, value)
        {

        }
    }
}
