--=============================================================================
-- Author		: Ajit Dangal
-- Create date	: 23/07/2015
-- Description	: refresh yesterday data.
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
CREATE PROCEDURE [dbo].[sp_nabusersynchdatarefresh]
AS
BEGIN

SET NOCOUNT ON;
---------- clean up yday data ----------
DELETE FROM [dbo].[nabusersynch_yesterday]
----------- copy today to yday ---------
INSERT INTO [dbo].[nabusersynch_yesterday] 
SELECT * FROM [dbo].[nabusersynch_today]
------ delete today ------
DELETE FROM [dbo].[nabusersynch_today]
END