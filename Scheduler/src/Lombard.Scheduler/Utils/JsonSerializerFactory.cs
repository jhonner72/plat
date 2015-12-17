using Newtonsoft.Json;

namespace Lombard.Scheduler.Utils
{
    public static class JsonSerializerFactory
    {
        public static JsonSerializer Get()
        {
            //--------------------------
            // IMPORTANT
            //--------------------------
            // JsonSerializerSettings should the settings in Lombard.Common.EasyNetQ.CustomJsonSerializer
            //--------------------------

            var settings = new JsonSerializerSettings
            {
                TypeNameHandling = TypeNameHandling.None,
                DateFormatString = "yyyy-MM-ddTHH:mm:ss.fffzzz",

            };

            // Added this to write Enums as strings, instead of int value
            settings.Converters.Add(new Newtonsoft.Json.Converters.StringEnumConverter());

            return JsonSerializer.Create(settings);
        }
    }
}