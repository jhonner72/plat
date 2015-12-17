---- Version History ----------------
-- Consider Sprint 26 as Baseline
-----------------------------------
--- Version 1.3. :- Date: 28 Aug 2015 - Sprint 27 Drop 1 by Ajit Dangal TFS [TBD]
--- Version 1.4. :- Date: 21 Sep 2015 - Sprint 28 Drop 1 by Ajit Dangal - Remove constraint on audit
--- Version 1.5. :- Date: 30 Sep 2015 - Sprint 29 Drop 1 by Ajit Dangal - Merge Changes for user synch
----------------------------------------

Create table report_types(type_name varchar(64))

insert into report_types values('NAB VIF Transmission Report');
insert into report_types values('BQL VIF Transmission Report');
insert into report_types values('NAB Inward FV Debit Report');
insert into report_types values('Agency Bank Inward FV DR Report');
insert into report_types values('NAB Outward For Value Credit Report');
insert into report_types values('BQL Outward For Value Credit Report');
insert into report_types values('NAB Adjustments Letter Report');
insert into report_types values('Agency Bank Adjustments Letter Report');
insert into report_types values('NAB IPOD Errors Report');
insert into report_types values('BQL IPOD Errors Report');
insert into report_types values('Agency Bank IPOD Errors Report');
insert into report_types values('NAB Adjustments Report');
insert into report_types values('NAB All Items Report');
insert into report_types values('BQL All Items Report');
insert into report_types values('NAB Incoming Suspect Fraud Report');
insert into report_types values('NAB Outgoing Suspect Fraud Report');
insert into report_types values('NAB Daily Merchant Substitutes Report');
insert into report_types values('NAB Outbound Image Exchange Report');
insert into report_types values('NAB Inbound Image Exchange Report');
insert into report_types values('NAB ECD Exceptions Report');
insert into report_types values('Agency Bank ECD Return Single Item Report');
insert into report_types values('Agency Bank ECD Return All Items Report');
insert into report_types values('Agency Bank Unprocessable Paper Report');
insert into report_types values('NAB Document Retrieval Report');
insert into report_types values('BQL Document Retrieval Report');
insert into report_types values('Agency Bank Surplus Items Report');
insert into report_types values('NAB Over $100k Report');
insert into report_types values('NAB $100M Report');
insert into report_types values('NAB Daily Listings Report');
insert into report_types values('NAB Locked Box DAILY Processing Details');
insert into report_types values('NAB Locked Box MONTHLY Processing Details');
insert into report_types values('NAB MONTHLY Volumes Report');
insert into report_types values('NAB Daily Volumes Report');
insert into report_types values('NAB Billing Report');
insert into report_types values('NAB QA Report');
insert into report_types values('NAB Document Retrieval Request Report');
insert into report_types values('FXA QA Report');


-- <<<<<<<<< fxa_voucher_audit table >>>>>>>>
IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_voucher_audit]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [fxa_voucher_audit]
go
CREATE TABLE [fxa_voucher_audit]
(
	[i_chronicle_id] nvarchar(16) NOT NULL,
	[subject_area] nvarchar(50) NOT NULL,
	[attribute_name] nvarchar(50) NOT NULL,
	[pre_value] nvarchar(50),
	[post_value] nvarchar(50),
	[operator_name] nvarchar(50),
	[modified_date] datetime NOT NULL
)
;


ALTER TABLE fxa_voucher_audit

DROP CONSTRAINT PK_fxa_voucher_audit

GO
-- <<<<<<<<fxa_file_receipt table>>>>>>>>>>>>>>>

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[fxa_file_receipt]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [fxa_file_receipt]
go

CREATE TABLE [fxa_file_receipt]
(
	[file_id] nvarchar(50) NOT NULL, -- commented  IDENTITY (1, 1),
	[filename] nvarchar(100) NOT NULL,
	[received_datetime] datetime NOT NULL,
	[transmission_datetime] datetime NOT NULL,
	[exchange] nvarchar(50) NOT NULL
)
go

ALTER TABLE [fxa_file_receipt] 
 ADD CONSTRAINT [PK_fxa_file_receipt]
	PRIMARY KEY CLUSTERED ([file_id])
go

ALTER TABLE [fxa_file_receipt] 
 ADD CONSTRAINT [UQ_filename] UNIQUE NONCLUSTERED ([filename])
go

-- Yogesh, please review the following code and advise.
-- ALTER TABLE [fxa_voucher] ADD CONSTRAINT [FK_fxa_voucher_fxa_file_receipt]
-- 	FOREIGN KEY ([fxa_file_receipt_id]) REFERENCES [fxa_file_receipt] ([id]) ON DELETE No Action ON UPDATE No Action
-- ;

/****** Object:  Table [dbo].[nabusersynch] 
*@ Author : Ajit Dangal
 Creat required table for user synchronisation and mapping tables
  Script Date: 7/22/2015 7:08:59 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO
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
/****** Object:  StoredProcedure [dbo].[sp_nabusersynch]    Script Date: 8/14/2015 10:15:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--=============================================================================
-- Author		: Ajit Dangal
-- Create date	: 23/07/2015
-- Description	: Update the status of the documentum users.
--
-- Comment		: Relies upon a nightly load and a previous load
--				  
--
-- History	  	:
-------------------------------------------------------------------------------
-- Date			Name		Comment	
-------------------------------------------------------------------------------
-- 23/07/2015	Ajit		Created	
--=============================================================================
CREATE PROCEDURE [dbo].[sp_nabusersynch]
AS
BEGIN

SET NOCOUNT ON;

DELETE FROM [dbo].[nabusersynch_delta]
      WHERE data_date < getDate() - 7


INSERT INTO [dbo].[nabusersynch_delta]
           ([user_id]
           ,[first_name]
           ,[last_name]
           ,[user_email]
           ,[user_group]
           ,[data_date],[ops_type])


---------------------------------------------------------------------------------
--INSERT

SELECT [user_id]
      ,[first_name]
      ,[last_name]
      ,[user_email]
      ,[user_group], getDate() as data_date ,'INSERT' as ops_type
  FROM [dbo].[nabusersynch_today] 
  where 
  [dbo].[nabusersynch_today].user_id in
 ( 
 ( 
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_today] 
 except
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_yesterday] 
 ) 
 )
 UNION

---------------------------------------------------------------------------------
--UPDATE
 (

 SELECT [user_id]
      ,[first_name]
      ,[last_name]
      ,[user_email]
      ,[user_group],getDate() as data_date ,'UPDATE' as ops_type
  FROM [dbo].[nabusersynch_today] 
   where 
[dbo].[nabusersynch_today].user_id not in
 ( 
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_today] 
 except
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_yesterday] 
 ) 
  except
  SELECT [user_id]
      ,[first_name]
      ,[last_name]
      ,[user_email]
      ,[user_group],getDate() as data_date ,'UPDATE' as ops_type
  FROM [dbo].[nabusersynch_yesterday] 

 )
 Union
 ---------------------------------------------------------------------------------
--DELETE 

 SELECT [user_id]
      ,[first_name]
      ,[last_name]
      ,[user_email]
      ,[user_group], getDate() as data_date ,'DELETE' as ops_type
  FROM [dbo].[nabusersynch_yesterday] 
  where 
  [dbo].[nabusersynch_yesterday].user_id in
 ( 
 ( 
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_yesterday] 
 except
 SELECT [user_id] as user_id
 FROM [dbo].[nabusersynch_today] 
 ) 
 )

 ; 
 ----------------------------------------------------------------------------------
 --End of Delta fill 

END








