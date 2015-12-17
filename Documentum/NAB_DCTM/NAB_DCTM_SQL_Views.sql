---- Version History ----------------
-- Consider Sprint 25 as Baseline
-----------------------------------
--- Version 1.1. :- Date: 14 Aug 2015 - Sprint 26 Drop 1 by Ajit Dangal TFS [21255]
--- Version 1.2. :- Date: 27 Aug 2015 - Defect 21912 by Yogesh Jankin
--- Version 1.3. :- Date: 28 Aug 2015 - Sprint 27 Drop 1 by Ajit Dangal TFS [TBD]
--- Version 1.4. :- Date: 31 Aug 2015 - Sprint 27 Drop 1 [Bug] - datetime overflow - by Ajit Dangal TFS [TBD]
--- Version 1.5. :- Date: 02 Sep 2015 - Sprint 27 Drop 1 [Bug][22459] - fxa_pre_adjusted_amt to bigint - by Ajit
--- Version 1.6. :- Date: 10 Sep 2015 - Defect 22840 - fxa_report_view is showing wrong processingDate - by Yogesh Jankin
--- Version 1.7. :- Date: 15 Sep 2015 - Sprint 28 Drop 1 - by Ajit Dangal
--- Version 1.8. :- Date: 28 Sep 2015 - Sprint 29 Drop 1 - by Ajit Dangal
------------- DROP VIEWS ------------
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_view]') ) 
DROP VIEW [fxa_voucher_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_im_view]') ) 
DROP VIEW [fxa_voucher_im_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_stripzero_view]') ) 
DROP VIEW [fxa_voucher_stripzero_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_transfer_view]') ) 
DROP VIEW [fxa_voucher_transfer_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_audit_amount_only_view]') ) 
DROP VIEW [fxa_voucher_audit_amount_only_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_dishonour_letter_view]') ) 
DROP VIEW [fxa_dishonour_letter_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_adjustment_letter_view]') ) 
DROP VIEW [fxa_adjustment_letter_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_listing_view]') ) 
DROP VIEW [fxa_listing_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_report_view]') ) 
DROP VIEW [fxa_report_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_im_cr_view]') ) 
DROP VIEW [fxa_voucher_im_cr_view]
go
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_im_cr_dr_view]') ) 
DROP VIEW [fxa_voucher_im_cr_dr_view]
go

------- CREATE VIEWS ------------

