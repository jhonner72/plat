using System;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Common.DateAndTime;
using Lombard.Adapters.DipsAdapter.Messages;
// ReSharper disable FunctionComplexityOverflow

namespace Lombard.Adapters.DipsAdapter.Helpers
{
    public class GenerateCorrespondingVoucherRequestMapHelper : IGenerateCorrespondingVoucherRequestMapHelper
    {
        private const string SLength = "01025";

        private readonly IDateTimeProvider dateTimeProvider;
        private readonly IAdapterConfiguration adapterConfiguration;

        public GenerateCorrespondingVoucherRequestMapHelper(
            IDateTimeProvider dateTimeProvider,
            IAdapterConfiguration adapterConfiguration)
        {
            this.dateTimeProvider = dateTimeProvider;
            this.adapterConfiguration = adapterConfiguration;
        }

        public DipsQueue CreateNewDipsQueue(
            DipsLocationType locationType,
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string jobIdentifier,
            string jobId)
        {
            var imagePath = string.Format(@"{0}\{1}", adapterConfiguration.ImagePath, batchNumber.Substring(0, 5));

            if (string.IsNullOrEmpty(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            if (WorkTypeEnum.NABCHQ_POD.ToString().Equals(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            // temporary fix to enable for value testing - uncomment if required
            //if (WorkTypeEnum.BQL_POD.ToString().Equals(jobId))
            //{
            //    jobId = WorkTypeEnum.NABCHQ_INWARDFV.ToString();
            //}

            var output = new DipsQueue
            {
                //Dynamic DipsQueue Values
                S_BATCH = batchNumber,
                S_TRACE = documentReferenceNumber.PadLeft(9, '0'),
                S_SDATE = processingDate.ToString("dd/MM/yy"),
                S_STIME = processingDate.ToString("HH:mm:ss"),
                S_SELNSTRING = GenerateSelectionString(processingDate, batchNumber),

                //Default DipsQueue Values
                S_LOCATION = locationType.Value.PadRight(33, ' '),
                S_PINDEX = GeneratePriorityIndex(),
                S_LOCK = "0".PadLeft(10, ' '),
                S_CLIENT = DipsClientType.NabChq.Value.PadRight(80, ' '),
                //S_JOB_ID = DipsJobIdType.NabChqPod.Value.PadRight(128, ' '),
                S_JOB_ID = jobId.PadRight(128, ' '),
                S_MODIFIED = "0".PadLeft(5, ' '),
                S_COMPLETE = "0".PadLeft(5, ' '),
                S_PRIORITY = adapterConfiguration.DipsPriority.PadLeft(5, ' '),
                S_IMG_PATH = imagePath.PadRight(80, ' '),
                S_VERSION = adapterConfiguration.Dbioff32Version.PadRight(32, ' '),

                //Ignored fields
                S_UTIME = string.Empty.PadLeft(10, ' '),
                S_LOCKUSER = string.Empty.PadLeft(17, ' '),
                S_LOCKMODULENAME = string.Empty.PadLeft(17, ' '),
                S_LOCKUNITID = string.Empty.PadLeft(10, ' '),
                S_LOCKMACHINENAME = string.Empty.PadLeft(17, ' '),
                S_LOCKTIME = string.Empty.PadLeft(10, ' '),
                S_PROCDATE = string.Empty.PadLeft(9, ' '),
                S_REPORTED = string.Empty.PadLeft(5, ' '),
            };
            return output;
        }

        public DipsDbIndex CreateNewDipsDbIndex(
            string batchNumber,
            string documentReferenceNumber)
        {
            var output = new DipsDbIndex
            {
                //Dynamic DipsDbIndex Values
                BATCH = batchNumber,
                TRACE = documentReferenceNumber.PadLeft(9, '0'),

                //Default DipsDbIndex Values
                DEL_IND = ZeroString(5),
                SEQUENCE = "0000 ",
                TABLE_NO = ZeroString(5),
                REC_NO = "0".PadRight(10)
            };
            return output;
        }

        public DipsNabChq CreateNewDipsNabChq(
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string extraAuxDom,
            bool extraAuxDomStatus,
            string auxDom,
            bool auxDomStatus,
            string bsbNumber,
            bool bsbNumberStatus,
            string accountNumber,
            bool accountNumberStatus,
            string transactionCode,
            bool transactionCodeStatus,
            string capturedAmount,
            string amountConfidenceLevel,
            string documentType,
            string jobId,
            int manualRepair,
            string amount,
            bool amountStatus,
            string captureBsb,
            string batchAccountNumber,
            string processingState,
            string collectingBank,
            string unitId,
            string preAdjustmentAmount,
            bool adjustedFlag,
            bool thirdPartyCheckFailed,
            bool thirdPartyPoolFlag,
            bool highValueFlag,
            string voucherDelayedIndicator,
            string batchType,
            bool unprocessable,
            bool unencodedEcdReturnFlag,
            string transactionLinkNumber,
            bool thirdPartyMixedDepositReturnFlag,
            bool suspectFraud,
            bool surplusItemFlag,
            string rawMicr,
            string rawOcr,
            string presentationMode,
            bool isGeneratedVoucher,
            bool adjustmentLetterRequired,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string adjustmentReasonCode,
            string adjustmentDescription,
            string subBatchType,
            string alt_acc_num,
            string alt_bsb_num,
            string alt_ead,
            string alt_ser_num,
            string alt_trancode,
            bool creditNoteFlag,
            string insertedCreditType
            )
        {
            if (string.IsNullOrEmpty(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            if (WorkTypeEnum.NABCHQ_POD.ToString().Equals(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }
            // calculate a value for the S_STATUS1 field, which represents a
            // bitmask of invalid fields in Dips
            int statusMask = DipsStatus1Bitmask.GetStatusMask(extraAuxDomStatus, auxDomStatus, accountNumberStatus,
                amountStatus, bsbNumberStatus, transactionCodeStatus);

            var output = new DipsNabChq
            {
                //Dynamic DipsNabChq Values
                S_BATCH = batchNumber,
                S_TRACE = documentReferenceNumber.PadLeft(9, '0'),
                acc_num = accountNumber,
                adjustedFlag = RequestHelper.ConvertBoolToIntString(adjustedFlag),
                amount = string.IsNullOrEmpty(capturedAmount) ? amount : capturedAmount,
                amountConfidenceLevel = RequestHelper.ResolveAmountConfidenceLevel(amountConfidenceLevel),
                batch = batchNumber,
                batchAccountNumber = batchAccountNumber,
                batch_type = batchType,
                bsb_num = bsbNumber,
                captureBSB = captureBsb,
                collecting_bank = collectingBank,
                doc_ref_num = documentReferenceNumber,
                doc_type = RequestHelper.DocumentumToDipsDocumentTypeConversion(documentType),
                ead = RequestHelper.ConvertNullValueToEmptyString(extraAuxDom),
                fxa_tpc_suspense_pool_flag = RequestHelper.ConvertBoolToIntString(thirdPartyPoolFlag),
                fxa_unencoded_ECD_return = RequestHelper.ConvertBoolToIntString(unencodedEcdReturnFlag),
                highValueFlag = RequestHelper.ConvertBoolToIntString(highValueFlag),
                isGeneratedVoucher = RequestHelper.ConvertBoolToIntString(isGeneratedVoucher),
                job_id = jobId.PadRight(15, ' '),
                man_rep_ind = RequestHelper.ConvertintvalueToString(manualRepair),
                micr_suspect_fraud_flag = RequestHelper.ConvertBoolToIntString(suspectFraud),
                orig_amount = preAdjustmentAmount,
                presentationMode = presentationMode,
                proc_date = processingDate.ToString("yyyyMMdd"),
                processing_state = processingState,
                raw_micr = rawMicr,
                raw_ocr = rawOcr,
                ser_num = RequestHelper.ConvertNullValueToEmptyString(auxDom),
                surplusItemFlag = RequestHelper.ConvertBoolToIntString(surplusItemFlag),
                tpcMixedDepRet = RequestHelper.ConvertBoolToIntString(thirdPartyMixedDepositReturnFlag),
                trace = documentReferenceNumber.PadLeft(9, '0'),
                trancode = RequestHelper.ConvertNullValueToEmptyString(transactionCode),
                transactionLinkNumber = transactionLinkNumber,
                unit_id = unitId,
                unproc_flag = RequestHelper.ConvertBoolToIntString(unprocessable),
                voucherIndicatorField = voucherDelayedIndicator,

                adjustmentLetterRequired = RequestHelper.ConvertBoolToIntString(adjustmentLetterRequired),

                fxaPtQAAmtFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaAmountFlag),
                fxaPtQACodelineFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaCodelineFlag),

                adjustmentReasonCode = adjustmentReasonCode,
                adjustmentDescription = adjustmentDescription,

                sub_batch_type = subBatchType,
                alt_acc_num =  alt_acc_num,
                alt_bsb_num = alt_bsb_num,
                alt_ead = alt_ead,
                alt_ser_num = alt_ser_num,
                alt_trancode = alt_trancode,
                // TODO alt fields, custome link number, max debit vouchers, isGeneratedBulkCredit=false
                creditNoteFlag = RequestHelper.ConvertBoolToIntString(creditNoteFlag),
                insertedCreditType = RequestHelper.InsertedCreditTypeToDipsConversion(insertedCreditType),

                //Default DipsNabChq Values
                //mandatory fields
                tpcRequired = RequestHelper.ConvertBoolToYNString(false),
                S_DEL_IND = ZeroString(5),
                S_MODIFIED = ZeroString(5),
                S_COMPLETE = ZeroString(5),
                S_TYPE = ZeroString(5),
                S_STATUS1 = statusMask.ToString().PadLeft(9, ' '),
                S_STATUS2 = ZeroString(9),
                S_STATUS3 = ZeroString(9),
                S_STATUS4 = ZeroString(9),
                S_IMG1_OFF = ZeroString(9),
                S_IMG1_LEN = ZeroString(9),
                S_IMG1_TYP = ZeroString(5),
                S_IMG2_OFF = ZeroString(9),
                S_IMG2_LEN = ZeroString(9),
                S_IMG2_TYP = ZeroString(5),
                S_LENGTH = SLength,
                S_SEQUENCE = "0000 ",
                S_BALANCE = ZeroString(6),
                S_REPROCESS = ZeroString(5),
                S_REPORTED = ZeroString(5),
                S_COMMITTED = ZeroString(5),
                //custom fields
                sys_date = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone().ToString("yyyyMMdd"),
                //ignored fields
                pocket = ZeroString(2),
                payee_name = IgnoreString(240),
                manual_repair = IgnoreString(5),
                rec_type_id = IgnoreString(4),
                proof_seq = IgnoreString(12),
                trans_seq = IgnoreString(5),
                delay_ind = IgnoreString(1),
                fv_exchange = IgnoreString(1),
                adj_code = IgnoreString(2),
                adj_desc = IgnoreString(30),
                op_id = IgnoreString(15),
                proc_time = IgnoreString(4),
                @override = IgnoreString(5),
                fv_ind = IgnoreString(1),
                host_trans_no = IgnoreString(3),
                volume = IgnoreString(8),
                img_location = IgnoreString(80),
                img_front = IgnoreString(8),
                img_rear = IgnoreString(8),
                held_ind = IgnoreString(1),
                receiving_bank = IgnoreString(3),
                ie_transaction_id = IgnoreString(12),
                
                micr_flag = IgnoreString(1),
                micr_unproc_flag = IgnoreString(1),

                adjustedBy = IgnoreString(15),
                //adjustedFlag = IgnoreString(1),

                adjustmentType = IgnoreString(9)
            };
            return output;
        }

        private string ZeroString(int length)
        {
            return "0".PadLeft(length, ' ');
        }

        private string IgnoreString(int length)
        {
            return string.Empty.PadLeft(length, ' ');
        }

        private string GeneratePriorityIndex()
        {
            return string.Format(
                "{0}-{1}",
                adapterConfiguration.DipsPriority.PadLeft(2, '0'),
                dateTimeProvider.CurrentTimeInAustralianEasternTimeZone().ToString("yyMMddHHmmssf"));
        }

        private string GenerateSelectionString(DateTime processingDate, string batchNumber)
        {
            return string
                .Format(
                    "{0} {1} {2}",
                    processingDate.ToString("dd/MM/yy"),
                    batchNumber,
                    DipsJobIdType.NabChqPod.Value)
                .PadRight(128, ' ');
        }
    }
}