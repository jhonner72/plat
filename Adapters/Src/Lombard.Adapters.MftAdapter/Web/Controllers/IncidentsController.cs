using System;
using System.Threading.Tasks;
using System.Web.Http;
using System.Net.Http;
using System.Net;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.Queues;
using Lombard.Extensions;
using Serilog;


namespace Lombard.Adapters.MftAdapter.Web.Controllers
{
    [Route("Incidents")]
    public class IncidentsController : ApiController
    {
        private readonly IExchangePublisher<Incident> incidentExchangePublisher;
        private readonly IMapper<CreateIncidentRequest, Incident> incidentRequestMapper;

        public IncidentsController(
            IExchangePublisher<Incident> incidentExchangePublisher,
            IMapper<CreateIncidentRequest, Incident> incidentRequestMapper)
        {
            this.incidentRequestMapper = incidentRequestMapper;
            this.incidentExchangePublisher = incidentExchangePublisher;
        }

        [HttpPost]
        public async Task<HttpResponseMessage> PostAsync(CreateIncidentRequest request)
        {
            Log.Information("Create incident from file has been requested : {@createIncidentFromFileRequest}", request);

            try
            {
                if (!ModelState.IsValid)
                {
                    Log.Error("Could not create incident from file: {errors}", ModelState.SerializeForLog());
                    return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ModelState);
                }

                var incident = incidentRequestMapper.Map(request);
                await incidentExchangePublisher.PublishAsync(incident, incident.jobIdentifier);

                return Request.CreateResponse(HttpStatusCode.OK,
                    new ApiResponse { message = string.Format("The request has completed successfully. A incident has been queued for {0}.", request.JobIdentifier) });
            }
            catch (Exception ex)
            {
                const string message = "An error occurred while creating the incident.";
                Log.Error(ex, message);
                return Request.CreateErrorResponse(HttpStatusCode.InternalServerError, message);
            }
        }
    }
}

