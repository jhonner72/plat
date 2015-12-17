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
    public class GenerateBulkCreditBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<GenerateBatchBulkCreditRequest> RequestExchange;
        private static readonly IQueue Queue;

        private static readonly List<GenerateBatchBulkCreditRequest> Requests = new List<GenerateBatchBulkCreditRequest>();
        private static readonly List<GenerateBatchBulkCreditResponse> Responses = new List<GenerateBatchBulkCreditResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static GenerateBulkCreditBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<GenerateBatchBulkCreditRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.GenerateBulkCreditResponseQueueName);
        }

        private GenerateBulkCreditBus()
        {
        }

        [BeforeScenario("GenerateBulkCreditRequest")]
        public static void BeforeGenerateBulkCreditRequestScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.GenerateBulkCreditRequestExchangeName);
            Bus.Consume<GenerateBatchBulkCreditResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(GenerateBatchBulkCreditRequest request, string jobIdentifier, string routingKey)
        {
            Requests.Add(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, jobIdentifier, routingKey));
        }

        public static DipsDbContext CreateContext()
        {
            return new DipsDbContext(new SqlConnection(ConfigurationHelper.DipsConnectionString));
        }

        public static async Task<GenerateBatchBulkCreditResponse> GetSingleResponseAsync(int timeOutSeconds)
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
