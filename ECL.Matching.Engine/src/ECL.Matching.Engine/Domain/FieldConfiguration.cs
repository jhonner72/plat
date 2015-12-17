namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using System.ComponentModel;

    public class FieldConfiguration : DescriptionAttribute
    {
        public int Order { get; set; }
        public int Length {get; set;}
        public string FillerChar { get; set; }
        public string FillerCharWhenEmpty { get; set; }

        public FieldConfiguration()
        {
            Order = 0;
            Length = 0;
            FillerChar = "";
            FillerCharWhenEmpty = "";
        }
    }
}
