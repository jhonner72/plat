using System.Text;
using Newtonsoft.Json;

namespace FujiXerox.Adapters.DipsAdapter.Serialization
{
    public static class CustomJsonSerializer
    {
        private static readonly JsonSerializerSettings serializerSettings = new JsonSerializerSettings
        {
            TypeNameHandling = TypeNameHandling.None,
            DateFormatString = "yyyy-MM-ddTHH:mm:ss.fffzzz"
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
