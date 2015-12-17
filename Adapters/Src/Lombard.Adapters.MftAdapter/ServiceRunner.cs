using System;
using System.Web.Http;
using Lombard.Adapters.MftAdapter.Configuration;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Common.Queues;
using Microsoft.Owin.Hosting;
using Owin;
using Serilog;

namespace Lombard.Adapters.MftAdapter
{
    public class ServiceRunner
    {
        private readonly IAdapterConfiguration adapterConfig;
        private readonly HttpConfiguration httpConfig;
        private readonly IExchangePublisher<JobRequest> jobExchangePublisher;
        private readonly IExchangePublisher<string> copyImagesExchangePublisher;
        private readonly IExchangePublisher<Incident> incidentExchangePublisher;

        private IDisposable webApp;

        public ServiceRunner(
            IAdapterConfiguration adapterConfig,
            HttpConfiguration httpConfig,
            IExchangePublisher<JobRequest> jobExchangePublisher,
            IExchangePublisher<string> copyImagesExchangePublisher,
            IExchangePublisher<Incident> incidentExchangePublisher)
        {
            this.adapterConfig = adapterConfig;
            this.httpConfig = httpConfig;
            this.jobExchangePublisher = jobExchangePublisher;
            this.copyImagesExchangePublisher = copyImagesExchangePublisher;
            this.incidentExchangePublisher = incidentExchangePublisher;
        }

        public void Start()
        {
            webApp = WebApp.Start(adapterConfig.ApiUrl, WebPipelineConfiguration);

            if (adapterConfig.HandleJobRequests)
            {
                Log.Information("This service will handle job requests");
                jobExchangePublisher.Declare(adapterConfig.JobsExchangeName);
            }

            if (adapterConfig.HandleCopyImageRequests)
            {
                Log.Information("This service will handle copy image requests");
                copyImagesExchangePublisher.Declare(adapterConfig.CopyImagesExchangeName);
            }

            if (adapterConfig.HandleIncidentRequests)
            {
                Log.Information("This service will handle incident requests");
                incidentExchangePublisher.Declare(adapterConfig.IncidentExchangeName);
            }

            Log.Information("MFT Adapter Service Started");
        }

        public void Stop()
        {
            webApp.Dispose();

            Log.Information("MFT Adapter Service Stopped");
        }

        private void WebPipelineConfiguration(IAppBuilder app)
        {
            //Use attribute based routing
            httpConfig.MapHttpAttributeRoutes();

            app.UseWebApi(httpConfig);
        }
    }
}
