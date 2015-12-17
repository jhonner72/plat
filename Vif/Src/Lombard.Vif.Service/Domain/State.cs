using System.Diagnostics.CodeAnalysis;
using Lombard.Common;
    
    namespace Lombard.Vif.Service.Domain
{
    public class State : Enumeration<State, string>
    {
        // Vif Documentation / File Spec
        //2 representing NSW
        //3 representing VIC
        //4 representing QLD
        //5 representing SA
        //6 representing WA
        //8 representing NT

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State NSW = new State("NSW", "2");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State VIC = new State("VIC", "3");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State QLD = new State("QLD", "4");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State SA = new State("SA", "5");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State WA = new State("WA", "6");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State NT = new State("NT", "8");

        public State(string value, string displayName) : base(value, displayName)
        {

        }
    }
}