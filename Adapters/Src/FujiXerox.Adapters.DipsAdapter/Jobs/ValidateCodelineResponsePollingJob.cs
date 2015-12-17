using System;
using System.Collections.Generic;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Jobs
{
    public class ValidateCodelineResponsePollingJob : PollingJob
    {

        public ValidateCodelineResponsePollingJob(IAdapterConfiguration configuration, ILogger log, RabbitMqExchange exchange) : base(configuration,log, exchange)
        {
        }

        public override void ExecuteJob()
        {
            using (var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
            {
                using (var dbContext = new DipsDbContext(dbConnection))
                {
                    Log.Information("Scanning database for completed codeline validation batches");
                    //Get all the potential batches that have been completed
                    var completedBatches =
                        dbContext.Queues
                            .Where(q =>
                                !q.ResponseCompleted
                                && q.S_LOCATION == DipsLocationType.CodelineValidationDone.Value
                                && q.S_LOCK.Trim() == "0")
                            .ToList();

                    Serilog.Log.Information("Found {0} completed codeline validation batches", completedBatches.Count());

                    foreach (var completedBatch in completedBatches)
                    {
                        string routingKey;
                        Serilog.Log.Debug("Creating response for batch {@batch}", completedBatch.S_BATCH);

                        //only commit the transaction if
                        // a) we were the first application to mark this batch row as ValidateCodelineCompleted (DipsQueue uses optimistic concurrency) 
                        // b) we were able to place a response message on the bus
                        using (var tx = dbContext.BeginTransaction())
                        {
                            try
                            {
                                //mark the batch as completed so it gets 'locked'
                                completedBatch.ResponseCompleted = true;
                                routingKey = completedBatch.RoutingKey;

                                dbContext.SaveChanges();

                                var batchNumber = completedBatch.S_BATCH;

                                //get the vouchers, generate and send the response
                                var vouchers = dbContext.NabChqPods
                                    .Where(v => v.S_BATCH == batchNumber)
                                    .AsEnumerable()
                                    .Select(v => new
                                    {
                                        S_STATUS1 =
                                            int.Parse(string.IsNullOrWhiteSpace(v.S_STATUS1) ? "0" : v.S_STATUS1),
                                        voucher = v
                                    }).ToList();

                                //use bitmasks to map the values from S_STATUS1 field
                                var batchResponse = new ValidateBatchCodelineResponse
                                {
                                    voucherBatch = new VoucherBatch
                                    {
                                        scannedBatchNumber = completedBatch.S_BATCH,
                                        workType =
                                            ResponseHelper.ParseWorkType(
                                                ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                        batchAccountNumber = vouchers.First().voucher.batchAccountNumber,
                                        captureBsb = vouchers.First().voucher.captureBSB,
                                        processingState =
                                            ResponseHelper.ParseState(
                                                ResponseHelper.TrimString(vouchers.First().voucher.processing_state)),
                                        collectingBank = vouchers.First().voucher.collecting_bank,
                                        unitID = vouchers.First().voucher.unit_id,
                                        batchType = ResponseHelper.TrimString(vouchers.First().voucher.batch_type),
                                        subBatchType =
                                            ResponseHelper.TrimString(vouchers.First().voucher.sub_batch_type),
                                    },
                                    voucher = vouchers.Select(v => new ValidateCodelineResponse
                                    {
                                        extraAuxDomStatus = DipsStatus1Bitmask.GetExtraAuxDomStatusMasked(v.S_STATUS1),
                                        auxDomStatus = DipsStatus1Bitmask.GetAuxDomStatusMasked(v.S_STATUS1),
                                        accountNumberStatus =
                                            DipsStatus1Bitmask.GetAccountNumberStatusMasked(v.S_STATUS1),
                                        amountStatus = DipsStatus1Bitmask.GetAmountStatusMasked(v.S_STATUS1),
                                        bsbNumberStatus = DipsStatus1Bitmask.GetBsbNumberStatusMasked(v.S_STATUS1),
                                        transactionCodeStatus =
                                            DipsStatus1Bitmask.GetTransactionCodeStatusMasked(v.S_STATUS1),
                                        documentReferenceNumber =
                                            ResponseHelper.ResolveDocumentReferenceNumber(
                                                ResponseHelper.TrimString(v.voucher.doc_ref_num)),
                                        targetEndPoint = ResponseHelper.TrimString(v.voucher.ie_endPoint),
                                        documentType = ResponseHelper.ParseDocumentType(v.voucher.doc_type),
                                        processingDate =
                                            DateTime.ParseExact(string.Format("{0}", v.voucher.proc_date), "yyyyMMdd",
                                                CultureInfo.InvariantCulture),
                                    }).ToArray()
                                };


                                if (Configuration.DeleteDatabaseRows)
                                {
                                    ResponseHelper.CleanupBatchData(batchNumber, dbContext);
                                }

                                if (string.IsNullOrEmpty(routingKey))
                                {
                                    routingKey = string.Empty;
                                }

                                //Task.WaitAll(responseExchange.PublishAsync(batchResponse, completedBatch.CorrelationId, routingKey.Trim()));

                                tx.Commit();

                                Serilog.Log.Debug(
                                    "Codeline validation batch '{@batch}' has been completed and a response has been placed on the queue",
                                    completedBatch.S_BATCH);
                                Serilog.Log.Information("Batch '{@batch}' response sent: {@response}",
                                    completedBatch.S_BATCH, batchResponse);

                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and roll back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Serilog.Log.Warning(
                                    "Could not create a codeline validation response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                    completedBatch.S_BATCH);

                                tx.Rollback();
                            }
                            catch (Exception ex)
                            {
                                Serilog.Log.Error(
                                    ex,
                                    "Could not complete and create a codeline validation response for batch '{@batch}'",
                                    completedBatch.S_BATCH);
                                tx.Rollback();
                            }
                        }
                    }
                }
            }
        }
    }
}
