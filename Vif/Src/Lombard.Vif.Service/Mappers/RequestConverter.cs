using System;
using System.Collections.Generic;
using System.Linq;
using Lombard.Common.FileProcessors;
using Lombard.Vif.Service.Domain;
using Lombard.Vif.Service.Extensions;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;
using Serilog;

namespace Lombard.Vif.Service.Mappers
{
    public interface IRequestConverter : IMapper<VifFileInfo, ValidatedResponse<IVifGenerator>>
    { }

    public class RequestConverter : IRequestConverter
    {
        private RequestConverterHelper requestConverterHelper;

        public RequestConverter(RequestConverterHelper helper)
        {
            this.requestConverterHelper = helper;
        }

        public ValidatedResponse<IVifGenerator> Map(VifFileInfo vifFileInfo)
        {
            var vifHeader = GetVifHeader(vifFileInfo);
            var vifTrailer = GetVifTrailer(vifFileInfo);
            var vifDetails = new List<VifDetail>();

            if (!vifFileInfo.VoucherInfos.Any())
            {
                Log.Warning("Request does not contain any ImageExchangeVoucherDetails, continue without VIFDetails.");
            }
            else
            {
                try
                {
                    IEnumerable<IGrouping<dynamic, VoucherInformation>> groupedVouchers;

                    switch (vifFileInfo.QueryLinkType)
                    {
                        case "CUSTOMER_LINK_NUMBER":
                            {
                                groupedVouchers = this.GetGroupedVouchersByCustomerLink(vifFileInfo);

                                break;
                            }
                        case "TRANSACTION_LINK_NUMBER":
                        case "NONE":
                            {
                                groupedVouchers = this.GetGroupedVouchersByTransactionLink(vifFileInfo);

                                break;
                            }
                        default:
                            {
                                throw new Exception(string.Format("Unexpected query link type '{0}'", vifFileInfo.QueryLinkType));
                            }
                    }

                    vifDetails = this.GetVifDetails(groupedVouchers, vifFileInfo.RecordTypeCode, vifFileInfo.SequenceNumber).ToList();

                    if (vifDetails.Count == 0)
                    {
                        throw new Exception("Unexpected empty vifDetails list.");
                    }
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "There was an error generating the vif details record.");
                    return ValidatedResponseHelper.Failure<IVifGenerator>("There was an error generating the vif details record.");
                }
            }

            var vifGenerator = new VifGenerator(vifHeader, vifDetails, vifTrailer);
            return ValidatedResponse<IVifGenerator>.Success(vifGenerator);
        }

        private VifHeader GetVifHeader(VifFileInfo vifFileInfo)
        {
            return new VifHeader
            {
                RECORD_TYPE_CODE = "A", // Constant Value

                STATE_NUMBER = vifFileInfo.State,

                RUN_NUMBER = vifFileInfo.SequenceNumber.Substring(0, 4), // TODO - Daily Run Number for this Site Id/Machine Id // NO INFO

                BANK_CODE = vifFileInfo.BankCode.ToString(),

                PROCESS_DATE = vifFileInfo.ProcessDate.ToString("yyyyMMdd"),

                CAPTURE_BSB = vifFileInfo.CaptureBSB,

                COLLECTING_BSB = vifFileInfo.CollectingBSB,

                BUNDLE_TYPE = vifFileInfo.BundleType.ToString(), // TODO

                EMPTY_SPACE_FILLER = string.Empty
            };
        }

