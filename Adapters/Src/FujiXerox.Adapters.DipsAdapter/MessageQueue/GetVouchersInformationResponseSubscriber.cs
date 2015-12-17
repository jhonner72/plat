﻿using System.Collections.Generic;
using System.Globalization;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.MessageQueue;
using Serilog;
using Lombard.Adapters.Data.Domain;
using System;
using System.Data.Entity.Core;
using System.Data.SqlClient;
using System.Linq;
using Lombard.Adapters.Data;
using Newtonsoft.Json;

namespace FujiXerox.Adapters.DipsAdapter.MessageQueue
{
    public class GetVouchersInformationResponseSubscriber : SubscriberBase<GetVouchersInformationResponse>
    {
        public GetVouchersInformationResponseSubscriber(DipsConfiguration configuration, ILogger logger, RabbitMqConsumer consumer, RabbitMqExchange invalidExchange, string invalidRoutingKey, string recoverableRoutingKey) 
            : base(configuration, logger, consumer, invalidExchange, invalidRoutingKey, recoverableRoutingKey)
        {
        }

        public override void Consumer_ReceiveMessage(IBasicGetResult message)
        {
            base.Consumer_ReceiveMessage(message);
            if (!ContinueProcessing) return;

            var request = Message;

            Log.Information("Processing GetVouchersInformationResponse '{@request}', '{@correlationId}'", request, CorrelationId);

            try
            {
                var jobIdentifier = CorrelationId;
                var batchNumber = string.Empty;

                if (request.voucherInformation.Length == 0)
                {
                    Log.Information("No matching vouchers found GetVouchersInformationResponse '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);

                    //Mapping responseDone fields
                    var responseDoneOutput = new DipsResponseDone
                    {
                        guid_name = CorrelationId,
                        response_time = DateTime.Now,
                        number_of_results = 0
                    };

                    using(var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
                    using ( var dipsDbContext = new DipsDbContext(dbConnection))
                    {
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //Adding to DipsResponseDone table
                                dipsDbContext.DipsResponseDone.Add(responseDoneOutput);
                                dipsDbContext.SaveChanges();

                                tx.Commit();

                                Log.Information("Successfully processed GetVouchersInformationResponse '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a GetVouchersInformationResponse '{@GetVouchersInformationResponse}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    request, jobIdentifier);

                                tx.Rollback();
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a GetVouchersInformationResponse '{@GetVouchersInformationResponse}', '{@jobIdentifier}'",
                                    request, jobIdentifier);
                                tx.Rollback();
                            }
                        }
                    }
                }
                else
                {
                    //Mapping criteria fields
                    var firstVoucher = request.voucherInformation.First();
                    var criterias = MapToCriterias(firstVoucher);
                    var jsonPayload = JsonConvert.SerializeObject(criterias);

                    //Mapping responseDone fields
                    var responseDoneOutput = new DipsResponseDone
                    {
                        guid_name = CorrelationId,
                        response_time = DateTime.Now,
                        number_of_results = 1
                    };

                    //Mapping responsData fields
                    var responseDataOutput = new DipsResponseData
                    {
                        doc_ref_number = firstVoucher.voucher.documentReferenceNumber,
                        guid_name = CorrelationId,
                        payload = jsonPayload,
                        front_image = System.Text.Encoding.Default.GetString(firstVoucher.voucherImage[0].content),
                        rear_image = System.Text.Encoding.Default.GetString(firstVoucher.voucherImage[1].content)
                    };

                    using(var dbConnection = new SqlConnection(Configuration.SqlConnectionString))
                    using ( var dipsDbContext = new DipsDbContext(dbConnection))
                    {
                        using (var tx = dipsDbContext.BeginTransaction())
                        {
                            try
                            {
                                //Adding to DipsResponseData table
                                dipsDbContext.DipsResponseData.Add(responseDataOutput);
                                Log.Verbose("Adding new response data {@drn} to the database", request.voucherInformation.First().voucher.documentReferenceNumber);

                                dipsDbContext.SaveChanges();

                                //Adding to DipsResponseDone table
                                dipsDbContext.DipsResponseDone.Add(responseDoneOutput);
                                Log.Verbose("Adding new response data done row to the database");

                                dipsDbContext.SaveChanges();

                                tx.Commit();

                                Log.Information("Successfully processed GetVouchersInformationResponse '{@batchNumber}', '{@jobIdentifier}'", batchNumber, jobIdentifier);
                            }
                            catch (OptimisticConcurrencyException)
                            {
                                //this is to handle the race condition where more than instance of this service is running at the same time and tries to update the row.

                                //basically ignore the message by loggin a warning and rolling back.
                                //if this row was not included by mistake (e.g. it should be included), it will just come in in the next batch run.
                                Log.Warning(
                                    "Could not create a GetVouchersInformationResponse '{@GetVouchersInformationResponse}', '{@jobIdentifier}' because the DIPS database row was updated by another connection",
                                    request, jobIdentifier);

                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                            catch (Exception ex)
                            {
                                Log.Error(
                                    ex,
                                    "Could not complete and create a GetVouchersInformationResponse '{@GetVouchersInformationResponse}', '{@jobIdentifier}'",
                                    request, jobIdentifier);
                                tx.Rollback();
                                InvalidExchange.SendMessage(message.Body, RecoverableRoutingKey, CorrelationId);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing GetVouchersInformationResponse {@GetVouchersInformationResponse}", request);
                InvalidExchange.SendMessage(message.Body, InvalidRoutingKey, CorrelationId);
            }

        }

        private static List<Criteria> MapToCriterias(VoucherInformation voucher)
        {
            return new List<Criteria>
                    {
                        new Criteria 
                        {
                            name = "accountNumber",
                            value =  voucher.voucher.accountNumber
                        },
                    
                        new Criteria 
                        {
                            name = "amount",
                            value =  voucher.voucher.amount
                        },

                        new Criteria 
                        {
                            name = "auxDom",
                            value =  voucher.voucher.auxDom
                        },

                        new Criteria 
                        {
                            name = "bsbNumber",
                            value =  voucher.voucher.bsbNumber
                        },

                        new Criteria 
                        {
                            name = "documentReferenceNumber",
                            value =  voucher.voucher.documentReferenceNumber
                        },

                        new Criteria 
                        {
                            name = "documentType",
                            value =  voucher.voucher.documentType.ToString()
                        },

                        new Criteria 
                        {
                            name = "extraAuxDom",
                            value =  voucher.voucher.extraAuxDom
                        },

                        new Criteria 
                        {
                            name = "processingDate",
                            value =  voucher.voucher.processingDate.ToString(CultureInfo.InvariantCulture)
                        },

                        new Criteria 
                        {
                            name = "transactionCode",
                            value =  voucher.voucher.transactionCode
                        },

                        new Criteria 
                        {
                            name = "batchAccountNumber",
                            value =  voucher.voucherBatch.batchAccountNumber
                        },

                        new Criteria 
                        {
                            name = "batchType",
                            value =  voucher.voucherBatch.batchType
                        },

                        new Criteria 
                        {
                            name = "captureBsb",
                            value =  voucher.voucherBatch.captureBsb
                        },

                        new Criteria 
                        {
                            name = "client",
                            value =  voucher.voucherBatch.client
                        },

                        new Criteria 
                        {
                            name = "collectingBank",
                            value =  voucher.voucherBatch.collectingBank
                        },

                        new Criteria 
                        {
                            name = "processingState",
                            value =  voucher.voucherBatch.processingState.ToString()
                        },

                        new Criteria 
                        {
                            name = "scannedBatchNumber",
                            value =  voucher.voucherBatch.scannedBatchNumber
                        },

                        new Criteria 
                        {
                            name = "source",
                            value =  voucher.voucherBatch.source
                        },

                        new Criteria 
                        {
                            name = "subBatchType",
                            value =  voucher.voucherBatch.subBatchType
                        },

                        new Criteria 
                        {
                            name = "unitID",
                            value =  voucher.voucherBatch.unitID
                        },

                        new Criteria 
                        {
                            name = "workType",
                            value =  voucher.voucherBatch.workType.ToString()
                        },

                        new Criteria 
                        {
                            name = "adjustedBy",
                            value =  voucher.voucherProcess.adjustedBy
                        },

                        new Criteria 
                        {
                            name = "adjustedFlag",
                            value =  voucher.voucherProcess.adjustedFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "adjustmentDescription",
                            value =  voucher.voucherProcess.adjustmentDescription
                        },

                        new Criteria 
                        {
                            name = "adjustmentLetterRequired",
                            value =  voucher.voucherProcess.adjustmentLetterRequired.ToString()
                        },

                        new Criteria 
                        {
                            name = "adjustmentReasonCode",
                            value =  voucher.voucherProcess.adjustmentReasonCode.ToString()
                        },

                        new Criteria 
                        {
                            name = "adjustmentsOnHold",
                            value =  voucher.voucherProcess.adjustmentsOnHold.ToString()
                        },

                        new Criteria 
                        {
                            name = "apPresentmentType",
                            value =  voucher.voucherProcess.apPresentmentType.ToString()
                        },

                        new Criteria 
                        {
                            name = "customerLinkNumber",
                            value =  voucher.voucherProcess.customerLinkNumber
                        },

                        new Criteria 
                        {
                            name = "documentRetrievalFlag",
                            value =  voucher.voucherProcess.documentRetrievalFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "forValueType",
                            value =  voucher.voucherProcess.forValueType.ToString()
                        },

                        new Criteria 
                        {
                            name = "highValueFlag",
                            value =  voucher.voucherProcess.highValueFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "inactiveFlag",
                            value =  voucher.voucherProcess.inactiveFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "isGeneratedBulkCredit",
                            value =  voucher.voucherProcess.isGeneratedBulkCredit.ToString()
                        },

                        new Criteria 
                        {
                            name = "isGeneratedVoucher",
                            value =  voucher.voucherProcess.isGeneratedVoucher.ToString()
                        },

                        new Criteria 
                        {
                            name = "isReservedForBalancing",
                            value =  voucher.voucherProcess.isReservedForBalancing.ToString()
                        },

                        new Criteria 
                        {
                            name = "isRetrievedVoucher",
                            value =  voucher.voucherProcess.isRetrievedVoucher.ToString()
                        },

                        new Criteria 
                        {
                            name = "listingPageNumber",
                            value =  voucher.voucherProcess.listingPageNumber
                        },

                        new Criteria 
                        {
                            name = "manualRepair",
                            value =  voucher.voucherProcess.manualRepair.ToString()
                        },

                        new Criteria 
                        {
                            name = "micrFlag",
                            value =  voucher.voucherProcess.micrFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "operatorId",
                            value =  voucher.voucherProcess.operatorId
                        },

                        new Criteria 
                        {
                            name = "postTransmissionQaAmountFlag",
                            value =  voucher.voucherProcess.postTransmissionQaAmountFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "postTransmissionQaCodelineFlag",
                            value =  voucher.voucherProcess.postTransmissionQaCodelineFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "preAdjustmentAmount",
                            value =  voucher.voucherProcess.preAdjustmentAmount
                        },

                        new Criteria 
                        {
                            name = "presentationMode",
                            value =  voucher.voucherProcess.presentationMode
                        },

                        new Criteria 
                        {
                            name = "rawMICR",
                            value =  voucher.voucherProcess.rawMICR
                        },

                        new Criteria 
                        {
                            name = "rawOCR",
                            value =  voucher.voucherProcess.rawOCR
                        },

                        new Criteria 
                        {
                            name = "releaseFlag",
                            value =  voucher.voucherProcess.releaseFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "repostFromDRN",
                            value =  voucher.voucherProcess.repostFromDRN
                        },

                        new Criteria 
                        {
                            name = "repostFromProcessingDate",
                            value =  voucher.voucherProcess.repostFromProcessingDate.ToString("dd-MM-yyyy")
                        },

                        new Criteria 
                        {
                            name = "surplusItemFlag",
                            value =  voucher.voucherProcess.surplusItemFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "suspectFraud",
                            value =  voucher.voucherProcess.suspectFraud.ToString()
                        },

                        new Criteria 
                        {
                            name = "thirdPartyCheckFailed",
                            value =  voucher.voucherProcess.thirdPartyCheckFailed.ToString()
                        },

                        new Criteria 
                        {
                            name = "thirdPartyMixedDepositReturnFlag",
                            value =  voucher.voucherProcess.thirdPartyMixedDepositReturnFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "thirdPartyPoolFlag",
                            value =  voucher.voucherProcess.thirdPartyPoolFlag.ToString()
                        },
                    
                        new Criteria 
                        {
                            name = "transactionLinkNumber",
                            value =  voucher.voucherProcess.transactionLinkNumber
                        },

                        new Criteria 
                        {
                            name = "unencodedECDReturnFlag",
                            value =  voucher.voucherProcess.unencodedECDReturnFlag.ToString()
                        },

                        new Criteria 
                        {
                            name = "unprocessable",
                            value =  voucher.voucherProcess.unprocessable.ToString()
                        },

                        new Criteria 
                        {
                            name = "voucherDelayedIndicator",
                            value =  voucher.voucherProcess.voucherDelayedIndicator
                        },

                    };
        }
    }
}