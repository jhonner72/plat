using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using TechTalk.SpecFlow;

namespace Lombard.ImageExchange.Nab.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class OutboundServiceBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly IQueue ResponseQueue;

        private static readonly List<CreateImageExchangeFileRequest> Requests = new List<CreateImageExchangeFileRequest>();
        private static readonly List<CreateImageExchangeFileResponse> Responses = new List<CreateImageExchangeFileResponse>();

        private static IDisposable responseConsumer;
        private static readonly IExchangePublisher<CreateImageExchangeFileRequest> RequestPublisher;

        static OutboundServiceBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString);

            RequestPublisher = new ExchangePublisher<CreateImageExchangeFileRequest>(Bus);
            RequestPublisher.Declare("lombard.service.outclearings.createimageexchangefile.request");
            ResponseQueue = Bus.QueueDeclare("lombard.service.outclearings.createimageexchangefile.response.queue");
        }

        private OutboundServiceBus()
        {
        }

        [BeforeScenario("outboundImageExchange")]
        public static void BeforeValidateCodeLineScenario()
        {
            Responses.Clear();
            //Bus.QueuePurge(RequestQueue);
            Bus.QueuePurge(ResponseQueue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            responseConsumer = Bus.Consume<CreateImageExchangeFileResponse>(ResponseQueue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            responseConsumer.Dispose();
            Bus.Dispose();
        }

        public static async Task<CreateImageExchangeFileResponse> GetSingleResponseAsync(int timeOutSeconds)
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

        public static void Publish(CreateImageExchangeFileRequest request)
        {
            Requests.Add(request);

            Task.WaitAll(RequestPublisher.PublishAsync(request, null));
        }
    }
}
