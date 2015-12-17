
using System.ComponentModel;

namespace Lombard.Vif.Service.Domain
{
    public class OutputFormatAttribute : DescriptionAttribute
    {
        public int Order { get; set; }
        public int FieldWidth { get; set; }
        public string FillerChar {get; set;}
        public string FillerCharWhenEmpty { get; set; }

        public OutputFormatAttribute()
        {
            Order = 0;
            FieldWidth = 1;
            FillerChar = string.Empty;
            FillerCharWhenEmpty = string.Empty;
        }
    }
}
