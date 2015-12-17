ALTER VIEW fxa_voucher_im_view AS
SELECT
  r_object_id,
  r_content_size,
  a_storage_type,
  fxa_tran_link_no,
  fxa_batch_number,
  fxa_m_cr_drn,
  fxa_drn,
  fxa_classification,
  fxa_processing_date,
  fxa_bsb,
  fxa_account_number,
  fxa_aux_dom,
  CASE WHEN ISNUMERIC(fxa_trancode) = 1 THEN CAST(fxa_trancode AS INT) ELSE 0 END AS fxa_trancode,
  fxa_extra_aux_dom,
  fxa_collecting_bsb,
  CASE fxa_processing_state WHEN 'NSW' THEN '2' WHEN 'ACT' THEN '2' WHEN 'VIC' THEN '3' WHEN 'QLD' THEN '4' WHEN 'SA' THEN '5' WHEN 'NT' THEN '5' WHEN 'WA' THEN '6' WHEN 'TAS' THEN '7' ELSE CASE SUBSTRING(fxa_collecting_bsb,3,1) WHEN '' THEN '0' WHEN NULL THEN '0' END END AS fxa_processing_state,
  CASE WHEN ISNUMERIC(fxa_amount) = 1 THEN CAST(fxa_amount AS DECIMAL(13,0)) ELSE 0 END as fxa_amount
FROM fxa_voucher_sp
  WHERE i_has_folder = 1 and i_is_deleted = 0
GO