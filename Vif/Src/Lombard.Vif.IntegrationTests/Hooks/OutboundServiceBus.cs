using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using TechTalk.SpecFlow;

namespace Lombard.Vif.IntegrationTests.Hooks
{
    /// <summary>
    /// Message Bus (Rabbit MQ) Singleton
    /// </summary>
    [Binding]
    public class OutboundServiceBus
    {
        private static readonly IAdvancedBus Bus;
        private static readonly IQueue RequestQueue;
        private static readonly IQueue ResponseQueue;

        private static readonly List<CreateValueInstructionFileRequest> Requests = new List<CreateValueInstructionFileRequest>();
        private static readonly List<CreateValueInstructionFileResponse> Responses = new List<CreateValueInstructionFileResponse>();

        private static IDisposable responseConsumer;
        private static readonly IExchangePublisher<CreateValueInstructionFileRequest> RequestPublisher;

        static OutboundServiceBus()
        {
            Bus = MessageBusFactory.CreateBus(ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString);

            RequestPublisher = new ExchangePublisher<CreateValueInstructionFileRequest>(Bus);

            // TODO: read from config
            RequestPublisher.Declare("lombard.service.outclearings.createvalueinstructionfile.request");
            RequestQueue = Bus.QueueDeclare("lombard.service.outclearings.createvalueinstructionfile.request.queue");
            ResponseQueue = Bus.QueueDeclare("lombard.service.outclearings.createvalueinstructionfile.response.queue");
        }

        private OutboundServiceBus()
        {
        }

        [BeforeScenario("vif")]
        public static void BeforeValidateCodeLineScenario()
        {
            Responses.Clear();
            Bus.QueuePurge(RequestQueue);
            Bus.QueuePurge(ResponseQueue);
        }

        [BeforeTestRun]
        public static void BeforeTestRun()
        {
            responseConsumer = Bus.Consume<CreateValueInstructionFileResponse>(ResponseQueue, (message, info) => Responses.Add(message.Body));
        }

        [AfterTestRun]
        public static void AfterTestRun()
        {
            responseConsumer.Dispose();
            Bus.Dispose();
        }

        public static async Task<CreateValueInstructionFileResponse> GetSingleResponseAsync(int timeOutSeconds)
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

        public static void Publish(CreateValueInstructionFileRequest request)
        {
            Requests.Add(request);
            
            Task.WaitAll(RequestPublisher.PublishAsync(request, null, "NVIF"));
        }

        public static uint RequestQueueCount()
        {
            return Bus.MessageCount(RequestQueue);
        }
    }
}
