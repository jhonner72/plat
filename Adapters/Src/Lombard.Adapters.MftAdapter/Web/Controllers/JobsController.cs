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
    [Route("Jobs")]
    public class JobsController : ApiController
    {
        private readonly IExchangePublisher<JobRequest> jobExchangePublisher;
        private readonly IMapper<CreateJobFromFileRequest, JobRequest> jobRequestMapper;

        public JobsController(
            IExchangePublisher<JobRequest> jobExchangePublisher,
            IMapper<CreateJobFromFileRequest, JobRequest> jobRequestMapper)
        {
            this.jobRequestMapper = jobRequestMapper;
            this.jobExchangePublisher = jobExchangePublisher;
        }

        [HttpPost]
        public async Task<HttpResponseMessage> PostAsync(CreateJobFromFileRequest request)
        {
            Log.Information("Create job from file has been requested : {@createJobFromFileRequest}", request);

            try
            {
                if (!ModelState.IsValid)
                {
                    Log.Error("Could not create job from file: {errors}", ModelState.SerializeForLog());
                    return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ModelState);
                }

                var job = jobRequestMapper.Map(request);
                await jobExchangePublisher.PublishAsync(job, job.jobIdentifier);
                
                return Request.CreateResponse(HttpStatusCode.OK,
                    new ApiResponse { message = string.Format("The request has completed successfully. A job has been queued for {0}.", request.FileName) });
            }
            catch (Exception ex)
            {
                const string message = "An error occurred while creating the job.";
                Log.Error(ex, message);
                return Request.CreateErrorResponse(HttpStatusCode.InternalServerError, message);
            }
        }
    }
}

