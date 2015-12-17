using System;
using System.Threading;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Jobs
{
    public abstract class PollingJob
    {
        public bool ShutdownJob { private get; set; }
        private AutoResetEvent AutoResetEvent { get; set; }
        private Timer timer;
        private bool running;
        protected IAdapterConfiguration Configuration { get; private set; }
        protected ILogger Log { get; private set; }
        protected RabbitMqExchange Exchange { get; private set; }

        protected PollingJob(IAdapterConfiguration configuration, ILogger log, RabbitMqExchange exchange)
        {
            Configuration = configuration;
            Log = log;
            Exchange = exchange;
        }

        public void Start(TimeSpan period)
        {
            if (running) return;
            running = true;
            AutoResetEvent = new AutoResetEvent(false);
            timer = new Timer(ExecutePollingJob, AutoResetEvent, TimeSpan.Zero, period);
        }

        private void ExecutePollingJob(object stateInfo)
        {
            var autoEvent = (AutoResetEvent) stateInfo;
            if (autoEvent != null && ShutdownJob)
            {
                autoEvent.Set();
                running = false;
                return;
            }
            ExecuteJob();
        }

        public abstract void ExecuteJob();
    }
}
