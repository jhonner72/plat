namespace Lombard.ECLMatchingEngine.Service.Domain
{

    public class MatchedECLRecordTrailer : MatchedECLRecord
    {

        [FieldConfiguration(Order = 1, Length = 6, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string TransactionNumberTotal { get; set; }

        [FieldConfiguration(Order = 2, Length = 105, FillerChar = " ", FillerCharWhenEmpty = " ")]
        public string Filler { get; set; }
    }
}
