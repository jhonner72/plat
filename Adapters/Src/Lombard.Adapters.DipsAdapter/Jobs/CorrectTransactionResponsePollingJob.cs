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
    public class CorrectTransactionResponsePollingJob : IJob
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IExchangePublisher<CorrectBatchTransactionResponse> responseExchange;
        private readonly IAdapterConfiguration adapterConfiguration;

        public CorrectTransactionResponsePollingJob(
            ILifetimeScope component,
            IExchangePublisher<CorrectBatchTransactionResponse> responseExchange,
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
            Log.Information("Scanning database for completed transaction correction batches");

            using (var lifetimeScope = component.BeginLifetimeScope())
            {
                using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                {
                    //Get all the potential batches that have been completed
                    var completedBatches =
                        dipsDbContext.Queues
                            .Where(q =>
                                !q.ResponseCompleted
                                && q.S_LOCATION == DipsLocationType.TransactionCorrectionDone.Value
                                && q.S_LOCK.Trim() == "0")
                            .ToList();

                    Log.Information("Found {0} completed transaction correction batches", completedBatches.Count);

                    foreach (var completedBatch in completedBatches)
                    {
                        Log.Debug("Creating response for batch {@batch}", completedBatch.S_BATCH);

                        //only commit the transaction if
                        // a) we were the first application to mark this batch row as CorrectTransactionCompleted (DipsQueue uses optimistic concurrency) 
                        // b) we were able to place a response message on the bus
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //mark the batch as completed so it gets 'locked'
                                completedBatch.ResponseCompleted = true;
                                var routingKey = completedBatch.RoutingKey;

                                dipsDbContext.SaveChanges();

                                var batchNumber = completedBatch.S_BATCH;

                                //get the vouchers, generate and send the response
                                var vouchers = dipsDbContext.NabChqPods
                                    .Where(v =>
                                        v.S_BATCH == batchNumber
                                        && v.S_DEL_IND != "  255")
                                    .ToList();

                                var firstVoucher = vouchers.First(v => v.isGeneratedVoucher != "1");

                                //use bitmasks to map the values from S_STATUS1 field
                                var batchResponse = new CorrectBatchTransactionResponse
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
                                    voucher = vouchers.Select(v => new CorrectTransactionResponse
                                    {
                                        adjustedBy = ResponseHelper.TrimString(v.adjustedBy),
                                        adjustedFlag = ResponseHelper.ParseStringAsBool(v.adjustedFlag),
                                        adjustmentDescription = ResponseHelper.TrimString(v.adjustmentDescription),
                                        adjustmentLetterRequired =
                                            ResponseHelper.ParseStringAsBool(v.adjustmentLetterRequired),
                                        adjustmentReasonCode = ResponseHelper.ParseStringAsInt(v.adjustmentReasonCode),
                                        dips_override = ResponseHelper.TrimString(v.@override),
                                        forValueIndicator = ResponseHelper.TrimString(v.fv_ind),
                                        highValueFlag = ResponseHelper.ParseStringAsBool(v.highValueFlag),
                                        isGeneratedVoucher = ResponseHelper.ParseStringAsBool(v.isGeneratedVoucher),
                                        manualRepair = ResponseHelper.ParseStringAsInt(v.man_rep_ind),
                                        operatorId = ResponseHelper.TrimString(v.op_id),
                                        postTransmissionQaAmountFlag =
                                            ResponseHelper.ParseStringAsBool(v.fxaPtQAAmtFlag),
                                        postTransmissionQaCodelineFlag =
                                            ResponseHelper.ParseStringAsBool(v.fxaPtQACodelineFlag),
                                        preAdjustmentAmount = ResponseHelper.ParseAmountField(v.orig_amount),
                                        presentationMode = ResponseHelper.TrimString(v.presentationMode),
                                        surplusItemFlag = ResponseHelper.ParseStringAsBool(v.surplusItemFlag),
                                        suspectFraudFlag =
                                            ResponseHelper.ParseOverloadedSuspectFraudFlag(v.micr_suspect_fraud_flag),
                                        targetEndPoint = ResponseHelper.TrimString(v.ie_endPoint),
                                        thirdPartyCheckRequired = ResponseHelper.ParseYNStringAsBool(v.tpcRequired),
                                        thirdPartyMixedDepositReturnFlag =
                                            ResponseHelper.ParseStringAsBool(v.tpcMixedDepRet),
                                        transactionLinkNumber = ResponseHelper.TrimString(v.transactionLinkNumber),
                                        unencodedECDReturnFlag =
                                            ResponseHelper.ParseStringAsBool(v.fxa_unencoded_ECD_return),
                                        unprocessable = ResponseHelper.ParseStringAsBool(v.unproc_flag),
                                        voucherDelayedIndicator = ResponseHelper.TrimString(v.delay_ind),
                                        dipsTraceNumber = ResponseHelper.TrimString(v.S_TRACE),
                                        dipsSequenceNumber = ResponseHelper.TrimString(v.S_SEQUENCE),
                                        listingPageNumber = ResponseHelper.TrimString(v.listingPageNumber),
                                        isRetrievedVoucher = ResponseHelper.ParseStringAsBool(v.isRetrievedVoucher),
                                        alternateAccountNumber = ResponseHelper.TrimString(v.alt_acc_num),
                                        alternateAuxDom = ResponseHelper.TrimString(v.alt_ser_num),
                                        alternateBsbNumber = ResponseHelper.TrimString(v.alt_bsb_num),
                                        alternateExAuxDom = ResponseHelper.TrimString(v.alt_ead),
                                        alternateTransactionCode = ResponseHelper.TrimString(v.alt_trancode),
                                        creditNoteFlag = ResponseHelper.ParseStringAsBool(v.creditNoteFlag),
                                        insertedCreditType = ResponseHelper.ParseInsertedCreditType(v.insertedCreditType),

                                        voucher = new Voucher
                                        {
                                            accountNumber = ResponseHelper.TrimString(v.acc_num),
                                            amount = ResponseHelper.ParseAmountField(v.amount),
                                            auxDom = ResponseHelper.TrimString(v.ser_num),
                                            bsbNumber = v.bsb_num,
                                            documentReferenceNumber = ResponseHelper.TrimString(v.doc_ref_num),
                                            extraAuxDom = ResponseHelper.TrimString(v.ead),
                                            transactionCode = ResponseHelper.TrimString(v.trancode),
                                            documentType = ResponseHelper.ParseDocumentType(v.doc_type),
                                            processingDate =
                                                DateTime.ParseExact(
                                                    string.Format("{0}{1}", completedBatch.S_SDATE,
                                                        completedBatch.S_STIME),
                                                    "dd/MM/yyHH:mm:ss", CultureInfo.InvariantCulture),
                                        }
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

                                Task.WaitAll(responseExchange.PublishAsync(batchResponse, completedBatch.CorrelationId,
                                    routingKey.Trim()));

                                tx.Commit();

                                Log.Debug(
                                    "Transaction correction batch '{@batch}' has been completed and a response has been placed on the queue",
                                    completedBatch.S_BATCH);
                                Log.Information("Batch '{@batch}' response sent: {@response}", completedBatch.S_BATCH,
                                    batchResponse);

                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and roll back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a transaction correction response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                    completedBatch.S_BATCH);

                                tx.Rollback();
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create an transaction correction response for batch '{@batch}'",
                                    completedBatch.S_BATCH);
                                tx.Rollback();
                            }
                        }
                    }
                }
            }

            Log.Information("Finished processing completed transaction correction batches");
        }
    }
}
