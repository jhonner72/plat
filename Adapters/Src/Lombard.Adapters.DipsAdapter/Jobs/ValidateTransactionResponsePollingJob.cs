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
    public class ValidateTransactionResponsePollingJob : IJob
    {
        private readonly ILifetimeScope component;
        private readonly IDipsDbContext dbContext;
        private readonly IExchangePublisher<ValidateBatchTransactionResponse> responseExchange;
        private readonly IAdapterConfiguration adapterConfiguration;

        public ValidateTransactionResponsePollingJob(
            ILifetimeScope component,
            IExchangePublisher<ValidateBatchTransactionResponse> responseExchange,
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
            Log.Information("Scanning database for completed transaction validation batches");

            using (var lifetimeScope = component.BeginLifetimeScope())
            {
                using (var dipsDbContext = dbContext ?? lifetimeScope.Resolve<IDipsDbContext>())
                {
                //Get all the potential batches that have been completed
                var completedBatches =
                        dipsDbContext.Queues
                    .Where(q =>
                        !q.ResponseCompleted
                        && q.S_LOCATION == DipsLocationType.TransactionValidationDone.Value
                        && q.S_LOCK.Trim() == "0")
                    .ToList();

                    Log.Information("Found {0} completed transaction validation batches", completedBatches.Count);

                foreach (var completedBatch in completedBatches)
                {
                    Log.Debug("Creating response for batch {@batch}", completedBatch.S_BATCH);

                    //only commit the transaction if
                    // a) we were the first application to mark this batch row as ValidateTransactionCompleted (DipsQueue uses optimistic concurrency) 
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
                                .Where(v => v.S_BATCH == batchNumber && v.S_DEL_IND != "  255")
                                .ToList()
                                .Select(v => new
                                {
                                        S_STATUS1 =
                                            int.Parse(string.IsNullOrWhiteSpace(v.S_STATUS1) ? "0" : v.S_STATUS1),
                                    voucher = v
                                }).ToList();

                            if (vouchers.Count > 0)
                            {

                                var firstVoucher = vouchers.First(v => v.voucher.isGeneratedVoucher != "1");

                                //use bitmasks to map the values from S_STATUS1 field
                                var batchResponse = new ValidateBatchTransactionResponse
                                {
                                    voucherBatch = new VoucherBatch
                                    {
                                        scannedBatchNumber = completedBatch.S_BATCH,
                                            workType =
                                                ResponseHelper.ParseWorkType(
                                                    ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                        batchAccountNumber = firstVoucher.voucher.batchAccountNumber,
                                        captureBsb = firstVoucher.voucher.captureBSB,
                                            processingState =
                                                ResponseHelper.ParseState(
                                                    ResponseHelper.TrimString(firstVoucher.voucher.processing_state)),
                                        collectingBank = firstVoucher.voucher.collecting_bank,
                                        unitID = firstVoucher.voucher.unit_id,
                                        batchType = ResponseHelper.TrimString(firstVoucher.voucher.batch_type),
                                            subBatchType =
                                                ResponseHelper.TrimString(firstVoucher.voucher.sub_batch_type),
                                    },
                                    voucher = vouchers.Select(v => new ValidateTransactionResponse
                                    {
                                        documentReferenceNumber = v.voucher.S_TRACE.Trim(),
                                        surplusItemFlag = ResponseHelper.ParseStringAsBool(v.voucher.surplusItemFlag),
                                        suspectFraudFlag = ResponseHelper.ParseStringAsBool(v.voucher.micr_suspect_fraud_flag),
                                        codelineFieldsStatus = new CodelineStatus
                                        {
                                                extraAuxDomStatus =
                                                    DipsStatus1Bitmask.GetExtraAuxDomStatusMasked(v.S_STATUS1),
                                            auxDomStatus = DipsStatus1Bitmask.GetAuxDomStatusMasked(v.S_STATUS1),
                                                accountNumberStatus =
                                                    DipsStatus1Bitmask.GetAccountNumberStatusMasked(v.S_STATUS1),
                                            amountStatus = DipsStatus1Bitmask.GetAmountStatusMasked(v.S_STATUS1),
                                                bsbNumberStatus =
                                                    DipsStatus1Bitmask.GetBsbNumberStatusMasked(v.S_STATUS1),
                                                transactionCodeStatus =
                                                    DipsStatus1Bitmask.GetTransactionCodeStatusMasked(v.S_STATUS1),
                                        },
                                        reasonCode = ResponseHelper.GetExpertBalanceReason(v.voucher.balanceReason),
                                            transactionLinkNumber =
                                                ResponseHelper.TrimString(v.voucher.transactionLinkNumber),
                                        unprocessable = ResponseHelper.ParseStringAsBool(v.voucher.unproc_flag),
                                        forValueIndicator = ResponseHelper.TrimString(v.voucher.fv_ind),
                                        dips_override = ResponseHelper.TrimString(v.voucher.@override),
                                            thirdPartyCheckRequired =
                                                ResponseHelper.ParseYNStringAsBool(v.voucher.tpcRequired),
                                            unencodedECDReturnFlag =
                                                ResponseHelper.ParseStringAsBool(v.voucher.fxa_unencoded_ECD_return),
                                            thirdPartyMixedDepositReturnFlag =
                                                ResponseHelper.ParseStringAsBool(v.voucher.tpcMixedDepRet),
                                            postTransmissionQaAmountFlag =
                                                ResponseHelper.ParseStringAsBool(v.voucher.fxaPtQAAmtFlag),
                                            postTransmissionQaCodelineFlag =
                                                ResponseHelper.ParseStringAsBool(v.voucher.fxaPtQACodelineFlag),
                                        voucher = new Voucher
                                        {
                                            accountNumber = ResponseHelper.TrimString(v.voucher.acc_num),
                                            amount = ResponseHelper.ParseAmountField(v.voucher.amount),
                                            auxDom = ResponseHelper.TrimString(v.voucher.ser_num),
                                            bsbNumber = v.voucher.bsb_num,
                                            //DIPS overwrites doc_ref_num with empty values during auto balancing ..
                                            documentReferenceNumber = v.voucher.S_TRACE.Trim(),
                                            extraAuxDom = ResponseHelper.TrimString(v.voucher.ead),
                                            transactionCode = ResponseHelper.TrimString(v.voucher.trancode),
                                            documentType = ResponseHelper.ParseDocumentType(v.voucher.doc_type),
                                                processingDate =
                                                    DateTime.ParseExact(
                                                        string.Format("{0}{1}", completedBatch.S_SDATE,
                                                            completedBatch.S_STIME), "dd/MM/yyHH:mm:ss",
                                                        CultureInfo.InvariantCulture),
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

                                    Task.WaitAll(responseExchange.PublishAsync(batchResponse,
                                        completedBatch.CorrelationId, routingKey.Trim()));

                                tx.Commit();

                                    Log.Debug(
                                        "Transaction validation batch '{@batch}' has been completed and a response has been placed on the queue",
                                        completedBatch.S_BATCH);
                                    Log.Information("Batch '{@batch}' response sent: {@response}",
                                        completedBatch.S_BATCH, batchResponse);
                            }
                            else
                            {
                                Log.Error(
                                   "Could not create a transaction validation response for batch '{@batch}' because the vouchers cannot be queried",
                                   completedBatch.S_BATCH);
                            }
                        }
                        catch (OptimisticConcurrencyException)
                        {
                            //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                            //basically ignore the message by loggin a warning and roll back.
                            //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                            Log.Warning(
                                "Could not create a transaction validation response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                completedBatch.S_BATCH);

                            tx.Rollback();
                        }
                        catch (Exception ex)
                        {
                            Log.Error(
                                ex,
                                "Could not complete and create an transaction validation response for batch '{@batch}'",
                                completedBatch.S_BATCH);
                            tx.Rollback();
                        }
                    }
                }
            }
            }

            Log.Information("Finished processing completed transaction validation batches");
        }

    }
}