CREATE VIEW [dbo].[fxa_voucher_view](
	r_object_id,
	object_name,
	i_chronicle_id,
	i_is_deleted,
	folder_path,
	fxa_amount,
	fxa_processing_date,
	fxa_m_batch_number,
	fxa_m_balanced_sequence,
	fxa_migration_batch_no,
	fxa_checksum,
	fxa_checksum_type,
	fxa_tran_link_no,
	fxa_dishonoured,
	fxa_extra_aux_dom,
	fxa_aux_dom,
	fxa_bsb,
	fxa_account_number,
	fxa_trancode,
	fxa_drn,
	fxa_classification,
	fxa_collecting_bsb,
	fxa_m_entry_number,
	fxa_m_bal_seq_for_deposit,
	fxa_m_cr_drn,
	fxa_batch_number,
	fxa_target_end_point,
	fxa_unit_id,
	fxa_voucher_delayed_id,
	fxa_processing_state,
	fxa_capture_bsb,
	fxa_is_duplicate_flag,
	fxa_for_value_type,
	fxa_suspect_fraud_flag,
	fxa_adjustment_flag,
	fxa_source_type,
	fxa_batch_type_code,
	fxa_high_value_flag,
	fxa_ie_image_only,
	fxa_surplus_item_flag,
	fxa_adjustment_description,
	fxa_release_flag,
	fxa_payment_type,
	fxa_inactive_flag,
	fxa_work_type_code,
	fxa_manual_repair,
	fxa_matched_flag,
	fxa_delete_transaction_flag,
	fxa_listing_page_number,
	fxa_tpc_suspense_pool_flag,
	fxa_batch_account_number,
	fxa_unprocessable_item_flag,
	fxa_presentation_mode,
	fxa_presentation_bsb,
	fxa_adjusted_by_id,
	fxa_adjustment_reason_code,
	fxa_micr_flag,
	fxa_raw_micr,
	fxa_adjustment_on_hold_flag,
	fxa_adj_letter_req_flag,
	fxa_file_receipt_id,
	fxa_doc_retr_flag,
	fxa_delayed_image,
	fxa_doc_retr_date,
	fxa_doc_retr_seq_id,
	fxa_doc_retr_delivery_site,
	fxa_repost_processing_date,
	fxa_repost_drn,
	fxa_ptqa_amt_flag, 
	fxa_ptqa_code_line_flag,
	fxa_pre_adjustment_amt,
	fxa_uecd_return_flag,
	fxa_tpc_failed_flag,
	fxa_generated_voucher_flag,
	fxa_mixed_dep_return_flag,
	fxa_raw_ocr,
	fxa_ptqa_amt_complete_flag,
	fxa_ptqa_cdc_complete_flag,
	fxa_ptqa_amt_processed_date,
	fxa_ptqa_cdc_processed_date,
	fxa_generated_bulk_cr_flag,
	fxa_customer_link_no,
	fxa_reserved_for_bal_flag,
	fxa_ap_presentment_type
	
) 
WITH SCHEMABINDING  AS  
SELECT 
	S_.r_object_id,
	S_.object_name,
	S_.i_chronicle_id,
	S_.i_is_deleted,
	'Vouchers\' + 
		left(CONVERT (varchar, V_.fxa_processing_date-getutcdate()+getdate(), 102),4) + 
		'\' + 
		substring(CONVERT(varchar,V_.fxa_processing_date-getutcdate()+getdate(), 102),6,2) + 
		'\' + 
		RIGHT(CONVERT (varchar, V_.fxa_processing_date-getutcdate()+getdate(), 102),2)
		,
	CAST(V_.fxa_amount AS bigint),
	V_.fxa_processing_date-getutcdate()+getdate(), 
	V_.fxa_m_batch_number,
	V_.fxa_m_balanced_sequence,
	V_.fxa_migration_batch_no,
	V_.fxa_checksum,
	V_.fxa_checksum_type,
	isnull(V_.fxa_tran_link_no, V_.fxa_m_cr_drn),
	V_.fxa_dishonoured,
	V_.fxa_extra_aux_dom,
	V_.fxa_aux_dom,
	V_.fxa_bsb,
	V_.fxa_account_number,
	V_.fxa_trancode,
	V_.fxa_drn,
	V_.fxa_classification,
	V_.fxa_collecting_bsb,
	V_.fxa_m_entry_number,
	V_.fxa_m_bal_seq_for_deposit,
	V_.fxa_m_cr_drn,
	isnull(V_.fxa_batch_number, V_.fxa_m_batch_number),
	V_.fxa_target_end_point,
	V_.fxa_unit_id,
	V_.fxa_voucher_delayed_id,
	V_.fxa_processing_state,
	V_.fxa_capture_bsb,
	V_.fxa_is_duplicate_flag,
	V_.fxa_for_value_type,
	V_.fxa_suspect_fraud_flag,
	V_.fxa_adjustment_flag,
	V_.fxa_source_type,
	V_.fxa_batch_type_code,
	V_.fxa_high_value_flag,
	V_.fxa_ie_image_only,
	V_.fxa_surplus_item_flag,
	V_.fxa_adjustment_description,
	V_.fxa_release_flag,
	V_.fxa_payment_type,
	V_.fxa_inactive_flag,
	V_.fxa_work_type_code,
	V_.fxa_manual_repair,
	V_.fxa_matched_flag,
	V_.fxa_delete_transaction_flag,
	V_.fxa_listing_page_number,
	V_.fxa_tpc_suspense_pool_flag,
	V_.fxa_batch_account_number,
	V_.fxa_unprocessable_item_flag,
	V_.fxa_presentation_mode,
	V_.fxa_presentation_bsb,
	V_.fxa_adjusted_by_id,
	V_.fxa_adjustment_reason_code,
	V_.fxa_micr_flag,
	V_.fxa_raw_micr,
	V_.fxa_adjustment_on_hold_flag,
	V_.fxa_adj_letter_req_flag,
	V_.fxa_file_receipt_id ,
	V_.fxa_doc_retr_flag,
	V_.fxa_delayed_image,
	CASE
	when convert(nvarchar, V_.fxa_doc_retr_date, 112) like '1753%' then NULL
	else V_.fxa_doc_retr_date -getutcdate()+getdate()
	end,	
	V_.fxa_doc_retr_seq_id,
	V_.fxa_doc_retr_delivery_site,
	V_.fxa_repost_processing_date,
	V_.fxa_repost_drn,
	V_.fxa_ptqa_amt_flag, 
	V_.fxa_ptqa_code_line_flag,
	CAST(V_.fxa_pre_adjustment_amt AS bigint),
	V_.fxa_uecd_return_flag,
	V_.fxa_tpc_failed_flag,
	V_.fxa_generated_voucher_flag,
	V_.fxa_mixed_dep_return_flag,
	V_.fxa_raw_ocr,
	V_.fxa_ptqa_amt_complete_flag,
	V_.fxa_ptqa_cdc_complete_flag,
	CASE
	when convert(nvarchar, V_.fxa_ptqa_amt_processed_date, 112) like '1753%' then NULL
	else V_.fxa_ptqa_amt_processed_date -getutcdate()+getdate()
	end,
	CASE
	when convert(nvarchar, V_.fxa_ptqa_cdc_processed_date, 112) like '1753%' then NULL
	else V_.fxa_ptqa_cdc_processed_date -getutcdate()+getdate()
	end,
	V_.fxa_generated_bulk_cr_flag,
	V_.fxa_customer_link_no,
	V_.fxa_reserved_for_bal_flag,
	V_.fxa_ap_presentment_type
