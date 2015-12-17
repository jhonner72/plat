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
    public class GenerateCorrespondingVoucherBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<GenerateCorrespondingVoucherRequest> RequestExchange;
        private static readonly IQueue Queue;
    
        private static readonly List<GenerateCorrespondingVoucherRequest> Requests = new List<GenerateCorrespondingVoucherRequest>();
        private static readonly List<GenerateCorrespondingVoucherResponse> Responses = new List<GenerateCorrespondingVoucherResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static GenerateCorrespondingVoucherBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<GenerateCorrespondingVoucherRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.GenerateCorrespondingVoucherResponseQueueName);
        }

        private GenerateCorrespondingVoucherBus()
        {
        }

        [BeforeScenario("GenerateCorrespondingVoucher")]
        public static void BeforeGenerateCorrespondingVoucherScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }
        
        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.GenerateCorrespondingVoucherRequestExchangeName);
            Bus.Consume<GenerateCorrespondingVoucherResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(GenerateCorrespondingVoucherRequest request, string jobIdentifier, string routingKey)
        {
            Requests.Add(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, jobIdentifier, routingKey));
        }

        public static DipsDbContext CreateContext()
        {
            return new DipsDbContext(new SqlConnection(ConfigurationHelper.DipsConnectionString));
        }

        public static async Task<GenerateCorrespondingVoucherResponse> GetSingleResponseAsync(int timeOutSeconds)
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