        private IEnumerable<VifDetail> GetVifDetails(IEnumerable<IGrouping<dynamic, VoucherInformation>> groupedVouchers, RecordTypeCode[] recordType, string batchNumber)
        {
            var details = new List<VifDetail>();
            int sequenceNumber = 1;

            foreach (IGrouping<dynamic, VoucherInformation> grp in groupedVouchers)
            {
                var primeCredit = this.requestConverterHelper.GetPrimeCredit(grp.ToList());
                if (primeCredit == null)
                {
                    Log.Error("Prime credit is null for the group transaction. {@group}", grp.ToList());
                    throw new Exception(string.Format("Prime credit is null for the group transaction with batch number {0}.", grp.First().voucherBatch.scannedBatchNumber));
                }
                else
                {
                    var creditTransactionCount = (from voucher in grp
                                                  where voucher.voucher.documentType == DocumentTypeEnum.Cr
                                                  select voucher).Count();

                    var debitTransactionCount = (from voucher in grp
                                                 where voucher.voucher.documentType == DocumentTypeEnum.Dr
                                                 select voucher).Count();

                    string multipleCreditFlag = (creditTransactionCount > 1) ? "Y" : string.Empty;

                    string extraAuxDom1Shared = primeCredit.GetExtraAuxDom();
                    string auxDom1Shared = primeCredit.GetAuxDom();

                    if (string.IsNullOrEmpty(auxDom1Shared))
                    {
                        auxDom1Shared = grp.Count((vi) =>
                        {
                            if (vi.voucher.documentType == DocumentTypeEnum.Dr)
                            {
                                string transactionCode = vi.GetTransactionCode();

                                if (transactionCode == "9" || transactionCode == "09" || string.IsNullOrEmpty(transactionCode))
                                {
                                    return true;
                                }
                            }

                            return false;
                        }).ToString();
                    }

                    foreach (VoucherInformation vifRequest in grp)
                    {
                        string auxDom2Self = vifRequest.GetAuxDom();
                        if (primeCredit.Equals(vifRequest) && string.IsNullOrEmpty(auxDom2Self))
                        {
                            auxDom2Self = auxDom1Shared;
                        }

                        string extraAuxDom2Self = vifRequest.GetExtraAuxDom();
                        string creditDepositAccountBsb;
                        string creditDepositAccountNumber;
                        string drawerAccountNumber = vifRequest.voucher.accountNumber;
                        string debitAccountBsb = vifRequest.voucher.bsbNumber;


                        if (vifRequest.voucher.documentType == DocumentTypeEnum.Dr)
                        {
                            creditDepositAccountBsb = primeCredit.voucher.bsbNumber;
                            creditDepositAccountNumber = primeCredit.voucher.accountNumber;
                        }
                        else
                        {
                            creditDepositAccountBsb = string.IsNullOrEmpty(primeCredit.voucher.bsbNumber) ? vifRequest.voucher.bsbNumber : primeCredit.voucher.bsbNumber;
                            creditDepositAccountNumber = string.IsNullOrEmpty(primeCredit.voucher.accountNumber) ? vifRequest.voucher.accountNumber : primeCredit.voucher.accountNumber;
                        }

                        if (vifRequest.voucherProcess.highValueFlag)
                        {
                            if (!string.IsNullOrEmpty(vifRequest.voucherProcess.alternateAccountNumber))
                            {
                                drawerAccountNumber = vifRequest.voucherProcess.alternateAccountNumber;
                            }

                            if (!string.IsNullOrEmpty(vifRequest.voucherProcess.alternateBsbNumber))
                            {
                                debitAccountBsb = vifRequest.voucherProcess.alternateBsbNumber;
                            }
                        }

                        details.Add(new VifDetail
                        {
                            //Possible Value:
                            //C = cash slips
                            //D = detail records
                            //M = detail merchant transactions
                            //P = credit card deposits
                            //S = summary total merchant transactions
                            //V = other bank value transactions
                            RECORD_TYPE_CODE = GetRecordTypeCode(recordType, vifRequest.voucher.accountNumber, vifRequest.voucher.bsbNumber, vifRequest.voucher.transactionCode), // TODO

                            // Identifies the BSB encoded on the cheque or payment or the card BIN if a credit card payment. 
                            LEDGER_BSB = debitAccountBsb,

                            // Identifies the credit bsb from the last credit in the deposit or the BIN if a credit card payment.
                            DEPOSIT_ACCOUNT_BSB = creditDepositAccountBsb, // TODO: used to be vifRequest.depositorNominatedBsb,

                            // Identifies the BSB on the batch header. 
                            NEGOTIATING_BSB = vifRequest.voucherBatch.collectingBank,

                            // A non-zero batch number within a runfile that starts at one for the first batch in the runfile.
                            // Mapped to SequenceNumber
                            BATCH_NUMBER = batchNumber,

                            // (Transaction ID) - Represents a non-zero sequence number for each individual transaction in the runfile. It starts at one for the first transaction in the batch. 
                            TRANSACTION_ID = sequenceNumber.ToString(), // TODO

                            // Industry agreed transaction codes.
                            //0-49 Debit
                            //50-99 Credit
                            //111 Cash Slip
                            //222 non-Cash Slip
                            TRANSACTION_CODE = vifRequest.GetTransactionCode(),

                            // Trace ID for transaction
                            //DIN number where machine number component is > 39.
                            //DIN is made up of:
                            //Pos. 1-2 = Machine NoPos. 3 = Adjustment indicator
                            //(0-no adjustment, 2-adjustment)
                            //Pos 4-9 = Trace Number
                            UNIQUE_TRACE_ID = vifRequest.voucher.documentReferenceNumber, // TODO

                            TRANSACTION_AMOUNT = vifRequest.voucher.amount,

                            // Debit/Credit Flag - C,D
                            DEBIT_CREDIT_CODE = vifRequest.voucher.documentType.ToString().Substring(0, 1), // TODO

                            // Y or empty
                            MULTIPLE_CREDIT_FLAG = multipleCreditFlag, // TODO

                            // Account number on the item or the card number minus the 6-digit BIN if a card transaction. RJSF
                            DRAWER_ACCOUNT_NUMBER = drawerAccountNumber, // TODO

                            // Account number of the depositing account or last credit for a multiple credit deposit.
                            DEPOSIT_ACCOUNT_NUMBER = creditDepositAccountNumber, // TODO: used to be vifRequest.depositorNominatedAccountNumber,

                            // Serial number of the deposit or of  the last deposit for a multiple credit deposit.
                            AUX_DOM_1_SHARED = auxDom1Shared,

                            // EAD from the deposit or the last deposit for a multiple credit deposit if applicable, else zero filled
                            EX_AUX_DOM_1_SHARED = extraAuxDom1Shared,

                            // Serial number of the cheque
                            AUX_DOM_2_SELF = auxDom2Self,

                            // EAD from the cheque if applicable, else zero filled
                            EX_AUX_DOM_2_SELF = extraAuxDom2Self,

                            // Total number of debits in the deposit
                            DEPOSIT_CHEQUE_ITEM_COUNT = debitTransactionCount.ToString(),

                            //Delayed Voucher Indicator
                            DELAY_VOUCHER_INDICATOR = string.Empty, // TODO - vifRequest.voucherDelayedIndicator : Y = yes, empty = no

                            // TODO - vifRequest.manualRepair : 1 if repair, otherwise 0
                            MANUAL_REPAIR_FLAG = vifRequest.voucherProcess.manualRepair.ToString(),

                            // E for "Electronic", "M" for manual -- would it be "M" if manual repair? //advised by Julia to hard-code this value in this sprint. (22.May.2015)
                            PRESENTATION_MODE = vifRequest.voucherProcess.presentationMode,

                            // "Pocket Cut" - Last two digits of the output pocket allocated in prime pass.
                            POCKET_CUT = string.Empty, // TODO

                            // Rightmost 4 digits of the AD field on the batch header
                            BATCH_HEADER_REFERENCE = vifRequest.voucherBatch.captureBsb.Substring(2, 4), // TODO

                            // Channel Id - Refence to the Channel ID - See "04. Rules" tab for details
                            CHANNEL_ID = vifRequest.voucherBatch.batchAccountNumber.Substring(vifRequest.voucherBatch.batchAccountNumber.Length - 2, 2), //vifRequest.voucherBatch.batchAccountNumber

                            EMPTY_SPACE_FILLER = string.Empty
                        });
                    }
                }

                //increase the sequence number for the next group.
                sequenceNumber++;
            }

            return details;
        }

