using System;
using System.Globalization;
using FujiXerox.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.DateAndTime;

// ReSharper disable FunctionComplexityOverflow

namespace FujiXerox.Adapters.DipsAdapter.Helpers
{
    public interface IBatchTransactionRequestMapHelper
    {
        DipsQueue CreateNewDipsQueue(
            DipsLocationType locationType,
            string batchNumber,
            string traceId,
            DateTime processingDate,
            string jobId);

        DipsDbIndex CreateNewDipsDbIndex(
            string batchNumber, 
            string traceId);

        DipsNabChq CreateNewDipsNabChqForCorrectTransactionRequest(
            string batchNumber,
            string traceId,
            DateTime processingDate,
            string reasonCode,
            string transactionLinkNumber,
            bool unprocessable,
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
            bool amountStatus,
            string documentType,
            string jobId,
            string presentationMode,
            string rawMicr,
            string rawOcr,
            string captureBsb,
            string batchAccountNumber,
            bool surplusItemFlag,
            bool suspectFraudFlag,
            string processingState,
            string collectingBank,
            string unitId,
            bool isGeneratedVoucher,
            string targetEndPoint,
            string forValueIndicator,
            string dipsOverride,
            bool thirdPartyCheckRequired,
            bool unencodedEcdReturnFlag,
            bool thirdPartyMixedDepositReturnFlag,
            string batchType,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string subBatchType);

        DipsNabChq CreateNewDipsNabChqForValidateTransactionRequest(
            string batchNumber,
            string traceId,
            DateTime processingDate,
            string reasonCode,
            string transactionLinkNumber,
            bool unprocessable,
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
            bool amountStatus,
            string documentType,
            string jobId,
            string rawMicr,
            string rawOcr,
            string captureBsb,
            string batchAccountNumber,
            string processingState,
            string collectingBank,
            string unitId,
            string forValueIndicator,
            string dipsOverride,
            string batchType,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string subBatchType);
    }

    public class BatchTransactionRequestMapHelper : IBatchTransactionRequestMapHelper
    {
        private const string SLength = "01025";

        private readonly IDateTimeProvider dateTimeProvider;
        private readonly IAdapterConfiguration adapterConfiguration;

        public BatchTransactionRequestMapHelper(
            IDateTimeProvider dateTimeProvider,
            IAdapterConfiguration adapterConfiguration)
        {
            this.dateTimeProvider = dateTimeProvider;
            this.adapterConfiguration = adapterConfiguration;
        }

