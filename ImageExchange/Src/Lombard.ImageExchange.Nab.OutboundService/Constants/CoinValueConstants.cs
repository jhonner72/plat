namespace Lombard.ImageExchange.Nab.OutboundService.Constants
{
    public static class CoinValueConstants
    {
        /*
         * ************************************
         * HEADERS / ITEMS / TRAILERS- IMAGE EXCHANGE
         * ************************************
         */

        // Header Constants - ImageExhange
        public const string HeaderRecordType = "9000";
        public const string HeaderVersion = "20";
        public const string HeaderBsbSendingFi = "000000";
        public const string HeaderBsbReceivingFi = "000000";
        public const string HeaderFileSequenceNumber = "000";
        public const string HeaderRecordIdentifier = "2010";

        // Item Constants - ImageExchange
        public const string ItemRecordType = "2010";
        public const string ItemVersion = "20";
        public const string ItemBsbDepositorNominatedFi = "";
        public const string ItemDepositorsNominatedAccount = "";
        public const string ItemCaptureDeviceIdentifier = "";
        public const string ItemManualRepair = "";

        // Trailer Constants - ImageExchange
        public const string TrailerRecordType = "9090";
        public const string TrailerVersion = "20";

        /*
         * ************************************
         * HEADERS / ITEMS / TRAILERS- AGENCY BANKS
         * ************************************
         */

        // Header Constants- Other Agency
        public const string OtherAgencyHeaderRecordType = "0001";
        public const string OtherAgencyHeaderVersion = "01";
        public const string OtherAgencyHeaderFileSequenceNumber = "001";

        // Item Constants - Other Agency
        public const string OtherAgencyItemRecordType = "0002";
        public const string OtherAgencyItemVersion = "01";

        //Trailer Constants - Other Agency
        public const string OtherAgencyTrailerRecordType = "0003";
        public const string OtherAgencyTrailerVersion = "01";
        public const string OtherAgencyFileCreditTotalAmount = "0";
        public const string OtherAgencyFileDebitTotalAmount = "0";
        public const string OtherAgencyFileCountCreditItems = "0";
        public const string OtherAgencyFileCountDebitItems = "0";

        /*
        * ************************************
        * ITEMS - CUSCAL
        * ************************************
        */

        public const string CuscalFormName = "Cheque";


        /*
        * ************************************
        * COMMON ITEMS
        * ************************************
        */

        // Format Constants
        public const string CoinDateFormatString = "yyyyMMdd";
        public const string CoinTimeFormatString = "HHmmss";
        public const string CoinAmountFormatString = "0";

        //Transaction code credit ranges taken from ref APCS Procedures Appendix B:
        public const int CreditLower1 = 50;
        public const int CreditHigher1 = 99;
        public const int CreditLower2 = 950;
        public const int CreditHigher2 = 999;

        //Coin EOD Constants taken from APCA test Strategy Appendix 6
        public const string EodFileNamePrefix = "IMGEXCHEOD";
        public const string EodFileNameExtension = "DAT";

        // Coin File Name
        public const string CoinFilePrefix = "IMGEXCH";
        public const string CoinFileExtension = "xml";
    }
}