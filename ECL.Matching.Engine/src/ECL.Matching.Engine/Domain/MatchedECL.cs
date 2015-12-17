using System.Linq;
using System.Text;

namespace Lombard.ECLMatchingEngine.Service.Domain
{
    public abstract class MatchedECLRecord
    {
        [FieldConfiguration(Order = 0, Length = 1, FillerChar = "", FillerCharWhenEmpty = "")]
        public string Record_Type_Code { get; set; }

        public override string ToString()
        {
            var output = new StringBuilder();

            var properties = from property in GetType().GetProperties()
                             let orderAttribute = property.GetCustomAttributes(typeof(FieldConfiguration), false).SingleOrDefault() as FieldConfiguration
                             orderby orderAttribute.Order
                             select property;

            foreach (var prop in properties)
            {
                var outputFormat = this.GetAttributeFrom<FieldConfiguration>(prop.Name);
                var propertyValue = prop.GetValue(this, null);
                if (propertyValue != null)
                {
                    var outFormatFiller = string.IsNullOrEmpty(propertyValue.ToString()) ? outputFormat.FillerCharWhenEmpty : outputFormat.FillerChar;
                    var formattedPropertyValue = outputFormat.FillerChar != string.Empty ? propertyValue.ToString().PadLeft(outputFormat.Length, char.Parse(outFormatFiller)) :
                        propertyValue.ToString().PadLeft(outputFormat.Length);
                    
                    output.Append(formattedPropertyValue);
                }

            }

            return output.ToString();
        }
    }
}