        private string GetRecordTypeCode(RecordTypeCode[] recordType, string drawerAccountNumber, string ledgerBSB, string transCode)
        {
            var recordTypeCodeP = (recordType.Select(item => item.bsb == ledgerBSB)).Where(result => result == true).Count();
            var recordTypeCodeC = (recordType.Select(item => item.transactionCode == transCode)).Where(result => result == true).Count();

            if (recordTypeCodeP != 0)
                return "P";
            else if (recordTypeCodeC != 0)
                return "C";
            else
                return "D";
        }

        private IEnumerable<IGrouping<dynamic, VoucherInformation>> GetGroupedVouchersByCustomerLink(VifFileInfo vifFileInfo)
        {
            return vifFileInfo.VoucherInfos.Where(x => Convert.ToInt64(x.voucherProcess.customerLinkNumber) != 0)
                                                                   .OrderByDescending(x => x.voucher.documentType.ToString())
                                                                   .GroupBy(x => new { x.voucherProcess.customerLinkNumber });
        }

        private IEnumerable<IGrouping<dynamic, VoucherInformation>> GetGroupedVouchersByTransactionLink(VifFileInfo vifFileInfo)
        {
            return vifFileInfo.VoucherInfos.Where(x => (Convert.ToInt64(x.voucherProcess.transactionLinkNumber) != 0 && !string.IsNullOrEmpty(x.voucherBatch.scannedBatchNumber)))
                                                        .OrderByDescending(x => x.voucher.documentType.ToString())
                                                        .GroupBy(x => new { x.voucherProcess.transactionLinkNumber, x.voucherBatch.scannedBatchNumber });
        }