FROM dbo.fxa_voucher_s V_,
	dbo.dm_sysobject_s S_ 
WHERE V_.r_object_id=S_.r_object_id and 
	S_.i_has_folder = 1 and 
	S_.i_is_deleted = 0 and
	V_.fxa_processing_date >= '2015/05/01'
go

CREATE VIEW [dbo].[fxa_voucher_transfer_view]
(
r_object_id,
object_name,
i_chronicle_id,
v_i_chronicle_id,
status,filename,
transmission_date,
transmission_type,
target_end_point,
transfer_type,
transaction_id) 
WITH SCHEMABINDING AS 
SELECT 
S_.r_object_id,
S_.object_name,
S_.i_chronicle_id,
JTD_.v_i_chronicle_id,
JTD_.status,
JTD_.filename,
CASE
when convert(nvarchar, JTD_.transmission_date, 112) like '1753%' then NULL
else DATEADD(MILLISECOND,DATEDIFF(MILLISECOND,getutcdate(),GETDATE()),JTD_.transmission_date )  
end as transmission_date,
JTD_.transmission_type,
JTD_.target_end_point,
JTD_.transfer_type,
JTD_.transaction_id 
FROM 
dbo.fxa_voucher_transfer_s JTD_,
dbo.dm_sysobject_s S_
WHERE 
JTD_.r_object_id=S_.r_object_id and
S_.i_has_folder = 1 and 
S_.i_is_deleted = 0 
go

create view fxa_voucher_audit_amount_only_view
as
select i_chronicle_id,
       subject_area,
       attribute_name,
       CAST(pre_value as int) as pre_value,
       CAST(post_value as int) as post_value
from dbo.fxa_voucher_audit
where attribute_name = 'amt'
go

