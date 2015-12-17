using System;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using FujiXerox.Adapters.DipsAdapter.Helpers;
using FujiXerox.Adapters.DipsAdapter.Serialization;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.MessageQueue;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Jobs
{
    public class CheckThirdPartyResponsePollingJob : PollingJob
    {
        public CheckThirdPartyResponsePollingJob(IAdapterConfiguration configuration, ILogger log, RabbitMqExchange exchange) 
            : base(configuration, log, exchange)
        {
        }

        public override void ExecuteJob()
        {
            Log.Information("Scanning database for completed third party checking batches");
            using (var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
            {
                using (var dbContext = new DipsDbContext(dbConnection))
                {
                    //Get all the potential batches that have been completed
                    var completedBatches =
                        dbContext.Queues
                            .Where(q =>
                                !q.ResponseCompleted
                                && q.S_LOCATION == DipsLocationType.CheckThirdPartyDone.Value
                                && q.S_LOCK.Trim() == "0")
                            .ToList();

                    Log.Information("Found {0} completed third party checking batches", completedBatches.Count());

                    foreach (var completedBatch in completedBatches)
                    {
                        Log.Debug("Creating response for batch {@batch}", completedBatch.S_BATCH);

                        //only commit the transaction if
                        // a) we were the first application to mark this batch row as CorrectCodelineCompleted (DipsQueue uses optimistic concurrency) 
                        // b) we were able to place a response message on the bus
                        using (var tx = dbContext.BeginTransaction())
                        {
                            try
                            {
                                //mark the line as completed
                                completedBatch.ResponseCompleted = true;
                                var routingKey = completedBatch.RoutingKey;

                                dbContext.SaveChanges();

                                var batchNumber = completedBatch.S_BATCH;

                                //get the vouchers, generate and send the response
                                var vouchers = dbContext.NabChqPods
                                    .Where(
                                        v =>
                                            v.S_BATCH == batchNumber && v.S_DEL_IND != "  255" &&
                                            (v.export_exclude_flag.Trim() != "1"))
                                    .ToList();

                                if (vouchers.Count > 0)
                                {
                                    var firstVoucher = vouchers.First(v => v.isGeneratedVoucher != "1");

                                    var batchResponse = new CheckThirdPartyBatchResponse
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
                                                ResponseHelper.ParseWorkType(ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                            subBatchType = ResponseHelper.TrimString(firstVoucher.sub_batch_type),
                                        },
                                        voucher = vouchers.Select(v => new CheckThirdPartyResponse
                                        {
                                            voucherProcess = new VoucherProcess
                                            {
                                                adjustedFlag = ResponseHelper.ParseStringAsBool(v.adjustedFlag),
                                                highValueFlag = ResponseHelper.ParseStringAsBool(v.highValueFlag),
                                                isGeneratedVoucher = ResponseHelper.ParseStringAsBool(v.isGeneratedVoucher),
                                                manualRepair = ResponseHelper.ParseStringAsInt(v.man_rep_ind),
                                                preAdjustmentAmount = ResponseHelper.ParseAmountField(v.orig_amount),
                                                presentationMode = ResponseHelper.TrimString(v.presentationMode),
                                                rawMICR = ResponseHelper.TrimString(v.raw_micr),
                                                rawOCR = ResponseHelper.TrimString(v.raw_ocr),
                                                repostFromDRN = ResponseHelper.TrimString(v.repostFromDRN),
                                                repostFromProcessingDate =
                                                    ResponseHelper.ParseDateField(v.repostFromProcessingDate),
                                                surplusItemFlag = ResponseHelper.ParseStringAsBool(v.surplusItemFlag),
                                                suspectFraud = ResponseHelper.ParseStringAsBool(v.micr_suspect_fraud_flag),
                                                thirdPartyCheckFailed = ResponseHelper.ParseTpcResult(v.tpcResult),
                                                thirdPartyMixedDepositReturnFlag =
                                                    ResponseHelper.ParseStringAsBool(v.tpcMixedDepRet),
                                                thirdPartyPoolFlag =
                                                    ResponseHelper.ParseStringAsBool(v.fxa_tpc_suspense_pool_flag),
                                                transactionLinkNumber = ResponseHelper.TrimString(v.transactionLinkNumber),
                                                unencodedECDReturnFlag =
                                                    ResponseHelper.ParseStringAsBool(v.fxa_unencoded_ECD_return),
                                                unprocessable = ResponseHelper.ParseStringAsBool(v.unproc_flag),
                                                voucherDelayedIndicator = ResponseHelper.TrimString(v.voucherIndicatorField),
                                                operatorId = ResponseHelper.TrimString(v.op_id),
                                                adjustedBy = ResponseHelper.TrimString(v.op_id),
                                                adjustmentLetterRequired =
                                                    ResponseHelper.ParseStringAsBool(v.adjustmentLetterRequired),
                                                forValueType = null,
                                                postTransmissionQaAmountFlag =
                                                    ResponseHelper.ParseStringAsBool(v.fxaPtQAAmtFlag),
                                                postTransmissionQaCodelineFlag =
                                                    ResponseHelper.ParseStringAsBool(v.fxaPtQACodelineFlag),
                                                adjustmentReasonCode = ResponseHelper.ParseStringAsInt(v.adjustmentReasonCode),
                                                adjustmentDescription = ResponseHelper.TrimString(v.adjustmentDescription)
                                            },

                                            voucher = new Voucher
                                            {
                                                accountNumber = ResponseHelper.TrimString(v.acc_num),
                                                amount = ResponseHelper.ParseAmountField(v.amount),
                                                auxDom = ResponseHelper.TrimString(v.ser_num),
                                                bsbNumber = v.bsb_num,
                                                documentReferenceNumber =
                                                    ResponseHelper.ResolveDocumentReferenceNumber(
                                                        ResponseHelper.TrimString(v.doc_ref_num)),
                                                documentType = ResponseHelper.ParseDocumentType(v.doc_type),
                                                extraAuxDom = ResponseHelper.TrimString(v.ead),
                                                processingDate =
                                                    DateTime.ParseExact(string.Format("{0}", v.proc_date), "yyyyMMdd",
                                                        CultureInfo.InvariantCulture),
                                                transactionCode = ResponseHelper.TrimString(v.trancode),
                                                //targetEndPoint = ResponseHelper.TrimString(v.ie_endPoint),
                                                //forValueIndicator = ResponseHelper.TrimString(v.fv_ind),
                                                //dips_override = ResponseHelper.TrimString(v.@override),
                                            }
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
                                    Exchange.SendMessage(CustomJsonSerializer.MessageToBytes(batchResponse), routingKey.Trim(), completedBatch.CorrelationId);

                                    tx.Commit();

                                    Log.Debug(
                                        "Third party checking batch '{@batch}' has been completed and a response has been placed on the queue",
                                        completedBatch.S_BATCH);
                                    Log.Information("Batch '{@batch}' response sent: {@response}", completedBatch.S_BATCH,
                                        batchResponse);
                                }
                                else
                                {
                                    Log.Error(
                                       "Could not create a third party checking response for batch '{@batch}' because the vouchers cannot be queried",
                                       completedBatch.S_BATCH);
                                }
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a third party checking response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                    completedBatch.S_BATCH);

                                tx.Rollback();
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a third party checking response for batch '{@batch}'",
                                    completedBatch.S_BATCH);
                                tx.Rollback();
                            }
                        }
                    }
                }
            }
            Log.Information("Finished processing completed third party checking batches");
        }
    }
}