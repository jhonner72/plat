using System;
using System.Threading.Tasks;
using System.Web.Http;
using System.Net.Http;
using System.Net;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.Queues;
using Lombard.Extensions;
using Serilog;

namespace Lombard.Adapters.MftAdapter.Web.Controllers
{
    [Route("CopyImages")]
    public class CopyImagesController : ApiController
    {
        private readonly IExchangePublisher<string> copyImageExchangePublisher;

        public CopyImagesController(
            IExchangePublisher<string> copyImageExchangePublisher)
        {
            this.copyImageExchangePublisher = copyImageExchangePublisher;
        }

        [HttpPost]
        public async Task<HttpResponseMessage> PostAsync(CopyImageRequest request)
        {
            Log.Information("Create CopyImage from file has been requested : {@createCopyImageRequest}", request);

            try
            {
                if (!ModelState.IsValid)
                {
                    Log.Error("Could not create CopyImage from file: {errors}", ModelState.SerializeForLog());
                    return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ModelState);
                }

                await copyImageExchangePublisher.PublishAsync(request.FileName, request.FileName, string.IsNullOrEmpty(request.RoutingKey) ? string.Empty : request.RoutingKey);

                return Request.CreateResponse(HttpStatusCode.OK,
                    new ApiResponse { message = string.Format("The request has completed successfully. A CopyImage has been queued for {0}.", request) });
            }
            catch (Exception ex)
            {
                const string message = "An error occurred while creating the CopyImage.";
                Log.Error(ex, message);
                return Request.CreateErrorResponse(HttpStatusCode.InternalServerError, message);
            }
        }
    }
}