        public DipsQueue CreateNewDipsQueue(
            DipsLocationType locationType, 
            string batchNumber, 
            string traceId, 
            DateTime processingDate, 
            string jobId)
        {
            var imagePath = string.Format(@"{0}\{1}", adapterConfiguration.ImagePath, batchNumber.Substring(0, 5));
            //traceId = traceId.Substring(traceId.Length - 9);
            //traceId = ResolveTraceNumber(traceId);
            traceId = RequestHelper.ResolveDocumentReferenceNumber(traceId);

            if (string.IsNullOrEmpty(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            if (WorkTypeEnum.NABCHQ_POD.ToString().Equals(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            var output = new DipsQueue
            {
                //Dynamic DipsQueue Values
                S_BATCH = batchNumber.PadLeft(8, '0'),
                S_TRACE = traceId.PadLeft(9, '0'),
                S_SDATE = processingDate.ToString("dd/MM/yy"),
                S_STIME = processingDate.ToString("HH:mm:ss"),
                S_SELNSTRING = GenerateSelectionString(processingDate, batchNumber),
                S_JOB_ID = jobId.PadRight(128, ' '),

                //Default DipsQueue Values
                S_LOCATION = locationType.Value.PadRight(33, ' '),
                S_PINDEX = GeneratePriorityIndex(),
                S_LOCK = "0".PadLeft(10, ' '),
                S_CLIENT = DipsClientType.NabChq.Value.PadRight(80, ' '),
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
            string traceId)
        {

            //traceId = traceId.Substring(traceId.Length - 9);
            //traceId = ResolveTraceNumber(traceId);
            traceId = RequestHelper.ResolveDocumentReferenceNumber(traceId);

            var output = new DipsDbIndex
            {
                //Dynamic DipsDbIndex Values
                BATCH = batchNumber.PadLeft(8, '0'),
                TRACE = traceId.PadLeft(9, '0'),

                //Default DipsDbIndex Values
                DEL_IND = ZeroString(5),
                SEQUENCE = "0000 ",
                TABLE_NO = ZeroString(5),
                REC_NO = "0".PadRight(10)
            };
            return output;
        }

        public DipsNabChq CreateNewDipsNabChqForCorrectTransactionRequest(
            string batchNumber,
            string traceId,
            DateTime processingDate,
            string reasonCode,
            string transactionLinkNumber,
            bool unprocessable,
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
            bool amountStatus,
            string documentType,
            string jobId,
            string presentationMode,
            string rawMicr,
            string rawOcr,
            string captureBsb,
            string batchAccountNumber,
            bool surplusItemFlag,
            bool suspectFraudFlag,
            string processingState,
            string collectingBank,
            string unitId,
            bool isGeneratedVoucher,
            string targetEndPoint,
            string forValueIndicator,
            string dipsOverride,
            bool tpcRequired,
            bool unencodedEcdReturnFlag,
            bool thirdPartyMixedDepositReturnFlag,
            string batchType,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string subBatchType)
        {
            // calculate a value for the S_STATUS1 field, which represents a
            // bitmask of invalid fields in Dips
            int statusMask = DipsStatus1Bitmask.GetStatusMask(extraAuxDomStatus, auxDomStatus, accountNumberStatus,
                amountStatus, bsbNumberStatus, transactionCodeStatus);

            traceId = RequestHelper.ResolveDocumentReferenceNumber(traceId);

            if (string.IsNullOrEmpty(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            if (WorkTypeEnum.NABCHQ_POD.ToString().Equals(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            var output = new DipsNabChq
            {
                //Dynamic DipsNabChq Values
                @override = dipsOverride,
                S_BATCH = batchNumber.PadRight(8, '0'),
                S_TRACE = traceId.PadLeft(9, '0'),
                acc_num = accountNumber,
                amount = capturedAmount ?? string.Empty,
                balanceReason = reasonCode,
                batch = batchNumber.PadRight(8, '0'),
                batchAccountNumber = batchAccountNumber,
                batch_type = batchType,
                bsb_num = bsbNumber,
                captureBSB = captureBsb,
                collecting_bank = collectingBank,
                doc_ref_num = traceId,
                doc_type = documentType,
                ead = RequestHelper.ConvertNullValueToEmptyString(extraAuxDom),
                fv_ind = RequestHelper.ConvertNullValueToEmptyString(forValueIndicator),
                fxaPtQAAmtFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaAmountFlag),
                fxaPtQACodelineFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaCodelineFlag),
                fxa_unencoded_ECD_return = RequestHelper.ConvertBoolToIntString(unencodedEcdReturnFlag),
                ie_endPoint = targetEndPoint,
                isGeneratedVoucher = RequestHelper.ConvertBoolToIntString(isGeneratedVoucher),
                job_id = jobId.PadRight(15, ' '),
                micr_suspect_fraud_flag = RequestHelper.ConvertBoolToIntString(suspectFraudFlag),
                micr_unproc_flag = Convert.ToInt32(unprocessable).ToString(CultureInfo.InvariantCulture),
                proc_date = processingDate.ToString("yyyyMMdd"),
                processing_state = processingState,
                raw_micr = rawMicr,
                raw_ocr = rawOcr,
                ser_num = RequestHelper.ConvertNullValueToEmptyString(auxDom),
                surplusItemFlag = RequestHelper.ConvertBoolToIntString(surplusItemFlag),
                tpcMixedDepRet = RequestHelper.ConvertBoolToIntString(thirdPartyMixedDepositReturnFlag),
                tpcRequired = RequestHelper.ConvertBoolToYNString(tpcRequired),
                trace = traceId.PadLeft(9, '0'),
                trancode = RequestHelper.ConvertNullValueToEmptyString(transactionCode),
                transactionLinkNumber = transactionLinkNumber,
                unit_id = unitId,
                unproc_flag = Convert.ToInt32(unprocessable).ToString(CultureInfo.InvariantCulture),
                sub_batch_type = subBatchType,
                
                //Default DipsNabChq Values
                //mandatory fields
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
                man_rep_ind = "0",
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
                host_trans_no = IgnoreString(3),
                volume = IgnoreString(8),
                img_location = IgnoreString(80),
                img_front = IgnoreString(8),
                img_rear = IgnoreString(8),
                held_ind = IgnoreString(1),
                receiving_bank = IgnoreString(3),
                ie_transaction_id = IgnoreString(12),
                micr_flag = IgnoreString(1),
                presentationMode = presentationMode,
                adjustmentReasonCode = IgnoreString(2),
                adjustmentDescription = IgnoreString(60),
                adjustedBy = IgnoreString(15),
                adjustedFlag = IgnoreString(1),
                adjustmentLetterRequired = IgnoreString(1),
                adjustmentType = IgnoreString(9)
            };
            return output;
        }

        public DipsNabChq CreateNewDipsNabChqForValidateTransactionRequest(
            string batchNumber,
            string traceId,
            DateTime processingDate,
            string reasonCode,
            string transactionLinkNumber,
            bool unprocessable,
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
            bool amountStatus,
            string documentType,
            string jobId,
            string rawMicr,
            string rawOcr,
            string captureBsb,
            string batchAccountNumber,
            string processingState,
            string collectingBank,
            string unitId,
            string forValueIndicator,
            string dipsOverride,
            string batchType,
            bool postTransmissionQaAmountFlag,
            bool postTransmissionQaCodelineFlag,
            string subBatchType)
        {
            // calculate a value for the S_STATUS1 field, which represents a
            // bitmask of invalid fields in Dips
            int statusMask = DipsStatus1Bitmask.GetStatusMask(extraAuxDomStatus, auxDomStatus, accountNumberStatus,
                amountStatus, bsbNumberStatus, transactionCodeStatus);

            //traceId = traceId.Substring(traceId.Length - 9);
            //traceId = ResolveTraceNumber(traceId);
            traceId = RequestHelper.ResolveDocumentReferenceNumber(traceId);

            if (string.IsNullOrEmpty(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            if (WorkTypeEnum.NABCHQ_POD.ToString().Equals(jobId))
            {
                jobId = DipsJobIdType.NabChqPod.Value;
            }

            var output = new DipsNabChq
            {
                //Dynamic DipsNabChq Values
                @override = dipsOverride,
                S_BATCH = batchNumber.PadRight(8, '0'),
                S_TRACE = traceId.PadLeft(9, '0'),
                acc_num = accountNumber,
                amount = capturedAmount ?? string.Empty,
                balanceReason = reasonCode,
                batch = batchNumber.PadRight(8, '0'),
                batchAccountNumber = batchAccountNumber,
                batch_type = batchType,
                bsb_num = bsbNumber,
                captureBSB = captureBsb,
                collecting_bank = collectingBank,
                doc_type = documentType,
                ead = RequestHelper.ConvertNullValueToEmptyString(extraAuxDom),
                fv_ind = RequestHelper.ConvertNullValueToEmptyString(forValueIndicator),
                fxaPtQAAmtFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaAmountFlag),
                fxaPtQACodelineFlag = RequestHelper.ConvertBoolToIntString(postTransmissionQaCodelineFlag),
                job_id = jobId.PadRight(15, ' '),
                micr_unproc_flag = Convert.ToInt32(unprocessable).ToString(CultureInfo.InvariantCulture),
                proc_date = processingDate.ToString("yyyyMMdd"),
                processing_state = processingState,
                raw_micr = rawMicr,
                raw_ocr = rawOcr,
                ser_num = RequestHelper.ConvertNullValueToEmptyString(auxDom),
                trace = traceId.PadLeft(9, '0'),
                trancode = RequestHelper.ConvertNullValueToEmptyString(transactionCode),
                transactionLinkNumber = transactionLinkNumber,
                unit_id = unitId,
                unproc_flag = Convert.ToInt32(unprocessable).ToString(CultureInfo.InvariantCulture),
                sub_batch_type = subBatchType,

                //Default DipsNabChq Values
                //mandatory fields
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
                man_rep_ind = "0",
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
                host_trans_no = IgnoreString(3),
                volume = IgnoreString(8),
                img_location = IgnoreString(80),
                img_front = IgnoreString(8),
                img_rear = IgnoreString(8),
                held_ind = IgnoreString(1),
                receiving_bank = IgnoreString(3),
                ie_transaction_id = IgnoreString(12),
                doc_ref_num = IgnoreString(9),
                micr_flag = IgnoreString(1),
                micr_suspect_fraud_flag = IgnoreString(1),
                presentationMode = IgnoreString(1),
                adjustmentReasonCode = IgnoreString(2),
                adjustmentDescription = IgnoreString(60),
                adjustedBy = IgnoreString(15),
                adjustedFlag = IgnoreString(1),
                adjustmentLetterRequired = IgnoreString(1),
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
                    batchNumber.PadLeft(8, '0'),
                    DipsJobIdType.NabChqPod.Value)
                .PadRight(128, ' ');
        }

    }
}