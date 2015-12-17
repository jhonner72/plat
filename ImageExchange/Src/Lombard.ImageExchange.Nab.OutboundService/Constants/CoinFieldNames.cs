namespace Lombard.ImageExchange.Nab.OutboundService.Constants
{
    public static class CoinFieldNames
    {
        //tranheader fields
        public const string RecordTypeIdentifier = "Record Type Identifier";

        public const string Version = "Version";
        public const string TransmissionDate = "Transmission Date";
        public const string SendingBsb = "BSB (Sending FI)";
        public const string ReceivingBsb = "BSB (Receiving FI)";
        public const string DateCreated = "Date File Created";
        public const string TimeCreated = "Time File Created";
        public const string SequenceNumber = "File Sequence Number";
        public const string RecordIdentifier = "Record Identifier";

        //addition item fields
        public const string BsbLedgerFi = "BSB (Ledger FI)";
        public const string BsbCollectingFi = "BSB (Collecting FI)";
        public const string TransactionCode = "Transaction Code";
        public const string Amount = "Amount";
        public const string BsbDepositorNominatedFi = "BSB (Depositor Nominated FI)";
        public const string DrawerAccountNumber = "Drawer Account Number";
        public const string DepositorsNominatedAccount = "Account Number of Depositors Nominated Account";
        public const string AuxiliaryDomestic = "Auxiliary Domestic";
        public const string ExtraAuxiliaryDomestic = "Extra Auxiliary Domestic";
        public const string BsbCapturingFi = "BSB (Capturing FI)";
        public const string CaptureDeviceIdentifier = "Capture Device Identifier";
        public const string TransactionIdentifier = "Transaction Identifier";
        public const string VoucherIndicator = "Voucher Indicator";
        public const string EndpointBank = "EndpointBank";
        public const string ManualRepair = "Manual Repair";
        public const string BatchNumber = "Batch Number";

        //additional trantrailer fields
        public const string FileCreditTotalAmount = "File Credit Total Amount";
        public const string FileDebitTotalAmount = "File Debit Total Amount";
        public const string FileCountNonValueItems = "File Count of Non Value Items";
        public const string FileCountCreditItems = "File Count of Credit Items";
        public const string FileCountDebitItems = "File Count of Debit Items";
    }
}