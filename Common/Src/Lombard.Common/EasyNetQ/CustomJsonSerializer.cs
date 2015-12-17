using System.Text;
using EasyNetQ;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace Lombard.Common.EasyNetQ
{
    public class CustomJsonSerializer : ISerializer
    {
        private readonly ITypeNameSerializer typeNameSerializer;

        private readonly JsonSerializerSettings serializerSettings = new JsonSerializerSettings
        {
            TypeNameHandling = TypeNameHandling.None,
            DateFormatString = "yyyy-MM-ddTHH:mm:ss.fffzzz"
        };

        public CustomJsonSerializer(ITypeNameSerializer typeNameSerializer)
        {
            Guard.IsNotNull(typeNameSerializer, "typeNameSerializer");
            this.typeNameSerializer = typeNameSerializer;
            this.serializerSettings.Converters.Add(new StringEnumConverter { CamelCaseText = false, AllowIntegerValues = false });
        }

        public byte[] MessageToBytes<T>(T message) where T : class
        {
            Guard.IsNotNull(message, "message");
            return Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(message, serializerSettings));
        }

        public T BytesToMessage<T>(byte[] bytes)
        {
            Guard.IsNotNull(bytes, "bytes");
            return JsonConvert.DeserializeObject<T>(Encoding.UTF8.GetString(bytes), serializerSettings);
        }

        public object BytesToMessage(string typeName, byte[] bytes)
        {
            Guard.IsNotNull(typeName, "typeName");
            Guard.IsNotNull(bytes, "bytes");
            var type = typeNameSerializer.DeSerialize(typeName);
            return JsonConvert.DeserializeObject(Encoding.UTF8.GetString(bytes), type, serializerSettings);
        }
    }
}
