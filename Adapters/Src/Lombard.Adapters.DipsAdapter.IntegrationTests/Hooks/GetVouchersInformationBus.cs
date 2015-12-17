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
    public class GetVouchersInformationBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<GetVouchersInformationResponse> ResponseExchange;
        private static readonly IQueue RequestQueue;

        private static readonly List<GetVouchersInformationRequest> Requests = new List<GetVouchersInformationRequest>();
        private static readonly List<GetVouchersInformationResponse> Responses = new List<GetVouchersInformationResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static GetVouchersInformationBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            ResponseExchange = new ExchangePublisher<GetVouchersInformationResponse>(Bus);

            RequestQueue = Bus.QueueDeclare(ConfigurationHelper.GetPoolVouchersQueueName);
        }

        private GetVouchersInformationBus()
        {
        }

        [BeforeScenario("GetVouchersInformation")]
        public static void BeforeGetVouchersInformationScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(RequestQueue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            ResponseExchange.Declare(ConfigurationHelper.GetPoolVouchersExchangeName);
            Bus.Consume<GetVouchersInformationRequest>(RequestQueue, (message, info) => Requests.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(GetVouchersInformationResponse response, string jobIdentifier, string routingKey)
        {
            Responses.Add(response);

            Task.WaitAll(ResponseExchange.PublishAsync(response, jobIdentifier, routingKey));
        }

        public static DipsDbContext CreateContext()
        {
            return new DipsDbContext(new SqlConnection(ConfigurationHelper.DipsConnectionString));
        }

        public static async Task<List<GetVouchersInformationRequest>> GetRequestAsync(int timeOutSeconds)
        {
            var response = new List<GetVouchersInformationRequest>();

            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    if (response != null)
                    {
                        response.AddRange(Requests);
                        break;
                    }
                    await Task.Delay(250);
                }

                return response;
            });

            return await task;
        }

        public static async Task<GetVouchersInformationRequest> GetSingleRequestAsync(int timeOutSeconds)
        {

            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    var response = Requests.SingleOrDefault();

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
