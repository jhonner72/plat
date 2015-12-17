using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Common.Queues;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class IncidentBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly IQueue Queue;

        private static readonly List<Incident> Responses = new List<Incident>();

        private static IDisposable consumer;

        [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1409:RemoveUnnecessaryCode", Justification = "Explicit static constructor to tell C# compiler not to mark type as beforefieldinit")]
        static IncidentBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationHelper.RabbitMqConnectionString);

            Queue = Bus.QueueDeclare(ConfigurationHelper.IncidentQueueName);
        }

        private IncidentBus()
        {
        }

        [BeforeScenario("Incident")]
        public static void BeforeIncidentScenario()
        {
            Responses.Clear();
            Bus.QueuePurge(Queue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            consumer = Bus.Consume<Incident>(Queue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            consumer.Dispose();
            Bus.Dispose();
        }

        public static async Task<Incident> GetSingleResponseAsync(int timeOutSeconds)
        {
            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalMilliseconds > 0)
                {
                    var response = Responses.LastOrDefault();

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
