using System;
using System.Data.Entity.Core;
using System.Globalization;
using System.Linq;
using System.Threading.Tasks;
using Autofac;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.Queues;
using Quartz;
using Serilog;
using Lombard.Adapters.DipsAdapter.Helpers;

namespace Lombard.Adapters.DipsAdapter.Jobs
{
    [DisallowConcurrentExecution]
    public class CorrectCodelineResponsePollingJob : IJob
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IExchangePublisher<CorrectBatchCodelineResponse> responseExchange;
        private readonly IAdapterConfiguration adapterConfiguration;

        public CorrectCodelineResponsePollingJob(
            ILifetimeScope component,
            IExchangePublisher<CorrectBatchCodelineResponse> responseExchange,
            IAdapterConfiguration adapterConfiguration,
            IDipsDbContext dbContext = null)
        {
            this.component = component;
            this.dbContext = dbContext;
            this.responseExchange = responseExchange;
            this.adapterConfiguration = adapterConfiguration;
        }

        public void Execute(IJobExecutionContext context)
        {
            Log.Information("Scanning database for completed codeline correction batches");

            using (var lifetimeScope = component.BeginLifetimeScope())
            {
                using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                {
                    //Get all the potential batches that have been completed
                    var completedBatches =
                        dipsDbContext.Queues
                            .Where(q =>
                                !q.ResponseCompleted
                                && q.S_LOCATION == DipsLocationType.CodelineCorrectionDone.Value
                                && q.S_LOCK.Trim() == "0")
                            .ToList();

                    Log.Information("Found {0} completed codeline correction batches", completedBatches.Count);

                    foreach (var completedBatch in completedBatches)
                    {
                        Log.Debug("Creating response for batch {@batch}", completedBatch.S_BATCH);

                        //only commit the transaction if
                        // a) we were the first application to mark this batch row as CorrectCodelineCompleted (DipsQueue uses optimistic concurrency) 
                        // b) we were able to place a response message on the bus
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //mark the line as completed
                                completedBatch.ResponseCompleted = true;
                                var routingKey = completedBatch.RoutingKey;

                                dipsDbContext.SaveChanges();

                                var batchNumber = completedBatch.S_BATCH;

                                //get the vouchers, generate and send the response
                                var vouchers = dipsDbContext.NabChqPods
                                    .Where(
                                        v =>
                                            v.S_BATCH == batchNumber && v.S_DEL_IND != "  255" &&
                                            (v.export_exclude_flag.Trim() != "1"))
                                    .ToList();

                                if (vouchers.Count > 0)
                                {
                                    var firstVoucher =
                                        vouchers.First(v => !ResponseHelper.ParseStringAsBool(v.isGeneratedVoucher));

                                    var batchResponse = new CorrectBatchCodelineResponse
                                    {
                                        voucherBatch = new VoucherBatch
                                        {
                                            batchAccountNumber = firstVoucher.batchAccountNumber,
                                            batchType = ResponseHelper.TrimString(firstVoucher.batch_type),
                                            captureBsb = firstVoucher.captureBSB,
                                            collectingBank = firstVoucher.collecting_bank,
                                            processingState =
                                                ResponseHelper.ParseState(
                                                    ResponseHelper.TrimString(firstVoucher.processing_state)),
                                            scannedBatchNumber = completedBatch.S_BATCH,
                                            unitID = firstVoucher.unit_id,
                                            workType =
                                                ResponseHelper.ParseWorkType(
                                                    ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                            subBatchType = ResponseHelper.TrimString(firstVoucher.sub_batch_type),

                                        },
                                        voucher = vouchers.Select(v => new CorrectCodelineResponse
                                        {
                                            accountNumber = ResponseHelper.TrimString(v.acc_num),
                                            amount = ResponseHelper.ParseAmountField(v.amount),
                                            auxDom = ResponseHelper.TrimString(v.ser_num),
                                            bsbNumber = v.bsb_num,
                                            dips_override = ResponseHelper.TrimString(v.@override),
                                            documentReferenceNumber = ResponseHelper.TrimString(v.doc_ref_num),
                                            documentType = ResponseHelper.ParseDocumentType(v.doc_type),
                                            extraAuxDom = ResponseHelper.TrimString(v.ead),
                                            forValueIndicator = ResponseHelper.TrimString(v.fv_ind),
                                            manualRepair = ResponseHelper.ParseStringAsInt(v.man_rep_ind),
                                            postTransmissionQaAmountFlag =
                                                ResponseHelper.ParseStringAsBool(v.fxaPtQAAmtFlag),
                                            postTransmissionQaCodelineFlag =
                                                ResponseHelper.ParseStringAsBool(v.fxaPtQACodelineFlag),
                                            presentationMode = ResponseHelper.TrimString(v.presentationMode),
                                            processingDate =
                                                DateTime.ParseExact(string.Format("{0}", v.proc_date), "yyyyMMdd",
                                                    CultureInfo.InvariantCulture),
                                            repostFromDRN = ResponseHelper.TrimString(v.repostFromDRN),
                                            repostFromProcessingDate =
                                                ResponseHelper.ParseDateField(v.repostFromProcessingDate),
                                            targetEndPoint = ResponseHelper.TrimString(v.ie_endPoint),
                                            transactionCode = ResponseHelper.TrimString(v.trancode),
                                            transactionLink = ResponseHelper.TrimString(v.transactionLinkNumber),
                                            unprocessable = ResponseHelper.ParseStringAsBool(v.unproc_flag),
                                            operatorID = ResponseHelper.TrimString(v.op_id),
                                            collectingBank = ResponseHelper.TrimString(v.collecting_bank),

                                            creditNoteFlag = ResponseHelper.ParseStringAsBool(v.creditNoteFlag)
                                        }).ToArray()
                                    };

                                    if (adapterConfiguration.DeleteDatabaseRows)
                                    {
                                        ResponseHelper.CleanupBatchData(batchNumber, dipsDbContext);
                                    }

                                    if (string.IsNullOrEmpty(routingKey))
                                    {
                                        routingKey = string.Empty;
                                    }

                                    Task.WaitAll(responseExchange.PublishAsync(batchResponse,
                                        completedBatch.CorrelationId,
                                        routingKey.Trim()));

                                    tx.Commit();

                                    Log.Debug(
                                        "Codeline correction batch '{@batch}' has been completed and a response has been placed on the queue",
                                        completedBatch.S_BATCH);
                                    Log.Information("Batch '{@batch}' response sent: {@response}",
                                        completedBatch.S_BATCH,
                                        batchResponse);
                                }
                                else
                                {
                                    Log.Error(
                                        "Could not create a codeline correction response for batch '{@batch}' because the vouchers cannot be queried",
                                        completedBatch.S_BATCH);
                                }
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a codeline correction response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                    completedBatch.S_BATCH);

                                tx.Rollback();
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a codeline correction response for batch '{@batch}'",
                                    completedBatch.S_BATCH);
                                tx.Rollback();
                            }
                        }
                    }
                }
            }

            Log.Information("Finished processing completed codeline correction batches");
        }
    }
}
