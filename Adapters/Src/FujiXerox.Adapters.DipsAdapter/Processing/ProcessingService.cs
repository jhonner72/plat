using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Processing
{
    public abstract class ProcessingService : IDisposable
    {
        private DipsConfiguration Configuration { get; set; }
        private ILogger Log { get; set; }
        private string ConsumerName { get; set; }
        private string ExchangeName { get; set; }
        protected RabbitMqConsumer Consumer { get; private set; }
        protected RabbitMqExchange Exchange { get; private set; }
        protected RabbitMqExchange InvalidExchange { get; private set; }
        protected string InvalidRoutingKey { get; private set; }
        protected string RecoverableRoutingKey { get; private set; }
        protected int PollingIntervalSecs { get; private set; }
        private bool disposed;

        public ProcessingService(DipsConfiguration configuration, ILogger log, string consumerName, string exchangeName)
        {
            Configuration = configuration;
            Log = log;
            InvalidRoutingKey = Configuration.InvalidRoutingKey;
            RecoverableRoutingKey = Configuration.RecoverableRoutingKey;
            ConsumerName = consumerName;
            ExchangeName = exchangeName;
            InitializeQueueConnection();
            Consumer.ConnectionLost += Consumer_ConnectionLost;
        }

        private void InitializeQueueConnection()
        {
            var invalidExchange = Configuration.InvalidExchangeName;
            var hostNames = Configuration.HostName;
            var userName = Configuration.UserName;
            var password = Configuration.Password;
            var timeout = Configuration.Timeout;
            var heartbeat = Configuration.HeartbeatSeconds;
            InvalidRoutingKey = Configuration.InvalidRoutingKey;
            RecoverableRoutingKey = Configuration.RecoverableRoutingKey;
            PollingIntervalSecs = Configuration.PollingIntervalSecs;
            Consumer = new RabbitMqConsumer(ConsumerName, hostNames, userName, password, timeout, heartbeat);
            Exchange = new RabbitMqExchange(ExchangeName, hostNames, userName, password, timeout, heartbeat);
            InvalidExchange = new RabbitMqExchange(invalidExchange, hostNames, userName, password, timeout, heartbeat);
        }

        private void Consumer_ConnectionLost(RabbitMQ.Client.IConnection connection,
            RabbitMQ.Client.ShutdownEventArgs reason)
        {
            Consumer.ConnectionLost -= Consumer_ConnectionLost;
            Log.Warning("Connection lost to RabbitMQ Server due to {0}", reason.Cause);
            try
            {
                Consumer.StartConsuming();
            }
            catch (Exception ex)
            {
                Log.Warning(ex, "Shutting down old connection to allow new connection to replace it");
            }
            InitializeQueueConnection();
            StartConsuming();
        }

        protected abstract void StartConsuming();

        public virtual void Start()
        {
            Log.Verbose("Start Processing Service {0}", GetType().ToString());
        }

        public virtual void Stop()
        {
            Log.Verbose("Stop Processing Service {0}", GetType().ToString());
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (disposed) return;
            Log.Verbose("Disposing of Processing Service {0}", GetType().ToString());
            if (disposing)
            {
                Consumer.Dispose();
                Exchange.Dispose();
                InvalidExchange.Dispose();
            }
            disposed = true;
        }
    }
}
