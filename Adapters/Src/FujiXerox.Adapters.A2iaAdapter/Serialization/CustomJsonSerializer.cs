using System.Text;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FujiXerox.Adapters.A2iaAdapter.Serialization
{
    public static class CustomJsonSerializer
    {
        private static readonly JsonSerializerSettings serializerSettings = new JsonSerializerSettings
        {
            TypeNameHandling = TypeNameHandling.None,
            DateFormatString = "yyyy-MM-ddTHH:mm:ss.fffzzz",
            Converters = new JsonConverter[] { new StringEnumConverter { CamelCaseText = false, AllowIntegerValues = false } }
        };

        public static byte[] MessageToBytes<T>(T message) where T : class
        {
            return Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(message, serializerSettings));
        }

        public static T BytesToMessage<T>(byte[] bytes)
        {
            return JsonConvert.DeserializeObject<T>(Encoding.UTF8.GetString(bytes), serializerSettings);
        }
    }
}
