/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]    Script Date: 8/10/2015 4:19:38 PM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]    Script Date: 8/10/2015 4:19:38 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ==================================================
-- Author:		James Honner
-- Create date: 21/08/2015
-- Description:	used for the EOD EDA Reconciliation
--EXEC [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]
--@processdate = ''20151007''
-- ==================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]  @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

--DECLARE reportCursor CURSOR FOR

	SELECT t1.record
		,fxa_processing_state
		,t1.line
	FROM (
		----header----

			SELECT FORMAT(@processdate, ''yyyyMMdd'') AS record
				,'''' AS fxa_processing_state			
				,''0'' AS line
			
		UNION ALL

		--report name file type 4 ----
					
			SELECT CONCAT (CASE WHEN SUBSTRING(t.[object_name], 31, 1) = ''2'' THEN 2
								WHEN SUBSTRING(t.[object_name], 31, 1) = ''3'' THEN 3
								WHEN SUBSTRING(t.[object_name], 31, 1) = ''4'' THEN 4
								WHEN SUBSTRING(t.[object_name], 31, 1) = ''5'' THEN 5
								WHEN SUBSTRING(t.[object_name], 31, 1) = ''6'' THEN 6
								WHEN SUBSTRING(t.[object_name], 31, 1) = ''8'' THEN 8 ELSE ''0'' END	--state number--
				,'',4,''																				--file type--
				,t.[object_name]																	--report name--	 
				,'',,,,,''																
				,''S''																				--status--
				) AS record
				,''0'' AS fxa_processing_state						
				,''2'' AS line
				FROM (SELECT DISTINCT r.[object_name]
				    FROM [dbo].[fxa_report_view] r 
					  INNER JOIN  (SELECT MAX(fxa_processing_date) fxa_processing_date
									FROM [dbo].[fxa_report_view] r1
									WHERE fxa_report_type = ''NAB EOD EDA'') r1 ON r1.fxa_processing_date = r.fxa_processing_date
					  WHERE fxa_report_type = ''NAB EOD EDA'') t
				
		UNION ALL

		--data record file type 1----

		SELECT t1.record
			,fxa_processing_state						
			,line
		FROM(	
			
			SELECT CONCAT (
				SUBSTRING(vt.[filename], 15, 1)																									--state number-
				,'',''
				,CASE WHEN vt.transmission_type IN (''VIF_ACK_OUTBOUND'', ''VIF_OUTBOUND'') THEN 1
					  WHEN v.fxa_processing_state = ''VIC'' THEN 3 ELSE '''' END																	--file type--
				,'',''
				,CONCAT(SUBSTRING(vt.[filename], 12, 3)
				,SUBSTRING(vt.[filename], 15, 1)
				,SUBSTRING(vt.[filename], 16, 2)
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')
				,SUBSTRING(vt.[filename], 28, 2)
				,''M1'')																															--filename--
				,'',''																																 																
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),SUM(CASE WHEN v.fxa_classification = ''Dr'' THEN 1 ELSE ''0'' END)),6)							--Debit Item Count(VIF only)--
				,'',''
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),SUM(CASE WHEN v.fxa_classification = ''Cr'' THEN 1 ELSE ''0'' END)),6)							--Credit Item Count(VIF only)--
				,'',''
				,RIGHT(''0000000000000''+CONVERT(NVARCHAR(16),SUM(CASE WHEN v.fxa_classification = ''Dr'' THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END)),16)	--Total Debit Amount(VIF only)--
				,'',''
				,RIGHT(''0000000000000''+CONVERT(NVARCHAR(16),SUM(CASE WHEN v.fxa_classification = ''Cr'' THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END)),16)	--Total Credit Amount(VIF only)--
				,'',''
				,CASE WHEN vt.transmission_type = ''VIF_ACK_OUTBOUND'' AND vt.[status] = ''Completed'' THEN ''S''
					  WHEN vt.transmission_type = ''VIF_ACK_OUTBOUND'' AND vt.[status] = ''Error'' THEN ''F''
					  WHEN vt.transmission_type = ''VIF_OUTBOUND'' AND vt.[status] = ''Sent'' THEN '''' ELSE '''' END 
				) AS record
				,SUBSTRING(vt.[filename], 15, 1) AS fxa_processing_state						
				,''3'' AS line
			FROM [dbo].[fxa_voucher_view] v
			INNER JOIN  (SELECT LEFT([filename],29) [filename] 
							,vt.[status]
							,vt.transmission_type
							,vt.v_i_chronicle_id
							,vt.transmission_date
							,ROW_NUMBER() OVER (PARTITION BY LEFT(filename,29),v_i_chronicle_id ORDER BY transmission_date DESC) AS Row_Num
							FROM [dbo].[fxa_voucher_transfer_view]vt
							WHERE vt.transmission_type IN (''VIF_OUTBOUND'', ''VIF_ACK_OUTBOUND'') 
								AND	vt.[status] IN (''Completed'', ''Error'', ''Sent'') 
								AND vt.[filename] <> ''''
							) vt ON vt.v_i_chronicle_id = v.i_chronicle_id
			WHERE v.fxa_inactive_flag = 0 
				AND CAST(v.fxa_processing_date AS DATE) = @processdate
				AND vt.transmission_type in (''VIF_ACK_OUTBOUND'',''VIF_OUTBOUND'')
				AND vt.[status] IN (''Completed'', ''Error'', ''Sent'')
				AND vt.Row_Num = 1
			GROUP BY SUBSTRING(vt.[filename], 15, 1)
				,SUBSTRING(vt.[filename], 12, 3)
				,vt.[status] 
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')
				,v.fxa_processing_state	
				,vt.transmission_type
				,SUBSTRING(vt.[filename], 16, 2)
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')
				,SUBSTRING(vt.[filename], 28, 2)

		UNION ALL

			SELECT CONCAT (
				CASE WHEN r.fxa_report_type = ''Adjustment Letter Zip'' 
								THEN SUBSTRING(r.[object_name], 32, 1) 
								ELSE SUBSTRING(r.[object_name], 31, 1) END					--state number-
				,'',4,''																		--file type--
				,r.[object_name]															--report name--	 
				,'',,,,,''
				,''S''																		--status--
				) AS record
				,CASE WHEN r.fxa_report_type = ''Adjustment Letter Zip'' 
								THEN SUBSTRING(r.[object_name], 32, 1) 
								ELSE SUBSTRING(r.[object_name], 31, 1) END AS fxa_processing_state						
				,''4'' AS line
			FROM [dbo].[fxa_report_view] r
			WHERE r.fxa_report_type IN (''NAB All Items Report'', ''Adjustment Letter Zip'',''BQL All Items Report'')
				AND CAST(r.fxa_processing_date AS DATE) = @processdate
				)t1
			) t1
		ORDER BY fxa_processing_state
			,t1.line
			,t1.record
			
		
	--DECLARE @repRecord varchar(200)
	--DECLARE @repProcessingState int
	--DECLARE @repLine int

	---- Truncate the report table
 --   TRUNCATE TABLE ext_EOD_EDA_DAT;


	--OPEN reportCURSOR
	--FETCH NEXT FROM reportCURSOR INTO @repRecord, @repProcessingState, @repLine

	--WHILE @@FETCH_STATUS = 0
	--BEGIN
	--    INSERT INTO [dbo].[ext_EOD_EDA_DAT]
	--	VALUES (@repRecord, @repProcessingState, @repLine);

	--    FETCH NEXT FROM reportCursor INTO @repRecord, @repProcessingState, @repLine
	--END

	--CLOSE reportCursor
	--DEALLOCATE reportCursor

END' 
END
GO
