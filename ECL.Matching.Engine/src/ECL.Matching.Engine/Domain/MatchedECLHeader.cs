namespace Lombard.ECLMatchingEngine.Service.Domain
{

    public class MatchedECLRecordHeader : MatchedECLRecord
    {
        [FieldConfiguration(Order = 1, Length = 1, FillerChar = "", FillerCharWhenEmpty = "")]
        public string StateId { get; set; }

        [FieldConfiguration(Order = 2, Length = 8, FillerChar = "", FillerCharWhenEmpty = "")]
        public string ProcessingDate { get; set; }

        [FieldConfiguration(Order = 3, Length = 102, FillerChar = "", FillerCharWhenEmpty = "")]
        public string Filler { get; set; }
    }
}
