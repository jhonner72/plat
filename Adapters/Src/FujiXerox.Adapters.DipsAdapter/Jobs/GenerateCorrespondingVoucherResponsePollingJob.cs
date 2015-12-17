﻿using System;
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
    public class GenerateCorrespondingVoucherResponsePollingJob:PollingJob
    {
        public GenerateCorrespondingVoucherResponsePollingJob(IAdapterConfiguration configuration, ILogger log, RabbitMqExchange exchange) 
            : base(configuration, log, exchange)
        {
        }

        public override void ExecuteJob()
        {
            Log.Information("Scanning database for completed generate corresponding voucher");

            using (var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
            {
            using (var dbContext = new DipsDbContext(dbConnection))
            {
                //Get all the potential batches that have been completed
                var completedBatches =
                    dbContext.Queues
                    .Where(q =>
                        !q.ResponseCompleted
                        && q.S_LOCATION == DipsLocationType.GenerateCorrespondingVoucherDone.Value
                        && q.S_LOCK.Trim() == "0")
                    .ToList();

                Log.Information("Found {0} completed generate corresponding voucher", completedBatches.Count());

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
                            var originalVouchers = dbContext.NabChqPods
                                .Where(
                                    v =>
                                        v.S_BATCH == batchNumber && v.S_DEL_IND != "  255" &&
                                        v.isGeneratedVoucher != "1" &&
                                        (v.export_exclude_flag.Trim() != "1"))
                                .ToList();

                            var generatedVouchers = dbContext.NabChqPods
                                .Where(
                                    v =>
                                        v.S_BATCH == batchNumber && v.S_DEL_IND != "  255" &&
                                        v.isGeneratedVoucher == "1" &&
                                        (v.export_exclude_flag.Trim() != "1"))
                                .ToList();

                            var batchResponse = new GenerateCorrespondingVoucherResponse
                            {
                                updateVoucher = originalVouchers.Select(v => new VoucherInformation()
                                {
                                    voucherBatch = new VoucherBatch
                                    {
                                        batchAccountNumber = v.batchAccountNumber,
                                        batchType = ResponseHelper.TrimString(v.batch_type),
                                        captureBsb = v.captureBSB,
                                        collectingBank = v.collecting_bank,
                                        processingState =
                                            ResponseHelper.ParseState(ResponseHelper.TrimString(v.processing_state)),
                                        scannedBatchNumber = completedBatch.S_BATCH,
                                        unitID = originalVouchers.First().unit_id,
                                        workType =
                                            ResponseHelper.ParseWorkType(
                                                ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                        subBatchType = ResponseHelper.TrimString(v.sub_batch_type),
                                    },

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
                                        forValueType = ForValueTypeEnum.Inward_Non_For_Value,
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
                                    }
                                }).ToArray(),

                                generatedVoucher = generatedVouchers.Select(v => new VoucherInformation()
                                {
                                    voucherBatch = new VoucherBatch
                                    {
                                        batchAccountNumber = v.batchAccountNumber,
                                        batchType = ResponseHelper.TrimString(v.batch_type),
                                        captureBsb = v.captureBSB,
                                        collectingBank = v.collecting_bank,
                                        processingState =
                                            ResponseHelper.ParseState(ResponseHelper.TrimString(v.processing_state)),
                                        scannedBatchNumber = completedBatch.S_BATCH,
                                        unitID = originalVouchers.First().unit_id,
                                        workType =
                                            ResponseHelper.ParseWorkType(
                                                ResponseHelper.TrimString(completedBatch.S_JOB_ID)),
                                        subBatchType = ResponseHelper.TrimString(v.sub_batch_type),
                                    },

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
                                        forValueType = ForValueTypeEnum.Inward_Non_For_Value,
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
                                "Generate corresponding voucher batch '{@batch}' has been completed and a response has been placed on the queue",
                                completedBatch.S_BATCH);
                            Log.Information("Batch '{@batch}' response sent: {@response}", completedBatch.S_BATCH,
                                batchResponse);
                        }
                        catch (OptimisticConcurrencyException)
                        {
                            //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                            //basically ignore the message by loggin a warning and rolling back.
                            //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                            Log.Warning(
                                "Could not create a generate corresponding voucher response for batch '{@batch}' because the DIPS database row was updated by another connection",
                                completedBatch.S_BATCH);

                            tx.Rollback();
                        }
                        catch (Exception ex)
                        {
                            Log.Error(
                                ex,
                                "Could not complete and create a generate corresponding voucher response for batch '{@batch}'",
                                completedBatch.S_BATCH);
                            tx.Rollback();
                        }
                    }
                }
            }
        }
            Log.Information("Finished processing completed generate corresponding voucher batches");
        }
    }
}