using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Exceptions;
using RabbitMQ.Client.Framing;
using Serilog;
using BasicGetResult = FujiXerox.Adapters.DipsAdapter.MessageQueue.BasicGetResult;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class RabbitMq
    {
        private readonly IList<string> hostNames;
        private readonly string userName;
        private readonly string password;
        private readonly int timeout;
        //private int currentTimer;
        //protected int retryCounter;
        protected bool waitingToConnect;
        private IConnectionFactory factory;
        protected IConnection connection;

        public RabbitMq(string hostNames, string userName, string password, int timeout = 5000)
        {
            this.hostNames = hostNames.Split(',').ToList();
            this.userName = userName;
            this.password = password;
            this.timeout = timeout;
        }

        protected void TryCreateConnectionFactory(object stateInfo)
        {
            connection = null;
            //Log.Debug("RabbitMq: TryCreateConnectionFactory Creating connection factory on {0} - {1}", hostNames, stateInfo);

            if (stateInfo != null)
            {
                var autoEvent = (AutoResetEvent) stateInfo;
                autoEvent.Set();
            }
            var tempHostNames = hostNames;
            //hostNames.Shuffle();
            foreach (var hostName in tempHostNames)
            {
                try
                {
                    factory = new ConnectionFactory { HostName = hostName, UserName = userName, Password = password, NetworkRecoveryInterval = TimeSpan.FromMilliseconds(timeout) };
                    connection = factory.CreateConnection();
                    waitingToConnect = false;
                    break;
                }
                catch (SocketException se)
                {
                    Log.Error(se, "RabbitMq: TryCreateConnectionFactory has an Error creating connection to host {0}", hostName);
                }
                catch (BrokerUnreachableException be)
                {
                    Log.Error(be, "RabbitMq: TryCreateConnectionFactory Cannot reach broker for host {0}", hostName);
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "RabbitMq: TryCreateConnectionFactory has an Error with {0} - {1}", hostName, ex.Message);
                }
            }
            if (connection == null) StartTryToConnect();
        }

        private void StartTryToConnect()
        {
            const string compare = "Milliseconds";
            waitingToConnect = true;
            var autoEvent = new AutoResetEvent(false);
            var timerDelegate = new TimerCallback(TryCreateConnectionFactory);
            var timer = new Timer(timerDelegate, autoEvent, TimeSpan.FromMilliseconds(timeout), TimeSpan.Zero);
            timer.Change(timeout, Timeout.Infinite);
            Log.Warning(
                "RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} {1}",
                timer, compare);
        }

        //private void StartTryToConnect()
        //{
        //    var doubler = (int)Math.Pow(2, retryCounter++);
        //    var sw = new Dictionary<string, Func<int, TimeSpan>>
        //    {
        //        {"Millseconds", _ => ToMilliseconds(doubler)},
        //        {"Seconds", _ => ToSeconds(doubler)},
        //        {"Minutes", _ => ToMinutes(doubler)}
        //    } ;

        //    var compare = "Milliseconds";
        //    if (timeout > 1000) compare = "Seconds";
        //    if (timeout > 60000) compare = "Minutes";
        //    var timer = new Timer(TryCreateConnectionFactory, null, sw[compare].Invoke(doubler), TimeSpan.Zero);
        //    Log.Warning("RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} {1}", currentTimer, compare);
        //    System.Diagnostics.Debug.WriteLine("RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} {1}", currentTimer, compare);

        //    //if (timeout > 1000)
        //    //{
        //    //    var timeoutSeconds = timeout/1000;
        //    //    var timer = new Timer(TryCreateConnectionFactory, null, TimeSpan.FromSeconds(timeoutSeconds), TimeSpan.MaxValue);
        //    //    Log.Warning("RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} seconds", timeout);
        //    //}
        //    //else
        //    //{
        //    //    var timer = new Timer(TryCreateConnectionFactory, null, timeout, Timeout.Infinite);
        //    //    Log.Warning("RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} milliseconds", timeout);
        //    //}
        //}

        //private TimeSpan ToMilliseconds(int doubler)
        //{
        //    currentTimer = timeout*doubler;
        //    return TimeSpan.FromSeconds(currentTimer);
        //}

        //private TimeSpan ToSeconds(int doubler)
        //{
        //    currentTimer = doubler * timeout/1000;
        //    return TimeSpan.FromSeconds(currentTimer);
        //}

        //private TimeSpan ToMinutes(int doubler)
        //{
        //    currentTimer = doubler*timeout/60000;
        //    return TimeSpan.FromMinutes(currentTimer);
        //}

        public void PublishToQueue(string queueName, string correlationId, byte[] message)
        {
            Log.Debug("RabbitMq: PublishToQueue(queueName = {0}, correlationId = {1}, message = {2})",
                queueName, correlationId, message);

            //retryCounter = 0;
            while (connection == null)
            {
                if(!waitingToConnect) TryCreateConnectionFactory(null);
            }
            try
            {
                using (connection)
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

            //retryCounter = 0;
            while (connection == null)
            {
                if(!waitingToConnect) TryCreateConnectionFactory(null);
            }
            try
            {
                using (connection)
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

        public void ReadFromQueue(string queueName, Func<IBasicGetResult, bool> action)
        {
            //retryCounter = 0;
            while (connection == null || !connection.IsOpen)
            {
               if(!waitingToConnect) TryCreateConnectionFactory(null);
            }
            try
            {
                using (connection)
                using (var channel = connection.CreateModel())
                {
                    channel.QueueDeclare(queueName, true, false, false, null);
                    //var consumer = new EventingBasicConsumer(channel);
                    
                    //consumer.Received+= (ch, ea) =>
                    //{
                    //    var body = ea.Body;
                    //    ch.BasicAck(ea.DeliveryTag, false);
                    //}
                    //channel.BasicConsume(queueName, false, null);
                    var data = channel.BasicGet(queueName, false);
                    if (data == null) return; // queue is empty hence no message

                    // execute an action with the message from the queue.
                    // If the action succeeds, acknowledge receipt
                    // Otherwise non-acknowledge and resubmit message to queue
                    if (action.Invoke(new BasicGetResult(data)))
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
