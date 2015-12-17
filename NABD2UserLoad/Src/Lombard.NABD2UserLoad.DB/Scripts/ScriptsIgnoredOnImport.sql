
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

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusersynch_today]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusersynch_today]
GO

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusersynch_delta]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusersynch_delta]
GO

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id('[nabusergroupmapping]') AND OBJECTPROPERTY(id, 'IsUserTable') = 1) 
DROP TABLE [nabusergroupmapping]
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-dpr-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-dpr-manager-grp','nab_read_voucher_grp|fxa_version_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-clientservices-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-chequeservices-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-chequeservices-manager-grp','nab_read_voucher_grp|fxa_version_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-corporateservicing-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-fraud-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-fraud-manager-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-international-analyst-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-international-manager-grp','nab_read_voucher_grp')
GO

INSERT INTO [dbo].[nabusergroupmapping]([nab_user_group],[dctm_user_group]) VALUES ('au-fxa-other-analyst-grp','nab_read_voucher_grp')
GO

SET ANSI_PADDING OFF
GO

