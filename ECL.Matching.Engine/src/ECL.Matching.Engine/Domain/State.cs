namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using Lombard.Common;
    using System.Diagnostics.CodeAnalysis;

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
        public static State NSW = new State("2", "NSW");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State VIC = new State("3", "VIC");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State QLD = new State("4", "QLD");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State SA = new State("5", "SA");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State WA = new State("6", "WA");

        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Reviewed.")]
        public static State NT = new State("8", "NT");

        public State(string value, string displayName)
            : base(value, displayName)
        {

        }
    }
}