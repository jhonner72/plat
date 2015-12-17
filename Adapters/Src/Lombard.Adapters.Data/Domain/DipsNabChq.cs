using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;

namespace Lombard.Adapters.Data.Domain
{
    // ReSharper disable InconsistentNaming
    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "Dips Database")]
    public class DipsNabChq
    {
        [MaxLength(5)]
        public string S_DEL_IND { get; set; }
        [MaxLength(8)]
        public string S_BATCH { get; set; }
        [MaxLength(5)]
        public string S_MODIFIED { get; set; }
        [MaxLength(5)]
        public string S_COMPLETE { get; set; }
        [MaxLength(5)]
        public string S_TYPE { get; set; }
        [MaxLength(10)]
        public string S_STATUS1 { get; set; }
        [MaxLength(10)]
        public string S_STATUS2 { get; set; }
        [MaxLength(10)]
        public string S_STATUS3 { get; set; }
        [MaxLength(10)]
        public string S_STATUS4 { get; set; }
        [MaxLength(10)]
        public string S_IMG1_OFF { get; set; }
        [MaxLength(10)]
        public string S_IMG1_LEN { get; set; }
        [MaxLength(5)]
        public string S_IMG1_TYP { get; set; }
        [MaxLength(10)]
        public string S_IMG2_OFF { get; set; }
        [MaxLength(10)]
        public string S_IMG2_LEN { get; set; }
        [MaxLength(5)]
        public string S_IMG2_TYP { get; set; }
        [MaxLength(9)]
        public string S_TRACE { get; set; }
        [MaxLength(5)]
        public string S_LENGTH { get; set; }
        [MaxLength(5)]
        public string S_SEQUENCE { get; set; }
        [MaxLength(10)]
        public string S_BALANCE { get; set; }
        [MaxLength(5)]
        public string S_REPROCESS { get; set; }
        [MaxLength(5)]
        public string S_REPORTED { get; set; }
        [MaxLength(5)]
        public string S_COMMITTED { get; set; }
        [MaxLength(8)]
        public string batch { get; set; }
        [MaxLength(9)]
        public string trace { get; set; }
        [MaxLength(8)]
        public string sys_date { get; set; }
        [MaxLength(8)]
        public string proc_date { get; set; }
        [MaxLength(16)]
        public string ead { get; set; }
        [MaxLength(14)]
        public string ser_num { get; set; }
        [MaxLength(6)]
        public string bsb_num { get; set; }
        [MaxLength(12)]
        public string acc_num { get; set; }
        [MaxLength(3)]
        public string trancode { get; set; }
        [MaxLength(12)]
        public string amount { get; set; }
        [MaxLength(2)]
        public string pocket { get; set; }
        [MaxLength(240)]
        public string payee_name { get; set; }
        [MaxLength(5)]
        public string manual_repair { get; set; }
        [MaxLength(3)]
        public string doc_type { get; set; }
        [MaxLength(4)]
        public string rec_type_id { get; set; }
        [MaxLength(6)]
        public string collecting_bank { get; set; }
        [MaxLength(3)]
        public string unit_id { get; set; }
        [MaxLength(1)]
        public string man_rep_ind { get; set; }
        [MaxLength(12)]
        public string proof_seq { get; set; }
        [MaxLength(5)]
        public string trans_seq { get; set; }
        [MaxLength(1)]
        public string delay_ind { get; set; }
        [MaxLength(1)]
        public string fv_exchange { get; set; }
        [MaxLength(2)]
        public string adj_code { get; set; }
        [MaxLength(30)]
        public string adj_desc { get; set; }
        [MaxLength(15)]
        public string op_id { get; set; }
        [MaxLength(4)]
        public string proc_time { get; set; }
        [MaxLength(5)]
        public string @override { get; set; }
        [MaxLength(1)]
        public string fv_ind { get; set; }
        [MaxLength(3)]
        public string host_trans_no { get; set; }
        [MaxLength(15)]
        public string job_id { get; set; }
        [MaxLength(8)]
        public string volume { get; set; }
        [MaxLength(80)]
        public string img_location { get; set; }
        [MaxLength(8)]
        public string img_front { get; set; }
        [MaxLength(8)]
        public string img_rear { get; set; }
        [MaxLength(1)]
        public string held_ind { get; set; }
        [MaxLength(3)]
        public string receiving_bank { get; set; }
        [MaxLength(12)]
        public string ie_transaction_id { get; set; }
        [MaxLength(20)]
        public string batch_type { get; set; }
        [MaxLength(20)]
        public string sub_batch_type { get; set; }
        [MaxLength(9)]
        public string doc_ref_num { get; set; }
        [MaxLength(64)]
        public string raw_micr { get; set; }
        [MaxLength(128)]
        public string raw_ocr { get; set; }
        [MaxLength(3)]
        public string processing_state { get; set; }
        [MaxLength(1)]
        public string micr_flag { get; set; }
        [MaxLength(1)]
        public string micr_unproc_flag { get; set; }
        [MaxLength(1)]
        public string micr_suspect_fraud_flag { get; set; }
        [MaxLength(3)]
        public string amountConfidenceLevel { get; set; }
        [MaxLength(20)]
        public string balanceReason { get; set; }
        [MaxLength(60)]
        public string transactionLinkNumber { get; set; }
        [MaxLength(1)]
        public string unproc_flag { get; set; }
        [MaxLength(10)]
        public string ie_endPoint { get; set; }
        [MaxLength(1)]
        public string export_exclude_flag { get; set; }
        [MaxLength(15)]
        public string alt_ead { get; set; }
        [MaxLength(9)]
        public string alt_ser_num { get; set; }
        [MaxLength(6)]
        public string alt_bsb_num { get; set; }
        [MaxLength(10)]
        public string alt_acc_num { get; set; }
        [MaxLength(3)]
        public string alt_trancode { get; set; }
        [MaxLength(12)]
        public string orig_amount { get; set; }
        [MaxLength(1)]
        public string presentationMode { get; set; }
        [MaxLength(2)]
        public string adjustmentReasonCode { get; set; }
        [MaxLength(60)]
        public string adjustmentDescription { get; set; }
        [MaxLength(15)]
        public string adjustedBy { get; set; }
        [MaxLength(1)]
        public string adjustedFlag { get; set; }
        [MaxLength(1)]
        public string adjustmentLetterRequired { get; set; }
        [MaxLength(9)]
        public string adjustmentType { get; set; }
        [MaxLength(3)]
        public string listingPageNumber { get; set; }
        [MaxLength(6)]
        public string captureBSB { get; set; }
        [MaxLength(1)]
        public string voucherIndicatorField { get; set; }
        [MaxLength(10)]
        public string batchAccountNumber { get; set; }
        [MaxLength(1)]
        public string surplusItemFlag { get; set; }
        [MaxLength(12)]
        public string repostFromDRN { get; set; }
        [MaxLength(8)]
        public string repostFromProcessingDate { get; set; }
        [MaxLength(1)]
        public string tpcRequired { get; set; }
        [MaxLength(1)]
        public string tpcResult { get; set; }
        [MaxLength(1)]
        public string fxa_tpc_suspense_pool_flag { get; set; }
        [MaxLength(1)]
        public string fxa_unencoded_ECD_return { get; set; }
        [MaxLength(1)]
        public string isGeneratedVoucher { get; set; }
        [MaxLength(1)]
        public string tpcMixedDepRet { get; set; }
        [MaxLength(1)]
        public string highValueFlag { get; set; }
        [MaxLength(1)]
        public string fxaPtQAAmtFlag { get; set; }
        [MaxLength(1)]
        public string fxaPtQACodelineFlag { get; set; }
        [MaxLength(1)]
        public string isRetrievedVoucher { get; set; }
        [MaxLength(1)]
        public string isInsertedCredit { get; set; }
        [MaxLength(1)]
        public string creditNoteFlag { get; set; }
        [MaxLength(6)]
        public string maxVouchers { get; set; }
        [MaxLength(10)]
        public string customerLinkNumber { get; set; }
        [MaxLength(1)]
        public string isGeneratedBulkCredit { get; set; }
        [MaxLength(14)]
        public string batchAuxDom { get; set; }
        [MaxLength(1)]
        public string insertedCreditType { get; set; }
    }
    // ReSharper restore InconsistentNaming   
}