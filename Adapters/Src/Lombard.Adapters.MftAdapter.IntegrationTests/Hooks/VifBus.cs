using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Adapters.MftAdapter.Messages.XsdImports;
using Lombard.Common.Queues;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class VifBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly ExchangePublisher<SendBatchValueInstructionFileRequest> RequestExchange;
        private static readonly IQueue Queue;

        private static readonly List<SendBatchValueInstructionFileRequest> Requests = new List<SendBatchValueInstructionFileRequest>();
        private static readonly List<SendBatchValueInstructionFileResponse> Responses = new List<SendBatchValueInstructionFileResponse>();

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static VifBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            RequestExchange = new ExchangePublisher<SendBatchValueInstructionFileRequest>(Bus);

            Queue = Bus.QueueDeclare(ConfigurationHelper.VifResponseQueueName);
        }

        private VifBus()
        {
        }

        [BeforeScenario("sendVif")]
        public static void BeforeValidateCodeLineScenario()
        {
            Requests.Clear();
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            RequestExchange.Declare(ConfigurationHelper.VifRequestExchangeName);
            Bus.Consume<SendBatchValueInstructionFileResponse>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            Bus.Dispose();
        }

        public static void Publish(SendBatchValueInstructionFileRequest request)
        {
            Requests.Add(request);

            Task.WaitAll(RequestExchange.PublishAsync(request, null));
        }

        public static async Task<SendBatchValueInstructionFileResponse> GetSingleResponseAsync(int timeOutSeconds)
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
