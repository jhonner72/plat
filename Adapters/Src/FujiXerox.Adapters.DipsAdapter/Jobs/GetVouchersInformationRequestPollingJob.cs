using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using FujiXerox.Adapters.DipsAdapter.Serialization;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.MessageQueue;
using Newtonsoft.Json;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Jobs
{
    public class GetVouchersInformationRequestPollingJob : PollingJob
    {
        public GetVouchersInformationRequestPollingJob(IAdapterConfiguration configuration, ILogger log, RabbitMqExchange exchange) : base(configuration, log, exchange)
        {
        }

        public override void ExecuteJob()
        {
            Log.Information("Scanning database for completed get vouchers information requests");

            using (var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
            {
            using (var dbContext = new DipsDbContext(dbConnection))
            {
                //Get all the potential requests that have been completed
                var pendingRequests =
                    dbContext.DipsRequest
                    .Where(q =>
                        !q.RequestCompleted.HasValue || !q.RequestCompleted.Value)
                    .ToList();

                Log.Information("Found {0} completed get vouchers information requests", pendingRequests.Count());

                foreach (var pendingRequest in pendingRequests)
                {
                    Log.Debug("Creating Request for request {@guidName}", pendingRequest.guid_name);

                    //only commit the transaction if
                    // a) we were the first application to mark this request row as CorrectCodelineCompleted (DipsQueue uses optimistic concurrency) 
                    // b) we were able to place a Request message on the bus
                    using (var tx = dbContext.BeginTransaction())
                    {
                        try
                        {
                            //mark the line as completed
                            pendingRequest.RequestCompleted = true;

                            dbContext.SaveChanges();

                            var requestNumber = pendingRequest.guid_name;

                            //get the record, generate and send the Request

                            var payload = JsonConvert.DeserializeObject<List<Criteria>>(pendingRequest.payload);

                            var requestRequest = new GetVouchersInformationRequest
                            {
                                jobIdentifier = pendingRequest.guid_name.Trim(),
                                imageRequired = ImageType.JPEG,
                                imageResponseType = ResponseType.MESSAGE,
                                metadataResponseType = ResponseType.MESSAGE,
                                searchCriteria = payload.ToArray()
                            };

                            if (Configuration.DeleteDatabaseRows)
                            {
                                RequestHelper.CleanupRequestData(requestNumber, dbContext);
                            }

                            Exchange.SendMessage(CustomJsonSerializer.MessageToBytes(requestRequest), "NSBD", pendingRequest.guid_name);

                            tx.Commit();

                            Log.Debug(
                                "get vouchers information request '{@guidName}' has been completed and a Request has been placed on the queue",
                                pendingRequest.guid_name);
                            Log.Information("Batch '{@guidName}' Request sent: {@requestRequest}",
                                pendingRequest.guid_name, requestRequest);
                        }
                        catch (OptimisticConcurrencyException)
                        {
                            //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                            //basically ignore the message by loggin a warning and rolling back.
                            //if this row was not included by mistake (e.g. it should be included), it will just come in in the next request run.
                            Log.Warning(
                                "Could not create a get vouchers information Request for request '{@guidName}' because the DIPS database row was updated by another connection",
                                pendingRequest.guid_name);

                            tx.Rollback();
                        }
                        catch (Exception ex)
                        {
                            Log.Error(
                                ex,
                                "Could not complete and create a get vouchers information Request for request '{@guidName}'",
                                pendingRequest.guid_name);
                            tx.Rollback();
                        }
                    }
                }
            }
           }
            Log.Information("Finished processing completed get vouchers information requests");
        }

        public class NameValuePair
        {
            public string Name { get; set; }
            public string Value { get; set; }
        }
    }
}