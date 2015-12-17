using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Linq;
using System.Threading.Tasks;
using Autofac;
using Lombard.Adapters.Data;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Newtonsoft.Json;
using Quartz;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.Jobs
{
    [DisallowConcurrentExecution]
    public class GetVouchersInformationRequestPollingJob : IJob
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IExchangePublisher<GetVouchersInformationRequest> requestExchange;
        private readonly IAdapterConfiguration adapterConfiguration;

        public GetVouchersInformationRequestPollingJob(
            ILifetimeScope component,
            IExchangePublisher<GetVouchersInformationRequest> requestExchange,
            IAdapterConfiguration adapterConfiguration,
            IDipsDbContext dbContext = null)
        {
            this.component = component;
            this.dbContext = dbContext;
            this.requestExchange = requestExchange;
            this.adapterConfiguration = adapterConfiguration;
        }

        public void Execute(IJobExecutionContext context)
        {
            Log.Information("Scanning database for completed get vouchers information requests");

            using (var lifetimeScope = component.BeginLifetimeScope())
            {
                using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                {
                    //Get all the potential requests that have been completed
                    var pendingRequests =
                        dipsDbContext.DipsRequest
                            .Where(q =>
                                !q.RequestCompleted.HasValue || !q.RequestCompleted.Value)
                            .ToList();

                    Log.Information("Found {0} completed get vouchers information requests", pendingRequests.Count);

                    foreach (var pendingRequest in pendingRequests)
                    {
                        Log.Debug("Creating Request for request {@guidName}", pendingRequest.guid_name);

                        //only commit the transaction if
                        // a) we were the first application to mark this request row as CorrectCodelineCompleted (DipsQueue uses optimistic concurrency) 
                        // b) we were able to place a Request message on the bus
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //mark the line as completed
                                pendingRequest.RequestCompleted = true;

                                dipsDbContext.SaveChanges();

                                var requestNumber = pendingRequest.guid_name;

                                //get the record, generate and send the Request

                                var payload = JsonConvert.DeserializeObject<List<Criteria>>(pendingRequest.payload);

                                //Add the isReserved for balancing to Update criteria
                                //This is just to pass the isreservedforbalancing as true. could be dependent on the payload in future 
                                var tmpCriteria = new Criteria();
                                tmpCriteria.name = "voucherProcess.isReservedForBalancing";
                                tmpCriteria.value = "TRUE";
                                var tmpCriteriaArr = new Criteria[1];
                                tmpCriteriaArr[0] = tmpCriteria;

                                var requestRequest = new GetVouchersInformationRequest
                                {
                                    jobIdentifier = pendingRequest.guid_name.Trim(),
                                    imageRequired = ImageType.JPEG,
                                    imageResponseType = ResponseType.MESSAGE,
                                    metadataResponseType = ResponseType.MESSAGE,
                                    searchCriteria = payload.ToArray(),
                                    updateCriteria = tmpCriteriaArr
                                };

                                if (adapterConfiguration.DeleteDatabaseRows)
                                {
                                    RequestHelper.CleanupRequestData(requestNumber, dipsDbContext);
                                }

                                Task.WaitAll(requestExchange.PublishAsync(requestRequest, pendingRequest.guid_name,
                                    "NSBD"));

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
    }

    public class NameValuePair
    {
        public string Name { get; set; }
        public string Value { get; set; }
    }
}