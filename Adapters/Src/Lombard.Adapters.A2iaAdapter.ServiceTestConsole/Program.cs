using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using EasyNetQ;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Lombard.Common.Queues;

namespace Lombard.Adapters.A2iaAdapter.ServiceTestConsole
{
    class Program
    {
        private static void Main(string[] args)
        {
            var appSettings = ConfigurationManager.AppSettings;

            var totalInboundMessages = int.Parse(appSettings["TotalInboundMessages"]);

            var inboundExchangeName = appSettings["InboundExchangeName"];
            var imageFileFolder = appSettings["ImageFileFolder"];

            Console.WriteLine("Total inbound messages is {0}.", totalInboundMessages);
            Console.WriteLine("Inbound Exchange Name is {0}.", inboundExchangeName);
            Console.WriteLine("Image folder path is {0}.", imageFileFolder);

            using (var rabbitBus = InitMessageBus())
            {
                IList<Task> tasks = new List<Task>();
                var carResponseExchangePublisher = new ExchangePublisher<RecogniseBatchCourtesyAmountRequest>(rabbitBus.Advanced);
                carResponseExchangePublisher.Declare(inboundExchangeName);
                int i = 0;
                // Get batch names (image folders)
                var folderList = Directory.GetDirectories(imageFileFolder);
                ConcurrentBag<string> folderBag = new ConcurrentBag<string>(folderList);

                while (i++ < totalInboundMessages)
                {
                    if (folderBag.IsEmpty)
                    {
                        folderBag = new ConcurrentBag<string>(folderList);
                    }

                    string path;
                    do
                    {
                        folderBag.TryTake(out path);
                    } while (path == null);

                    var batchRequest = PopulateInboundQueue(path);
                    var result = carResponseExchangePublisher.PublishAsync(batchRequest);
                    tasks.Add(result);
                    //Task.Run( async()=> await carResponseExchangePublisher.PublishAsync(batchRequest));

                    Console.WriteLine(
                        "Message with jobIdentifier {0} has been pushed into inbound queue", batchRequest.jobIdentifier);
                }

                Task.WaitAll(tasks.ToArray());
            }

            Console.ReadKey();
        }

        /// <summary>
        /// Generate test messages in the inbound queue
        /// </summary>
        /// <param name="imagesFolderPath">The full path of the image folder</param>
        /// <returns></returns>
        private static RecogniseBatchCourtesyAmountRequest PopulateInboundQueue(
            string imagesFolderPath)
        {
            int j;
            var batchRequest = new RecogniseBatchCourtesyAmountRequest();
            var list = new List<RecogniseCourtesyAmountRequest>();

            batchRequest.jobIdentifier = Path.GetFileName(imagesFolderPath);
            //var fileCount = Directory.GetFiles(imagesFolderPath, "*.tif").GetLength(0);
            var fileEnum = Directory.GetFiles(imagesFolderPath, "*.tif");
            foreach (var filePath in fileEnum)
            {
                var fileName = Path.GetFileName(filePath);
                if (fileName != null)
                {
                    var pos1 = fileName.IndexOf('-');
                    //var pos2 = fileName.LastIndexOf('_');
                    var request = new RecogniseCourtesyAmountRequest()
                    {
                        documentReferenceNumber = fileName.Substring(pos1 + 1, 9),
                        processingDate = new DateTime(2015, 3, 30)
                    };
                    list.Add(request);

                }
            }
            //Console.WriteLine(string.Format("The {0}th message has {1} image files", i, j));

            batchRequest.voucher = list.ToArray();
            //rabbitService.PopulateInboundQueue(batchRequest, correlationId++.ToString());
            return batchRequest;
        }

        /// <summary>
        /// Create an instance of RabbitMQ message bus
        /// </summary>
        /// <returns></returns>
        private static IBus InitMessageBus()
        {
            var connectionString = ConfigurationManager.ConnectionStrings["rabbitMQ"].ConnectionString;

            //message bus
            return RabbitHutch.CreateBus(connectionString);

        }
    }
}
