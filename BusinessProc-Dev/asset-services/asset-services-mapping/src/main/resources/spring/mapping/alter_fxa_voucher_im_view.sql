ALTER VIEW fxa_voucher_im_view AS
SELECT
  	VMAIN.v_i_chronicle_id, -- Question why we are not using v_i_chronicle_id
    VMAIN.r_object_id,
    VMAIN.r_content_size,
    VMAIN.a_storage_type,
    VMAIN.fxa_tran_link_no,
    VMAIN.fxa_batch_number,
	VMAIN.fxa_m_batch_number,
    VMAIN.fxa_m_cr_drn,
    VMAIN.fxa_drn,
    VMAIN.fxa_classification,
    VMAIN.fxa_processing_date-getutcdate()+getdate() as fxa_processing_date,
    VMAIN.fxa_bsb,
    VMAIN.fxa_account_number,
    VMAIN.fxa_aux_dom,
    CASE WHEN ISNUMERIC(VMAIN.fxa_trancode) = 1 THEN CAST(VMAIN.fxa_trancode AS INT) ELSE 0 END AS fxa_trancode,
    VMAIN.fxa_extra_aux_dom,
    VMAIN.fxa_collecting_bsb,
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
	CAST(VMAIN.fxa_amount AS int) as fxa_amount,
	VMAIN.fxa_m_entry_number,
	-- VMAIN.fxa_processing_state as fxa_site, -- Alternative to use 
	'' as fxa_run,
	VMAIN.fxa_m_bal_seq_for_deposit,
	'' as fxa_box_number,
	'' as fxa_tray_number, -- Rename
	VMAIN.fxa_m_balanced_sequence,
	'' as fxa_update_flag,
	VMAIN.fxa_adjustment_reason_code,
	SUBSTRING(VMAIN.fxa_capture_bsb,3,1) as fxa_site,
	DS.fxa_bsb as fxa_deposit_bsb,
	DS.fxa_account_number as fxa_deposit_account	
  FROM fxa_voucher_sp VMAIN
  inner join fxa_voucher_sp DS on DS.fxa_processing_date = VMAIN.fxa_processing_date 
	and DS.fxa_batch_number = VMAIN.fxa_batch_number
	and DS.fxa_tran_link_no = VMAIN.fxa_tran_link_no
  WHERE VMAIN.i_has_folder = 1 
	AND VMAIN.is_deleted = 0
	AND VMAIN.fxa_inactive_flag = 0
	AND DS.i_has_folder = 1 
	AND DS.is_deleted = 0
	AND DS.fxa_inactive_flag = 0
	AND DS.fxa_classification = CASE VMAIN.fxa_classification
									WHEN 'DR' THEN 'CR'
									ELSE 'DR'
								END
GO