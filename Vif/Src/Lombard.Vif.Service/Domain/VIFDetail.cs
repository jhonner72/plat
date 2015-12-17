namespace Lombard.Vif.Service.Domain
{
    /// <summary>
    /// <c>VifDetail</c> class representing 1 line of vif detail
    /// </summary>
    public class VifDetail : Vif
    {
        /// <summary>
        /// Gets or sets the Ledger BSB. Starting on character #2
        /// </summary>
        /// <value>
        /// The Ledger BSB.
        /// </value>
        [OutputFormat(Order = 1, FieldWidth = 6)]
        public string LEDGER_BSB { get; set; }

        /// <summary>
        /// Gets or sets the Deposit account BSB. Starting on character #8
        /// </summary>
        /// <value>
        /// The Deposit account BSB.
        /// </value>
        [OutputFormat(Order = 2, FieldWidth = 6, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string DEPOSIT_ACCOUNT_BSB { get; set; }

        /// <summary>
        /// Gets or sets the Negotiation BSB. Starting on character #14
        /// </summary>
        /// <value>
        /// The Negotiation BSB
        /// </value>
        [OutputFormat(Order = 3, FieldWidth = 6)]
        public string NEGOTIATING_BSB { get; set; }

        /// <summary>
        /// Gets or sets the Batch number. Starting on character #20
        /// </summary>
        /// <value>
        /// The Batch number.
        /// </value>
        [OutputFormat(Order = 4, FieldWidth = 4)]
        public string BATCH_NUMBER { get; set; }

        /// <summary>
        /// Gets or sets the Transaction id. Starting on character #24
        /// </summary>
        /// <value>
        /// The Transaction id.
        /// </value>
        [OutputFormat(Order = 5, FieldWidth = 4, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string TRANSACTION_ID { get; set; }

        /// <summary>
        /// Gets or sets the transaction code. Starting on character #28
        /// </summary>
        /// <value>
        /// The transaction code.
        /// </value>
        [OutputFormat(Order = 6, FieldWidth = 3, FillerChar = " ", FillerCharWhenEmpty = " ")]
        public string TRANSACTION_CODE { get; set; }

        /// <summary>
        /// Gets or sets the Unique trace id. Starting on character #31
        /// </summary>
        /// <value>
        /// The Unique trace id.
        /// </value>
        [OutputFormat(Order = 7, FieldWidth = 12, FillerChar = " ", FillerCharWhenEmpty = " ")] //Filler
        public string UNIQUE_TRACE_ID { get; set; }

        /// <summary>
        /// Gets or sets the Transaction amount. Starting on character #43
        /// </summary>
        /// <value>
        /// The Transaction amount.
        /// </value>
        [OutputFormat(Order = 8, FieldWidth = 12, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string TRANSACTION_AMOUNT { get; set; }

        /// <summary>
        /// Gets or sets the Debit/credit flag. Starting on character #55
        /// </summary>
        /// <value>
        /// The Debit/credit flag.
        /// </value>
        [OutputFormat(Order = 9, FieldWidth = 1)]
        public string DEBIT_CREDIT_CODE { get; set; }

        /// <summary>
        /// Gets or sets the Multiple credit flag. Starting on character #56
        /// </summary>
        /// <value>
        /// The Multiple credit flag.
        /// </value>
        [OutputFormat(Order = 10, FieldWidth = 1)]
        public string MULTIPLE_CREDIT_FLAG { get; set; }

        /// <summary>
        /// Gets or sets the Drawer account number. Starting on character #57
        /// </summary>
        /// <value>
        /// The Drawer account number.
        /// </value>
        [OutputFormat(Order = 11, FieldWidth = 10, FillerChar = " ", FillerCharWhenEmpty = " ")] //Filler
        public string DRAWER_ACCOUNT_NUMBER { get; set; }

        /// <summary>
        /// Gets or sets the Deposit account number. Starting on character #67
        /// </summary>
        /// <value>
        /// The Deposit account number.
        /// </value>
        [OutputFormat(Order = 12, FieldWidth = 10, FillerChar = " ", FillerCharWhenEmpty = " ")] //Filler
        public string DEPOSIT_ACCOUNT_NUMBER { get; set; }

        /// <summary>
        /// Gets or sets the AuxDom1 – shared. Starting on character #77
        /// </summary>
        /// <value>
        /// The AuxDom1 – shared.
        /// </value>
        [OutputFormat(Order = 13, FieldWidth = 9, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string AUX_DOM_1_SHARED { get; set; }

        /// <summary>
        /// Gets or sets the ExAuxDom1 – shared. Starting on character #86
        /// </summary>
        /// <value>
        /// The ExAuxDom1 – shared.
        /// </value>
        [OutputFormat(Order = 14, FieldWidth = 15, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string EX_AUX_DOM_1_SHARED { get; set; }

        /// <summary>
        /// Gets or sets the AuxDom2 - self. Starting on character #101
        /// </summary>
        /// <value>
        /// The AuxDom2 - self.
        /// </value>
        [OutputFormat(Order = 15, FieldWidth = 9, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string AUX_DOM_2_SELF { get; set; }

        /// <summary>
        /// Gets or sets the ExAuxDom2 – self. Starting on character #110
        /// </summary>
        /// <value>
        /// The ExAuxDom2 – self.
        /// </value>
        [OutputFormat(Order = 16, FieldWidth = 15, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string EX_AUX_DOM_2_SELF { get; set; }

        /// <summary>
        /// Gets or sets the Deposit cheque item count. Starting on character #125
        /// </summary>
        /// <value>
        /// The Deposit cheque item count.
        /// </value>
        [OutputFormat(Order = 17, FieldWidth = 5, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string DEPOSIT_CHEQUE_ITEM_COUNT { get; set; }

        /// <summary>
        /// Gets or sets the Delayed voucher indicator. Starting on character #130
        /// </summary>
        /// <value>
        /// The Delayed voucher indicator.
        /// </value>
        [OutputFormat(Order = 18, FieldWidth = 1)]
        public string DELAY_VOUCHER_INDICATOR { get; set; }

        /// <summary>
        /// Gets or sets the Manual repair flag. Starting on character #131
        /// </summary>
        /// <value>
        /// The Manual repair flag.
        /// </value>
        [OutputFormat(Order = 19, FieldWidth = 1)]
        public string MANUAL_REPAIR_FLAG { get; set; }

        /// <summary>
        /// Gets or sets the Presentation mode. Starting on character #132
        /// </summary>
        /// <value>
        /// The Presentation mode.
        /// </value>
        [OutputFormat(Order = 20, FieldWidth = 1)]
        public string PRESENTATION_MODE { get; set; }

        /// <summary>
        /// Gets or sets the Pocket cut. Starting on character #133
        /// </summary>
        /// <value>
        /// The Pocket cut.
        /// </value>
        [OutputFormat(Order = 21, FieldWidth = 2, FillerChar = "0", FillerCharWhenEmpty = "0")] //Filler
        public string POCKET_CUT { get; set; }

        /// <summary>
        /// Gets or sets the Batch header reference. Starting on character #135
        /// </summary>
        /// <value>
        /// The Batch header reference.
        /// </value>
        [OutputFormat(Order = 22, FieldWidth = 4)]
        public string BATCH_HEADER_REFERENCE { get; set; }

        /// <summary>
        /// Gets or sets the Channel id. Starting on character #139
        /// </summary>
        /// <value>
        /// The Channel id.
        /// </value>
        [OutputFormat(Order = 23, FieldWidth = 2)]
        public string CHANNEL_ID { get; set; }

        /// <summary>
        /// Gets or sets the empty space fillers. Starting on character #141
        /// </summary>
        /// <value>
        /// The empty space fillers.
        /// </value>
        [OutputFormat(Order = 24, FieldWidth = 10, FillerChar = " ", FillerCharWhenEmpty = " ")] //Filler
        public string EMPTY_SPACE_FILLER { get; set; }
    }
}