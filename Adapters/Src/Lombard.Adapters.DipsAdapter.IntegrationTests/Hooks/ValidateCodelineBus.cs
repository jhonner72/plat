using System.Collections.Generic;
using System.Data.SqlClient;
using System.Diagnostics.CodeAnalysis;
using System.Threading.Tasks;
using EasyNetQ;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using TechTalk.SpecFlow;
using System;
using System.Linq;
using EasyNetQ.Topology;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class ValidateCodelineBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<ValidateBatchCodelineRequest> RequestExchange;
        private static readonly IQueue Queue;

        private static readonly List<ValidateBatchCodelineRequest> Requests = new List<ValidateBatchCodelineRequest>();
        private static readonly List<ValidateBatchCodelineResponse> Responses = new List<ValidateBatchCodelineResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static ValidateCodelineBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<ValidateBatchCodelineRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.ValidateCodelineResponseQueueName);
        }

        private ValidateCodelineBus()
        {
        }

        [BeforeScenario("validateCodeline")]
        public static void BeforeValidateCodeLineScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.ValidateCodelineRequestExchangeName);
            Bus.Consume<ValidateBatchCodelineResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(ValidateBatchCodelineRequest request, string jobIdentifier, string routingKey)
        {
            Requests.Add(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, jobIdentifier, routingKey));
        }

        public static DipsDbContext CreateContext()
        {
            return new DipsDbContext(new SqlConnection(ConfigurationHelper.DipsConnectionString));
        }

        public static async Task<ValidateBatchCodelineResponse> GetSingleResponseAsync(int timeOutSeconds)
        {
            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    var response = Responses.SingleOrDefault();

                    if (response != null)
                    {
                        return response;
                    }
                    await Task.Delay(250);
                }

                return null;
            });

            return await task;
        }
    }
}
