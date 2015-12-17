using System;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Jobs;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter
{
    public class ProcessingService
    {
        private ILogger Log { get; set; }
        private DipsConfiguration Configuration { get; set; }
        private ValidateCodelineResponsePollingJob ValidateCodelineResponse { get; set; }

        public ProcessingService(DipsConfiguration configuration, ILogger log)
        {
            Configuration = configuration;
            Log = log;
            ValidateCodelineResponse = new ValidateCodelineResponsePollingJob(configuration, log);
        }

        public void Start()
        {
            ValidateCodelineResponse.Start(TimeSpan.FromSeconds(Configuration.PollingIntervalSecs));
        }

        public void Stop()
        {
            ValidateCodelineResponse.ShutdownJob = true;
        }
    }
}
