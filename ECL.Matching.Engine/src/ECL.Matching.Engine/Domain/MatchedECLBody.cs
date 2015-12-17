namespace Lombard.ECLMatchingEngine.Service.Domain
{

    public class MatchedECLRecordBody : MatchedECLRecord
    {

        [FieldConfiguration(Order = 1, Length = 6, FillerChar = " ", FillerCharWhenEmpty = " ")]
        public string LedgerBsbCode { get; set; }

        [FieldConfiguration(Order = 2, Length = 10, FillerChar = " ", FillerCharWhenEmpty = " ")]
        public string DrawerAccountNumber { get; set; }

        [FieldConfiguration(Order = 3, Length = 3, FillerChar = "", FillerCharWhenEmpty = "")]
        public string TransactionTypeCode { get; set; }

        [FieldConfiguration(Order = 4, Length = 9, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string ChequeSerialNumber { get; set; }

        [FieldConfiguration(Order = 5, Length = 12, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string ExtraAuxDom { get; set; }

        [FieldConfiguration(Order = 6, Length = 10, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string TransactionAmount { get; set; }

        [FieldConfiguration(Order = 7, Length = 2, FillerChar = " ", FillerCharWhenEmpty = " ")]
        public string Machine_Olp_Distribution_Number { get; set; }


    }
}