CREATE VIEW [dbo].[fxa_dishonour_letter_view](r_object_id,object_name,i_chronicle_id,fxa_dishonour_status) WITH SCHEMABINDING  AS  SELECT S_.r_object_id,S_.object_name,S_.i_chronicle_id,V_.fxa_dishonour_status FROM dbo.fxa_dishonour_letter_s V_,dbo.dm_sysobject_s S_ WHERE V_.r_object_id=S_.r_object_id and S_.i_has_folder = 1 and S_.i_is_deleted = 0
go

CREATE VIEW [dbo].[fxa_adjustment_letter_view](r_object_id,object_name,i_chronicle_id,fxa_filename,fxa_drn,fxa_processing_date,fxa_batch_number,fxa_tran_link_no
) WITH SCHEMABINDING  AS  SELECT S_.r_object_id,S_.object_name,S_.i_chronicle_id,V_.fxa_filename,V_.fxa_drn,V_.fxa_processing_date,V_.fxa_batch_number,V_.fxa_tran_link_no FROM dbo.fxa_adjustment_letter_s V_,dbo.dm_sysobject_s S_ WHERE V_.r_object_id=S_.r_object_id and S_.i_has_folder = 1 and S_.i_is_deleted = 0
go

CREATE VIEW [dbo].[fxa_voucher_stripzero_view](r_object_id,object_name,i_chronicle_id,fxa_processing_date,fxa_m_batch_number,fxa_m_balanced_sequence,fxa_migration_batch_no,fxa_checksum,fxa_checksum_type,fxa_tran_link_no,fxa_dishonoured,fxa_extra_aux_dom,fxa_aux_dom,fxa_bsb,fxa_account_number,fxa_trancode,fxa_amount,fxa_drn,fxa_classification,fxa_collecting_bsb,fxa_m_entry_number,fxa_m_bal_seq_for_deposit,fxa_m_cr_drn,fxa_delayed_image) 
WITH SCHEMABINDING  AS  SELECT S_.r_object_id,S_.object_name,S_.i_chronicle_id,V_.fxa_processing_date,V_.fxa_m_batch_number,V_.fxa_m_balanced_sequence,V_.fxa_migration_batch_no,V_.fxa_checksum,V_.fxa_checksum_type,V_.fxa_tran_link_no,V_.fxa_dishonoured,V_.fxa_extra_aux_dom,SUBSTRING(V_.fxa_aux_dom, PATINDEX('%[^0]%', V_.fxa_aux_dom+'.'), LEN(V_.fxa_aux_dom)),V_.fxa_bsb,SUBSTRING(V_.fxa_account_number, PATINDEX('%[^0]%', V_.fxa_account_number+'.'), LEN(V_.fxa_account_number)),V_.fxa_trancode,V_.fxa_amount,V_.fxa_drn,V_.fxa_classification,V_.fxa_collecting_bsb,V_.fxa_m_entry_number,V_.fxa_m_bal_seq_for_deposit,V_.fxa_m_cr_drn,V_.fxa_delayed_image 
FROM dbo.fxa_voucher_s V_,
	dbo.dm_sysobject_s S_ 
WHERE V_.r_object_id=S_.r_object_id and 
	S_.i_has_folder = 1 and 
	S_.i_is_deleted = 0
go

CREATE VIEW [dbo].[fxa_listing_view](r_object_id,object_name,i_chronicle_id,processing_date,collecting_bsb,operator_name,workstation_no,capture_bsb,work_type,drn,ead,batch_no,ad,batch_type_name,tc,acc,listing_id) WITH SCHEMABINDING  AS  SELECT S_.r_object_id,S_.object_name,S_.i_chronicle_id,
V_.processing_date,V_.collecting_bsb,V_.operator_name,V_.workstation_no,V_.capture_bsb,V_.work_type,V_.drn,V_.ead,V_.batch_no,V_.ad,V_.batch_type_name,V_.tc,V_.acc,V_.listing_id FROM dbo.fxa_listing_s V_,dbo.dm_sysobject_s S_ WHERE V_.r_object_id=S_.r_object_id and S_.i_has_folder = 1 and 
	S_.i_is_deleted = 0
