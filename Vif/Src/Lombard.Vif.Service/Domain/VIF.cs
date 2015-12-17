using System.Linq;
using System.Text;

namespace Lombard.Vif.Service.Domain
{
    public abstract class Vif
    {
        [OutputFormat(Order = 0, FieldWidth = 1)]
        public string RECORD_TYPE_CODE { get; set; }

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
                var outFormatFiller = string.Empty;
                if (propertyValue != null)
                {
                    var formattedPropertyValue = string.Empty;
                    if (string.IsNullOrEmpty(propertyValue.ToString()))
                    {
                        outFormatFiller = outputFormat.FillerCharWhenEmpty;
                    }
                    else
                    {
                        outFormatFiller = outputFormat.FillerChar;
                    }

                    if (outputFormat.FillerChar != string.Empty)
                        formattedPropertyValue = propertyValue.ToString().PadLeft(outputFormat.FieldWidth, char.Parse(outFormatFiller));
                    else
                        formattedPropertyValue = propertyValue.ToString().PadLeft(outputFormat.FieldWidth);
                    
                    output.Append(formattedPropertyValue);
                }
                
            }

            return output.ToString();
        }
    }
}
