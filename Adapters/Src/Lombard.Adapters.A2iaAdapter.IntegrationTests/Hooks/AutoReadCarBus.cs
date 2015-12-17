using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Common.Queues;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.A2iaAdapter.IntegrationTests.Hooks
{
    [Binding]
    public class AutoReadCarBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<RecogniseBatchCourtesyAmountRequest> InboundExchange;
        private static readonly IQueue Queue;

        // ReSharper disable once CollectionNeverQueried.Local
        private static readonly List<RecogniseBatchCourtesyAmountRequest> Requests = new List<RecogniseBatchCourtesyAmountRequest>();
        private static readonly List<RecogniseBatchCourtesyAmountResponse> Responses = new List<RecogniseBatchCourtesyAmountResponse>();

        private AutoReadCarBus()
        {
        }

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static AutoReadCarBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            InboundExchange = new ExchangePublisher<RecogniseBatchCourtesyAmountRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.OutboundQueueName);
        }

        [BeforeScenario("AutoReadCar")]
        public static void BeforeValidateCodeLineScenario()
        {
            Requests.Clear();
            Responses.Clear();

            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            InboundExchange.Declare(ConfigurationHelper.InboundExchangeName);
            Bus.Consume<RecogniseBatchCourtesyAmountResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(RecogniseBatchCourtesyAmountRequest request)
        {
            Requests.Add(request);

            Task.WaitAll(InboundExchange.PublishAsync(request, null));
        }

        public static async Task<RecogniseBatchCourtesyAmountResponse> GetSingleResponseAsync(int timeOutSeconds, string jobId)
        {
            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    if (Responses != null)
                    {
                        var response = Responses.SingleOrDefault(x => x.jobIdentifier == jobId);

                        if (response != null)
                        {
                            Responses.RemoveAt(0);
                            return response;
                        }
                    }
                    await Task.Delay(250);
                }
                return null;
            });

            return await task;
        }
    }
}
