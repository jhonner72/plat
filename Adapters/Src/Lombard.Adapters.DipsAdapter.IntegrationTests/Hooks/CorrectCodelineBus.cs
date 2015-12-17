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
using EasyNetQ.Topology;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class CorrectCodelineBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<CorrectBatchCodelineRequest> RequestExchange;
        private static readonly IQueue Queue;

        private static readonly Queue<CorrectBatchCodelineRequest> Requests = new Queue<CorrectBatchCodelineRequest>();
        private static readonly Queue<CorrectBatchCodelineResponse> Responses = new Queue<CorrectBatchCodelineResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static CorrectCodelineBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<CorrectBatchCodelineRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.CorrectCodelineResponseQueueName);
        }

        private CorrectCodelineBus()
        {
        }

        [BeforeScenario("CorrectCodeline")]
        public static void BeforeCorrectCodeLineScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }
        
        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.CorrectCodelineRequestExchangeName);
            Bus.Consume<CorrectBatchCodelineResponse>(Queue, (message, info) => Responses.Enqueue(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(CorrectBatchCodelineRequest request, string jobIdentifier, string routingKey)
        {
            Requests.Enqueue(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, jobIdentifier, routingKey));
        }

        public static DipsDbContext CreateContext()
        {
            return new DipsDbContext(new SqlConnection(ConfigurationHelper.DipsConnectionString));
        }

        public static async Task<CorrectBatchCodelineResponse> GetSingleResponseAsync(int timeOutSeconds)
        {
            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    var response = Responses.Dequeue();

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