go

CREATE VIEW [dbo].[fxa_voucher_im_cr_dr_view] AS
SELECT
  	VMAIN.i_chronicle_id, 
    VMAIN.r_object_id,
    VMAIN.r_content_size,
    VMAIN.a_storage_type,
--    isnull(VMAIN.fxa_tran_link_no, VMAIN.fxa_m_cr_drn) as fxa_tran_link_no,
--    isnull(VMAIN.fxa_batch_number,VMAIN.fxa_m_batch_number) as fxa_batch_number,
	CASE
		WHEN LEN(VMAIN.fxa_m_cr_drn) > 0 THEN VMAIN.fxa_m_cr_drn
		ELSE VMAIN.fxa_tran_link_no
	END as fxa_tran_link_no,
	CASE 
		WHEN LEN(VMAIN.fxa_m_batch_number) > 0 THEN VMAIN.fxa_m_batch_number
		ELSE VMAIN.fxa_batch_number
	END AS fxa_batch_number,
	VMAIN.fxa_m_batch_number,
    VMAIN.fxa_m_cr_drn,
    VMAIN.fxa_drn,
    VMAIN.fxa_classification,
    VMAIN.fxa_processing_date,
    VMAIN.fxa_bsb,
    VMAIN.fxa_account_number,
    VMAIN.fxa_aux_dom,
	VMAIN.fxa_trancode,
    VMAIN.fxa_extra_aux_dom,
    VMAIN.fxa_collecting_bsb,
    VMAIN.fxa_processing_state, 
	VMAIN.fxa_amount,	
	VMAIN.fxa_m_entry_number,
	'' as fxa_run,
	VMAIN.fxa_m_bal_seq_for_deposit,
	'' as fxa_box_number,
	'' as fxa_tray_number,
	VMAIN.fxa_m_balanced_sequence,
	case VMAIN.fxa_adjustment_reason_code
		when NULL then 'N'
		else 'Y'
	end as fxa_update_flag,
	VMAIN.fxa_adjustment_reason_code,
	VMAIN.fxa_capture_bsb
  FROM fxa_voucher_sp VMAIN
  WHERE VMAIN.i_has_folder = 1 
	AND VMAIN.i_is_deleted = 0
	AND VMAIN.fxa_inactive_flag = 0
	AND lower(VMAIN.fxa_classification) in ('dr','cr')
go

CREATE VIEW [dbo].[fxa_voucher_im_cr_view] AS
SELECT
  	ds.i_chronicle_id, 
    ds.r_object_id,
--    isnull(ds.fxa_tran_link_no, ds.fxa_m_cr_drn) as fxa_tran_link_no,
--    isnull(ds.fxa_batch_number, ds.fxa_m_batch_number) as fxa_batch_number,
	CASE
		WHEN LEN(ds.fxa_m_cr_drn) > 0 THEN ds.fxa_m_cr_drn
		ELSE ds.fxa_tran_link_no
	END as fxa_tran_link_no,
	CASE 
		WHEN LEN(ds.fxa_m_batch_number) > 0 THEN ds.fxa_m_batch_number
		ELSE ds.fxa_batch_number
	END AS fxa_batch_number,
	ds.fxa_m_batch_number,
    ds.fxa_m_cr_drn,
    ds.fxa_drn,
    ds.fxa_classification,
    ds.fxa_processing_date,
	ds.fxa_amount,	
	ds.fxa_bsb as fxa_deposit_bsb,
	ds.fxa_account_number as fxa_deposit_account	
  FROM fxa_voucher_sp ds
  WHERE ds.i_has_folder = 1 
	AND ds.i_is_deleted = 0
	AND ds.fxa_inactive_flag = 0
	AND lower(ds.fxa_classification) = 'cr'
