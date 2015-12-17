
namespace Lombard.Vif.Service.Domain
{
    /// <summary>
    /// <c>VifHeader</c> class representing 1 line of vif header
    /// </summary>
    public class VifHeader : Vif
    {
        /// <summary>
        /// Gets or sets the State number. Starting on character #2
        /// </summary>
        /// <value>
        /// The State number.
        /// </value>
        [OutputFormat(Order = 1, FieldWidth = 1)]
        public string STATE_NUMBER { get; set; }

        /// <summary>
        /// Gets or sets the Run number.
        /// </summary>
        /// <value>
        /// The Run number.
        /// </value>
        [OutputFormat(Order = 2, FieldWidth = 4)]
        public string RUN_NUMBER { get; set; }

        /// <summary>
        /// Gets or sets the Bank code.
        /// </summary>
        /// <value>
        /// The Bank code.
        /// </value>
        [OutputFormat(Order = 3, FieldWidth = 3)]
        public string BANK_CODE { get; set; }

        /// <summary>
        /// Gets or sets the Process date.
        /// </summary>
        /// <value>
        /// The Process date.
        /// </value>
        [OutputFormat(Order = 4, FieldWidth = 8)]
        public string PROCESS_DATE { get; set; }

        /// <summary>
        /// Gets or sets the Capture BSB.
        /// </summary>
        /// <value>
        /// The Capture BSB.
        /// </value>
        [OutputFormat(Order = 7, FieldWidth = 6)]
        public string CAPTURE_BSB { get; set; }

        /// <summary>
        /// Gets or sets the Collecting BSB.
        /// </summary>
        /// <value>
        /// The Collecting BSB.
        /// </value>
        [OutputFormat(Order = 8, FieldWidth = 6)]
        public string COLLECTING_BSB { get; set; }

        /// <summary>
        /// Gets or sets the Bundle type.
        /// </summary>
        /// <value>
        /// The Bundle type.
        /// </value>
        [OutputFormat(Order = 9, FieldWidth = 1)]
        public string BUNDLE_TYPE { get; set; }

        /// <summary>
        /// Gets or sets the empty space fillers. Starting on character #141
        /// </summary>
        /// <value>
        /// The empty space fillers.
        /// </value>
        [OutputFormat(Order = 10, FieldWidth = 120, FillerChar =" ", FillerCharWhenEmpty=" ")]
        public string EMPTY_SPACE_FILLER { get; set; }
    }
}
