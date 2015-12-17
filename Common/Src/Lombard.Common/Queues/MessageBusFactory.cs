using EasyNetQ;
using Lombard.Common.EasyNetQ;

namespace Lombard.Common.Queues
{
    public static class MessageBusFactory
    {
        public static IAdvancedBus CreateBus(string connectionString)
        {
            return RabbitHutch.CreateBus(
                connectionString,
                serviceRegister =>
                {
                    serviceRegister.Register<ISerializer>(
                        serviceProvider => new CustomJsonSerializer(serviceProvider.Resolve<ITypeNameSerializer>()));
                    serviceRegister.Register<IEasyNetQLogger>(
                        serviceProvider => new CustomSerilogLogger());
                }).Advanced;
        }
    }
}
