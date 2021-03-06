/****** Object:  StoredProcedure [dbo].[sp_nabusersynch]    Script Date: 26/08/2015 4:47:02 PM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_nabusersynch]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[sp_nabusersynch]
GO
/****** Object:  StoredProcedure [dbo].[sp_nabusersynch]    Script Date: 26/08/2015 4:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_nabusersynch]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
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
Create PROCEDURE [dbo].[sp_nabusersynch]
AS
BEGIN

SET NOCOUNT ON;

DELETE FROM [dbo].[nabusersynch_delta]
      WHERE data_date < getDate() - 5


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
      ,[user_group], getDate() as data_date ,''INSERT'' as ops_type
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
      ,[user_group],getDate() as data_date ,''UPDATE'' as ops_type
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
      ,[user_group],getDate() as data_date ,''UPDATE'' as ops_type
  FROM [dbo].[nabusersynch_yesterday] 

 )
 Union
 ---------------------------------------------------------------------------------
--DELETE 

 SELECT [user_id]
      ,[first_name]
      ,[last_name]
      ,[user_email]
      ,[user_group], getDate() as data_date ,''DELETE'' as ops_type
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

' 
END
GO
/****** Object:  Table [dbo].[nabusergroupmapping]    Script Date: 26/08/2015 4:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[nabusergroupmapping]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[nabusergroupmapping](
	[nab_user_group] [varchar](100) NOT NULL,
	[dctm_user_group] [varchar](100) NOT NULL
) ON [PRIMARY]

SET ANSI_PADDING OFF

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-dpr-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-dpr-manager-grp', N'nab_read_voucher_grp|fxa_version_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-clientservices-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-chequeservices-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-chequeservices-manager-grp', N'nab_read_voucher_grp|fxa_version_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-corporateservicing-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-fraud-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-fraud-manager-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-international-analyst-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-international-manager-grp', N'nab_read_voucher_grp')

INSERT [dbo].[nabusergroupmapping] ([nab_user_group], [dctm_user_group]) VALUES (N'au-fxa-other-analyst-grp', N'nab_read_voucher_grp')

END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[nabusersynch_delta]    Script Date: 26/08/2015 4:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[nabusersynch_delta]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[nabusersynch_delta](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](200) NULL,
	[data_date] [date] NULL,
	[ops_type] [varchar](20) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[nabusersynch_today]    Script Date: 26/08/2015 4:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[nabusersynch_today]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[nabusersynch_today](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](300) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[nabusersynch_yesterday]    Script Date: 26/08/2015 4:47:02 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[nabusersynch_yesterday]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[nabusersynch_yesterday](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](300) NULL
) ON [PRIMARY]
END

