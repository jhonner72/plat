using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Lombard.Reporting.AdapterService.Messages.XsdImports;
using TechTalk.SpecFlow;

namespace Lombard.Reporting.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class OutboundServiceBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly IQueue ResponseQueue;

        private static readonly List<ExecuteBatchReportRequest> Requests = new List<ExecuteBatchReportRequest>();
        private static readonly List<ExecuteBatchReportResponse> Responses = new List<ExecuteBatchReportResponse>();

        private static IDisposable responseConsumer;
        private static readonly IExchangePublisher<ExecuteBatchReportRequest> requestPublisher;

        static OutboundServiceBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString);

            requestPublisher = new ExchangePublisher<ExecuteBatchReportRequest>(Bus);

            // TODO: read from config
            requestPublisher.Declare("lombard.service.reporting.executereport.request");
            ResponseQueue = Bus.QueueDeclare("lombard.service.reporting.executereport.response.queue");
        }

        private OutboundServiceBus()
        {
        }

        [BeforeScenario("reporting")]
        public static void BeforeReportingScenario()
        {
            Responses.Clear();
            //Bus.QueuePurge(RequestQueue);
            Bus.QueuePurge(ResponseQueue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            responseConsumer = Bus.Consume<ExecuteBatchReportResponse>(ResponseQueue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            responseConsumer.Dispose();
            Bus.Dispose();
        }

        public static async Task<ExecuteBatchReportResponse> GetSingleResponseAsync(int timeOutSeconds)
        {
            var timeout = DateTime.Now.AddSeconds(timeOutSeconds);

            var task = Task.Run(async () =>
            {
                while (timeout.Subtract(DateTime.Now).TotalSeconds > 0)
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

        public static void Publish(ExecuteBatchReportRequest request)
        {
            Requests.Add(request);
            
            Task.WaitAll(requestPublisher.PublishAsync(request, null));
        }
    }
}
