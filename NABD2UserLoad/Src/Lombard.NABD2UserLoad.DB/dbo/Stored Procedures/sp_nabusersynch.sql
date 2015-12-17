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
      WHERE data_date < getDate() - 0


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