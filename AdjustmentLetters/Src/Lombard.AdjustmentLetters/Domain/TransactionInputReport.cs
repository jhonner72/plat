using System.Linq;
using System.Text;
using Lombard.AdjustmentLetters.Constants;

namespace Lombard.AdjustmentLetters.Domain
{
    public class TransactionInputReport
    {
        public override string ToString()
        {
            var output = new StringBuilder();

            var properties = from property in GetType().GetProperties()
                             let orderAttribute = property.GetCustomAttributes(typeof(OutputFormatAttribute), false).SingleOrDefault() as OutputFormatAttribute
                             orderby orderAttribute.Order
                             select property;

            foreach (var prop in properties)
            {
                var outputFormat = this.GetAttributeFrom<OutputFormatAttribute>(prop.Name);
                var propertyValue = prop.GetValue(this, null);
                if (propertyValue == null) continue;
                string formattedPropertyValue;

                switch (outputFormat.FieldName)
                {
                    case "BorderLine":
                        formattedPropertyValue = propertyValue.ToString().PadLeft(outputFormat.ColumnWidth, '-');
                        break;
                    case "WorkstationID":
                        formattedPropertyValue = FormatWorstationId(propertyValue, outputFormat);
                        break;
                    default:
                        formattedPropertyValue = PaddedValue(propertyValue, outputFormat);
                        break;
                }

                output.Append(formattedPropertyValue);
            }

            return output.ToString();
        }

        private static string FormatWorstationId(object value, OutputFormatAttribute attribute)
        {
            return string.Join(" ", attribute.Pad.Equals("Right") ? value.ToString().PadRight(attribute.ColumnWidth) : value.ToString().PadLeft(attribute.ColumnWidth), ReportConstants.ReportTitle);
        }

        private static string PaddedValue(object value, OutputFormatAttribute attribute)
        {
            return string.Join(" ", attribute.Pad.Equals("Right") ? value.ToString().PadRight(attribute.ColumnWidth) : value.ToString().PadLeft(attribute.ColumnWidth));
        }
    }
}
