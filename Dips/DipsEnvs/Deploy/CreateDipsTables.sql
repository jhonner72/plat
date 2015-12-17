USE [NabChq]
GO

/****** Object:  Table [dbo].[queue]    Script Date: 8/04/2015 11:48:26 AM ******/
DROP TABLE [dbo].[queue]
GO

/****** Object:  Table [dbo].[NabChqScan]    Script Date: 8/04/2015 11:48:26 AM ******/
DROP TABLE [dbo].[NabChqScan]
GO

/****** Object:  Table [dbo].[NabChq]    Script Date: 8/04/2015 11:48:26 AM ******/
DROP TABLE [dbo].[NabChq]
GO

/****** Object:  Table [dbo].[DB_INDEX]    Script Date: 8/04/2015 11:48:26 AM ******/
DROP TABLE [dbo].[DB_INDEX]
GO

/****** Object:  Table [dbo].[DB_INDEX]    Script Date: 8/04/2015 11:48:26 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[DB_INDEX](
	[DEL_IND] [char](5) NULL,
	[BATCH] [char](8) NULL,
	[TRACE] [char](9) NULL,
	[SEQUENCE] [char](5) NULL,
	[TABLE_NO] [char](5) NULL,
	[REC_NO] [char](10) NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[NabChq]    Script Date: 8/04/2015 11:48:26 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[NabChq](
	[S_DEL_IND] [char](5) NULL,
	[S_BATCH] [char](8) NULL,
	[S_MODIFIED] [char](5) NULL,
	[S_COMPLETE] [char](5) NULL,
	[S_TYPE] [char](5) NULL,
	[S_STATUS1] [char](10) NULL,
	[S_STATUS2] [char](10) NULL,
	[S_STATUS3] [char](10) NULL,
	[S_STATUS4] [char](10) NULL,
	[S_IMG1_OFF] [char](10) NULL,
	[S_IMG1_LEN] [char](10) NULL,
	[S_IMG1_TYP] [char](5) NULL,
	[S_IMG2_OFF] [char](10) NULL,
	[S_IMG2_LEN] [char](10) NULL,
	[S_IMG2_TYP] [char](5) NULL,
	[S_TRACE] [char](9) NULL,
	[S_LENGTH] [char](5) NULL,
	[S_SEQUENCE] [char](5) NULL,
	[S_BALANCE] [char](10) NULL,
	[S_REPROCESS] [char](5) NULL,
	[S_REPORTED] [char](5) NULL,
	[S_COMMITTED] [char](5) NULL,
	[batch] [char](8) NULL,
	[trace] [char](9) NULL,
	[sys_date] [char](8) NULL,
	[proc_date] [char](8) NULL,
	[ead] [char](11) NULL,
	[ser_num] [char](9) NULL,
	[bsb_num] [char](6) NULL,
	[acc_num] [char](21) NULL,
	[trancode] [char](3) NULL,
	[amount] [char](15) NULL,
	[pocket] [char](2) NULL,
	[payee_name] [char](240) NULL,
	[manual_repair] [char](5) NULL,
	[doc_type] [char](3) NULL,
	[rec_type_id] [char](4) NULL,
	[collecting_bank] [char](6) NULL,
	[unit_id] [char](3) NULL,
	[man_rep_ind] [char](1) NULL,
	[proof_seq] [char](12) NULL,
	[trans_seq] [char](5) NULL,
	[delay_ind] [char](1) NULL,
	[fv_exchange] [char](1) NULL,
	[adj_code] [char](2) NULL,
	[adj_desc] [char](30) NULL,
	[op_id] [char](15) NULL,
	[proc_time] [char](4) NULL,
	[override] [char](5) NULL,
	[fv_ind] [char](1) NULL,
	[host_trans_no] [char](3) NULL,
	[job_id] [char](15) NULL,
	[volume] [char](8) NULL,
	[img_location] [char](80) NULL,
	[img_front] [char](8) NULL,
	[img_rear] [char](8) NULL,
	[held_ind] [char](1) NULL,
	[receiving_bank] [char](3) NULL,
	[ie_transaction_id] [char](12) NULL,
	[batch_type] [char](20) NULL,
	[sub_batch_type] [char](20) NULL,
	[doc_ref_num] [char](9) NULL,
	[raw_micr] [char](64) NULL,
	[raw_ocr] [char](64) NULL,
	[processing_state] [char](3) NULL,
	[micr_flag] [char](1) NULL,
	[micr_unproc_flag] [char](1) NULL,
	[micr_suspect_fraud_flag] [char](1) NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[NabChqScan]    Script Date: 8/04/2015 11:48:26 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[NabChqScan](
	[S_DEL_IND] [char](5) NULL,
	[S_BATCH] [char](8) NULL,
	[S_MODIFIED] [char](5) NULL,
	[S_COMPLETE] [char](5) NULL,
	[S_TYPE] [char](5) NULL,
	[S_STATUS1] [char](10) NULL,
	[S_STATUS2] [char](10) NULL,
	[S_STATUS3] [char](10) NULL,
	[S_STATUS4] [char](10) NULL,
	[S_IMG1_OFF] [char](10) NULL,
	[S_IMG1_LEN] [char](10) NULL,
	[S_IMG1_TYP] [char](5) NULL,
	[S_IMG2_OFF] [char](10) NULL,
	[S_IMG2_LEN] [char](10) NULL,
	[S_IMG2_TYP] [char](5) NULL,
	[S_TRACE] [char](9) NULL,
	[S_LENGTH] [char](5) NULL,
	[S_SEQUENCE] [char](5) NULL,
	[S_BALANCE] [char](10) NULL,
	[S_REPROCESS] [char](5) NULL,
	[S_REPORTED] [char](5) NULL,
	[S_COMMITTED] [char](5) NULL,
	[batch] [char](8) NULL,
	[trace] [char](9) NULL,
	[sys_date] [char](8) NULL,
	[proc_date] [char](8) NULL,
	[ead] [char](11) NULL,
	[ser_num] [char](9) NULL,
	[bsb_num] [char](6) NULL,
	[acc_num] [char](21) NULL,
	[trancode] [char](3) NULL,
	[amount] [char](15) NULL,
	[pocket] [char](2) NULL,
	[manual_repair] [char](5) NULL,
	[doc_type] [char](3) NULL,
	[rec_type_id] [char](4) NULL,
	[collecting_bank] [char](6) NULL,
	[unit_id] [char](3) NULL,
	[man_rep_ind] [char](1) NULL,
	[proof_seq] [char](12) NULL,
	[trans_seq] [char](5) NULL,
	[delay_ind] [char](1) NULL,
	[op_id] [char](15) NULL,
	[proc_time] [char](4) NULL,
	[override] [char](5) NULL,
	[job_id] [char](15) NULL,
	[volume] [char](8) NULL,
	[img_location] [char](80) NULL,
	[img_front] [char](8) NULL,
	[img_rear] [char](8) NULL,
	[receiving_bank] [char](3) NULL,
	[batch_type] [char](20) NULL,
	[sub_batch_type] [char](20) NULL,
	[doc_ref_num] [char](9) NULL,
	[raw_micr] [char](64) NULL,
	[raw_ocr] [char](64) NULL,
	[processing_state] [char](3) NULL,
	[micr_flag] [char](1) NULL,
	[micr_unproc_flag] [char](1) NULL,
	[micr_suspect_fraud_flag] [char](1) NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[queue]    Script Date: 8/04/2015 11:48:26 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[queue](
	[S_LOCATION] [char](33) NULL,
	[S_PINDEX] [char](16) NULL,
	[S_LOCK] [char](10) NULL,
	[S_CLIENT] [char](80) NULL,
	[S_JOB_ID] [char](128) NULL,
	[S_MODIFIED] [char](5) NULL,
	[S_COMPLETE] [char](5) NULL,
	[S_BATCH] [char](8) NULL,
	[S_TRACE] [char](9) NULL,
	[S_SDATE] [char](8) NULL,
	[S_STIME] [char](8) NULL,
	[S_UTIME] [char](10) NULL,
	[S_PRIORITY] [char](5) NULL,
	[S_IMG_PATH] [char](80) NULL,
	[S_USERNAME] [char](250) NULL,
	[S_SELNSTRING] [char](128) NULL,
	[S_VERSION] [char](32) NULL,
	[S_LOCKUSER] [char](17) NULL,
	[S_LOCKMODULENAME] [char](17) NULL,
	[S_LOCKUNITID] [char](10) NULL,
	[S_LOCKMACHINENAME] [char](17) NULL,
	[S_LOCKTIME] [char](10) NULL,
	[S_PROCDATE] [char](9) NULL,
	[S_REPORTED] [char](5) NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


