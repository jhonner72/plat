using System;
using System.Threading.Tasks;
using System.Web.Http;
using System.Net.Http;
using System.Net;
using Lombard.Adapters.MftAdapter.Collections;
using Lombard.Adapters.MftAdapter.Messages.XsdImports;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.Queues;
using Lombard.Extensions;
using Serilog;


namespace Lombard.Adapters.MftAdapter.Web.Controllers
{
    [Route("File")]
    public class FileController : ApiController
    {
        private readonly IExchangePublisher<SendBatchValueInstructionFileResponse> sendVifExchangePublisher;
        private readonly VifDictionary vifDictionary;

        public FileController(
            IExchangePublisher<SendBatchValueInstructionFileResponse> sendVifExchangePublisher, 
            VifDictionary vifDictionary)
        {
            this.sendVifExchangePublisher = sendVifExchangePublisher;
            this.vifDictionary = vifDictionary;
        }

        [HttpPost]
        public async Task<HttpResponseMessage> PostAsync(FileSentNotificationRequest request)
        {
            Log.Information("File sent notification received : {@fileSentNotificationRequest}", request);

            try
            {
                if (!ModelState.IsValid)
                {
                    Log.Error("Could not create notification: {errors}", ModelState.SerializeForLog());
                    return Request.CreateErrorResponse(HttpStatusCode.BadRequest, ModelState);
                }

                await HandleFileSentRequest(request);
                
                return Request.CreateResponse(HttpStatusCode.OK,
                    new ApiResponse { message = string.Format("The request has completed successfully.") });
            }
            catch (Exception ex)
            {
                const string Message = "An error occurred while creating the job.";
                Log.Error(ex, Message);
                return Request.CreateErrorResponse(HttpStatusCode.InternalServerError, Message);
            }
        }

        private async Task HandleFileSentRequest(FileSentNotificationRequest request)
        {
            switch (request.FileType)
            {
                case "VIF":
                    string correlationId;

                    if (!vifDictionary.TryRemove(request.Filename, out correlationId))
                    {
                        throw new InvalidOperationException(string.Format("Could not find '{0}' in pending transfers", request.Filename));
                    }

                    var batchResponse = new SendBatchValueInstructionFileResponse
                    {
                        valueInstructionFile = new[]
                        {
                            new SendValueInstructionFileResponse
                            {
                                valueInstructionFileBatchFileStatus = ValueInstructionFileStatusEnum.Completed,
                                valueInstructionFileFilename = request.Filename,
                            }
                        }
                    };

                    await sendVifExchangePublisher.PublishAsync(batchResponse, correlationId);

                    break;
                default:
                    throw new InvalidOperationException(string.Format("The requested file type '{0}' is not supported.", request.Filename));
            }
            
        }
    }
}

