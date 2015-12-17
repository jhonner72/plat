using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using EasyNetQ;
using EasyNetQ.Topology;
using Serilog;
using Lombard.Common.Messages;
using Lombard.Common.Configuration;
using Lombard.Common.Helper;
using System.Text;

namespace Lombard.Common.Queues
{
    public sealed class QueueConsumer<TMessage> : IQueueConsumer<TMessage> where TMessage : class
    {
        private readonly CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        private readonly List<Task> tasks = new List<Task>();

        private readonly IMessageProcessorFactory<TMessage> messageProcessorFactory;
        private readonly IAdvancedBus messageBus;
        private readonly ICustomErrorHandling customErrorHandler;

        private bool started;
        private IQueue queue;
        private IDisposable consumer;

        private readonly IExchangePublisher<Error> errorExchange;

        public QueueConsumer(
            IMessageProcessorFactory<TMessage> messageProcessorFactory,
            IAdvancedBus messageBus, 
            ICustomErrorHandling customErrorHandler,
            IExchangePublisher<Error> errorExchange)
        {
            this.messageProcessorFactory = messageProcessorFactory;
            this.messageBus = messageBus;
            this.customErrorHandler = customErrorHandler;
            this.errorExchange = errorExchange;
            this.errorExchange.Declare(ErrorHandlingConfiguration.ErrorExchangeName);
        }

        public void Subscribe(string queueName)
        {
            if (started)
                return;

            started = true;

            Log.Information("Start consuming from queue {@queueName}", queueName, typeof(TMessage).Name);

            queue = messageBus.QueueDeclare(queueName, true);

            consumer = messageBus
                .Consume<TMessage>(queue,
                    (message, info) =>
                    {
                        var task = HandleMessageAsync(message.Body, message.Properties.CorrelationId, info.RoutingKey,
                            message, info);

                        tasks.Add(task);
                        return task;
                    });
        }

        public Task HandleMessageAsync(TMessage message, string correlationId, string routingKey, IMessage<TMessage> sourceMesage = null, MessageReceivedInfo info = null)
        {
            return Task
                .Run(async () =>
                {
                    var mesageProcessor = messageProcessorFactory.CreateMessageProcessor(message);
                    await mesageProcessor.ProcessAsync(cancellationTokenSource.Token, correlationId, routingKey);
                }, cancellationTokenSource.Token)
                .ContinueWith(
                    finishedTask =>
                    {
                        tasks.Remove(finishedTask);

                        if (!finishedTask.IsFaulted)
                        {
                            return;
                        }

                        HandleError(message, correlationId, sourceMesage, info, finishedTask);
                    });
        }

        private void HandleError(TMessage message, string correlationId, IMessage<TMessage> sourceMesage, MessageReceivedInfo info, Task finishedTask)
        {
            try
            {
                var serializer = messageBus.Container.Resolve<ISerializer>();
                string serialisedMessage = Encoding.UTF8.GetString(serializer.MessageToBytes<TMessage>(message));

                var error = customErrorHandler.MapMessageToErrorSchema(
                    finishedTask.Exception,
                    correlationId,
                    message.GetType().ToString(),
                    serialisedMessage,
                    sourceMesage == null ? null : sourceMesage.Properties,
                    sourceMesage == null ? null : sourceMesage.Properties.Headers,
                    info);

                var errorRoutingKey = customErrorHandler.GetRoutingKey(finishedTask.Exception).ToString().ToLower();

                Task.WaitAll(errorExchange.PublishAsync(error, correlationId, errorRoutingKey));
            }
            catch (Exception ex)
            {
                Log.Error(ex, "An unexpected exception occured in custom error handling.");

                throw new EasyNetQException(
                   "An unexpected exception occurred while processing the message.",
                   finishedTask.Exception);
            }
            finally
            {
                Log.Error(finishedTask.Exception,
                    "An unexpected exception occurred while processing the message.");
            }
        }

        public async Task CompleteAsync(bool forceCancel = false)
        {
            if (!started)
                return;

            Log.Information("Stop {@subscriptionId} - {@messageType} queue manager", queue.Name, typeof(TMessage).Name);

            consumer.Dispose();

            started = false;

            if (!forceCancel)
            {
                await Task.WhenAll(tasks.ToArray());
            }

            cancellationTokenSource.Cancel();
        }

        public void Dispose()
        {
            try
            {
                if (started)
                    Task.WaitAll(CompleteAsync());
            }
            catch (AggregateException ex)
            {
                Log.Fatal(ex, "QueueManager shut down failed");
            }
        }
    }
}
