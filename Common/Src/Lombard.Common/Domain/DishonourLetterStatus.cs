using Lombard.Common;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Common.Domain
{
    /// <summary>
    /// The status of the dishonours report
    /// </summary>
    public class DishonourLetterStatus : Enumeration<DishonourLetterStatus, string>
    {
        public DishonourLetterStatus(string value) : base(value, value)
        {
            
        }

        /// <summary>
        /// The file has not been queued for processing
        /// </summary>
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Enum")]
        public static DishonourLetterStatus None = new DishonourLetterStatus(string.Empty);
        
        /// <summary>
        /// The file is currently being processed
        /// </summary>
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Enum")]
        public static DishonourLetterStatus LetterRaised = new DishonourLetterStatus("Dishonour Letter Raised");

        /// <summary>
        /// The file is currently being processed
        /// </summary>
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Enum")]
        public static DishonourLetterStatus LetterFailed = new DishonourLetterStatus("Dishonour Letter Failed");

        /// <summary>
        /// The file has been processed successfully
        /// </summary>
        [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1401:FieldsMustBePrivate", Justification = "Enum")]
        public static DishonourLetterStatus LetterIssued = new DishonourLetterStatus("Dishonour Letter Issued");
    }
}
