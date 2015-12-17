using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using TechTalk.SpecFlow;

namespace Lombard.AdjustmentLetters.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class CreateAdjustmentLettersBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<CreateBatchAdjustmentLettersRequest> RequestExchange;
        private static readonly IQueue Queue;

        public static readonly List<CreateBatchAdjustmentLettersRequest> Requests = new List<CreateBatchAdjustmentLettersRequest>();
        private static readonly List<CreateBatchAdjustmentLettersResponse> Responses = new List<CreateBatchAdjustmentLettersResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static CreateAdjustmentLettersBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<CreateBatchAdjustmentLettersRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.ResponseQueueName);
        }

        private CreateAdjustmentLettersBus()
        {
        }

        [BeforeScenario("CheckThirdParty")]
        public static void BeforeCheckThirdPartyScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.RequestExchangeName);
            Bus.Consume<CreateBatchAdjustmentLettersResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(CreateBatchAdjustmentLettersRequest request, string jobIdentifier)
        {
            Requests.Add(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, jobIdentifier, "NGAL"));
        }

        public static async Task<CreateBatchAdjustmentLettersResponse> GetSingleResponseAsync(int timeOutSeconds)
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
