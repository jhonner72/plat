
namespace Lombard.Vif.Service.Domain
{
    public class VifTrailer : Vif
    {
        [OutputFormat(Order = 1, FieldWidth = 8)]
        public string PROCESS_DATE { get; set; }

        [OutputFormat(Order = 2, FieldWidth = 15, FillerChar = "0", FillerCharWhenEmpty= "0")]
        public string TOTAL_CREDIT_AMOUNT { get; set; }

        [OutputFormat(Order = 3, FieldWidth = 15, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string TOTAL_DEBIT_AMOUNT { get; set; }

        [OutputFormat(Order = 4, FieldWidth = 5, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string TOTAL_NUMBER_OF_DEBITS { get; set; }

        [OutputFormat(Order = 5, FieldWidth = 5, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string TOTAL_NUMBER_OF_CREDITS { get; set; }

        [OutputFormat(Order = 6, FieldWidth = 40, FillerChar = "0", FillerCharWhenEmpty = "0")]
        public string FILLER { get; set; }
    }
}
