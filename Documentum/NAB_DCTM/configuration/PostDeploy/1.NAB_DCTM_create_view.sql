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

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusersynch_yesterday]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusersynch_yesterday]
GO

CREATE TABLE [dbo].[nabusersynch_yesterday](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](300) NULL
) ON [PRIMARY]

GO

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusersynch_today]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusersynch_today]
GO

CREATE TABLE [dbo].[nabusersynch_today](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](300) NULL
) ON [PRIMARY]

GO

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusersynch_delta]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusersynch_delta]
GO

CREATE TABLE [dbo].[nabusersynch_delta](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](200) NULL,
	[data_date] [date] NULL,
	[ops_type] [varchar](20) NULL
) ON [PRIMARY]

GO
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusergroupmapping]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusergroupmapping]
GO
CREATE TABLE [dbo].[nabusergroupmapping](
	[nab_user_group] [varchar](100) NOT NULL,
	[dctm_user_group] [varchar](300) NOT NULL
) ON [PRIMARY]

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-dpr-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-dpr-manager-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-clientservices-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-chequeservices-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-chequeservices-manager-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-corporateservicing-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-fraud-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-fraud-manager-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-international-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-international-manager-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')
INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-chqdig-other-analyst-grp','nab_read_voucher_grp |nab_read_report_grp|nab_wf_surplus_grp')

GO
SET ANSI_PADDING OFF
GO



