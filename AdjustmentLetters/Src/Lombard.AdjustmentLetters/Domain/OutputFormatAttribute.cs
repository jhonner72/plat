using System.ComponentModel;

namespace Lombard.AdjustmentLetters.Domain
{
    public class OutputFormatAttribute : DescriptionAttribute
    {
        public string FieldName { get; set; }
        public int Order { get; set; }
        public int FieldWidth { get; set; }
        public int ColumnWidth { get; set; }
        public string Pad { get; set; }

        public OutputFormatAttribute()
        {
            FieldName = string.Empty;
            Order = 0;
            FieldWidth = 1;
            ColumnWidth = 0;
            Pad = string.Empty;
        }
    }
}
