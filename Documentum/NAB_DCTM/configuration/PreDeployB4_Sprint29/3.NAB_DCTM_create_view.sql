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
	R_.fxa_processing_date,
	R_.fxa_report_type 
FROM 
	dbo.fxa_report_s R_,
	dbo.dm_sysobject_s S_
WHERE 
	R_.r_object_id=S_.r_object_id and
	S_.i_has_folder = 1 and 
	S_.i_is_deleted = 0 
GO
ALTER TABLE [dbo].[fxa_voucher_audit] ADD [operator_name] nvarchar(50) NULL