        private VifTrailer GetVifTrailer(VifFileInfo vifFileInfo)
        {
            var amount = ((int)vifFileInfo.TotalDebitAmount).ToString();
            var decim = ((int)((vifFileInfo.TotalDebitAmount % 1) * 100)).ToString();
            var str = string.Concat(((int)vifFileInfo.TotalDebitAmount).ToString(), ((int)((vifFileInfo.TotalDebitAmount % 1) * 100)).ToString());
            return new VifTrailer
            {
                RECORD_TYPE_CODE = "Z", // Constant

                // Date of processing (proof processing date, not system date) - yyyymmdd
                PROCESS_DATE = vifFileInfo.ProcessDate.ToString("yyyyMMdd"), // TODO - item level has vifRequest.voucher.processingDate

                // Total value of all credits in this runfile
                // Max Value: 9999999999999.99
                // No Filler spaces or zeros at the front.
                TOTAL_CREDIT_AMOUNT = vifFileInfo.TotalCreditAmount.ToString(), // TODO - total amount

                // Total value of all debits in this runfile including cash slips
                // Max Value: 9999999999999.99
                // No Filler spaces or zeros at the front.
                TOTAL_DEBIT_AMOUNT = vifFileInfo.TotalDebitAmount.ToString(), // TODO - total amount

                // Count of all non generated debit items in the runfile excluding file header and trailer records
                // No Filler spaces or leading zeros.
                TOTAL_NUMBER_OF_DEBITS = vifFileInfo.TotalDebitCount.ToString(), // TODO - total #

                // Count of all non generated credit items in the runfile excluding file header and trailer records
                // No Filler spaces or leading zeros.
                TOTAL_NUMBER_OF_CREDITS = vifFileInfo.TotalCreditCount.ToString(), // TODO - total #

                FILLER = string.Empty
            };
        }
    }
}