go

CREATE VIEW [dbo].[fxa_voucher_im_view] AS
SELECT
  	VMAIN.i_chronicle_id as i_chronicle_id, 
    VMAIN.r_object_id as r_object_id,
    VMAIN.r_content_size as r_content_size,
    VMAIN.a_storage_type as a_storage_type,
    VMAIN.fxa_tran_link_no as fxa_tran_link_no,
    VMAIN.fxa_batch_number as fxa_batch_number,
	VMAIN.fxa_m_batch_number as fxa_m_batch_number,
    VMAIN.fxa_m_cr_drn as fxa_m_cr_drn,
    VMAIN.fxa_drn as fxa_drn,
    VMAIN.fxa_classification as fxa_classification,
    VMAIN.fxa_processing_date-getutcdate()+getdate() as fxa_processing_date,
    VMAIN.fxa_bsb as fxa_bsb,
    VMAIN.fxa_account_number as fxa_account_number,
    VMAIN.fxa_aux_dom as fxa_aux_dom,
    CASE WHEN ISNUMERIC(VMAIN.fxa_trancode) = 1 THEN CAST(VMAIN.fxa_trancode AS INT) ELSE 0 END AS fxa_trancode,
    VMAIN.fxa_extra_aux_dom as fxa_extra_aux_dom,
    VMAIN.fxa_collecting_bsb as fxa_collecting_bsb,
    CASE VMAIN.fxa_processing_state 
		WHEN 'NSW' THEN '2' 
		WHEN 'ACT' THEN '2' 
		WHEN 'VIC' THEN '3' 
		WHEN 'QLD' THEN '4' 
		WHEN 'SA' THEN '5' 
		WHEN 'NT' THEN '5' 
		WHEN 'WA' THEN '6' 
		WHEN 'TAS' THEN '7' 
		ELSE CASE SUBSTRING(VMAIN.fxa_collecting_bsb,3,1) 
			WHEN '' THEN '0' WHEN NULL THEN '0' 
		END END as fxa_processing_state,
	CASE WHEN ISNUMERIC(VMAIN.fxa_amount) = 1 THEN CAST(VMAIN.fxa_amount AS DECIMAL(13,0)) ELSE 0 END as fxa_amount,	
	VMAIN.fxa_m_entry_number as fxa_m_entry_number,
	VMAIN.fxa_run as fxa_run,
	VMAIN.fxa_m_bal_seq_for_deposit as fxa_m_bal_seq_for_deposit,
	VMAIN.fxa_box_number as fxa_box_number,
	VMAIN.fxa_tray_number as fxa_tray_number,
	VMAIN.fxa_m_balanced_sequence as fxa_m_balanced_sequence,
	VMAIN.fxa_update_flag as fxa_update_flag,
	VMAIN.fxa_adjustment_reason_code as fxa_adjustment_reason_code,
	SUBSTRING(VMAIN.fxa_capture_bsb,3,1) as fxa_site,
	ds.fxa_deposit_bsb as fxa_deposit_bsb,
	ds.fxa_deposit_account	as fxa_deposit_account
  FROM fxa_voucher_im_cr_dr_view VMAIN
  left join fxa_voucher_im_cr_view ds 
  on ds.fxa_processing_date = VMAIN.fxa_processing_date and ds.fxa_batch_number = VMAIN.fxa_batch_number and ds.fxa_tran_link_no = VMAIN.fxa_tran_link_no
  where VMAIN.fxa_tran_link_no <> ''
  and VMAIN.fxa_batch_number <> ''
  and ds.fxa_tran_link_no <> ''
  and ds.fxa_batch_number <> ''
  
