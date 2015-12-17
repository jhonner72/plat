using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Threading;
using FujiXerox.Adapters.A2iaAdapter.Extensions;
using RabbitMQ.Client;
using RabbitMQ.Client.Exceptions;
using RabbitMQ.Client.Framing;
using Serilog;

namespace FujiXerox.Adapters.A2iaAdapter.MessageQueue
{
    [Obsolete]
    public class RabbitMq
    {
        private readonly IList<string> hostNames;
        private readonly string userName;
        private readonly string password;
        private readonly int timeout;
        private ConnectionFactory factory;
        private bool running;

        public RabbitMq(string hostNames, string userName, string password, int timeout = 5000)
        {
            this.hostNames = hostNames.Split(',').ToList();
            this.userName = userName;
            this.password = password;
            this.timeout = timeout;
        }

        private void TryCreateConnectionFactory(object timer)
        {
            if (running) return;
            Log.Debug("RabbitMq: TryCreateConnectionFactory Creating connection factory on {0} - {1}", hostNames, timer);

            if (timer != null) ((Timer)timer).Dispose();
            hostNames.Shuffle();
            foreach (var hostName in hostNames)
            {
                try
                {
                    factory = new ConnectionFactory { HostName = hostName, UserName = userName, Password = password };
                    running = true;
                }
                catch (SocketException se)
                {
                    running = false;
                    Log.Error(se, "RabbitMq: TryCreateConnectionFactory has an Error creating connection to host {0}", hostName);
                }
                catch (BrokerUnreachableException be)
                {
                    running = false;
                    Log.Error(be, "RabbitMq: TryCreateConnectionFactory Cannot reach broker for host {0}", hostName);
                }
                catch (Exception ex)
                {
                    running = false;
                    Log.Error(ex, "RabbitMq: TryCreateConnectionFactory has an Error with {0} - {1}", hostName, ex.Message);
                }
            }
            if (factory == null) StartTryToConnect();
        }

        private void StartTryToConnect()
        {
            var timer = new Timer(TryCreateConnectionFactory);
            timer.Change(timeout, 10);
        }

        public void PublishToQueue(string queueName, string correlationId, byte[] message)
        {
            Log.Debug("RabbitMq: PublishToQueue(queueName = {0}, correlationId = {1}, message = {2})",
                queueName, correlationId, message);

            TryCreateConnectionFactory(null);
            if (factory == null)
            {
                //Retry?
                return;
            }
            try
            {
                using (var connection = factory.CreateConnection())
                using (var channel = connection.CreateModel())
                {
                    channel.QueueDeclare(queueName, true, false, false, null);
                    channel.BasicPublish("", queueName, new BasicProperties
                    {
                        CorrelationId = correlationId
                    }, message);
                }
                Log.Debug("RabbitMq: PublishToQueue complete");
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RabbitMq: PublishToQueue(queueName = {0}, correlationId = {1}, message = {2}) has thrown an exception",
                    queueName, correlationId, message);
            }
        }

        public void PublishToExchange(string exchangeName, string correlationId, string routingKey, byte[] message)
        {
            Log.Debug("RabbitMq: PublishToExchange(exchangeName = {0}, correlationId = {1}, routingKey = {2}, message = {3})",
                exchangeName, correlationId, routingKey, message);

            TryCreateConnectionFactory(null);
            if (factory == null)
            {
                //Retry?
                return;
            }
            try
            {
                using (var connection = factory.CreateConnection())
                using (var channel = connection.CreateModel())
                {
                    channel.ExchangeDeclare(exchangeName, "direct", true);
                    channel.BasicPublish(exchangeName, routingKey, new BasicProperties
                    {
                        CorrelationId = correlationId
                    }, message);
                }
                Log.Debug("RabbitMq: PublishToExchange complete");
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RabbitMq: PublishToExchange(exchangeName = {0}, correlationId = {1}, routingKey = {2}, message = {3}) has thrown an exception", exchangeName, correlationId, routingKey, message);
                throw;
            }
        }

        public void ReadFromQueue(string queueName, Func<BasicGetResult, bool> action)
        {
            TryCreateConnectionFactory(null);
            if (factory == null)
            {
                //Retry?
                return;
            }
            try
            {
                using (var connection = factory.CreateConnection())
                using (var channel = connection.CreateModel())
                {
                    channel.QueueDeclare(queueName, true, false, false, null);
                    RabbitMQ.Client.BasicGetResult data = channel.BasicGet(queueName, false);
                    if (data == null) return; // queue is empty hence no message

                    // execute an action with the message from the queue.
                    // If the action succeeds, acknowledge receipt
                    // Otherwise non-acknowledge and resubmit message to queue
                    if (action.Invoke(data))
                    {
                        Log.Information("RabbitMq: ReadFromQueue - Message Acknowledged from queue {0}", queueName);
                        channel.BasicAck(data.DeliveryTag, false);
                    }
                    else
                    {
                        Log.Information("RabbitMq: ReadFromQueue - Message not Acknowledged from queue {0} and requeued", queueName);
                        channel.BasicNack(data.DeliveryTag, false, true);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "RabbitMq: ReadFromQueue(queueName = {0}, action) has thrown an exception", queueName);
            }
        }
    }
}