union all
SELECT
  	VMAIN2.i_chronicle_id as i_chronicle_id, 
    VMAIN2.r_object_id as r_object_id,
    VMAIN2.r_content_size as r_content_size,
    VMAIN2.a_storage_type as a_storage_type,
    VMAIN2.fxa_tran_link_no as fxa_tran_link_no,
    VMAIN2.fxa_batch_number as fxa_batch_number,
	VMAIN2.fxa_m_batch_number as fxa_m_batch_number,
    VMAIN2.fxa_m_cr_drn as fxa_m_cr_drn,
    VMAIN2.fxa_drn as fxa_drn,
    VMAIN2.fxa_classification as fxa_classification,
    VMAIN2.fxa_processing_date-getutcdate()+getdate() as fxa_processing_date,
    VMAIN2.fxa_bsb as fxa_bsb,
    VMAIN2.fxa_account_number as fxa_account_number,
    VMAIN2.fxa_aux_dom as fxa_aux_dom,
    CASE WHEN ISNUMERIC(VMAIN2.fxa_trancode) = 1 THEN CAST(VMAIN2.fxa_trancode AS INT) ELSE 0 END AS fxa_trancode,
    VMAIN2.fxa_extra_aux_dom as fxa_extra_aux_dom,
    VMAIN2.fxa_collecting_bsb as fxa_collecting_bsb,
    CASE VMAIN2.fxa_processing_state 
		WHEN 'NSW' THEN '2' 
		WHEN 'ACT' THEN '2' 
		WHEN 'VIC' THEN '3' 
		WHEN 'QLD' THEN '4' 
		WHEN 'SA' THEN '5' 
		WHEN 'NT' THEN '5' 
		WHEN 'WA' THEN '6' 
		WHEN 'TAS' THEN '7' 
		ELSE CASE SUBSTRING(VMAIN2.fxa_collecting_bsb,3,1) 
			WHEN '' THEN '0' WHEN NULL THEN '0' 
		END END as fxa_processing_state,
	CASE WHEN ISNUMERIC(VMAIN2.fxa_amount) = 1 THEN CAST(VMAIN2.fxa_amount AS DECIMAL(13,0)) ELSE 0 END as fxa_amount,	
	VMAIN2.fxa_m_entry_number as fxa_m_entry_number,
	VMAIN2.fxa_run as fxa_run,
	VMAIN2.fxa_m_bal_seq_for_deposit as fxa_m_bal_seq_for_deposit,
	VMAIN2.fxa_box_number as fxa_box_number,
	VMAIN2.fxa_tray_number as fxa_tray_number,
	VMAIN2.fxa_m_balanced_sequence as fxa_m_balanced_sequence,
	VMAIN2.fxa_update_flag as fxa_update_flag,
	VMAIN2.fxa_adjustment_reason_code as fxa_adjustment_reason_code,
	SUBSTRING(VMAIN2.fxa_capture_bsb,3,1) as fxa_site,
	'' as fxa_deposit_bsb,
	''	as fxa_deposit_account
  FROM fxa_voucher_im_cr_dr_view VMAIN2
  where VMAIN2.fxa_tran_link_no = '' or VMAIN2.fxa_batch_number = ''  
go

CREATE VIEW [dbo].[fxa_report_view]
(
	r_object_id,
	object_name,
	i_chronicle_id,
	fxa_processing_date,
	fxa_report_type
) 
	WITH SCHEMABINDING AS 
SELECT 
	S_.r_object_id,
	S_.object_name,
	S_.i_chronicle_id,
CASE
when convert(nvarchar, R_.fxa_processing_date, 112) like '1753%' then NULL
else DATEADD(MILLISECOND,DATEDIFF(MILLISECOND,getutcdate(),GETDATE()),R_.fxa_processing_date ) 
end as fxa_processing_date,
	R_.fxa_report_type 
FROM 
	dbo.fxa_report_s R_,
	dbo.dm_sysobject_s S_
WHERE 
	R_.r_object_id=S_.r_object_id and
	S_.i_has_folder = 1 and 
	S_.i_is_deleted = 0 
go