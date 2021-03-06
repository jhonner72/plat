/****** Object:  Synonym [dbo].[voucher_transfer]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher_transfer' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[voucher_transfer]
GO
/****** Object:  Synonym [dbo].[voucher_audit]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher_audit' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[voucher_audit]
GO
/****** Object:  Synonym [dbo].[voucher]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[voucher]
GO
/****** Object:  Synonym [dbo].[reports]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'reports' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[reports]
GO
/****** Object:  Synonym [dbo].[listing_view]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'listing_view' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[listing_view]
GO
/****** Object:  Synonym [dbo].[file_receipt]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'file_receipt' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[file_receipt]
GO
/****** Object:  Synonym [dbo].[document]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'document' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[document]
GO
/****** Object:  Synonym [dbo].[dishonour]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.synonyms WHERE name = N'dishonour' AND schema_id = SCHEMA_ID(N'dbo'))
DROP SYNONYM [dbo].[dishonour]
GO
/****** Object:  StoredProcedure [dbo].[usp_Sequence_Number_Update]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Sequence_Number_Update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_Sequence_Number_Update]
GO
/****** Object:  StoredProcedure [dbo].[usp_Sequence_Number_Reset]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Sequence_Number_Reset]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_Sequence_Number_Reset]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_VIF_Transmission]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_VIF_Transmission]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_VIF_Transmission]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_QA]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_QA]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_QA]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Over_100K]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Over_100K]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Over_100K]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outward_FV_CR]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outward_FV_CR]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Outward_FV_CR]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outbound_IE]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outbound_IE]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Outbound_IE]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Agency]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Agency]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Agency]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Locked_Box_Monthly]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Locked_Box_Monthly]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Locked_Box_Monthly]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Locked_Box_Daily]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Locked_Box_Daily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Locked_Box_Daily]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_IPOD_Errors]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_IPOD_Errors]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Inward_FV_DR]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Inward_FV_DR]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Inward_FV_DR]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Inbound_IE]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Inbound_IE]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Inbound_IE]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_High_Value]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_High_Value]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_High_Value]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_ECD_Exceptions]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_ECD_Exceptions]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_ECD_Exceptions]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Document_Retrieval]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Document_Retrieval]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Document_Retrieval]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Merchants]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Merchants]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Merchants]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Delivery]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Delivery]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Delivery]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_AustPost]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_AustPost]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_AustPost]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Agency]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Agency]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Agency]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Listings]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Listings]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Daily_Listings]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_LockedBox]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_LockedBox]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_FV]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_FV]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_FV]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Dishonours]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Dishonours]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_Dishonours]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Clearings_Outward]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Clearings_Outward]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_Clearings_Outward]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Clearings_Inward]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Clearings_Inward]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_Clearings_Inward]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Capture]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Capture]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_Capture]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Adjustments]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Adjustments]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Adjustments]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Adjustment_Letter]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Adjustment_Letter]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_NAB_Adjustment_Letter]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_FXA_QA]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_FXA_QA]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_FXA_QA]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_FXA_OCR_vs_Human]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_FXA_OCR_vs_Human]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_FXA_OCR_vs_Human]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Corporate_Adjustment_Letter]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Corporate_Adjustment_Letter]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Corporate_Adjustment_Letter]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_VIF_Transmission]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_VIF_Transmission]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_BQL_VIF_Transmission]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_Outward_FV_CR]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_Outward_FV_CR]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_BQL_Outward_FV_CR]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_IPOD_Errors]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_BQL_IPOD_Errors]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_Document_Retrieval]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_Document_Retrieval]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_BQL_Document_Retrieval]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_All_Items_Summary]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_All_Items_Summary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_All_Items_Summary]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_All_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_All_Items]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_All_Items]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Unprocessable_Paper]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Unprocessable_Paper]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_Unprocessable_Paper]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Surplus_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Surplus_Items]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_Surplus_Items]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_IPOD_Errors]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_IPOD_Errors]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Inward_FV_DR]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Inward_FV_DR]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_Inward_FV_DR]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_ECD_Return_All_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_ECD_Return_All_Items]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_ECD_Return_All_Items]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_ECD_Exception_Single_Item]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Bank_List]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Bank_List]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_Bank_List]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Adjustment]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Adjustment]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_rpt_Agency_Adjustment]
GO
/****** Object:  StoredProcedure [dbo].[usp_Get_File_Logs]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Get_File_Logs]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_Get_File_Logs]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]    Script Date: 13/01/2016 8:03:41 AM ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ================================================================================
-- Author:		James Honner
-- Create date: 21/08/2015
-- Description:	used for the EOD COIN Reconciliation
--EXEC [dbo].[usp_ext_NAB_EOD_COIN_Reconciliation]
--@processdate = ''20151104'',@financialinstitution = ''ANZ'', @systemdate = ''20151104''
-- ================================================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_EOD_COIN_Reconciliation] @processdate DATE, @financialinstitution NVARCHAR(3), @systemdate  NVARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;

			SELECT DISTINCT CAST(RIGHT(SPACE(70) + vt.[filename],(70)) AS NVARCHAR(70)) AS record
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
			WHERE v.fxa_inactive_flag = 0 
				AND vt.transmission_type = ''IMAGE_EXCHANGE_OUTBOUND''
				AND vt.[filename] LIKE ''IMGEXCH%''
				AND CAST(v.fxa_processing_date AS DATE) = @processdate
				AND SUBSTRING (vt.[filename],55,3) = @financialinstitution
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- ==================================================
-- Author:		James Honner
-- Create date: 21/08/2015
-- Description:	used for the EOD EDA Reconciliation
--EXEC [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation]
--@processdate = ''20151217''
-- ==================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_EOD_EDA_Reconciliation] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT t1.record
		,fxa_processing_state
		,t1.line
	FROM (
		----header----

			SELECT FORMAT(@processdate, ''yyyyMMdd'') AS record
				,'''' AS fxa_processing_state			
				,''0'' AS line
			
		UNION ALL

		----report name file type 4 ----
					
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
					FROM [dbo].[reports] r 
					INNER JOIN  (SELECT MAX(fxa_processing_date) fxa_processing_date
								FROM [dbo].[reports] r1
								WHERE fxa_report_type = ''NAB EOD EDA'') r1 ON r1.fxa_processing_date = r.fxa_processing_date
					WHERE fxa_report_type = ''NAB EOD EDA'') t
				
		UNION ALL

		----data record file type 1----

		SELECT t1.record
			,fxa_processing_state						
			,line
		FROM(	
			
			SELECT CONCAT (
				SUBSTRING(vt.[filename], 15, 1)																									--state number--
				,'',''
				,CASE WHEN vt.transmission_type IN (''VIF_ACK_OUTBOUND'', ''VIF_OUTBOUND'') THEN 1
					  WHEN v.fxa_processing_state = ''VIC'' THEN 3 ELSE '''' END																	--file type--
				,'',''
				,CONCAT(SUBSTRING(vt.[filename], 12, 6)
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
				--,CASE WHEN vt.transmission_type = ''VIF_ACK_OUTBOUND'' AND vt.[status] = ''Completed'' THEN ''S''
				--	  WHEN vt.transmission_type = ''VIF_ACK_OUTBOUND'' AND vt.[status] = ''Error'' THEN ''F''
				--	  WHEN vt.transmission_type = ''VIF_OUTBOUND'' AND vt.[status] = ''Sent'' THEN '''' ELSE '''' END 
				,CASE WHEN vt.[status] = ''Completed'' THEN ''S'' ELSE ''F'' END 
				) AS record
				,SUBSTRING(vt.[filename], 15, 1) AS fxa_processing_state						
				,''3'' AS line
			FROM [dbo].[voucher] v
			INNER JOIN  (SELECT LEFT([filename],29) [filename] 
							,vt.[status]
							,vt.transmission_type
							,vt.v_i_chronicle_id
							,vt.transmission_date
							,ROW_NUMBER() OVER (PARTITION BY LEFT(filename,29),v_i_chronicle_id ORDER BY transmission_type) AS Row_Num
						FROM [dbo].[voucher_transfer] vt
						WHERE vt.transmission_type IN (''VIF_OUTBOUND'', ''VIF_ACK_OUTBOUND'') 
							AND	vt.[status] IN (''Completed'', ''Error'', ''Sent'') 
							AND vt.[filename] <> ''''
							) vt ON vt.v_i_chronicle_id = v.i_chronicle_id AND vt.Row_Num = 1
			WHERE v.fxa_inactive_flag = 0 
				AND CAST(v.fxa_processing_date AS DATE) = @processdate
			GROUP BY SUBSTRING(vt.[filename], 15, 1)
				,SUBSTRING(vt.[filename], 12, 6)
				,vt.[status] 
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')
				,v.fxa_processing_state	
				,vt.transmission_type
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')
				,SUBSTRING(vt.[filename], 28, 2)

		UNION ALL

		----data record file type 3----

			SELECT CONCAT (													
				CASE WHEN SUBSTRING(ft.[file_name], 21, 1) = ''2'' THEN ''2,'' 
					 WHEN SUBSTRING(ft.[file_name], 21, 1) = ''3'' THEN ''3,''
				     WHEN SUBSTRING(ft.[file_name], 21, 1) = ''4'' THEN ''4,''
					 WHEN SUBSTRING(ft.[file_name], 21, 1) = ''5'' THEN ''5,''
					 WHEN SUBSTRING(ft.[file_name], 21, 1) = ''6'' THEN ''6,''																		
				ELSE ''0,'' END															--state--													
				,''3,''																	--file type--
				,LEFT(ft.[file_name],22)												--report name--	 
				,'',,,,,''
				,''S''																	--status--
				) AS record
				,CASE WHEN SUBSTRING(ft.[file_name], 21, 1) = ''2'' THEN ''2'' 
					  WHEN SUBSTRING(ft.[file_name], 21, 1) = ''3'' THEN ''3''
				      WHEN SUBSTRING(ft.[file_name], 21, 1) = ''4'' THEN ''4''
					  WHEN SUBSTRING(ft.[file_name], 21, 1) = ''5'' THEN ''5''
					  WHEN SUBSTRING(ft.[file_name], 21, 1) = ''6'' THEN ''6''	
				ELSE ''0'' END AS fxa_processing_state						
				,''4'' AS line
			FROM [dbo].[ref_file_transmission] ft
			WHERE ft.[file_name] LIKE ''%ECL.FILE%''
				AND CAST(ft.[file_timestamp] AS DATE) = @processdate

		UNION ALL

		----data record file type 3 and 4----

			SELECT CONCAT (
				CASE WHEN r.fxa_report_type = ''Adjustment Letter Zip'' 
					 THEN SUBSTRING(r.[object_name], 32, 1) 
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%'' 
						AND r.[object_name] = ''MO.IMLC001.VR3.FILE01'' THEN ''2''
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%'' 
						AND r.[object_name] IN (''MO.IMLC001.VR3.FILE01'', ''MO.IRDS001.VR3.FILE01'', ''MO.IRDNS01.VR3.FILE01'', ''MO.INAB001.VR3.FILE01'') THEN ''3''
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%'' 
						AND r.[object_name] NOT IN (''MO.IMLC001.VR3.FILE01'', ''MO.IRDS001.VR3.FILE01'', ''MO.IRDNS01.VR3.FILE01'', ''MO.INAB001.VR3.FILE01'') 
					 THEN lb.[state] ELSE SUBSTRING(r.[object_name], 31, 1) END				--state number-
				,CASE WHEN r.fxa_report_type LIKE ''%Locked Box%'' THEN '',3,'' 
					 ELSE '',4,''	END															--file type--
				,r.[object_name]															--report name--	 
				,'',,,,,''
				,''S''																		--status--
				) AS record
				,CASE WHEN r.fxa_report_type = ''Adjustment Letter Zip'' 
					 THEN SUBSTRING(r.[object_name], 32, 1) 
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%''
					 	AND r.[object_name] = ''MO.IMLC001.VR3.FILE01'' THEN ''2''
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%'' 
						AND r.[object_name] IN (''MO.IMLC001.VR3.FILE01'', ''MO.IRDS001.VR3.FILE01'', ''MO.IRDNS01.VR3.FILE01'', ''MO.INAB001.VR3.FILE01'') THEN ''3''
					 WHEN r.fxa_report_type	LIKE ''%Locked Box%'' 
						AND r.[object_name] NOT IN (''MO.IMLC001.VR3.FILE01'', ''MO.IRDS001.VR3.FILE01'', ''MO.IRDNS01.VR3.FILE01'', ''MO.INAB001.VR3.FILE01'') 
					 THEN lb.[state] ELSE SUBSTRING(r.[object_name], 31, 1) END AS fxa_processing_state						
				,''4'' AS line
			FROM [dbo].[reports] r
			LEFT JOIN [dbo].[ref_locked_box] lb ON lb.[file_name] = r.[object_name]
			WHERE r.fxa_report_type IN (''NAB All Items Report'', ''Adjustment Letter Zip'',''BQL All Items Report'', ''NAB Locked Box Extract VR1'', ''NAB Locked Box Extract VR3'', ''NAB Locked Box Extract VR8 CGU'',
					''NAB Locked Box Extract VR4 NAB'', ''NAB Locked Box Extract VR8 MLC'', ''NAB Locked Box Extract VR4 OWKS'', ''NAB Locked Box Extract VR4 EANA'', ''NAB Locked Box Extract VR2'')
				AND CAST(r.fxa_processing_date AS DATE) = @processdate
				)t1
			) t1
		ORDER BY fxa_processing_state
			,t1.line
			,t1.record
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 05/08/2015
-- Description:	used for the NAB Locked Box Extract Files
--EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR1]
--@businessdate = ''20151130'',@lockedbox = ''LNC1''
---- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR1] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

		----header----

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_drn
	,t1.sug_key
FROM (SELECT DISTINCT CONCAT (
			''A''													--record type--
			,FORMAT(v.fxa_processing_date, ''yyyy'')				--processing year--
			,FORMAT(v.fxa_processing_date, ''MMdd'')				--processing month day--
			,FORMAT(v.fxa_processing_date, ''hhmmss'')			--processing time--
			,''1''												--file type--
			,''000''												--upload sequence number--
			,''31''												--site number--
			,CAST(RIGHT(SPACE(58) + '''',(58)) AS NVARCHAR(58))	--filler--
			) AS record
			,''1'' AS line
			,'''' AS line2
			,v.fxa_batch_type_code AS customer_id
			,'''' AS fxa_account_number
			,'''' AS fxa_drn
			,'''' AS sug_key
		FROM [dbo].[voucher] v
		WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
			AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----stub line 1----
		
		SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,t1.fxa_drn
			,t1.sug_key
		FROM (SELECT CONCAT (
				''F''																				--record type--
				,''1''																			--line number--
				,''0''																			--source document number--
				,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_batch_number),10)				--capture batch number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_drn,6)),6)						--image DIN--
				,''C''																			--DC code--
				,RIGHT(''00000000000''+CONVERT(NVARCHAR(11),ABS(v.fxa_amount)),11)				--Transaction Amount--
				,''000000000''																	--Merchant ID--
				,''000000''																		--bsb--
				,''3''																			--state of origin--
				,''0000000000000000''																--acc number--
				,''000000''																		--aux dom--
				,''0''																			--organisation number--
				,CAST(RIGHT(SPACE(9) + '''',(9)) AS NVARCHAR(9))									--filler--
				) AS record						
				,''2'' AS line
				,''1'' AS line2
				,v.fxa_batch_type_code AS customer_id
				,v.fxa_account_number
				,v.fxa_drn
				,'''' AS sug_key
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX'' 
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Cr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate

		UNION ALL

		----stub line 2 ----

			SELECT CONCAT (
				''F''																					--record type--
				,''2''																				--line number--
				,RIGHT(CAST(YEAR(v.fxa_processing_date) AS NVARCHAR(4)),1) + RIGHT(''000'' + CAST(DATEPART(dy,v.fxa_processing_date) AS NVARCHAR(3)),3)
				,''66''																				--capture transport ID--
			--	,RIGHT(''00''+CONVERT(NVARCHAR(2),vt.run_number),2)									--run number--no VIF for cc
				,CASE WHEN vt.run_number IS NULL THEN ''00'' ELSE vt.run_number END 
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_drn,6)),6)							--sequence number--
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_extra_aux_dom),16)				--acc number-- 
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')											--processing year--					
				,''000000''																			--authorisation number--
				,''00000''																			--item count--
				,CAST(RIGHT(SPACE(28) + '''',(28)) AS NVARCHAR(28))									--filler--
				) AS record							
				,''2'' AS line
				,''2'' AS line2
				,v.fxa_batch_type_code AS customer_id
				,v.fxa_account_number
				,v.fxa_drn
				,CONCAT(v.fxa_batch_number,CAST(fxa_processing_date AS DATE)) sug_key
			FROM [dbo].[voucher] v
			LEFT JOIN  (SELECT DISTINCT SUBSTRING(vt.[filename], 28, 2) AS run_number
							,CONCAT(v.fxa_batch_number,CAST(fxa_processing_date AS DATE)) sug_key
						FROM [dbo].[voucher] v
						INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
						WHERE v.fxa_work_type_code = ''NABCHQ_LBOX''
							AND vt.transmission_type = ''VIF_OUTBOUND'' 
							AND vt.[status] = ''Sent''
							AND vt.[filename] <> ''''
							AND vt.[filename] <> ''Testing''
							AND CAST(vt.transmission_date AS DATE) = @businessdate
							) vt ON vt.sug_key = CONCAT(v.fxa_batch_number,CAST(fxa_processing_date AS DATE))
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Cr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_processing_date
				,v.fxa_drn
				,v.fxa_extra_aux_dom
				,v.fxa_batch_type_code
				,v.fxa_account_number
				,CONCAT(v.fxa_batch_number,CAST(fxa_processing_date AS DATE))
				,vt.run_number
				) t1

		UNION ALL

		----Debit Clearing Entry Line 1--

			SELECT CONCAT (
				''B''																	--record type--
				,''1''																--line number--
				,''0''																--source document number--
				,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),RIGHT(v.fxa_batch_number,10)),10)	--batch number--
				,''999999''															--item number--
				,''D''																--DC code-
				,RIGHT(''00000000000''+CONVERT(NVARCHAR(11),SUM(ABS(v.fxa_amount))),11)--total stub amount--
				,''000000000''														--merchant ID--
				,''083996''															--bsb--
				,''3''																--state of origin--
				,''0000000455464506''													--acc number--
				,''0''																--auxdom--	
				,FORMAT(v.fxa_processing_date, ''dd'')								--day of month--
				,''002''																--lockbox number--
				,''0''																--merchant organisation number--
				,CAST(RIGHT(SPACE(9) + ''VR Debit'',(9)) AS NVARCHAR(9))				--descriptive text--
				) AS record	
				,''4'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
				,'''' AS sug_key
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_batch_number
				,v.fxa_processing_date
				,v.fxa_batch_type_code

		UNION ALL

		----Debit Clearing Entry Line 2----

			SELECT DISTINCT CONCAT (
				''B''																					--record type--
				,''2''																				--line number--
				,RIGHT(CAST(YEAR(v.fxa_processing_date) AS NVARCHAR(4)),1) + RIGHT(''000'' + CAST(DATEPART(dy,v.fxa_processing_date) AS NVARCHAR(3)),3)
				,''00''																				--trace field 1--
				,''00''																				--trace field 2--
				,''000002''																			--lockbox number--
				,''0000000000000000''																	--acc number--
				,''00000000''																			--transaction date--
				,''000000''																			--authorisation number--
				,''00000''																			--item count--
				,CAST(RIGHT(SPACE(28) + ''Transmission Clearing Entry'',(28)) AS NVARCHAR(28))		--descriptive text--
				) AS record	
				,''5'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
				,'''' AS sug_key
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate

		UNION ALL

		----trailer----

			SELECT CONCAT (
				''Y''																		--record type--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
								  THEN 1 ELSE 0 END)),6)								--Number of detail transactions in the file (F1 records)--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
						  THEN 1 ELSE 0 END)),6)										--Number of Credits in the file (F2 Records)--
				,''000001''																--Number of Debits in the file – always 000001--
				,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
					,SUM(CASE WHEN v.fxa_classification = ''Cr''
							  THEN ABS(v.fxa_amount) ELSE 0 END)),12)					--Total value of Credits in the file (F1 Records)--
				,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
			  				 THEN ABS(v.fxa_amount) ELSE 0 END)),12)					--Total value of Debits in the file (B1 Record)--
				,CAST(RIGHT(SPACE(36) + '''',(36)) AS NVARCHAR(36))						--filler--
				) AS record 
				,''6'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
				,'''' AS sug_key
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification <> ''Bh''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_batch_type_code 
				) t1
GROUP BY t1.record, t1.line, t1.line2, t1.fxa_drn, t1.customer_id, t1.fxa_account_number, t1.sug_key
ORDER BY t1.line, t1.fxa_drn, t1.line2;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 14/08/2015
-- Description:	used for the NAB Locked Box Extract Files
--EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR2]
--@businessdate = ''20151110'',@lockedbox = ''LML2''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR2] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_drn
FROM (		
		----header----
		
			SELECT DISTINCT CONCAT (
				''A''													--record type--
				,FORMAT(v.fxa_processing_date, ''yyyy'')				--processing year--
				,FORMAT(v.fxa_processing_date, ''MMdd'')				--processing month day--
				,FORMAT(GETDATE(), ''hhmmss'')						--processing time--
				,''1''												--file type--
				,''001''												--upload sequence number--
				,CASE WHEN lb.bsb = ''991202'' THEN ''21''
					  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''31'' ELSE ''00'' END	--site number--
				,CAST(RIGHT(SPACE(58) + '''',(58)) AS NVARCHAR(58))	--filler--
				) AS record
				,''1'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate

		UNION ALL

		----Clearing Warrant Line 1----

		SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,t1.fxa_drn
		FROM (SELECT CONCAT (
					''B''																	--record type--
					,''1''																--line number--
					,''0''																--source document number--
					,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_batch_number),10)	--batch number--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),(SELECT RIGHT(v.fxa_drn,6) 
															FROM [dbo].[voucher] v
															WHERE v.fxa_inactive_flag = 0 
																AND v.fxa_classification = ''Bh''
																AND v.fxa_work_type_code = ''NABCHQ_LBOX''
																AND v.fxa_batch_type_code = @lockedbox
																AND CAST(v.fxa_processing_date AS DATE) = @businessdate)),6)--sequence number--
				--	,RIGHT(''000000''+CONVERT(NVARCHAR(6), RIGHT(v.fxa_drn,6)),6)			--sequence number--
					,''C''																--DC code-
					,RIGHT(''00000000000''+CONVERT(NVARCHAR(11),SUM(v.fxa_amount)),11)	--transaction amount--
					,''000000000''														--merchant ID--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),v.fxa_bsb),6)					--bsb--
					,CASE WHEN lb.bsb = ''991202'' THEN ''2''
						  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''0'' END		--state of origin--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),
							CASE WHEN lb.bsb = ''991202'' THEN ''562337094''
								 WHEN lb.bsb IN (''991302'', ''991317'') 
								 THEN v.fxa_extra_aux_dom ELSE '''' END),16)				--acc number--
					,CONCAT(''0'',FORMAT(v.fxa_processing_date, ''dd''),RIGHT(lb.bsb,3))	--auxdom--
					,CASE WHEN lb.bsb = ''991202'' THEN ''2''
						  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''0'' END		--merchant organisation number--
					,CAST(RIGHT(SPACE(9) + '''',(9)) AS NVARCHAR(9))						--filler--
					) AS record	
					,''2'' AS line
					,''1'' AS line2
					,lb.customer_id
					,'''' AS fxa_account_number
					,'''' AS fxa_drn
				FROM [dbo].[voucher] v
				INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_payment_type = ''RMO''
					AND v.fxa_classification = ''Cr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				GROUP BY v.fxa_batch_number
					,v.fxa_bsb
					,lb.bsb
					,CASE WHEN lb.bsb = ''991202'' THEN ''562337094''
								 WHEN lb.bsb IN (''991302'', ''991317'') 
								 THEN v.fxa_extra_aux_dom ELSE '''' END
					,CONCAT(''0'',FORMAT(v.fxa_processing_date, ''dd''),RIGHT(lb.bsb,3))
					,lb.customer_id		
			
		UNION ALL

		----Clearing Warrant Line 2----

			SELECT CONCAT (
				''B''																					--record type--
				,''2''																				--line number--
				,RIGHT(CAST(YEAR(v.fxa_processing_date) AS NVARCHAR(4)),1) + RIGHT(''000'' + CAST(DATEPART(dy, v.fxa_processing_date) AS NVARCHAR(3)),3)
			--	,''00''																				--Transport ID--
				,CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''47'' 
					  WHEN v.fxa_capture_bsb = ''083340'' THEN ''66'' ELSE ''61'' END						--Transport ID--
				,RIGHT(''00''+CONVERT(NVARCHAR(2),RIGHT(v.fxa_batch_number,2)),2)						--run number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),(SELECT RIGHT(v.fxa_drn,6) 
													FROM [dbo].[voucher] v
													WHERE v.fxa_inactive_flag = 0 
														AND v.fxa_classification = ''Bh''
														AND v.fxa_work_type_code = ''NABCHQ_LBOX''
														AND v.fxa_batch_type_code = @lockedbox
														AND CAST(v.fxa_processing_date AS DATE) = @businessdate)),6)--Image DIN---
			--	,RIGHT(''000000''+CONVERT(NVARCHAR(6), RIGHT(v.fxa_drn,6)),6)
				,''0000000000000000''																	--acc number--
				,''00000000''																			--transaction date--
				,''000000''																			--authorisation number--
				,''00000''																			--item count--
				,''VR Clearing Warrant''																--Descriptive text--
				,CAST(RIGHT(SPACE(8) + '''',(8)) AS NVARCHAR(8))										--filler--
				) AS record							
				,''2'' AS line
				,''2'' AS line2
				,v.fxa_batch_type_code AS customer_id
				,v.fxa_account_number
				,v.fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_payment_type = ''RMO''
					AND v.fxa_classification = ''Bh''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1

		UNION ALL

		----Merchant Summary 1----

		SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,t1.fxa_drn
		FROM (

			SELECT CONCAT (
				''D''																				--record type--
				,''1''																			--line number--
				,''0''																			--source document number--
				,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_batch_number),10)				--capture batch number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),(SELECT RIGHT(v.fxa_drn,6) 
													FROM [dbo].[voucher] v
													WHERE v.fxa_inactive_flag = 0 
														AND v.fxa_classification = ''Bh''
														AND v.fxa_work_type_code = ''NABCHQ_LBOX''
														AND v.fxa_batch_type_code = @lockedbox
														AND CAST(v.fxa_processing_date AS DATE) = @businessdate)),6)   --image DIN--
			--	,RIGHT(''000000''+CONVERT(NVARCHAR(6), RIGHT(v.fxa_drn,6)),6)
				,''C''																			--DC code--
				,RIGHT(''00000000000''+CONVERT(NVARCHAR(11),SUM(v.fxa_amount)),11)				--Transaction Amount--
				,RIGHT(''000000000''+CONVERT(NVARCHAR(9),
						CASE WHEN lb.bsb = ''991202'' THEN ''003283409''
							 WHEN lb.bsb = ''991302'' THEN ''006134928'' 
							 WHEN lb.bsb = ''991012'' THEN ''009237694'' ELSE ''0'' END),9)			--Merchant ID--
				,''000000''																		--bsb--
				,CASE WHEN lb.bsb = ''991202'' THEN ''2''
					  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''0'' END					--state of origin--
				,''0000000000000000''																--acc number--
				,''000000''																		--aux dom--
				,CASE WHEN lb.bsb = ''991202'' THEN ''2''
					  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''0'' END					--organisation number--
				,CAST(RIGHT(SPACE(9) + '''',(9)) AS NVARCHAR(9))									--filler--
				) AS record						
				,''3'' AS line
				,''1'' AS line2
				,lb.customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_payment_type = ''RMO''
				AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_batch_number
					,lb.bsb
					,lb.customer_id

		UNION ALL

		----Merchant Summary 2----

			SELECT CONCAT (
				''D''																					--record type--
				,''2''																				--line number--
				,RIGHT(CAST(YEAR(v.fxa_processing_date) AS NVARCHAR(4)),1) + RIGHT(''000'' + CAST(DATEPART(dy, v.fxa_processing_date) AS NVARCHAR(3)),3)
			--	,''00''																				--capture transport ID--
				,CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''47'' 
					  WHEN v.fxa_capture_bsb = ''083340'' THEN ''66'' ELSE ''61'' END						--Transport ID--
				,RIGHT(''00''+CONVERT(NVARCHAR(2),RIGHT(v.fxa_batch_number,2)),2)						--run number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),(SELECT RIGHT(v.fxa_drn,6) 
													FROM [dbo].[voucher] v
													WHERE v.fxa_inactive_flag = 0 
														AND v.fxa_classification = ''Bh''
														AND v.fxa_work_type_code = ''NABCHQ_LBOX''
														AND v.fxa_batch_type_code = @lockedbox
														AND CAST(v.fxa_processing_date AS DATE) = @businessdate)),6) --batch number-
			--	,RIGHT(''000000''+CONVERT(NVARCHAR(6), RIGHT(v.fxa_drn,6)),6)
				,''0000000000000000''																	--acc number--
				,''00000000''																			--processing date--							
				,''000000''																			--authorisation number--
				,RIGHT(''00000''+CONVERT(NVARCHAR(5)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
						  THEN 1 ELSE 0 END)),5)													--item count--
				,CAST(''Merchant Summary'' + LEFT(SPACE(20) ,(20)) AS NVARCHAR(20))					--descriptive text--
				,CAST(RIGHT(SPACE(8) + '''',(8)) AS NVARCHAR(8))										--filler--
				) AS record						
				,''3'' AS line
				,''2'' AS line2
				,lb.customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_payment_type = ''RMO''
				--AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_processing_date
				,RIGHT(lb.bsb,2)
				,lb.customer_id
				,v.fxa_capture_bsb
				,v.fxa_batch_number
				) t1

		UNION ALL

		----stub line  1----

		SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,t1.fxa_drn
		FROM (
			SELECT CONCAT (
				''E''																				--record type--
				,''1''																			--line number--
				,''0''																			--source document number--
				,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_batch_number),10)				--capture batch number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_drn,6)),6)						--Transaction number--
				,''D''																			--DC code--
				,RIGHT(''00000000000''+CONVERT(NVARCHAR(11),ABS(v.fxa_amount)),11)				--Transaction Amount--
				,RIGHT(''000000000''+CONVERT(NVARCHAR(9),
						CASE WHEN lb.bsb = ''991202'' THEN ''003283409''
							 WHEN lb.bsb = ''991302'' THEN ''006134928'' 
							 WHEN lb.bsb = ''991012'' THEN ''009237694'' ELSE ''0'' END),9)			--Merchant ID--
				,''000000''																		--bsb--
				,CASE WHEN lb.bsb = ''991202'' THEN ''2''
					  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''0'' END					--state of origin--
				,''0000000000000000''																--acc number--
				,''000000''																		--aux dom--
				,CASE WHEN lb.bsb = ''991202'' THEN ''2''
					  WHEN lb.bsb IN (''991302'', ''991012'') THEN ''3'' ELSE ''00'' END				--merchant organisation number--
				,CAST(RIGHT(SPACE(9) + '''',(9)) AS NVARCHAR(9))									--filler--
				) AS record						
				,''4'' AS line
				,''1'' AS line2
				,lb.customer_id
				,'''' AS fxa_account_number
				,v.fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				AND lb.customer_id = @lockedbox

		UNION ALL

		----stub line 2----

			SELECT CONCAT (
				''E''																					--record type--
				,''2''																				--line number--
				,RIGHT(CAST(YEAR(v.fxa_processing_date) AS NVARCHAR(4)),1) + RIGHT(''000'' + CAST(DATEPART(dy, v.fxa_processing_date) AS NVARCHAR(3)),3)
			--	,''66''																				--capture transport ID--
				,CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''47'' 
					  WHEN v.fxa_capture_bsb = ''083340'' THEN ''66'' ELSE ''61'' END						--Transport ID--
				,RIGHT(''00''+CONVERT(NVARCHAR(2),RIGHT(v.fxa_batch_number,2)),2)						--run number--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_drn,6)),6)							--sequence number--
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_extra_aux_dom),16)				--acc number-- 
				,FORMAT(v.fxa_processing_date, ''yyyy'')												--processing year--		
				,FORMAT(v.fxa_processing_date, ''MMdd'')												--processing month day--
				,''000000''																			--authorisation number--
				,''00000''																			--item count--
				,CAST(''Adjustment Entry''+ LEFT(SPACE(20) ,(20)) AS NVARCHAR(20))					--descriptive text--
				,CAST(RIGHT(SPACE(8) + '''',(8)) AS NVARCHAR(8))										--filler--
				) AS record							
				,''4'' AS line
				,''2'' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,v.fxa_drn
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification = ''Dr''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				) t1

		UNION ALL

		----trailer----

			SELECT  CONCAT (
				''Y''														--record type--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0	
								   AND v.fxa_generated_bulk_cr_flag = 0	
								  THEN 1 ELSE 0 END)),6)				--Number of detail transactions in the file--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
							AND v.fxa_generated_voucher_flag = 0
							AND v.fxa_generated_bulk_cr_flag = 0
							THEN 1 ELSE 0 END)),6)						--Number of Credits in the file--
				,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
							AND v.fxa_generated_voucher_flag = 0
						    THEN 1 ELSE 0 END)),6)						--Number of ‘E’ records (total number of stubs)--
				,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
					,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0
							   AND v.fxa_generated_bulk_cr_flag = 0
							  THEN ABS(v.fxa_amount) ELSE 0 END)),12)	--Total value of Credits in the file--
				,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
						        AND v.fxa_generated_voucher_flag = 0	
								THEN ABS(v.fxa_amount) ELSE 0 END)),12)	--Total value of ‘E’ records in the file)--
				,CAST(RIGHT(SPACE(36) + '''',(36)) AS NVARCHAR(36))		--filler--
				) AS record
				,''5'' AS line
				,'''' AS line2
				,lb.customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification <>''Bh''
				AND v.fxa_generated_voucher_flag = 0
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY lb.customer_id
				) t1
GROUP BY t1.record, t1.line ,t1.line2, t1.customer_id, t1.fxa_account_number, t1.fxa_drn
ORDER BY t1.line, t1.fxa_drn, t1.line2;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =======================================================
-- Author:		V Cheong
-- Create date: 24/09/2015
-- Description:	used for the NAB Locked Box Extract Files
--EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]
--@businessdate = ''20150728'',@lockedbox = ''LRD1''
---- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3]
	@ProcessDate DATE,
	@CustomerId NVARCHAR (10),
	@HideDebit BIT
AS
BEGIN
	SET NOCOUNT ON;
	
	SELECT	v.fxa_batch_number AS BatchNumber,
			(CASE v.fxa_payment_type 
				WHEN ''SIN'' THEN ''Singles''
				WHEN ''MLT'' THEN ''Multiples''
				WHEN ''CHO'' THEN ''Cheques Only''
				ELSE v.fxa_payment_type END) AS PaymentType,
			v.fxa_drn AS SequenceNumber,
			(CASE v.fxa_classification 
				WHEN ''Bh'' THEN ''Header''
				WHEN ''Cr'' THEN ''CREDIT''
				WHEN ''Dr'' THEN ''DEBIT''
				ELSE v.fxa_classification END) AS TransactionType,
			v.fxa_tran_link_no AS TransactionNumber,
			v.fxa_aux_dom AS AuxDom,
			v.fxa_extra_aux_dom AS ExtraAuxDom,
			v.fxa_bsb AS BSB,
			v.fxa_account_number AS AccountNumber,
			CAST(v.fxa_amount AS MONEY) / 100.0 AS Amount,
			v.fxa_batch_type_code
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code = ''NABCHQ_LBOX'' 
		AND v.fxa_batch_type_code = @CustomerId
		AND CAST(v.fxa_processing_date AS DATE) = @ProcessDate
		AND (@HideDebit = 0 OR v.fxa_classification IN (''Cr'', ''Bh''))
		AND v.fxa_generated_bulk_cr_flag = 0
	ORDER BY v.fxa_batch_number, v.fxa_drn
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_ext_NAB_Locked_Box_Extract_VR3] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 12/08/2015
-- Description:	used for the NAB Locked Box Extract Files
 --EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA]
 --@businessdate = ''20151111'',@lockedbox = ''LEA1''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_EANA] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_drn
FROM (
		----file header record----

			SELECT DISTINCT CONCAT (
					''EANA''																		
					,FORMAT(v.fxa_processing_date, ''ddMM'')										
					,''.LBX''
					) AS record		
					,''1'' AS line
					,lb.customer_id
					,'''' AS fxa_account_number
					,'''' AS fxa_drn
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
	
			UNION ALL

			----stub line 1----	

			SELECT t1.record
				,t1.line
				,t1.customer_id
				,t1.fxa_account_number
				,t1.fxa_drn
			FROM (SELECT CONCAT (
					FORMAT(v.fxa_processing_date, ''ddMMyy'')											--processing date--	
					,CAST(RIGHT(SPACE(12) + v.fxa_aux_dom,(12)) AS NVARCHAR(12))					--invoice number--
					,CAST(RIGHT(SPACE(8) + RIGHT(v.fxa_extra_aux_dom,8),(8)) AS NVARCHAR(8))			--member number--
					,REPLACE(STR(v.fxa_amount,13),SPACE(13),'''')										--credit amount--
					,CAST(RIGHT(SPACE(6) + RIGHT(v.fxa_batch_number,3),(6)) AS NVARCHAR(6))			--batch number--
					,CAST(RIGHT(SPACE(5) + RIGHT(v.fxa_drn,5),(5)) AS NVARCHAR(5))					--sequence number/image din--
					) AS record		
					,''2'' AS line
					,v.fxa_batch_type_code AS customer_id
					,v.fxa_account_number
					,v.fxa_drn
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Cr''
					AND v.fxa_generated_voucher_flag = 0
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate

			UNION ALL

			----stub line 2----	

				SELECT CONCAT (
					 CAST(RIGHT(SPACE(8) + RIGHT(v.fxa_aux_dom,8),(8)) AS NVARCHAR(8))					--chq serial number--
					,CAST(RIGHT(SPACE(7) + v.fxa_bsb,(7)) AS NVARCHAR(7))								--chq bsb--
					,CAST(RIGHT(SPACE(11) + v.fxa_account_number,(11)) AS NVARCHAR(11))					--chq account number--
					,CAST(RIGHT(SPACE(24) + RIGHT(v.fxa_drn,5),(24)) AS NVARCHAR(24))					--sequence number/image din--
					) AS record						
					,''2'' AS line
					,v.fxa_batch_type_code AS customer_id
					,v.fxa_account_number
					,v.fxa_drn
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Dr''
					AND v.fxa_generated_voucher_flag = 0
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1

			UNION ALL

			----file trailer record----	

				SELECT CONCAT (
					CAST(RIGHT(SPACE(15),(15)) AS NVARCHAR(15))							--filler--
					,REPLACE(STR(SUM(v.fxa_amount),11),SPACE(11),'''')					--total stub amount--
					,REPLACE(STR(COUNT(v.fxa_drn),7),SPACE(17),'''')						--total stub count--
					) AS record						
					,''3'' AS line
					,v.fxa_batch_type_code AS customer_id
					,'''' AS fxa_account_number
					,'''' AS fxa_drn
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Cr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				GROUP BY v.fxa_processing_date
				,v.fxa_batch_type_code
					) t1
GROUP BY t1.record, t1.line, t1.customer_id, t1.fxa_account_number,t1.fxa_drn
ORDER BY t1.line, t1.fxa_drn;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 06/08/2015
-- Description:	used for the NAB Locked Box Extract Files
--EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB]
--@businessdate = ''20151112'',@lockedbox = ''LPC1''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_NAB] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_tran_link_no 
FROM (
		----file header----

		SELECT DISTINCT CONCAT(
				lb.nol_prefix
				,CASE WHEN v.fxa_collecting_bsb = ''991304'' THEN FORMAT(v.fxa_processing_date,''yyMMdd'') ELSE FORMAT(v.fxa_processing_date,''ddMM'') END
				,lb.nol_suffix
				) AS record				
				,''1'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
			AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----file header record----

			SELECT DISTINCT CONCAT (
				''90''																			--record type--
				,FORMAT(v.fxa_processing_date, ''ddMMyy'')										--processing date--
				,CAST(RIGHT(SPACE(4) + RIGHT(lb.bsb,4),(4)) AS NVARCHAR(4))						--locked box number--
				) AS record		
				,''2'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
	
		UNION ALL

		----batch header record----

			SELECT DISTINCT CONCAT (
				''91''																				--record type--					
				,RIGHT(''66''+CONVERT(NVARCHAR(2),''''),2)												--transport ID--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8),RIGHT(v.fxa_batch_number,8)),8)				--batch number/run ID--
				,RIGHT(''0''+CONVERT(NVARCHAR(1),lb.[state]),1)										--state number--
				) AS record							
				,''3'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_batch_type_code = @lockedbox
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				
		UNION ALL

		----stub records----

		SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,CAST(t1.fxa_tran_link_no AS BIGINT)
		FROM (SELECT CONCAT (
					''92''																				--record type--	
					,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--image DRN--
					,RIGHT(''00000000000000000''+CONVERT(NVARCHAR(17),v.fxa_extra_aux_dom),17)			--extra aux dom--
					,RIGHT(''00000000000000''+CONVERT(NVARCHAR(14),v.fxa_aux_dom),14)						--aux dom--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),v.fxa_bsb),6)									--OCR locked box identifier--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),v.fxa_account_number),12)				--account number--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),v.fxa_trancode),3)									--trancode--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--amount recieved--
					) AS record		
					,''4'' AS line
					,''1'' AS line2
					,v.fxa_batch_type_code AS customer_id
					,'''' AS fxa_account_number
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Cr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
	
			UNION ALL

			----credit card/cheque records----	
			
				SELECT CONCAT (
					''93''																				--record type--	
					,RIGHT(''0000000''+CONVERT(NVARCHAR(7),
							CASE WHEN (v.fxa_payment_type ='''') THEN '''' ELSE RIGHT(v.fxa_drn,7)END),7)		--image DRN--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),
							CASE WHEN (v.fxa_payment_type ='''') THEN '''' ELSE RIGHT(v.fxa_aux_dom,6)END),6)	--cheque number--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),
							CASE WHEN (v.fxa_payment_type ='''') THEN '''' ELSE v.fxa_bsb END),6)				--bsb--
					,RIGHT(''000000000''+CONVERT(NVARCHAR(9),
							CASE WHEN (v.fxa_payment_type ='''') THEN '''' ELSE v.fxa_account_number END),9)	--account number--
					,RIGHT(''00000000000000000''+CONVERT(NVARCHAR(17),v.fxa_extra_aux_dom),17)				--cc number--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)						--amount paid--
					) AS record		
					,''4'' AS line
					,''2'' AS line2
					,v.fxa_batch_type_code AS customer_id
					,'''' AS fxa_account_number
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Dr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1
	
		UNION ALL

		----file trailer record----

			SELECT CONCAT (
				''94''																			--record type--
				,FORMAT(v.fxa_processing_date, ''ddMMyy'')										--processing date--
				,CAST(RIGHT(SPACE(4) + RIGHT(lb.bsb,4),(4)) AS NVARCHAR(4))						--locked box number--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
						      THEN 1 ELSE 0 END)),8)											--total item count--
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
							  THEN ABS(v.fxa_amount) ELSE 0 END)),16)							--total item amount--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
					,SUM(CASE WHEN v.fxa_classification = ''Cr'' 
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN 1 ELSE 0 END)),8)											--total stub count--	
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_classification = ''Cr''
						      AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN ABS(v.fxa_amount) ELSE 0 END)),16)							--total stub amount--							  			
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND  v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
							  THEN 1 ELSE ''0'' END)),8)											--total cheque count--					
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN ABS(v.fxa_amount) ELSE ''0'' END)),16)							--total cheque amount--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
					,SUM(CASE WHEN v.fxa_classification = ''Dr'' 
							  AND v.fxa_payment_type = ''RMO''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN 1 ELSE ''0'' END)),8)											--total cc count--					
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_payment_type = ''RMO'' 
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN ABS(v.fxa_amount) ELSE ''0'' END)),16)						--total cc amount--
				) AS record
				,''5'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification <> ''Bh''
				AND v.fxa_generated_voucher_flag = 0
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_processing_date
				,lb.bsb
				,v.fxa_batch_type_code
				) t1
GROUP BY t1.record, t1.line, t1.line2, t1.customer_id, t1.fxa_account_number,t1.fxa_tran_link_no
ORDER BY t1.line, t1.fxa_tran_link_no, t1.line2;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 06/08/2015
-- Description:	used for the NAB Locked Box Extract Files
 --EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS]
 --@businessdate = ''20151111'',@lockedbox = ''LOW1''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR4_OWKS] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_tran_link_no
FROM (
		----file header----

		SELECT DISTINCT CONCAT(
				lb.nol_prefix
				,FORMAT(v.fxa_processing_date,''ddMM'')
				,lb.nol_suffix
				) AS record			
				,''1'' AS line
				,'''' AS line2
				,lb.customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
			AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----file header record----

			SELECT CONCAT (
				''90''																			--record type--
				,FORMAT(v.fxa_processing_date, ''ddMMyy'')										--processing date--
				,''2030''																			--locked box number--
				) AS record																
				,''2'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
	
		UNION ALL

		----Stub – VOPR records----

			SELECT t1.record
			,t1.line
			,t1.line2
			,t1.customer_id
			,t1.fxa_account_number
			,CAST(t1.fxa_tran_link_no AS BIGINT)
		FROM (SELECT CONCAT (
					''92''																				--record type--	
					,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--image DRN--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_tran_link_no,6)),6)					--Transaction number--
					,''02''																				--PE Pocket Number--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Value Amount--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Net Amount--
					,''000000000000''																		--filler--
					,''000000000000''																		--filler--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_aux_dom),16)					--Invoice Number--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_extra_aux_dom),16)				--account number--
					,CAST(RIGHT(SPACE(16) +'''',(16)) AS NVARCHAR(16))									--Correction Indicator--
					,''0000000000000000''																	--filler--
					,''0000000000000000''																	--filler--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),''''),3)												--Stub AE Operator ID--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),''''),3)												--DC Operator ID--
					) AS record									
					,''4'' AS line
					,''1'' AS line2
					,v.fxa_batch_type_code AS customer_id
					,v.fxa_extra_aux_dom AS  fxa_account_number
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Cr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate

			UNION ALL

			----Cheque - VOPR records----
		
				SELECT CONCAT (
					''93''																				--record type--	
					,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--image DRN--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_tran_link_no,6)),6)					--Transaction number--
					,CAST(RIGHT(SPACE(2) + ''  '',(2)) AS NVARCHAR(2))									--filler--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Amount paid--
					,''000000''																			--transaction code--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_account_number),16)			--account number--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_bsb),16)						--bsb--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_aux_dom),16)					--aux dom--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),''''),3)												--Data Correction Operator ID--
					) AS record									
					,''4'' AS line
					,''2'' AS line2
					,v.fxa_batch_type_code AS customer_id
					,v.fxa_account_number
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification = ''Dr''
					AND v.fxa_generated_voucher_flag = 0
					AND v.fxa_generated_bulk_cr_flag = 0
					AND v.fxa_credit_note_flag <> 1
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1

		UNION ALL

		----Credit Notes - VOPR records----

				SELECT CONCAT (
					''96''																				--record type--	
					,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--image DRN--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(v.fxa_tran_link_no,6)),6)					--Transaction ID--
					,''02''																				--PE Pocket Number--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Value Amount--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Net Amount--
					,''000000000000''																		--filler--
					,''000000000000''																		--filler--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_aux_dom),16)					--credit Number--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16),v.fxa_account_number),16)			--account number--
					,''0000000000000999''																	--item identifier--
					,''0000000000000000''																	--filler--
					,''0000000000000000''																	--filler--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),''''),3)												--Stub AE Operator ID--
					,RIGHT(''000''+CONVERT(NVARCHAR(3),''''),3)												--DC Operator ID--
					) AS record									
					,''5'' AS line
					,'''' AS line2
					,v.fxa_batch_type_code AS customer_id
					,v.fxa_account_number
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_credit_note_flag = 1
					--AND v.fxa_generated_voucher_flag = 0
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate	
			
					
		UNION ALL

		----file trailer record----

			SELECT CONCAT (
				''94''																				--record type--
				,FORMAT(v.fxa_processing_date, ''ddMMyy'')											--processing date--
				,''2030''																				--locked box number--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0	
									AND v.fxa_generated_bulk_cr_flag = 0
									AND v.fxa_credit_note_flag <> 1
								  THEN 1 ELSE 0 END)),8)											--total item count--
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0	
								AND v.fxa_generated_bulk_cr_flag = 0
								AND v.fxa_credit_note_flag <> 1
							  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total item amount--
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
					,SUM(CASE WHEN v.fxa_classification = ''Cr'' 
							  AND v.fxa_generated_voucher_flag = 0	
							  AND v.fxa_generated_bulk_cr_flag = 0
							  THEN 1 ELSE 0 END)),8)												--total stub count--	
				,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_payment_type <> ''RMO''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN 1 ELSE 0 END)),8)												--total cheque count--	
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_classification = ''Cr''
							AND v.fxa_payment_type <> ''RMO''
						      AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
							  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total stub amount--							  			
				,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
					,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_payment_type <> ''RMO''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1	
							  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total cheque amount--
				) AS record
				,''6'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification <> ''Bh''
				AND v.fxa_generated_voucher_flag = 0
				AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_processing_date
				,v.fxa_batch_type_code 
				) t1
GROUP BY t1.record, t1.line, t1.line2, t1.customer_id, t1.fxa_account_number, t1.fxa_tran_link_no
ORDER BY t1.line, t1.fxa_tran_link_no, t1.line2;
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 13/08/2015
-- Description:	used for the NAB Locked Box Extract Files
 --EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU]
 --@businessdate = ''20151110'',@lockedbox = ''LCG1''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_CGU] @businessdate DATE, @lockedbox NVARCHAR (10)
AS
 
BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_tran_link_no 
FROM (
		----file header----

			SELECT DISTINCT CONCAT(
				lb.nol_prefix
				,''_''
				,FORMAT(v.fxa_processing_date,''yyMMdd'')
				,lb.nol_suffix
				) AS record																	--file header--
				,''1'' AS line
				,'''' AS line2
				,'''' AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
				AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----file header record----

			SELECT CONCAT (
				''0''																			--record type--
				,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')									--processing date--
				,CAST(RIGHT(SPACE(4) + RIGHT(lb.bsb,4),(4)) AS NVARCHAR(4))					--locked box number--
				,CAST(RIGHT(SPACE(67) + '''',(67)) AS NVARCHAR(67))							--filler--
				) AS record		
				,''2'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
				AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----Stub CGU OCR records----

				SELECT t1.record
					,t1.line
					,t1.line2
					,t1.customer_id
					,t1.fxa_account_number
					,t1.fxa_tran_link_no
				FROM (SELECT CONCAT (
						''1''																					--record type--	
						,CAST(v.fxa_extra_aux_dom + RIGHT(SPACE(16), (16)) AS NVARCHAR(16))					--Bill Reference Number EAD--			
						,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),ABS(v.fxa_amount)),10)						--amount recieved--		
						,CAST(v.fxa_aux_dom + RIGHT(SPACE(9), (9)) AS NVARCHAR(9))							--aux dom--
						,RIGHT(''000000''+CONVERT(NVARCHAR(6),v.fxa_bsb),6)									--bsb--
						,RIGHT(''000000000''+CONVERT(NVARCHAR(9),v.fxa_account_number),9)						--account number--
						,RIGHT(''000000000''+CONVERT(NVARCHAR(9),v.fxa_drn),9)								--drn--
						,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_tran_link_no),10)					--Transaction Identifier--
						--,CAST(RIGHT(SPACE(10) + v.fxa_tran_link_no,(10)) AS NVARCHAR(10))					--Transaction Identifier--
						,CAST(RIGHT(SPACE(10) + '''',(10)) AS NVARCHAR(10))									--filler--
						) AS record							
						,''3'' AS line
						,''1'' AS line2
						,v.fxa_batch_type_code AS customer_id
						,v.fxa_account_number
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
					FROM [dbo].[voucher] v
					WHERE v.fxa_inactive_flag = 0 
						AND v.fxa_work_type_code = ''NABCHQ_LBOX''
						AND v.fxa_batch_type_code = @lockedbox
						AND v.fxa_classification =''Cr''
						AND v.fxa_generated_voucher_flag = 0
						AND v.fxa_generated_bulk_cr_flag = 0
						AND v.fxa_credit_note_flag <> 1
						AND CAST(v.fxa_processing_date AS DATE) = @businessdate

			UNION ALL

			----credit card/cheque records----	

					SELECT CONCAT (
						''2''																					--record type--	
						,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_aux_dom),10)							--cheque number--
						,RIGHT(''000000''+CONVERT(NVARCHAR(6),v.fxa_bsb),6)									--bsb--
						,RIGHT(''0000000000''+CONVERT(NVARCHAR(10),v.fxa_account_number) ,10)					--account number--
						,''0000000000000000''																	--cc number--
						,RIGHT(''000000000000''+CONVERT(NVARCHAR(10),ABS(v.fxa_amount)),10)					--amount paid--
						,RIGHT(''000000000''+CONVERT(NVARCHAR(9),v.fxa_drn),9)								--image DRN--
						,RIGHT(''000000000000''+CONVERT(NVARCHAR(10),v.fxa_tran_link_no),10)					--Transaction Identifier--
						,CAST(RIGHT(SPACE(8) + '''',(8)) AS NVARCHAR(8))										--filler--
						) AS record		
						,''3'' AS line
						,''2'' AS line2
						,v.fxa_batch_type_code AS customer_id
						,v.fxa_account_number
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
					FROM [dbo].[voucher] v
					WHERE v.fxa_inactive_flag = 0 
						AND v.fxa_work_type_code = ''NABCHQ_LBOX''
						AND v.fxa_batch_type_code = @lockedbox
						AND v.fxa_classification = ''Dr''
						AND v.fxa_generated_voucher_flag = 0
						AND v.fxa_generated_bulk_cr_flag = 0
						AND v.fxa_credit_note_flag <> 1
						AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1
	
		UNION ALL

		----file trailer record----

			SELECT CONCAT (
					''9''																					--record type--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
								  AND v.fxa_generated_voucher_flag = 0 
							      AND v.fxa_generated_bulk_cr_flag = 0	
								  THEN 1 ELSE ''0'' END)),6)												--total stub count--
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
								  AND v.fxa_generated_voucher_flag = 0
						          AND v.fxa_generated_bulk_cr_flag = 0	
								  THEN ABS(v.fxa_amount) ELSE ''0'' END)),12)								--total stub amount--
					,RIGHT(''000000''+CONVERT(NVARCHAR(6)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_payment_type <> ''RMO''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
								  THEN 1 ELSE ''0'' END)),6)												--total cheque count--					
					,RIGHT(''000000000000''+CONVERT(NVARCHAR(12)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
							  AND v.fxa_payment_type <> ''RMO''
							  AND v.fxa_generated_voucher_flag = 0
							  AND v.fxa_generated_bulk_cr_flag = 0
							  AND v.fxa_credit_note_flag <> 1
								   THEN ABS(v.fxa_amount) ELSE ''0'' END)),12)							--total cheque amount--
					,''000000''																			--total cc count--					
					,''000000000000''																		--total cc amount--
					,CAST(RIGHT(SPACE(25) + '''',(25)) AS NVARCHAR(25))									--filler--
					) AS record
					,''4'' AS line
					,'''' AS line2
					,v.fxa_batch_type_code AS customer_id
					,'''' AS fxa_account_number
					,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			WHERE v.fxa_inactive_flag = 0 
				AND v.fxa_work_type_code = ''NABCHQ_LBOX''
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_classification <> ''Bh''
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
			GROUP BY v.fxa_batch_type_code
				) t1
GROUP BY t1.record, t1.line, t1.line2, t1.customer_id, t1.fxa_account_number,t1.fxa_tran_link_no 
ORDER BY t1.line, t1.fxa_tran_link_no, t1.line2;
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =======================================================
-- Author:		James Honner
-- Create date: 17/09/2015
-- Description:	used for the NAB Locked Box Extract Files
 --EXEC [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC]
 --@businessdate = ''20151118'',@lockedbox = ''LML1''
-- =======================================================
CREATE PROCEDURE [dbo].[usp_ext_NAB_Locked_Box_Extract_VR8_MLC] @businessdate DATE, @lockedbox NVARCHAR (10)
AS

BEGIN
	SET NOCOUNT ON;

SELECT t1.record
	,t1.line
	,t1.line2
	,t1.customer_id
	,t1.fxa_account_number
	,t1.fxa_tran_link_no
FROM (
		----file header----

			SELECT DISTINCT CONCAT(
				lb.nol_prefix
				,FORMAT(v.fxa_processing_date,''yyMMdd'')
				,lb.nol_suffix
				) AS record																	--file header--
				,''1'' AS line
				,'''' AS line2
				,'''' AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
			AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----file header record----
		
			SELECT CONCAT (
				''90''																		--record type--
				,FORMAT(v.fxa_processing_date, ''ddMMyy'')									--processing date--
				,''1211''																		--locked box number--
				) AS record		
				,''2'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
				AND v.fxa_batch_type_code = @lockedbox

		UNION ALL

		----batch header----

			SELECT CONCAT (
				''91''																		--record type--
				,''00''																		--transport id--
				,RIGHT(''000000000''+CONVERT(NVARCHAR(8),v.fxa_batch_number),8)				--Batch/Run number--
				,''2''																		--state--
				) AS record		
				,''2'' AS line
				,'''' AS line2
				,v.fxa_batch_type_code AS customer_id
				,'''' AS fxa_account_number
				,'''' AS fxa_tran_link_no
			FROM [dbo].[voucher] v
			WHERE CAST(v.fxa_processing_date AS DATE) = @businessdate
				AND v.fxa_batch_type_code = @lockedbox
				AND v.fxa_generated_voucher_flag = 0	
				AND v.fxa_generated_bulk_cr_flag = 0
				AND v.fxa_credit_note_flag <> 1	

		UNION ALL

		--Stub Record--

				SELECT t1.record
					,t1.line
					,t1.line2
					,t1.customer_id
					,t1.fxa_account_number
					,CAST(t1.fxa_tran_link_no AS BIGINT)
				FROM (SELECT CONCAT (
						''92''																				--record type--	
						,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--drn--
						,RIGHT(''00000000000000000''+CONVERT(NVARCHAR(17),v.fxa_extra_aux_dom),17)			--Reference Number 1--	
						,RIGHT(''00000000000000''+CONVERT(NVARCHAR(14),v.fxa_aux_dom),14)						--Reference Number 2--
						,''002521''																			--OCR Locked Box Identifier--
						,''000000000011''																		--account number--
						,''000''																				--Item Identifier--
						,RIGHT(''000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--Amount Received--
						) AS record							
						,''3'' AS line
						,''1'' AS line2
						,v.fxa_batch_type_code AS customer_id
						,v.fxa_account_number
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
					FROM [dbo].[voucher] v
					WHERE v.fxa_inactive_flag = 0 
						AND v.fxa_work_type_code = ''NABCHQ_LBOX''
						AND v.fxa_batch_type_code = @lockedbox
						AND v.fxa_classification =''Cr''
						AND v.fxa_generated_voucher_flag = 0
						AND v.fxa_generated_bulk_cr_flag = 0
						AND v.fxa_credit_note_flag <> 1
						AND CAST(v.fxa_processing_date AS DATE) = @businessdate

			UNION ALL

			----credit card/cheque records----	
	
					SELECT CONCAT (
						''93''																				--record type--	
						,RIGHT(''0000000''+CONVERT(NVARCHAR(7),RIGHT(v.fxa_drn,7)),7)							--image DRN-
						,CASE WHEN fxa_aux_dom = '''' THEN ''000000'' ELSE RIGHT(''000000''+CONVERT(NVARCHAR(6),RIGHT(fxa_aux_dom,6)),6) END	--cheque number--
						,RIGHT(''000000''+CONVERT(NVARCHAR(6),
								CASE WHEN v.fxa_payment_type <> ''RMO'' 
								THEN v.fxa_bsb ELSE '''' END),6)												--bsb--
						,RIGHT(''000000000''+CONVERT(NVARCHAR(9),
								CASE WHEN v.fxa_payment_type <> ''RMO'' 
								THEN v.fxa_account_number ELSE '''' END),9)									--account number--
						,''00000000000000000''																--cc number--
						,RIGHT(''00000000000000''+CONVERT(NVARCHAR(12),ABS(v.fxa_amount)),12)					--amount paid--
						) AS record		
						,''3'' AS line
						,''2'' AS line2
						,v.fxa_batch_type_code AS customer_id
						,v.fxa_account_number
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS fxa_tran_link_no
					FROM [dbo].[voucher] v
					WHERE v.fxa_inactive_flag = 0 
						AND v.fxa_work_type_code = ''NABCHQ_LBOX''
						AND v.fxa_batch_type_code = @lockedbox
						AND v.fxa_classification = ''Dr''
						AND v.fxa_generated_voucher_flag = 0
						AND v.fxa_generated_bulk_cr_flag = 0
						AND v.fxa_credit_note_flag <> 1
						AND CAST(v.fxa_processing_date AS DATE) = @businessdate
					) t1
	
		UNION ALL

		----file trailer record----
	
			SELECT CONCAT (
					''94''																				--record type--
					,FORMAT(v.fxa_processing_date, ''ddMMyy'')											--processing date--
					,''1211''																				--Locked Box Number--
					,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0	
								AND v.fxa_generated_bulk_cr_flag = 0
								AND v.fxa_credit_note_flag <> 1	
								  THEN 1 ELSE 0 END)),8)												--total item count--
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
						,SUM(CASE WHEN v.fxa_generated_voucher_flag = 0	
									AND v.fxa_generated_bulk_cr_flag = 0
									AND v.fxa_credit_note_flag <> 1
								  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total item amount--
					,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_classification = ''Cr'' 
								  AND v.fxa_generated_voucher_flag = 0	
								  AND v.fxa_generated_bulk_cr_flag = 0
								  THEN 1 ELSE 0 END)),8)												--total stub count--	
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
						,SUM(CASE WHEN v.fxa_classification = ''Cr''
								  AND v.fxa_generated_voucher_flag = 0
								  AND v.fxa_generated_bulk_cr_flag = 0
								  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total stub amount--
					,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
								  AND v.fxa_payment_type <> ''RMO''
								  AND v.fxa_generated_voucher_flag = 0
								  AND v.fxa_generated_bulk_cr_flag = 0
								  AND v.fxa_credit_note_flag <> 1	
								  THEN 1 ELSE 0 END)),8)												--total cheque count--	
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
								  AND v.fxa_payment_type <> ''RMO''
								  AND v.fxa_generated_voucher_flag = 0
								  AND v.fxa_generated_bulk_cr_flag = 0
								  AND v.fxa_credit_note_flag <> 1	
								  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total cheque amount--
					,RIGHT(''00000000''+CONVERT(NVARCHAR(8)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
								  AND v.fxa_payment_type = ''RMO''
								  AND v.fxa_generated_voucher_flag = 0
								  AND v.fxa_generated_bulk_cr_flag = 0
								  AND v.fxa_credit_note_flag <> 1	
								  THEN 1 ELSE 0 END)),8)												--total CC count--	
					,RIGHT(''0000000000000000''+CONVERT(NVARCHAR(16)
						,SUM(CASE WHEN v.fxa_classification = ''Dr''
								  AND v.fxa_payment_type = ''RMO''
								  AND v.fxa_generated_voucher_flag = 0
								  AND v.fxa_generated_bulk_cr_flag = 0
								  AND v.fxa_credit_note_flag <> 1	
								  THEN ABS(v.fxa_amount) ELSE 0 END)),16)								--total CC amount--				  			
						) AS record
						,''4'' AS line
						,'''' AS line2
						,v.fxa_batch_type_code AS customer_id
						,'''' AS fxa_account_number
						,'''' AS fxa_tran_link_no
				FROM [dbo].[voucher] v
				INNER JOIN [dbo].[ref_locked_box] lb ON lb.customer_id = v.fxa_batch_type_code
				WHERE v.fxa_inactive_flag = 0 
					AND v.fxa_work_type_code = ''NABCHQ_LBOX''
					AND v.fxa_batch_type_code = @lockedbox
					AND v.fxa_classification <> ''Bh''
					AND CAST(v.fxa_processing_date AS DATE) = @businessdate
				GROUP BY v.fxa_processing_date
					,v.fxa_batch_type_code
				) t1
GROUP BY t1.record, t1.line,  t1.line2, t1.customer_id, t1.fxa_account_number,CAST(t1.fxa_tran_link_no AS BIGINT)
ORDER BY t1.line, t1.fxa_tran_link_no, t1.line2;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_Get_File_Logs]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Get_File_Logs]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_Get_File_Logs] 
	@StartTime DATETIME,
	@EndTime DATETIME
AS
BEGIN
	SET NOCOUNT ON;
	
	DECLARE @UtcStartTime DATETIME
	DECLARE @UtcEndTime DATETIME
	DECLARE @HourDifference INT

	-- Will only ever look at the current day''s data so we are getting the daylight saving hour difference
	-- by using today''s date
	SET @HourDifference = DATEDIFF(HOUR, GETUTCDATE(), GETDATE())
	SET @UtcStartTime = DATEADD(HOUR, -@HourDifference, @StartTime)
	SET @UtcEndTime = DATEADD(HOUR, -@HourDifference, @EndTime)

	SELECT [batch_number] AS [Batch_Number], [file_name], file_timestamp, ''1. DMS'' AS Batch_Location, [record_count] AS [Total_Vouchers]
	FROM ref_batch_audit 
	WHERE [file_name] LIKE ''OUTCLEARINGSPKG_%''
		AND [file_timestamp] BETWEEN @StartTime AND @EndTime

	UNION

	SELECT SUBSTRING([file_name], 30, 8) AS [Batch_Number], [file_name], file_timestamp, ''2. FXA'' AS [Batch_Location], 0 AS [Total_Vouchers]
	FROM ref_file_transmission
	WHERE [file_name] LIKE ''OUTCLEARINGSPKG_%'' AND [direction] = ''I''
		AND [file_timestamp] BETWEEN @StartTime AND @EndTime

	UNION

	SELECT v.fxa_batch_number AS [Batch_Number], NULL AS [file_name], DATEADD(HOUR, @HourDifference, MAX(o.r_creation_date)) AS file_timestamp, ''3. Documentum'' AS [Batch_Location], COUNT(1) AS [Total_Vouchers]
	FROM [dbo].[voucher] v
	INNER JOIN [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[dm_sysobject_s] o ON o.r_object_id = v.r_object_id
	WHERE o.r_creation_date BETWEEN @UtcStartTime AND @UtcEndTime
	GROUP BY v.fxa_batch_number
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_Get_File_Logs] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Adjustment]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Adjustment]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ========================================================
-- Author:		James Honner
-- Create date: 23/06/2015
-- Description:	used for the Agency Adjustments
--EXEC [dbo].[usp_rpt_Agency_Adjustment_Letter]
--@processdate = ''20151023'', @agencybank = ''Capricornian Ltd (The)'', @agencybankgroup = ''CAP''
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_Adjustment] @processdate DATE, @agencybank NVARCHAR(100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM (

	SELECT v.fxa_bsb AS ''Deposit BSB''
		,v.fxa_account_number AS ''Deposit Account Number''
		,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
		,CASE WHEN CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) IS NULL 
			  THEN CAST(CAST(v.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) ELSE CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) END AS ''Adjustment Amount''
		,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
		,v.fxa_adjustment_description AS ''Reason''
		,v.fxa_batch_type_code AS ''Batch Type Code''
		,v.folder_path AS ''Voucher Image Path''
		,CAST(v.fxa_processing_date AS DATE) AS ''Processing Date''
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
		,'''' AS DIN
		,'''' AS NEGBSB
		,'''' AS EAD
		,'''' AS AD
		,'''' AS BSB
		,'''' AS ''Account No''
		,'''' AS TC
		,'''' AS ''DR/CR''
		,'''' AS Amount
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT DISTINCT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_inactive_flag = 0
					AND v.fxa_adjustment_flag = 1
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					AND rb.bank_name = @agencybank
					AND rb.bank_group_code = @agencybankgroup
					AND (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
			LEFT JOIN (SELECT DISTINCT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
							,CASE WHEN v.fxa_drn LIKE ''777%'' THEN CAST(v.fxa_amount AS NVARCHAR) ELSE '''' END AS fxa_amount
						FROM [dbo].[voucher] v
						INNER JOIN (SELECT DISTINCT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
												,v.fxa_amount
											FROM [dbo].[voucher] v
											LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
													OR rb.bsb = (LEFT(v.fxa_bsb, 3))
											INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
													AND v.fxa_processing_state = rs.state_code
											WHERE v.fxa_inactive_flag = 0
												AND v.fxa_adjustment_flag = 1
												AND CAST(v.fxa_processing_date AS DATE) = @processdate
												AND rb.bank_name = @agencybank
												AND rb.bank_group_code = @agencybankgroup
												AND (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
										) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						WHERE v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc])
						)t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))						
	WHERE v.fxa_classification = ''Cr''
		AND v.fxa_account_number NOT IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc])
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY v.fxa_bsb 
		,v.fxa_account_number 
		,CASE WHEN CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) IS NULL 
			  THEN CAST(CAST(v.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) ELSE CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) END
		,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
		,CAST(v.fxa_amount AS MONEY)/100 
		,v.fxa_adjustment_description
		,v.fxa_batch_type_code 
		,v.folder_path 
		,CAST(v.fxa_processing_date AS DATE)
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 
	
	UNION ALL
		
		SELECT '''' AS ''Deposit BSB''
			,'''' AS ''Deposit Account Number''
			,'''' AS ''Original Deposit Amount''
			,'''' AS ''Adjustment Amount''
			,'''' AS ''Adjusted Deposit Amount''
			,'''' AS ''Reason''
			,'''' AS ''Batch Type Code''
			,'''' AS ''Voucher Image Path''
			,CAST(t1.fxa_processing_date AS DATE) AS ''Processing Date''
			,'''' AS fxa_drn
			,t1.sug_key
			,t1.fxa_drn AS DIN
			,t1.fxa_collecting_bsb AS NEGBSB
			,t1.fxa_extra_aux_dom AS EAD
			,t1.fxa_aux_dom AS AD
			,t1.fxa_bsb AS BSB
			,t1.fxa_account_number AS ''Account No''
			,t1.fxa_trancode AS TC
			,t1.fxa_classification AS ''DR/CR''
			,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT DISTINCT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_inactive_flag = 0
					AND v.fxa_adjustment_flag = 1
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					AND rb.bank_name = @agencybank
					AND rb.bank_group_code = @agencybankgroup
					AND (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
		LEFT JOIN  (SELECT v.fxa_drn 
						,v.fxa_collecting_bsb
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom 
						,v.fxa_bsb 
						,v.fxa_account_number
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
																FROM [dbo].[voucher] v
																LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
																	OR rb.bsb = (LEFT(v.fxa_bsb, 3))
																INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																	AND v.fxa_processing_state = rs.state_code
																WHERE v.fxa_inactive_flag = 0
																	AND v.fxa_adjustment_flag = 1
																	AND CAST(v.fxa_processing_date AS DATE) = @processdate
																	AND rb.bank_name = @agencybank
																	AND rb.bank_group_code = @agencybankgroup
																	AND (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
																												)
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
					GROUP BY v.fxa_drn 
						,v.fxa_collecting_bsb 
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom  
						,v.fxa_bsb 
						,v.fxa_account_number 
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))														
		WHERE v.fxa_classification = ''Cr''
			AND v.fxa_account_number NOT IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc])
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
		GROUP BY t1.fxa_drn
			,t1.fxa_collecting_bsb
			,t1.sug_key
			,t1.fxa_extra_aux_dom
			,t1.fxa_aux_dom
			,t1.fxa_bsb
			,t1.fxa_account_number 
			,t1.fxa_trancode 
			,t1.fxa_classification
			,CAST(t1.fxa_processing_date AS DATE)
			,CAST(t1.fxa_amount AS MONEY)/100
			) t1
	ORDER BY 11,12,10 DESC;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_Adjustment] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Bank_List]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Bank_List]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- ========================================================
-- Author:		James Honner
-- Create date: 23/06/2015
-- Description:	used for the Agency Bank Reporting 
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_Bank_List]
AS
BEGIN

SET NOCOUNT ON;

	SELECT DISTINCT rb.bank_code
		,rb.bank_name
		,rb.bank_group_code
	FROM [dbo].[ref_bank] rb
	INNER JOIN [dbo].[ref_state] rs ON rs.bank_code = rb.bank_code
	WHERE rs.parent_fi = ''NAB''
		AND rb.effective_date <= GETDATE()
	ORDER BY 2
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_Bank_List] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_ECD_Exception_Single_Item]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =====================================================================
-- Author:		James Honner
-- Create date: 07/05/2015
-- Description:	used for the Agency ECD Exceptions Return Single Report
--EXEC [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item] 
--@processdate = ''20150901'', @agencybank = ''Citigroup Pty Ltd'', @agencybankgroup = ''CTI''
-- =====================================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item] @processdate DATE, @agencybank NVARCHAR(100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT t1.fxa_drn AS DRN
		,v.fxa_collecting_bsb AS ''Negotiating BSB''
		,t1.fxa_account_number AS ''Negotiating Account Number''
		,CAST(t1.fxa_amount AS MONEY)/100 AS ''Deposit Amount''
		,v.fxa_bsb AS ''Drawer''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Cheque Amount''
		,v.fxa_tpc_failed_flag
		,v.fxa_uecd_return_flag
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1
			  THEN ''Failed Third Party Reason''
			  ELSE ''Unencoded Deposit'' END AS ''Exception Reason''
		,v.fxa_batch_number
		,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date 
		,rb.bank_group_code
		,rb.bank_code
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) 
		OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
		AND v.fxa_processing_state = rs.state_code 
	INNER JOIN (SELECT v.fxa_batch_number
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_processing_date 
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
										AND v.fxa_processing_state = rs.state_code 
				WHERE rs.parent_fi =''NAB''
					AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
				--	AND v.fxa_classification = ''Dr''
					AND v.fxa_inactive_flag = 0
					AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
				) t ON t.fxa_batch_number = v.fxa_batch_number
					AND CAST(t.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT)
					AND CAST(t.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)
	LEFT JOIN (SELECT v.fxa_drn 
					,v.fxa_bsb 
					,v.fxa_account_number
					,v.fxa_amount
					,v.fxa_batch_number
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_processing_date
				FROM [dbo].[voucher] v
				WHERE CAST(v.fxa_processing_date AS DATE) = @processdate
					AND CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
															FROM [dbo].[voucher] v
															LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
															INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																					AND v.fxa_processing_state = rs.state_code 
															WHERE rs.parent_fi =''NAB''
																AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
																AND v.fxa_classification = ''Dr''
																AND v.fxa_inactive_flag = 0
																AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
															)
					AND v.fxa_account_number NOT IN (SELECT fxa_account_number
													FROM [dbo].[voucher] v
													LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
													INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																			AND v.fxa_processing_state = rs.state_code 
													WHERE rs.parent_fi =''NAB''
														AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
														AND v.fxa_classification = ''Dr''
														AND v.fxa_inactive_flag = 0
														AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
													)
										) t1 ON t1.fxa_batch_number = v.fxa_batch_number
											AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT)
											AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)														
	WHERE (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
		AND rs.parent_fi =''NAB''
		AND v.fxa_classification = ''Dr''
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
		--AND rb.bank_code = @agencybank 
		--AND rb.bank_group_code = @agencybankgroup 
	GROUP BY t1.fxa_drn
		,v.fxa_collecting_bsb 
		,t1.fxa_account_number 
		,CAST(t1.fxa_amount AS MONEY)/100 
		,v.fxa_bsb 
		,CAST(v.fxa_amount AS MONEY)/100
		,v.fxa_tpc_failed_flag
		,v.fxa_uecd_return_flag 
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1
			  THEN ''Failed Third Party Reason''
			  ELSE ''Unencoded Deposit'' END 
		,v.fxa_batch_number
		,CAST(v.fxa_tran_link_no AS BIGINT) 
		,CAST(v.fxa_processing_date AS DATE) 
		,rb.bank_group_code
		,rb.bank_code;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_ECD_Exception_Single_Item] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_ECD_Return_All_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_ECD_Return_All_Items]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
 --=========================================================
 --Author:		James Honner
 --Create date: 23/06/2015
 --Description:	used for the Agency ECD All Items 
--EXEC [dbo].[usp_rpt_Agency_ECD_Return_All_Items] 
----@processdate = ''20151123'', @agencybank = ''Citigroup Pty Ltd'', @agencybankgroup = ''CTI''
--@processdate = ''20151124'', @agencybank = ''Macquarie Bank Ltd'', @agencybankgroup = ''MBL''
 --=========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_ECD_Return_All_Items] @processdate DATE, @agencybank NVARCHAR(100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

	SELECT  v.fxa_bsb AS ''Deposit BSB''
		,v.fxa_account_number AS ''Deposit Account Number''
		,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
		,CASE WHEN CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) IS NULL 
			  THEN CAST(CAST(v.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) ELSE CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) END AS ''Adjustment Amount''
		,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1
			  THEN ''Failed Third Party Reason''
			  ELSE ''Unencoded Deposit'' END AS ''Reason''
		,v.fxa_batch_type_code AS ''Batch Type Code''
		,v.folder_path AS ''Voucher Image Path''
		,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
		,'''' AS DIN
		,'''' AS NEGBSB
		,'''' AS EAD
		,'''' AS AD
		,'''' AS BSB
		,'''' AS ''Account No''
		,'''' AS TC
		,'''' AS ''DR/CR''
		,'''' AS Amount
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT DISTINCT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key 
					,CASE WHEN v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc])
						  THEN CAST(v.fxa_amount AS NVARCHAR) ELSE '''' END AS fxa_amount
					,rb.bank_name
					,rb.bank_group_code
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
					OR rb.bsb = (LEFT(v.fxa_bsb, 3)) 
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
										AND v.fxa_processing_state = rs.state_code 
				WHERE v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
					AND v.fxa_inactive_flag = 0
					AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
					--AND (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
					--AND CAST(v.fxa_drn AS NVARCHAR) LIKE ''777%''
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))													
	WHERE v.fxa_classification = ''Cr''
		AND v.fxa_account_number NOT IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc])
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
		AND t.bank_name = @agencybank
		AND t.bank_group_code = @agencybankgroup
	GROUP BY v.fxa_bsb 
		,v.fxa_account_number 
		,CASE WHEN CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) IS NULL 
			  THEN CAST(CAST(v.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) ELSE CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) END
		,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
		,CAST(v.fxa_amount AS MONEY)/100 
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1
			  THEN ''Failed Third Party Reason''
			  ELSE ''Unencoded Deposit'' END 
		,v.fxa_batch_type_code 
		,v.folder_path
		,CAST(v.fxa_processing_date AS DATE)
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
				
	UNION ALL
		
		SELECT '''' AS ''Deposit BSB''
		,'''' AS ''Deposit Account Number''
		,'''' AS ''Original Deposit Amount''
		,'''' AS ''Adjustment Amount''
		,'''' AS ''Adjusted Deposit Amount''
		,'''' AS ''Reason''
		,'''' AS ''Batch Type Code''
		,'''' AS ''Voucher Image Path''
		,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
		,'''' AS fxa_drn
		,t1.sug_key
		,t1.fxa_drn AS DIN
		,t1.fxa_collecting_bsb AS NEGBSB
		,t1.fxa_extra_aux_dom AS EAD
		,t1.fxa_aux_dom AS AD
		,t1.fxa_bsb AS BSB
		,t1.fxa_account_number AS ''Account No''
		,t1.fxa_trancode AS TC
		,t1.fxa_classification AS ''DR/CR''
		,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2)) 
		OR rb.bsb = (LEFT(v.fxa_bsb, 3)) 
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code 
	INNER JOIN (SELECT v.fxa_amount
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key 
					,rb.bank_name
					,rb.bank_group_code
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2)) 
					OR rb.bsb = (LEFT(v.fxa_bsb, 3)) 
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
										AND v.fxa_processing_state = rs.state_code 
				WHERE (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
					AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
					AND v.fxa_inactive_flag = 0
					AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
				) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
	INNER JOIN  (SELECT v.fxa_drn 
					,v.fxa_collecting_bsb
					,v.fxa_extra_aux_dom 
					,v.fxa_aux_dom 
					,v.fxa_bsb 
					,v.fxa_account_number
					,v.fxa_trancode 
					,v.fxa_classification 
					,v.fxa_amount
					,v.fxa_processing_date
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
				FROM [dbo].[voucher] v
				WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT)
															FROM [dbo].[voucher] v
															LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2)) 
																OR rb.bsb = (LEFT(v.fxa_bsb, 3)) 
															INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																				AND v.fxa_processing_state = rs.state_code 
															WHERE (rs.parent_fi =''NAB'' OR v.fxa_account_number IN (SELECT suspense_acc FROM [dbo].[ref_suspense_acc]))
																AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
																AND v.fxa_inactive_flag = 0
																AND (v.fxa_uecd_return_flag = 1  OR v.fxa_tpc_failed_flag = 1)
																AND CAST(v.fxa_processing_date AS DATE) = @processdate
																)
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
				GROUP BY v.fxa_drn 
					,v.fxa_collecting_bsb 
					,v.fxa_extra_aux_dom 
					,v.fxa_aux_dom  
					,v.fxa_bsb 
					,v.fxa_account_number 
					,v.fxa_trancode 
					,v.fxa_classification 
					,v.fxa_amount
					,v.fxa_processing_date
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
					) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))													
	WHERE CAST(v.fxa_processing_date AS DATE) = @processdate
		AND t.bank_name = @agencybank
		AND t.bank_group_code = @agencybankgroup
	GROUP BY t1.fxa_drn
		,t1.fxa_collecting_bsb
		,t1.sug_key
		,t1.fxa_extra_aux_dom
		,t1.fxa_aux_dom
		,t1.fxa_bsb
		,t1.fxa_account_number 
		,t1.fxa_trancode 
		,t1.fxa_classification
		,CAST(t1.fxa_processing_date AS DATE)
		,CAST(t1.fxa_amount AS MONEY)/100
	) t1
ORDER BY 11,12,10 DESC;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_ECD_Return_All_Items] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Inward_FV_DR]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Inward_FV_DR]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =====================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the Agency_Inward_FV_DR Report
-- =====================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_Inward_FV_DR] @processdate DATE, @agencybank NVARCHAR (100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_drn AS DIN
		,rb.bank_code AS WorkSource
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS Account
		,v.fxa_trancode AS Trancode
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		,CASE WHEN v.fxa_collecting_bsb = ''082172'' THEN ''CRU'' ELSE rb.bank_group_code END AS bank_group_code
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	LEFT JOIN dbo.ref_bank rb1 ON rb1.bsb = (LEFT(v.fxa_bsb,2)) OR rb1.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
		AND v.fxa_processing_state = rs.state_code
	WHERE (v.fxa_classification = ''Dr'' AND (rs.parent_fi = ''NAB'' OR (rb1.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')))
		AND v.fxa_work_type_code = ''NABCHQ_INWARDFV''
		AND vt.transmission_type = ''INWARD_FOR_VALUE''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
		AND rb.bank_group_code = @agencybankgroup
		AND rb.bank_name = @agencybank;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_Inward_FV_DR] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_IPOD_Errors]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
 --=========================================================
 --Author:		James Honner
 --Create date: 23/06/2015
 --Description:	used for the Agency IPOD Errors
 --EXEC [dbo].[usp_rpt_Agency_IPOD_Errors] 
 --@processdate = ''20151012'', @agencybank = ''Citigroup Pty Ltd'', @agencybankgroup = ''CTI''
 --=========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_IPOD_Errors] @processdate DATE, @agencybank NVARCHAR (100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

	SELECT v.fxa_bsb AS ''Deposit BSB''
			,v.fxa_account_number AS ''Deposit Account Number''
			,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
			,CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjustment Amount''
			,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
			,v.fxa_adjustment_description AS ''Reason''
			,v.fxa_batch_type_code AS ''Batch Type Code''
			,v.folder_path AS ''Voucher Image Path''
			,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
			,v.fxa_drn
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
			,'''' AS DIN
			,'''' AS NEGBSB
			,'''' AS EAD
			,'''' AS AD
			,'''' AS BSB
			,'''' AS ''Account No''
			,'''' AS TC
			,'''' AS ''DR/CR''
			,'''' AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
						,rb.bank_name
						,rb.bank_group_code
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_inactive_flag = 0
						AND v.fxa_adjustment_flag = 1
						AND (rb.bank_group_code = ''MBL'' AND (v.fxa_account_number = ''052951001'' OR v.fxa_bsb = ''182222''))
							OR (rb.bank_group_code = ''CTI'' AND (v.fxa_account_number IN (''999999915'', ''9002260007'') OR v.fxa_bsb = ''242200''))
						)	t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 													
		WHERE  v.fxa_classification = ''Cr''
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
			AND t.bank_name = @agencybank
			AND t.bank_group_code = @agencybankgroup
			AND (v.fxa_account_number <> ''052951001'' OR v.fxa_bsb <> ''182222'')
			AND (v.fxa_account_number NOT IN (''999999915'', ''9002260007'') OR v.fxa_bsb <> ''242200'')
		GROUP BY v.fxa_bsb 
			,v.fxa_account_number 
			,CAST(t.fxa_amount AS MONEY)/100
			,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
			,CAST(v.fxa_amount AS MONEY)/100 
			,v.fxa_adjustment_description
			,v.fxa_batch_type_code 
			,v.folder_path 
			,CAST(v.fxa_processing_date AS DATE)
			,v.fxa_drn
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
				
			UNION ALL
		
			SELECT '''' AS ''Deposit BSB''
			,'''' AS ''Deposit Account Number''
			,'''' AS ''Original Deposit Amount''
			,'''' AS ''Adjustment Amount''
			,'''' AS ''Adjusted Deposit Amount''
			,'''' AS ''Reason''
			,'''' AS ''Batch Type Code''
			,'''' AS ''Voucher Image Path''
			,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
			,'''' AS fxa_drn
			,t1.sug_key
			,t1.fxa_drn AS DIN
			,t1.fxa_collecting_bsb AS NEGBSB
			,t1.fxa_extra_aux_dom AS EAD
			,t1.fxa_aux_dom AS AD
			,t1.fxa_bsb AS BSB
			,t1.fxa_account_number AS ''Account No''
			,t1.fxa_trancode AS TC
			,t1.fxa_classification AS ''DR/CR''
			,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
						,rb.bank_name
						,rb.bank_group_code
						,CAST(fxa_processing_date AS DATE) fxa_processing_date
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_inactive_flag = 0
						AND v.fxa_adjustment_flag = 1
						AND (rb.bank_group_code = ''MBL'' AND (v.fxa_account_number = ''052951001'' OR v.fxa_bsb = ''182222''))
							OR (rb.bank_group_code = ''CTI'' AND (v.fxa_account_number IN (''999999915'', ''9002260007'') OR v.fxa_bsb = ''242200''))
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 
		INNER JOIN  (SELECT v.fxa_drn 
						,v.fxa_collecting_bsb
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom 
						,v.fxa_bsb 
						,v.fxa_account_number
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
						OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
						AND v.fxa_processing_state = rs.state_code
					WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT)
												FROM [dbo].[voucher] v
												LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
													OR rb.bsb = (LEFT(v.fxa_bsb, 3))
												INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
													AND v.fxa_processing_state = rs.state_code
												WHERE fxa_inactive_flag = 0
													AND v.fxa_adjustment_flag = 1
													AND (rb.bank_group_code = ''MBL'' AND (v.fxa_account_number = ''052951001'' OR v.fxa_bsb = ''182222''))
														OR (rb.bank_group_code = ''CTI'' AND (v.fxa_account_number IN (''999999915'', ''9002260007'') OR v.fxa_bsb = ''242200''))
													AND CAST(v.fxa_processing_date AS DATE) = @processdate
													)
						AND rb.bank_group_code IN (''MBL'', ''CTI'')
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
					GROUP BY v.fxa_drn 
						,v.fxa_collecting_bsb 
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom  
						,v.fxa_bsb 
						,v.fxa_account_number 
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 													
		WHERE v.fxa_classification = ''Cr''
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
			AND t.bank_name = @agencybank
			AND t.bank_group_code = @agencybankgroup
			AND (v.fxa_account_number <> ''052951001'' OR v.fxa_bsb <> ''182222'')
			AND (v.fxa_account_number NOT IN (''999999915'', ''9002260007'') OR v.fxa_bsb <> ''242200'')
		GROUP BY t1.fxa_drn
			,t1.fxa_collecting_bsb
			,t1.sug_key
			,t1.fxa_extra_aux_dom
			,t1.fxa_aux_dom
			,t1.fxa_bsb
			,t1.fxa_account_number 
			,t1.fxa_trancode 
			,t1.fxa_classification
			,CAST(t1.fxa_processing_date AS DATE)
			,CAST(t1.fxa_amount AS MONEY)/100
	) t1
ORDER BY 11,12,10 DESC;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_IPOD_Errors] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Surplus_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Surplus_Items]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 01/07/2015
-- Description:	used for the Agency Surplus Items Report
--EXEC [dbo].[usp_rpt_Agency_Surplus_Items]
--@processdate = ''20151013'', @agencybank = ''Bank of Sydney Ltd'', @agencybankgroup = ''LBA''
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_Surplus_Items] @processdate DATE, @agencybank NVARCHAR (100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_collecting_bsb AS ''Collecting BSB''
		,v.fxa_drn AS DIN
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account Number''
		,v.fxa_extra_aux_dom AS EAD
		,v.fxa_aux_dom AS AD
		,v.fxa_classification
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Amount''
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code
	WHERE v.fxa_surplus_item_flag = 1
		AND v.fxa_inactive_flag = 0
		AND (rs.parent_fi = ''NAB'' OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = ''''))
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
		AND rb.bank_name = @agencybank
		AND rb.bank_group_code = @agencybankgroup;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_Surplus_Items] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Agency_Unprocessable_Paper]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Agency_Unprocessable_Paper]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ========================================================
-- Author:		James Honner
-- Create date: 18/06/2015
-- Description:	used for the AB Unprocessable Paper Report
--EXEC [dbo].[usp_rpt_Agency_Unprocessable_Paper]
--@processdate = ''20151109'', @agencybank = ''Arab Bank Australia Ltd'', @agencybankgroup = ''ARA''
--@processdate = ''20151109'', @agencybank = ''The Royal Bank of Scotland PLC, Australia Branch'', @agencybankgroup = ''RBS''
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Agency_Unprocessable_Paper] @processdate DATE, @agencybank NVARCHAR(100), @agencybankgroup NVARCHAR (10)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_drn AS DIN
			,'''' AS ''Account Name''
			,CASE WHEN v.fxa_classification = ''Cr'' THEN v.fxa_bsb ELSE '''' END AS ''Negotiating BSB''
			,CASE WHEN v.fxa_classification = ''Cr'' THEN v.fxa_account_number ELSE '''' END AS ''Negotiating Account Number''
			,CASE WHEN v.fxa_classification = ''Cr'' THEN CONVERT(NVARCHAR(10),CAST(v.fxa_amount/100 AS MONEY)) ELSE '''' END AS ''Deposit Amount'' 
			,CASE WHEN v.fxa_classification = ''Dr'' THEN v.fxa_bsb ELSE '''' END AS ''Drawer''
			,CASE WHEN v.fxa_classification = ''Dr'' THEN CONVERT(NVARCHAR(10),CAST(v.fxa_amount/100AS MONEY)) ELSE '''' END AS ''Cheque Amount''
			,''Unprocessable'' AS ''Exception Reason''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
			AND v.fxa_processing_state = rs.state_code
	WHERE (rs.parent_fi = ''NAB'' OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = ''''))
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'')
		AND v.fxa_unprocessable_item_flag = 1
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
		AND rb.bank_group_code = @agencybankgroup
		AND rb.bank_name = @agencybank
	ORDER BY v.fxa_drn;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_Agency_Unprocessable_Paper] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_All_Items]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_All_Items]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ================================================
-- Author:		James Honner
-- Create date: 27/04/2015
-- Description:	used for the NAB All_Items Report
--EXEC [dbo].[usp_rpt_All_Items]
--@ProcessDate = ''20151117'', @ProcessingState = ''VIC'', @BankCode = ''NAB''
-- ================================================
CREATE PROCEDURE [dbo].[usp_rpt_All_Items] @ProcessDate DATE, @ProcessingState NVARCHAR(5), @BankCode NVARCHAR(3)
AS

BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_batch_number AS BatchNumber
		,v.fxa_tran_link_no AS TransactionNumber
		,v.fxa_drn AS Drn
		,v.fxa_collecting_bsb AS CollectingBSB
		,v.fxa_extra_aux_dom AS ExtraAuxDom
		,v.fxa_aux_dom AS AuxDom
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS AccountNumber
		,v.fxa_trancode AS TranCode
		,v.fxa_classification AS Classification
		,CAST(v.fxa_amount AS MONEY) / 100.0 AS Amount
		,v.fxa_processing_state AS [State]
		,SUBSTRING(vt.[filename], 16, 2) + SUBSTRING(vt.[filename], 28, 2) AS RunNumber
	FROM [dbo].[voucher] v
	LEFT JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id AND vt.[status] = ''Sent'' AND vt.transmission_type = ''VIF_OUTBOUND''
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_capture_bsb,2)) OR rb.bsb = (LEFT(v.fxa_capture_bsb,3))
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_unprocessable_item_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''NABCHQ_APOST'', ''NABCHQ_INWARDFV'')
		AND v.fxa_classification <> ''Sp''
		AND rb.bank_group_code = @BankCode
		AND CAST(v.fxa_processing_date AS DATE) = @ProcessDate
	    AND v.fxa_processing_state = @ProcessingState
		AND ((v.fxa_classification IN (''Cr'', ''Dr'') AND vt.v_i_chronicle_id IS NOT NULL) OR (v.fxa_classification = ''Bh''))
	ORDER BY v.fxa_batch_number 
		,v.fxa_tran_link_no
		,CASE WHEN v.fxa_classification = ''Bh'' THEN 1
			  WHEN v.fxa_classification = ''Dr'' THEN 2
			  WHEN v.fxa_classification = ''Cr'' THEN 3
			  END
		,v.fxa_drn
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_All_Items] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_All_Items_Summary]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_All_Items_Summary]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ================================================
-- Author:		James Honner
-- Create date: 27/04/2015
-- Description:	used for the NAB All Items File
 --EXEC [dbo].[usp_rpt_NAB_All_Items_File_Total] ''2015/11/17'', ''VIC''
-- ================================================
CREATE PROCEDURE [dbo].[usp_rpt_All_Items_Summary] @ProcessDate DATE, @ProcessingState NVARCHAR(5), @BankCode NVARCHAR(3)
AS

BEGIN
	SET NOCOUNT ON;

	SELECT SUM (CASE WHEN v.fxa_classification = ''Dr'' THEN 1 ELSE 0 END) AS DrCount
		,SUM (CASE WHEN v.fxa_classification = ''Dr'' THEN CAST(v.fxa_amount AS MONEY) / 100.0 ELSE ''0'' END) AS DrTotal
		,SUM (CASE WHEN v.fxa_classification = ''Cr'' THEN 1 ELSE 0 END) AS CrCount
		,SUM (CASE WHEN v.fxa_classification = ''Cr'' THEN CAST(v.fxa_amount AS MONEY) / 100.0 ELSE ''0'' END) AS CrTotal
	FROM [dbo].[voucher] v
	LEFT JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id AND vt.[status] = ''Sent'' AND vt.transmission_type = ''VIF_OUTBOUND''
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_capture_bsb,2)) OR rb.bsb = (LEFT(v.fxa_capture_bsb,3))
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_unprocessable_item_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''NABCHQ_APOST'', ''NABCHQ_INWARDFV'')
		AND v.fxa_classification <> ''Sp''
		AND rb.bank_group_code = @BankCode
		AND CAST(v.fxa_processing_date AS DATE) = @ProcessDate
	    AND v.fxa_processing_state = @ProcessingState
		AND ((v.fxa_classification IN (''Cr'', ''Dr'') AND vt.v_i_chronicle_id IS NOT NULL) OR (v.fxa_classification = ''Bh''))
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_All_Items_Summary] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_Document_Retrieval]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_Document_Retrieval]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 30/06/2015
-- Description:	used for the BQL Document Retreival Report
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_BQL_Document_Retrieval] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT CASE WHEN va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN va.operator_name ELSE '''' END AS ''Requestor''
		,CASE WHEN va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN va.post_value ELSE '''' END AS ''Priority''
		,v.fxa_doc_retr_seq_id	AS ''Request ID''
		,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
		,v.fxa_drn AS DIN
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
		,v.fxa_batch_number
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,CASE WHEN (v.fxa_work_type_code = ''NABCHQ_LBOX'') THEN ''Locked Box''
			  WHEN (v.fxa_work_type_code = ''NABCHQ_APOST'') THEN ''Australia Post''
			  WHEN (v.fxa_batch_type_code <> '''' 
				AND v.fxa_work_type_code = ''NABCHQ_POD'') THEN v.fxa_batch_type_code
			  WHEN (v.fxa_batch_type_code = '''' 
				AND v.fxa_work_type_code = ''NABCHQ_POD'') THEN ''NABCHQ_POD'' ELSE '''' END AS ''Work Type''
		,v.fxa_processing_state AS ''Processing Centre''
		,CASE WHEN v.fxa_capture_bsb IN (''082082'', ''084014'') THEN ''DPR Northern''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1 
				AND v.fxa_release_flag IN (''reject_user'', ''reject_system'') THEN ''DPR Southern''
			  WHEN v.fxa_doc_retr_flag = 1 AND va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN v.fxa_doc_retr_delivery_site
			  WHEN v.fxa_unprocessable_item_flag = 1 AND v.fxa_capture_bsb IN (''082082'', ''084014'') THEN ''DPR Northern'' 
			  WHEN (v.fxa_unprocessable_item_flag = 1 
				OR v.fxa_uecd_return_flag = 1 
				OR v.fxa_tpc_failed_flag = 1 
				OR v.fxa_surplus_item_flag = 1) 
				AND (rs.parent_fi = ''NAB'' 
				OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')) THEN rb.bank_name
			  WHEN v.fxa_doc_retr_flag = 1 
				AND va.subject_area = ''docr'' 
				AND va.attribute_name = ''delivery_site'' THEN ''va.post_value''
			  WHEN v.fxa_doc_retr_flag = 1
				AND v.fxa_work_type_code = ''NABCHQ_APOST'' THEN ''Australia Post'' ELSE ''DPR Southern'' END AS ''Delivery Site''
		,CASE WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1
				AND v.fxa_release_flag = ''reject_user'' THEN ''Rejected TPC''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1
				AND v.fxa_release_flag = ''reject_system'' THEN ''Returned TPC''
			  WHEN v.fxa_unprocessable_item_flag = 1 THEN ''Unprocessable Item''
			  WHEN v.fxa_uecd_return_flag = 1 THEN ''Unencoded ECD''
			  WHEN v.fxa_tpc_failed_flag = 1 THEN ''TPC Failed''
			  WHEN v.fxa_surplus_item_flag = 1 
				AND (rs.parent_fi = ''NAB'' 
				OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')) THEN ''Surplus Item''
			  WHEN v.fxa_doc_retr_flag = 1
				AND v.fxa_work_type_code = ''NABCHQ_APOST'' THEN ''Australia Post Unmatched''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND va.subject_area = ''docr'' 
				AND va.attribute_name = ''priority'' THEN ''Manual Retrieval Request'' ELSE '''' END AS ''Return Reason''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
	LEFT JOIN (SELECT i_chronicle_id
					,va.subject_area
					,va.attribute_name
					,va.operator_name
					,va.post_value
				FROM [dbo].[voucher_audit] va 
				WHERE va.subject_area = ''docr''
				AND va.attribute_name IN (''priority'', ''delivery_site'')) AS va ON va.i_chronicle_id = v.i_chronicle_id
	WHERE v.fxa_inactive_flag = 0
		AND ((CAST(v.fxa_doc_retr_date AS DATE) = @processdate OR CAST(v.fxa_processing_date AS DATE) = @processdate)
			AND v.fxa_capture_bsb = ''124001''
			AND (v.fxa_doc_retr_flag = 1 OR v.fxa_tpc_failed_flag = 1 OR v.fxa_unprocessable_item_flag = 1)
			AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST''))
		OR ((CAST(v.fxa_doc_retr_date AS DATE) = @processdate OR CAST(v.fxa_processing_date AS DATE) = @processdate)
			AND v.fxa_capture_bsb = ''124001''
			AND (v.fxa_uecd_return_flag = 1 OR v.fxa_tpc_failed_flag = 1 OR v.fxa_unprocessable_item_flag = 1 OR v.fxa_surplus_item_flag = 1)
			--AND (rs.parent_fi = ''NAB'' OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = ''''))
			AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD''));
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_BQL_Document_Retrieval] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_IPOD_Errors]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ========================================================
-- Author:		James Honner
-- Create date: 23/06/2015
-- Description:	used for the BQL IPOD Errors
--EXEC [dbo].[usp_rpt_BQL_IPOD_Errors]
--@processdate = ''20151203''
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_BQL_IPOD_Errors] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

	SELECT v.fxa_bsb AS ''Deposit BSB''
			,v.fxa_account_number AS ''Deposit Account Number''
			,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
			,CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjustment Amount''
			,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
			,v.fxa_adjustment_description AS ''Reason''
			,v.fxa_batch_type_code AS ''Batch Type Code''
			,v.folder_path AS ''Voucher Image Path''
			,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
			,v.fxa_drn
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
			,'''' AS DIN
			,'''' AS NEGBSB
			,'''' AS EAD
			,'''' AS AD
			,'''' AS BSB
			,'''' AS ''Account No''
			,'''' AS TC
			,'''' AS ''DR/CR''
			,'''' AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
						,CAST(fxa_processing_date AS DATE) fxa_processing_date
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE rb.bank_code IN (	''BQL'',''HOM'',''PPB'')
						AND v.fxa_capture_bsb = ''124001''
						--(v.fxa_capture_bsb = ''124001'' OR rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')
						AND v.fxa_work_type_code = ''NABCHQ_POD''
						AND v.fxa_adjustment_flag = 1
						AND v.fxa_inactive_flag = 0
						AND (v.fxa_account_number = ''10457814'' OR v.fxa_bsb = ''124001'')
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
						)	t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))													
		WHERE v.fxa_inactive_flag = 0
			AND v.fxa_classification = ''Cr''
			AND (v.fxa_account_number <> ''10457814'' OR v.fxa_bsb <> ''124001'')
			AND CAST(v.fxa_processing_date AS DATE)  = @processdate
		GROUP BY v.fxa_bsb 
			,v.fxa_account_number 
			,CAST(t.fxa_amount AS MONEY)/100
			,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
			,CAST(v.fxa_amount AS MONEY)/100 
			,v.fxa_adjustment_description
			,v.fxa_batch_type_code 
			,v.folder_path 
			,CAST(v.fxa_processing_date AS DATE) 
			,v.fxa_drn
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
				
		UNION ALL
		
			SELECT '''' AS ''Deposit BSB''
			,'''' AS ''Deposit Account Number''
			,'''' AS ''Original Deposit Amount''
			,'''' AS ''Adjustment Amount''
			,'''' AS ''Adjusted Deposit Amount''
			,'''' AS ''Reason''
			,'''' AS ''Batch Type Code''
			,'''' AS ''Voucher Image Path''
			,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
			,'''' AS fxa_drn
			,t1.sug_key
			,t1.fxa_drn AS DIN
			,t1.fxa_collecting_bsb AS NEGBSB
			,t1.fxa_extra_aux_dom AS EAD
			,t1.fxa_aux_dom AS AD
			,t1.fxa_bsb AS BSB
			,t1.fxa_account_number AS ''Account No''
			,t1.fxa_trancode AS TC
			,t1.fxa_classification AS ''DR/CR''
			,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
						,CAST(fxa_processing_date AS DATE) fxa_processing_date
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE rb.bank_code IN (''BQL'',''HOM'',''PPB'')
						AND v.fxa_capture_bsb = ''124001''
						--(v.fxa_capture_bsb = ''124001'' OR rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')
						AND v.fxa_work_type_code = ''NABCHQ_POD''
						AND v.fxa_adjustment_flag = 1
						AND v.fxa_inactive_flag = 0
						AND (v.fxa_account_number = ''10457814'' OR v.fxa_bsb = ''124001'')
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
						)	t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
		LEFT JOIN  (SELECT v.fxa_drn 
						,v.fxa_collecting_bsb
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom 
						,v.fxa_bsb 
						,v.fxa_account_number
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
																FROM [dbo].[voucher] v
																LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
																INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																							AND v.fxa_processing_state = rs.state_code
																WHERE rb.bank_code IN (''BQL'',''HOM'',''PPB'')
																	AND v.fxa_capture_bsb = ''124001''
																	--(v.fxa_capture_bsb = ''124001'' OR rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')
																	AND v.fxa_work_type_code = ''NABCHQ_POD''
																	AND v.fxa_adjustment_flag = 1
																	AND v.fxa_inactive_flag = 0
																	AND (v.fxa_account_number = ''10457814'' OR v.fxa_bsb = ''124001'')
																	AND CAST(v.fxa_processing_date AS DATE) = @processdate
																)
					GROUP BY v.fxa_drn 
						,v.fxa_collecting_bsb 
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom  
						,v.fxa_bsb 
						,v.fxa_account_number 
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))														
		WHERE v.fxa_inactive_flag = 0
			AND v.fxa_classification = ''Cr''
			AND (v.fxa_account_number <> ''10457814'' OR v.fxa_bsb <> ''124001'')
			AND CAST(v.fxa_processing_date AS DATE)  = @processdate
		GROUP BY t1.fxa_drn
			,t1.fxa_collecting_bsb
			,t1.sug_key
			,t1.fxa_extra_aux_dom
			,t1.fxa_aux_dom
			,t1.fxa_bsb
			,t1.fxa_account_number 
			,t1.fxa_trancode 
			,t1.fxa_classification
			,CAST(t1.fxa_processing_date AS DATE)
			,CAST(t1.fxa_amount AS MONEY)/100
	) t1
ORDER BY 11,12,10 DESC;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_BQL_IPOD_Errors] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_Outward_FV_CR]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_Outward_FV_CR]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the BQL_Outward FV CR Report
--EXEC [dbo].[usp_rpt_BQL_Outward_FV_CR]
--@processdate = ''20151109''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_BQL_Outward_FV_CR] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;
 
	SELECT v.fxa_drn AS DIN
		,CASE WHEN vt.target_end_point IN (''ANZ'', ''RBA'', ''BQL'') THEN vt.target_end_point
              WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''245485472'') THEN ''CBA''
              WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''823300223'') THEN ''WBC''
              WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number NOT IN  (''823300223'', ''245485472'')) THEN vt.target_end_point ELSE '''' END AS ''Receiving Bank''
		,v.fxa_extra_aux_dom AS EAD
		,v.fxa_collecting_bsb
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS Account
		,v.fxa_trancode AS Trancode
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		,v.fxa_processing_state
		,v.fxa_batch_number
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
		AND v.fxa_processing_state = rs.state_code
	WHERE (v.fxa_capture_bsb = ''124001'' OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = ''''))
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_APOST'')
        AND v.fxa_classification = ''Cr''
        AND v.fxa_for_value_type = ''Outward_For_Value''
        AND v.fxa_inactive_flag = 0
        AND vt.transmission_type LIKE ''IMAGE_EXCHANGE_OUTBOUND%''
        AND CAST(v.fxa_processing_date AS DATE) = @processdate
        AND (vt.target_end_point IN (''ANZ'', ''RBA'', ''BQL'')
            OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''245485472'')
            OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''823300223'')
            OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number NOT IN  (''823300223'', ''245485472'')));
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_BQL_Outward_FV_CR] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_BQL_VIF_Transmission]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_BQL_VIF_Transmission]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =======================================================
-- Author:		James Honner
-- Create date: 28/04/2015
-- Description:	used for the BQL VIF Transmission Report
-- =======================================================
CREATE PROCEDURE [dbo].[usp_rpt_BQL_VIF_Transmission] @transmissiondate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT vt.[filename] AS ''File No''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0''	END) AS ''Total Debits''
		,SUM(CASE WHEN (v.fxa_classification = ''Dr'')
				  THEN 1 ELSE ''0'' END) AS ''Debit Count''
		,SUM(CASE WHEN v.fxa_classification = ''Cr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END) AS ''Total Credits''
		,SUM(CASE WHEN (v.fxa_classification = ''Cr'')
				  THEN 1 ELSE ''0''END) AS ''Credit Count''
		,CAST(vt.transmission_date AS DATETIME) AS ''Run Time''
		,v.fxa_processing_state AS ''Site ID''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	WHERE v.fxa_capture_bsb = ''124001''
		AND vt.[status] = ''Sent''
		AND vt.transmission_type = ''VIF_OUTBOUND''
		AND v.fxa_classification NOT IN (''Bh'', ''Sp'')
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_INWARDFV'',''NABCHQ_APOST'')
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @transmissiondate
	GROUP BY vt.[filename]
		,v.fxa_processing_state
		,CAST(vt.transmission_date AS DATETIME)
	ORDER BY vt.[filename];
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_BQL_VIF_Transmission] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_Corporate_Adjustment_Letter]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_Corporate_Adjustment_Letter]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ========================================================
-- Author:		James Honner
-- Create date: 10/09/2015
-- Description:	used for the Corporate Adjustments Letter 
--EXEC [dbo].[usp_rpt_Corporate_Adjustment_Letter]
-- @processdate = ''20151201'',
-- @corporategroup = ''CSP1''
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_Corporate_Adjustment_Letter] @processdate DATE, @corporategroup NVARCHAR(4)
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

	SELECT v.fxa_bsb AS ''Deposit BSB''
		,v.fxa_extra_aux_dom AS ''Deposit EAD''
		,v.fxa_account_number AS ''Deposit Account Number''
		,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
		,CAST(CAST(t.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Adjustment Amount''
		,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
		,v.fxa_adjustment_description AS ''Reason''
		,v.fxa_batch_type_code AS ''Batch Type Code''
		,v.folder_path AS ''Voucher Image Path''
		,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
		,'''' AS DIN
		,'''' AS NEGBSB
		,'''' AS EAD
		,'''' AS AD
		,'''' AS BSB
		,'''' AS ''Account No''
		,'''' AS TC
		,'''' AS ''DR/CR''
		,'''' AS Amount
		,t.customer_name
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					,v.fxa_amount
					,c.customer_name
				FROM [dbo].[voucher] v
				LEFT JOIN [dbo].[ref_corporate] c ON (c.bsb = v.fxa_bsb AND c.acc = v.fxa_account_number)
				WHERE v.fxa_adjustment_flag = 1
					AND v.fxa_work_type_code = ''NABCHQ_POD''
					AND v.fxa_inactive_flag = 0
					AND c.corporate_group_code = @corporategroup
					AND c.[status] = ''A''
					AND c.effective_date < GETDATE()
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 
	WHERE v.fxa_classification = ''Cr''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY t.customer_name
		,v.fxa_bsb 
		,v.fxa_extra_aux_dom
		,v.fxa_account_number 
		,CAST(t.fxa_amount-v.fxa_pre_adjustment_amt AS MONEY)/100
		,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
		,CAST(v.fxa_amount AS MONEY)/100 
		,v.fxa_adjustment_description
		,v.fxa_batch_type_code 
		,v.folder_path 
		,CAST(v.fxa_processing_date AS DATE) 
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
				
	UNION ALL
		
		SELECT '''' AS ''Deposit BSB''
		,'''' AS ''Deposit EAD''
		,'''' AS ''Deposit Account Number''
		,'''' AS ''Original Deposit Amount''
		,'''' AS ''Adjustment Amount''
		,'''' AS ''Adjusted Deposit Amount''
		,'''' AS ''Reason''
		,'''' AS ''Batch Type Code''
		,'''' AS ''Voucher Image Path''
		,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
		,'''' AS fxa_drn
		,t1.sug_key
		,t1.fxa_drn AS DIN
		,t1.fxa_collecting_bsb AS NEGBSB
		,t1.fxa_extra_aux_dom AS EAD
		,t1.fxa_aux_dom AS AD
		,t1.fxa_bsb AS BSB
		,t1.fxa_account_number AS ''Account No''
		,t1.fxa_trancode AS TC
		,t1.fxa_classification AS ''DR/CR''
		,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		,t.customer_name
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					,v.fxa_amount
					,c.customer_name
				FROM [dbo].[voucher] v
				left JOIN [dbo].[ref_corporate] c ON (c.bsb = v.fxa_bsb AND c.acc = v.fxa_account_number)
				WHERE v.fxa_adjustment_flag = 1
					AND v.fxa_work_type_code = ''NABCHQ_POD''
					AND v.fxa_inactive_flag = 0
					AND c.corporate_group_code = @corporategroup
					AND c.[status] = ''A''
					AND c.effective_date < GETDATE()
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					)	t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
	LEFT JOIN  (SELECT v.fxa_drn 
					,v.fxa_collecting_bsb
					,v.fxa_extra_aux_dom 
					,v.fxa_aux_dom 
					,v.fxa_bsb 
					,v.fxa_account_number
					,v.fxa_trancode 
					,v.fxa_classification 
					,v.fxa_amount
					,v.fxa_processing_date
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
				FROM [dbo].[voucher] v
				WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
															FROM [dbo].[voucher] v
															INNER JOIN [dbo].[ref_corporate] c ON c.bsb = v.fxa_bsb
																							  AND c.acc = v.fxa_account_number
															WHERE v.fxa_adjustment_flag = 1
																AND v.fxa_work_type_code = ''NABCHQ_POD''
																AND v.fxa_inactive_flag = 0
																AND c.corporate_group_code = @corporategroup
																AND c.[status] = ''A''
																AND c.effective_date < GETDATE()
																AND CAST(v.fxa_processing_date AS DATE) = @processdate
															)
				GROUP BY v.fxa_drn 
					,v.fxa_collecting_bsb 
					,v.fxa_extra_aux_dom 
					,v.fxa_aux_dom  
					,v.fxa_bsb 
					,v.fxa_account_number 
					,v.fxa_trancode 
					,v.fxa_classification 
					,v.fxa_amount
					,v.fxa_processing_date
					,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 
				) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))													
	WHERE  v.fxa_adjustment_flag = 1
		AND v.fxa_work_type_code = ''NABCHQ_POD''
		AND v.fxa_classification = ''Cr''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY t1.fxa_drn
		,t1.fxa_collecting_bsb
		,t1.sug_key
		,t1.fxa_extra_aux_dom
		,t1.fxa_aux_dom
		,t1.fxa_bsb
		,t1.fxa_account_number 
		,t1.fxa_trancode 
		,t1.fxa_classification
		,CAST(t1.fxa_processing_date AS DATE)
		,CAST(t1.fxa_amount AS MONEY)/100
		,t.customer_name
	) t1
ORDER BY 12,13,11 DESC;
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_FXA_OCR_vs_Human]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_FXA_OCR_vs_Human]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 07/08/2015
-- Description:	used for the FXA OCR vs Human Report
 --EXEC [dbo].[usp_rpt_FXA_OCR_vs_Human]
 --@processdate = ''20151125''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_FXA_OCR_vs_Human]  @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_unit_id AS ''Machine ID''
		,vc.machine_count as ''Total Vouchers Processed''
		,vc.[Batch Headers]
		,vc.Vouchers
		,vc.Generated_Vouchers
		,SUM(CASE WHEN va.subject_area = ''car'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0	END) AS ''Items through CAR''
		,SUM(CASE WHEN va.subject_area = ''cdv''
				AND va.attribute_name = ''amount_status'' 
				AND va.post_value = 1 THEN 1 ELSE 0	END) AS ''Pass CAR confidence level''
		,SUM(CASE WHEN va.subject_area = ''car'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0	END)
			-SUM(CASE WHEN va.subject_area = ''cdv''
				AND va.attribute_name = ''amount_status'' 
				AND va.post_value = 1 THEN 1 ELSE 0	END) AS ''Fail CAR confidence level''
		,SUM(CASE WHEN va.subject_area = ''cdv'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END) AS ''Items Captured''
		,SUM(CASE WHEN va.subject_area = ''cdc''
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END) AS ''Pass initial codeline validation''
		,SUM(CASE WHEN va.subject_area = ''cdv'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END)
			-SUM(CASE WHEN va.subject_area = ''cdc''
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0	END) AS ''Fail initial codeline validation''
		,SUM(CASE WHEN va.subject_area = ''abal'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END) AS ''Transactions through auto balancing''
		,SUM(CASE WHEN va.subject_area = ''abal'' 
				AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END)
			-SUM(CASE WHEN va.subject_area = ''ebal'' 
				AND va.attribute_name = ''timings'' 
				AND v.fxa_generated_voucher_flag = 0 
				AND v.fxa_generated_bulk_cr_flag = 0 THEN 1 ELSE 0 END) AS ''Pass auto balancing''
		,SUM(CASE WHEN va.subject_area = ''ebal'' 
				AND va.attribute_name = ''timings'' 
				AND v.fxa_generated_voucher_flag = 0 
				AND v.fxa_generated_bulk_cr_flag = 0 THEN 1 ELSE 0 END) AS ''Failed auto balancing''
	FROM [dbo].[voucher] v
	LEFT JOIN (SELECT va.post_value
					,va.subject_area
					,va.attribute_name
					,va.i_chronicle_id
				FROM [dbo].[voucher_audit] va
					) va ON va.i_chronicle_id = v.i_chronicle_id
	LEFT JOIN (SELECT COUNT(v.fxa_drn) machine_count
					,SUM(CASE WHEN v.fxa_classification = ''Bh''  THEN 1 ELSE 0 END) AS ''Batch Headers''
					,SUM(CASE WHEN v.fxa_classification <> ''Bh'' AND v.fxa_generated_voucher_flag = 0 AND v.fxa_generated_bulk_cr_flag = 0 THEN 1 ELSE 0 END) Vouchers
					,SUM(CASE WHEN (v.fxa_classification <> ''Bh'' AND v.fxa_generated_voucher_flag = 1) 
								OR (v.fxa_classification <> ''Bh'' AND v.fxa_generated_bulk_cr_flag = 1) THEN 1 ELSE 0 END)  Generated_Vouchers
					,v.fxa_unit_id
				FROM [dbo].[voucher] v
				WHERE v.fxa_inactive_flag = 0
				AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''NABCHQ_APOST'', ''NABCHQ_SURPLUS'')
				AND CAST(v.fxa_processing_date AS DATE) = @processdate
				GROUP BY v.fxa_unit_id) vc ON vc.fxa_unit_id = v.fxa_unit_id
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''NABCHQ_APOST'', ''NABCHQ_SURPLUS'')
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY v.fxa_unit_id,vc.machine_count
		,CAST(v.fxa_processing_date AS DATE)
		,vc.[Batch Headers]
		,vc.Vouchers
		,vc.Generated_Vouchers
	ORDER BY v.fxa_unit_id;		
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_FXA_OCR_vs_Human] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_FXA_QA]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_FXA_QA]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 30/06/2015
-- Description:	used for the FXA QA Report
--EXEC [dbo].[usp_rpt_FXA_QA]
--@processdate = ''20151204''
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_FXA_QA] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT CAST(v.fxa_processing_date AS DATE) AS Processing_Date
			,v.fxa_drn AS DIN
			,va.operator_name AS Dips_Operator
			,CASE WHEN (v.fxa_ptqa_amt_flag = 1 AND ve.subject_area = ''ebal'' AND ve.attribute_name = ''amt'') THEN ve.operator_name 
				  WHEN (v.fxa_ptqa_amt_flag = 1 AND vc.subject_area = ''cdc'' AND vc.attribute_name = ''amt'') THEN vc.operator_name 
				  ELSE ''No change to amount'' END AS Amount_OPERATOR
			,CAST(v.fxa_amount AS MONEY)/100 AS Amount
			,CASE WHEN (v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 0) THEN ''Failed'' 
			      WHEN (v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 1) THEN ''Passed'' 
				  ELSE ''Not Assessed'' END AS Amount_QA_Result
			,CASE WHEN (v.fxa_ptqa_code_line_flag = 1 AND ve.subject_area = ''ebal'' AND ve.attribute_name IN (''acc'', ''ead'', ''ad'', ''bsb'', ''tc'')) THEN ve.operator_name 
				  WHEN (v.fxa_ptqa_code_line_flag = 1 AND vc.subject_area = ''cdc'' AND vc.attribute_name IN (''acc'', ''ead'', ''ad'', ''bsb'', ''tc'')) THEN vc.operator_name 
				  ELSE ''No change to codeline'' END AS OPERATOR
		--	,CASE WHEN (v.fxa_ptqa_code_line_flag = 1) THEN vd.operator_name ELSE '''' END AS Codeline_OPERATOR
			,v.fxa_extra_aux_dom AS EAD
			,v.fxa_aux_dom AS AD
			,v.fxa_bsb AS BSB
			,v.fxa_account_number AS ACCOUNT
			,v.fxa_trancode AS TC
			,CASE WHEN (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 0) THEN ''Failed'' 
			      WHEN (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 1) THEN ''Passed'' 
				  ELSE ''Not Assessed'' END AS Codeline_QA_Result
		FROM [dbo].[voucher] v
		LEFT JOIN (SELECT va.operator_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''dips'' 
						AND va.attribute_name = ''operator_name'') va ON va.i_chronicle_id = v.i_chronicle_id
																   AND va.Row_Num = 1
		LEFT JOIN (SELECT va.pre_value
						,va.post_value
						,va.operator_name 
						,va.i_chronicle_id
						,va.subject_area 
						,va.attribute_name
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''qa''
						AND va.attribute_name = ''amt_accurate'') vb ON vb.i_chronicle_id = v.i_chronicle_id 
																	AND vb.Row_Num = 1
		LEFT JOIN (SELECT va.operator_name
						,va.subject_area 
						,va.attribute_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''cdc''
						AND va.attribute_name IN (''amt'', ''acc'', ''ead'', ''ad'', ''bsb'', ''tc'')
					) vc ON vc.i_chronicle_id = v.i_chronicle_id
					    AND vc.Row_Num = 1
		LEFT JOIN (SELECT va.operator_name
						,va.subject_area 
						,va.attribute_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''ebal''
						AND va.attribute_name IN (''amt'', ''acc'', ''ead'', ''ad'', ''bsb'', ''tc'')
					) ve ON ve.i_chronicle_id = v.i_chronicle_id
					    AND ve.Row_Num = 1
		LEFT JOIN (SELECT va.pre_value
						,va.post_value
						,va.operator_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''qa''
						AND va.attribute_name = ''codeline_entry_accurate'') vd ON vd.i_chronicle_id = v.i_chronicle_id
																				AND vd.Row_Num = 1
		WHERE v.fxa_inactive_flag = 0
			AND ((v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 0) OR (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 0))
			AND (CAST(v.fxa_ptqa_amt_processed_date AS DATE) = @processdate OR CAST(v.fxa_ptqa_cdc_processed_date AS DATE) = @processdate)
		ORDER BY v.fxa_drn;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_FXA_QA] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Adjustment_Letter]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Adjustment_Letter]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ========================================================
-- Author:		James Honner
-- Create date: 18/06/2015
-- Description:	used for the NAB Adjustments Letter 
-- =========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Adjustment_Letter] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

		SELECT v.fxa_bsb AS ''Deposit BSB''
			,v.fxa_account_number AS ''Deposit Account Number''
			,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
			,CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjustment Amount''
			,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
			,v.fxa_adjustment_description AS ''Reason''
			,v.fxa_batch_type_code AS ''Batch Type Code''
			,v.folder_path AS ''Voucher Image Path''
			,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
			,'''' AS DIN
			,'''' AS NEGBSB
			,'''' AS EAD
			,'''' AS AD
			,'''' AS BSB
			,'''' AS ''Account No''
			,'''' AS TC
			,'''' AS ''DR/CR''
			,'''' AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_adj_letter_req_flag = 1
						AND v.fxa_inactive_flag = 0
						AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
						AND v.fxa_classification = ''Cr''
						AND v.fxa_account_number = ''899919946''
						AND rb.bank_code IN (''NAB'',''BNZ'')
						AND rs.parent_fi = ''''
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
						)	t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 													
		WHERE v.fxa_classification = ''Cr''
			AND v.fxa_account_number <> ''899919946''
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
		GROUP BY v.fxa_bsb 
			,v.fxa_account_number 
			,CAST(t.fxa_amount AS MONEY)/100
			,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
			,CAST(v.fxa_amount AS MONEY)/100 
			,v.fxa_adjustment_description
			,v.fxa_batch_type_code 
			,v.folder_path 
			,CAST(v.fxa_processing_date AS DATE)
			,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
				
			UNION ALL
		
		SELECT '''' AS ''Deposit BSB''
			,'''' AS ''Deposit Account Number''
			,'''' AS ''Original Deposit Amount''
			,'''' AS ''Adjustment Amount''
			,'''' AS ''Adjusted Deposit Amount''
			,'''' AS ''Reason''
			,'''' AS ''Batch Type Code''
			,'''' AS ''Voucher Image Path''
			,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
			,t1.sug_key
			,t1.fxa_drn AS DIN
			,t1.fxa_collecting_bsb AS NEGBSB
			,t1.fxa_extra_aux_dom AS EAD
			,t1.fxa_aux_dom AS AD
			,t1.fxa_bsb AS BSB
			,t1.fxa_account_number AS ''Account No''
			,t1.fxa_trancode AS TC
			,t1.fxa_classification AS ''DR/CR''
			,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_adj_letter_req_flag = 1
						AND v.fxa_inactive_flag = 0
						AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
						AND v.fxa_classification = ''Cr''
						AND v.fxa_account_number = ''899919946''
						AND rb.bank_code IN (''NAB'',''BNZ'')
						AND rs.parent_fi = ''''
						) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
		LEFT JOIN  (SELECT v.fxa_drn 
						,v.fxa_collecting_bsb
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom 
						,v.fxa_bsb 
						,v.fxa_account_number
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
																FROM [dbo].[voucher] v
																LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
																	OR rb.bsb = (LEFT(v.fxa_bsb, 3))
																INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																	AND v.fxa_processing_state = rs.state_code
																WHERE v.fxa_adj_letter_req_flag = 1
																	AND v.fxa_inactive_flag = 0
																	AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
																	AND v.fxa_classification = ''Cr''
																	AND v.fxa_account_number = ''899919946''
																	AND rb.bank_code IN (''NAB'',''BNZ'')
																	AND rs.parent_fi = ''''
																	)
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
					GROUP BY v.fxa_drn 
						,v.fxa_collecting_bsb 
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom  
						,v.fxa_bsb 
						,v.fxa_account_number 
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))													
		WHERE v.fxa_account_number <> ''899919946''
			AND v.fxa_classification = ''Cr''
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
		GROUP BY t1.fxa_drn
			,t1.fxa_collecting_bsb
			,t1.sug_key
			,t1.fxa_extra_aux_dom
			,t1.fxa_aux_dom
			,t1.fxa_bsb
			,t1.fxa_account_number 
			,t1.fxa_trancode 
			,t1.fxa_classification
			,t1.fxa_processing_date
			,CAST(t1.fxa_amount AS MONEY)/100
		) t1
	ORDER BY 10,11;
END








' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Adjustment_Letter] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Adjustments]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Adjustments]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =================================================
-- Author:		James Honner
-- Create date: 14/04/2015
-- Description:	used for the NAB Adjustments Report
-- =================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Adjustments] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_batch_number AS BCH
		,CAST(v.fxa_tran_link_no AS BIGINT) AS TXN
		,v.fxa_drn AS DIN
		,v.fxa_collecting_bsb AS NEGBSB
		,'''' AS BUNDLE
		,v.fxa_extra_aux_dom AS EAD
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account No''
		,v.fxa_trancode AS TC
		,v.fxa_classification AS ''DR/CR''
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,v.fxa_adjusted_by_id AS ''Adjustment performed by''
		,v.fxa_adjustment_reason_code AS ''Adjustment Reason Code''
		,v.fxa_adjustment_description AS ''Adjustment Description''
		,v.fxa_processing_state
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
	FROM [dbo].[voucher] v
	WHERE v.fxa_adjustment_flag = 1
		AND v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (
			''NABCHQ_POD''
			,''NABCHQ_LBOX''
			)
		AND v.fxa_adjustment_reason_code NOT IN (''75'', ''51'')
		AND v.fxa_adjustment_description NOT LIKE ''%Writeoff < $10''
		AND CAST(v.fxa_amount AS MONEY)/100 > 10.00
		AND CAST(v.fxa_processing_date AS DATE)  = @processdate
		ORDER BY v.fxa_batch_number 
		,CAST(v.fxa_tran_link_no AS BIGINT)
		,v.fxa_drn;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Adjustments] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking]
--@startdate = ''20150701'', @enddate = ''20151130''
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking] @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT COUNT (CASE WHEN va.subject_area IN (''abal'', ''ebal'')
					AND va.attribute_name = ''tpc_check_required'' THEN v.fxa_drn ELSE 0 END) AS ''Third Party Checking''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Third Party Checking'') AS ''3RD_PARTY_CHECKING_PRICE''
		,rb.bank_group_code
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT va.i_chronicle_id
					,va.subject_area 
					,va.attribute_name
					,va.modified_date
					,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.subject_area DESC, va.modified_date DESC) AS Row_Num
				FROM [dbo].[voucher_audit] va
				WHERE va.subject_area IN (''abal'', ''ebal'') 
					AND va.attribute_name = ''tpc_check_required'') va ON va.i_chronicle_id = v.i_chronicle_id
																	AND va.Row_Num = 1												
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
		OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_LBOX'',''NABCHQ_POD'',''NABCHQ_APOST'')
		AND va.subject_area IN (''abal'', ''ebal'')
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_group_code
	HAVING SUM (CASE WHEN va.subject_area IN (''abal'', ''ebal'')
					AND va.attribute_name = ''tpc_check_required'' THEN 1 ELSE 0 END) >0;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation]
--@startdate = ''20150701'', @enddate = ''20151130''
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_3rd_Party_Checking_Validation] @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT SUM (CASE WHEN va.subject_area = ''tpc''
					AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END) AS ''Third Party Checking Validation''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Third Party Checking Validation'') AS ''3RD_PARTY_CHECKING_VALIDATION_PRICE''
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT va.i_chronicle_id
					,va.subject_area 
					,va.attribute_name
					,va.modified_date
					,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.subject_area DESC, va.modified_date DESC) AS Row_Num
				FROM [dbo].[voucher_audit] va
				WHERE va.subject_area = ''tpc''
					AND va.attribute_name = ''timings'') va ON va.i_chronicle_id = v.i_chronicle_id
													     AND va.Row_Num = 1													
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_LBOX'',''NABCHQ_POD'',''NABCHQ_APOST'')
		AND va.subject_area = ''tpc''
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	HAVING SUM (CASE WHEN va.subject_area = ''tpc''
					AND va.attribute_name = ''timings'' THEN 1 ELSE 0 END) >0;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Capture]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Capture]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_Capture]
--@startdate = ''20151001'', @enddate = ''20151031''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_Capture]  @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT COUNT (v.fxa_drn)  AS ''Number of Items Processed''
		,CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''NSW'' 
			 WHEN v.fxa_capture_bsb = ''083340'' THEN ''VIC'' 
			 WHEN v.fxa_capture_bsb = ''085015'' THEN ''SA''
			 WHEN v.fxa_capture_bsb = ''086016'' THEN ''WA'' 
			 WHEN v.fxa_capture_bsb = ''084014'' THEN ''QLD-NAB'' 
			 WHEN v.fxa_capture_bsb = ''124001'' THEN ''QLD-BQL'' ELSE '''' END AS ''Capture BSB''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''NSW'' 
			 WHEN v.fxa_capture_bsb = ''083340'' THEN ''VIC'' 
			 WHEN v.fxa_capture_bsb = ''085015'' THEN ''SA''
			 WHEN v.fxa_capture_bsb = ''086016'' THEN ''WA'' 
			 WHEN v.fxa_capture_bsb = ''084014'' THEN ''QLD-NAB'' 
			 WHEN v.fxa_capture_bsb = ''124001'' THEN ''QLD-BQL'' ELSE '''' END
	ORDER BY CASE WHEN v.fxa_capture_bsb = ''082082'' THEN ''NSW'' 
			 WHEN v.fxa_capture_bsb = ''083340'' THEN ''VIC'' 
			 WHEN v.fxa_capture_bsb = ''085015'' THEN ''SA''
			 WHEN v.fxa_capture_bsb = ''086016'' THEN ''WA'' 
			 WHEN v.fxa_capture_bsb = ''084014'' THEN ''QLD-NAB'' 
			 WHEN v.fxa_capture_bsb = ''124001'' THEN ''QLD-BQL'' ELSE '''' END;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Billing_Capture] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Clearings_Inward]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Clearings_Inward]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_Clearings_Inward]  
--@startdate = ''20151101'', @enddate =''20151130''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_Clearings_Inward]  @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT SUM(CASE WHEN  v.fxa_work_type_code = ''NABCHQ_INWARDNFV'' THEN 1 ELSE 0 END) AS ''Inward Clearings IE Total''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Inward Clearings IE'') AS ''INWARD_CLEARING_PRICE''
	--	,vt.target_end_point
		,rb.bank_group_code
	FROM [dbo].[voucher] v
	LEFT JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	WHERE v.fxa_inactive_flag = 0
		AND vt.transmission_type = ''INWARD_NON_FOR_VALUE'' 
		AND v.fxa_work_type_code = ''NABCHQ_INWARDNFV'' 
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_group_code 
	--,vt.target_end_point
	;
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Clearings_Outward]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Clearings_Outward]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_Clearings]  
--@startdate = ''20151101'', @enddate =''20151130''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_Clearings_Outward]  @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT COUNT(v.fxa_drn) AS ''Outward Clearings Total''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Outward Clearings'') AS ''OUTWARD_CLEARING_PRICE''
		,rb.bank_group_code
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
			OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD'', ''NABCHQ_APOST'')
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_group_code;
END' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_Dishonours]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_Dishonours]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_Dishonours] @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT SUM(CASE WHEN dl.fxa_dishonour_status = ''Letter Issued'' THEN 1 ELSE 0 END) AS ''Inward Dishonour Letters''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Inward Dishonour Letters'') AS ''DISHONOURS_PRICE''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[dishonour] dl ON dl.i_chronicle_id = v.i_chronicle_id
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_LBOX'',''NABCHQ_POD'',''NABCHQ_APOST'')
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate;
END






' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Billing_Dishonours] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_FV]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_FV]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
--EXEC [dbo].[usp_rpt_NAB_Billing_FV]
--@startdate = ''20151001'', @enddate = ''20151031''
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_FV]  @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT SUM (CASE WHEN (v.fxa_classification = ''Dr'') THEN 1 ELSE 0 END) AS ''FV DR''
		,SUM (CASE WHEN (v.fxa_classification = ''Cr'') THEN 1 ELSE 0 END) AS ''FV CR''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Inward For Value Processing'') AS ''INWARD_FV_PRICE''
		,rb.bank_group_code
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
											AND vt.transmission_type = ''INWARD_FOR_VALUE''
											AND vt.[status] = ''Completed''
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							  OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code = ''NABCHQ_INWARDFV''
		AND CAST(v.fxa_processing_date AS DATE) BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_group_code;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Billing_FV] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Billing_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Billing_LockedBox]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 17/06/2015
-- Description:	used for the NAB Billing Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Billing_LockedBox] @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT COUNT (v.fxa_drn) AS ''Locked Box''
		,(SELECT [fee_unit_price] FROM [dbo].[ref_fee] WHERE [fee_name] = ''Locked Box'') AS ''LOCKEDBOX_PRICE''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code = ''NABCHQ_LBOX''
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Billing_LockedBox] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Listings]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Listings]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 25/05/2015
-- Description:	used for the NAB Daily Listings Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Listings] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT CAST(l.processing_date AS DATE) AS fxa_processing_date
		,COUNT(l.i_chronicle_id) AS ''Number of Items''
	FROM [dbo].[listing_view] l
	WHERE l.processing_date BETWEEN @startdate AND @enddate
	GROUP BY CAST(l.processing_date AS DATE)
	ORDER BY CAST(l.processing_date AS DATE);
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Listings] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Agency]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Agency]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Agency] @processdate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_name AS ''Customer/FI''
		,v.fxa_work_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code
	WHERE rs.parent_fi = ''NAB''
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''BQL_POD'')
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date = @processdate
	GROUP BY rb.bank_name, v.fxa_work_type_code;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_Agency] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_AustPost]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_AustPost]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================

CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_AustPost] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_name AS ''Customer/FI''
		,v.fxa_work_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code
	WHERE v.fxa_work_type_code = ''NABCHQ_APOST''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date = @processdate
	GROUP BY rb.bank_name, v.fxa_work_type_code;
END





' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_AustPost] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Delivery]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Delivery]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================

CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Delivery] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_doc_retr_delivery_site AS ''Delivery Site''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_doc_retr_flag = 1
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
		AND v.fxa_doc_retr_date = @processdate
	GROUP BY v.fxa_doc_retr_delivery_site;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_Delivery] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT lb.customer_name AS ''Customer/FI''
		,v.fxa_payment_type AS ''Payment Type''
		,COUNT (v.fxa_drn)  AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[ref_locked_box] lb ON lb.bsb = v.fxa_collecting_bsb
	WHERE v.fxa_work_type_code = ''NABCHQ_LBOX''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date = @processdate
	GROUP BY lb.customer_name, v.fxa_payment_type;
END





' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_LockedBox] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_Merchants]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_Merchants]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_Merchants] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_work_type_code AS ''Work Type''
		,COUNT (CASE WHEN ((v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125''))
				OR (v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506''))) THEN fxa_drn ELSE 0 END) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND ((v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125''))
				OR (v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506'')))
		AND v.fxa_processing_date = @processdate
	GROUP BY v.fxa_work_type_code;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_Merchants] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Daily Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_batch_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE  v.fxa_work_type_code = ''NABCHQ_POD''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date = @processdate
	GROUP BY v.fxa_work_type_code, v.fxa_batch_type_code;
END





' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Daily_Volumes_NAB_POD] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Document_Retrieval]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Document_Retrieval]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 18/06/2015
-- Description:	used for the NAB Document Retreival Report
--EXEC [dbo].[usp_rpt_NAB_Document_Retrieval]
--@processdate = ''20151123''
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Document_Retrieval] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT CASE WHEN va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN va.operator_name ELSE '''' END AS ''Requestor''
		,CASE WHEN va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN va.post_value ELSE '''' END AS ''Priority''
		,v.fxa_doc_retr_seq_id	AS ''Request ID''
		,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
		,v.fxa_drn AS DIN
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
		,v.fxa_batch_number
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,CASE WHEN (v.fxa_work_type_code = ''NABCHQ_LBOX'') THEN ''Locked Box''
			  WHEN (v.fxa_work_type_code = ''NABCHQ_APOST'') THEN ''Australia Post''
			  WHEN (v.fxa_batch_type_code <> '''' 
				AND v.fxa_work_type_code = ''NABCHQ_POD'') THEN v.fxa_batch_type_code
			  WHEN (v.fxa_batch_type_code = '''' 
				AND v.fxa_work_type_code = ''NABCHQ_POD'') THEN ''NABCHQ_POD'' ELSE '''' END AS ''Work Type''
		,v.fxa_processing_state AS ''Processing Centre''
		,CASE WHEN v.fxa_capture_bsb IN (''082082'', ''084014'') THEN ''DPR Northern''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1 
				AND v.fxa_release_flag IN (''reject_user'', ''reject_system'') THEN ''DPR Southern''
			  WHEN v.fxa_doc_retr_flag = 1 AND va.subject_area = ''docr'' AND va.attribute_name = ''priority'' THEN v.fxa_doc_retr_delivery_site
			  WHEN v.fxa_unprocessable_item_flag = 1 AND v.fxa_capture_bsb IN (''082082'', ''084014'') THEN ''DPR Northern'' 
			  WHEN (v.fxa_unprocessable_item_flag = 1 
				OR v.fxa_uecd_return_flag = 1 
				OR v.fxa_tpc_failed_flag = 1 
				OR v.fxa_surplus_item_flag = 1) 
				AND (rs.parent_fi = ''NAB'' 
				OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')) THEN rb.bank_name
			  WHEN v.fxa_doc_retr_flag = 1 
				AND va.subject_area = ''docr'' 
				AND va.attribute_name = ''delivery_site'' THEN ''va.post_value''
			  WHEN v.fxa_doc_retr_flag = 1
				AND v.fxa_work_type_code = ''NABCHQ_APOST'' THEN ''Australia Post'' ELSE ''DPR Southern'' END AS ''Delivery Site''
		,CASE WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1
				AND v.fxa_release_flag = ''reject_user'' THEN ''Rejected TPC''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND v.fxa_tpc_failed_flag = 1
				AND v.fxa_release_flag = ''reject_system'' THEN ''Returned TPC''
			  WHEN v.fxa_unprocessable_item_flag = 1 THEN ''Unprocessable Item''
			  WHEN v.fxa_uecd_return_flag = 1 THEN ''Unencoded ECD''
			  WHEN v.fxa_tpc_failed_flag = 1 THEN ''TPC Failed''
			  WHEN v.fxa_surplus_item_flag = 1 
				AND (rs.parent_fi = ''NAB'' 
				OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = '''')) THEN ''Surplus Item''
			  WHEN v.fxa_doc_retr_flag = 1
				AND v.fxa_work_type_code = ''NABCHQ_APOST'' THEN ''Australia Post Unmatched''
			  WHEN v.fxa_doc_retr_flag = 1 
				AND va.subject_area = ''docr'' 
				AND va.attribute_name = ''priority'' THEN ''Manual Retrieval Request'' ELSE '''' END AS ''Return Reason''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3)) 
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
	LEFT JOIN (SELECT i_chronicle_id
					,va.subject_area
					,va.attribute_name
					,va.operator_name
					,va.post_value
				FROM [dbo].[voucher_audit] va 
				WHERE va.subject_area = ''docr''
				AND va.attribute_name IN (''priority'', ''delivery_site'')) AS va ON va.i_chronicle_id = v.i_chronicle_id
	WHERE v.fxa_inactive_flag = 0
		AND ((CAST(v.fxa_doc_retr_date AS DATE) = @processdate OR CAST(v.fxa_processing_date AS DATE) = @processdate)
			AND (v.fxa_doc_retr_flag = 1 OR v.fxa_tpc_failed_flag = 1 OR v.fxa_unprocessable_item_flag = 1)
			AND v.fxa_capture_bsb <> ''124001''
			AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST''))
		OR ((CAST(v.fxa_doc_retr_date AS DATE) = @processdate OR CAST(v.fxa_processing_date AS DATE) = @processdate)
			AND (v.fxa_uecd_return_flag = 1 OR v.fxa_tpc_failed_flag = 1 OR v.fxa_unprocessable_item_flag = 1 OR v.fxa_surplus_item_flag = 1)
			AND (rs.parent_fi = ''NAB'' OR (rb.bank_code IN (''BQL'', ''HOM'', ''PPB'') AND rs.parent_fi = ''''))
			AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''BQL_POD''));
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Document_Retrieval] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_ECD_Exceptions]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_ECD_Exceptions]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ================================================
-- Author:		James Honner
-- Create date: 14/04/2015
-- Description:	used for the ECD Exceptions Report
--EXEC [dbo].[usp_rpt_NAB_ECD_Exceptions] 
--@processdate = ''20151116''
-- ================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_ECD_Exceptions] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

--all debit ECD''s/3RD party--

	SELECT t1.fxa_drn AS DRN
		,t1.fxa_bsb AS ''Negotiating BSB''
		,t1.fxa_account_number AS ''Negotiating Account Number''
		,t1.fxa_amount AS ''Deposit Amount'' 
		,v.fxa_bsb AS ''Drawer''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Cheque Amount''
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1 THEN ''Failed Third Party Reason''
			  WHEN v.fxa_mixed_dep_return_flag = 1 THEN ''Mixed Deposit Return''
			  ELSE ''Unencoded Deposit'' END AS ''Exception Reason''
		,t1.fxa_batch_number
		,v.fxa_drn
		,t1.fxa_processing_date 
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
		OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
		AND v.fxa_processing_state = rs.state_code
	INNER JOIN  (SELECT CAST(v.fxa_amount AS MONEY)/100 fxa_amount
					,v.fxa_bsb
					,v.fxa_account_number
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_batch_number
					,v.fxa_drn
					,v.fxa_tpc_suspense_pool_flag
					,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
					OR rb.bsb = (LEFT(v.fxa_bsb, 3))
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
					AND v.fxa_processing_state = rs.state_code
				WHERE v.fxa_amount >= 500000
					AND v.fxa_classification <> ''Bh''
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					AND CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
											FROM [dbo].[voucher] v
											LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
												OR rb.bsb = (LEFT(v.fxa_bsb, 3))
											INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
												AND v.fxa_processing_state = rs.state_code
											WHERE rb.bank_code IN (''NAB'',''BNZ'')
												AND rs.parent_fi =''''
												AND CAST(v.fxa_processing_date AS DATE) = @processdate
												AND v.fxa_inactive_flag = 0
												AND v.fxa_work_type_code = ''NABCHQ_POD''
												AND v.fxa_classification = ''Dr''
												AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
													OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
												)
					AND v.fxa_account_number NOT IN (SELECT v.fxa_account_number
											FROM [dbo].[voucher] v
											LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
												OR rb.bsb = (LEFT(v.fxa_bsb, 3))
											INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
												AND v.fxa_processing_state = rs.state_code
											WHERE rb.bank_code IN (''NAB'',''BNZ'')
											AND rs.parent_fi =''''
											AND CAST(v.fxa_processing_date AS DATE) = @processdate
											AND v.fxa_inactive_flag = 0
											AND v.fxa_work_type_code = ''NABCHQ_POD''
											AND v.fxa_classification = ''Dr''
											AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
												OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
												)
				) t1 ON t1.fxa_batch_number = v.fxa_batch_number
						AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT) 
						AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)														
	WHERE rb.bank_code IN (''NAB'',''BNZ'')
		AND rs.parent_fi =''''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code = ''NABCHQ_POD''
		AND v.fxa_classification = ''Dr''
		AND v.fxa_amount >= 500000
		AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
			OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	
UNION ALL

--all credit ECD''s--

	SELECT v.fxa_drn AS DRN
		,v.fxa_bsb AS ''Negotiating BSB''
		,v.fxa_account_number AS ''Negotiating Account Number''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Deposit Amount'' 
		,t1.fxa_bsb AS ''Drawer''
		,t1.cheque_amount AS ''Cheque Amount''
		,CASE WHEN v.fxa_tpc_suspense_pool_flag = 1 THEN ''Failed Third Party Reason''
			  WHEN v.fxa_mixed_dep_return_flag = 1 THEN ''Mixed Deposit Return''
			  ELSE ''Unencoded Deposit'' END AS ''Exception Reason''
		,v.fxa_batch_number
		,t1.fxa_drn
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
		OR rb.bsb = (LEFT(v.fxa_bsb, 3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
		AND v.fxa_processing_state = rs.state_code
	INNER JOIN  (SELECT CAST(v.fxa_amount AS MONEY)/100 AS ''cheque_amount''
					,v.fxa_bsb
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_batch_number
					,v.fxa_drn
					,v.fxa_classification
					,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
					OR rb.bsb = (LEFT(v.fxa_bsb, 3))
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
					AND v.fxa_processing_state = rs.state_code
				WHERE v.fxa_classification <> ''Bh''
					AND rb.bank_code IN (''NAB'',''BNZ'')
					AND rs.parent_fi =''''
					AND v.fxa_inactive_flag = 0
					AND v.fxa_work_type_code = ''NABCHQ_POD''
					AND v.fxa_amount >= 500000
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					AND CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
																FROM [dbo].[voucher] v
																LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
																	OR rb.bsb = (LEFT(v.fxa_bsb, 3))
																INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																	AND v.fxa_processing_state = rs.state_code
																WHERE rb.bank_code IN (''NAB'',''BNZ'')
																	AND rs.parent_fi =''''
																	AND CAST(v.fxa_processing_date AS DATE) = @processdate
																	AND v.fxa_inactive_flag = 0
																	AND v.fxa_work_type_code = ''NABCHQ_POD''
																	AND v.fxa_classification = ''Cr''
																	AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
																		OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
																)
					AND v.fxa_account_number NOT IN (SELECT v.fxa_account_number
													FROM [dbo].[voucher] v
													LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
														OR rb.bsb = (LEFT(v.fxa_bsb, 3))
													INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
														AND v.fxa_processing_state = rs.state_code
													WHERE rb.bank_code IN (''NAB'',''BNZ'')
													AND rs.parent_fi =''''
													AND CAST(v.fxa_processing_date AS DATE) = @processdate
													AND v.fxa_inactive_flag = 0
													AND v.fxa_work_type_code = ''NABCHQ_POD''
													AND v.fxa_classification = ''Cr''
													AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
														OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
														)
				) t1 ON t1.fxa_batch_number = v.fxa_batch_number
						AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT) 
						AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)														
	WHERE rb.bank_code IN (''NAB'',''BNZ'')
		AND rs.parent_fi =''''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code = ''NABCHQ_POD''
		AND v.fxa_classification = ''Cr''
		AND ((v.fxa_tpc_suspense_pool_flag = 1 OR v.fxa_mixed_dep_return_flag = 1)
			OR (v.fxa_micr_flag = 0  AND v.fxa_amount >= 500000  AND v.fxa_tpc_failed_flag <> 1 AND v.fxa_classification = ''Cr''))
		AND CAST(v.fxa_processing_date AS DATE) = @processdate;	
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_ECD_Exceptions] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_High_Value]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_High_Value]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ================================================
-- Author:		James Honner
-- Create date: 25/05/2015
-- Description:	used for the NAB High Value Report
--EXEC [dbo].[usp_rpt_NAB_High_Value] 
--@processdate = ''20151123''
-- ================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_High_Value] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
		,v.fxa_batch_number
		,v.fxa_classification
		,v.fxa_drn AS ''DIN''
		,v.fxa_aux_dom AS ''AD''
		,v.fxa_extra_aux_dom AS ''EAD''
		,v.fxa_bsb AS ''BSB''
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Amount''
		,v.fxa_alt_account_number AS ''Deposit Account''
		--,t1.fxa_account_number AS ''Deposit Account''
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
	FROM [dbo].[voucher] v
	--INNER JOIN  (SELECT fxa_batch_number
	--				,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
	--				,v.fxa_account_number
	--				,v.fxa_processing_date
	--			FROM [dbo].[voucher] v
	--			WHERE CAST(v.fxa_processing_date AS DATE) = @processdate
	--				AND v.fxa_classification = ''Cr''
	--				AND CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
	--											FROM [dbo].[voucher] v
	--											WHERE v.fxa_inactive_flag = 0
	--												AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_SURPLUS'',''NABCHQ_APOST'')
	--												AND v.fxa_classification = ''Dr''
	--												AND (v.fxa_high_value_flag = 1 OR v.fxa_amount > 99999999999)
	--												AND CAST(v.fxa_processing_date AS DATE) = @processdate
	--											)
	--				AND v.fxa_account_number NOT IN (SELECT v.fxa_account_number
	--												FROM [dbo].[voucher] v
	--												WHERE v.fxa_inactive_flag = 0
	--													AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_SURPLUS'',''NABCHQ_APOST'')
	--													AND v.fxa_classification = ''Dr''
	--													AND (v.fxa_high_value_flag = 1 OR v.fxa_amount > 99999999999)
	--													AND CAST(v.fxa_processing_date AS DATE) = @processdate
	--													) 
	--											) t1 ON t1.fxa_batch_number = v.fxa_batch_number
	--												AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT)
	--												AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)														
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_SURPLUS'',''NABCHQ_APOST'')
		AND v.fxa_classification = ''Dr''
		AND (v.fxa_high_value_flag = 1 OR v.fxa_amount > 99999999999)
		AND CAST(v.fxa_processing_date AS DATE) = @processdate;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_High_Value] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Inbound_IE]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Inbound_IE]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 02/06/2015
-- Description:	used for the NAB Inbound IE Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Inbound_IE] @transmissiondate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT fr.[filename] AS ''File name or Reference''
		,CONVERT(NVARCHAR(8),vt.transmission_date,108) AS ''Time Exchanged''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END) AS ''Debit Value''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN 1 ELSE 0 END) AS ''Debit Items''
		,SUM(CASE WHEN v.fxa_classification = ''Cr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END) AS ''Credit Value''
		,SUM(CASE WHEN v.fxa_classification = ''Cr''
				THEN 1 ELSE 0 END) AS ''Credit Items''
		,CAST(vt.transmission_date AS DATE) AS transmission_date
		,rb.bank_group_code
		,rb.bank_name
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[file_receipt] fr ON fr.[file_id] = v.fxa_file_receipt_id
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb, 2))
		OR rb.bsb = (LEFT(v.fxa_collecting_bsb, 3))
	WHERE vt.[status] = ''Completed''
		AND vt.transmission_type IN (''INWARD_FOR_VALUE'', ''INWARD_NON_FOR_VALUE'') 
		AND v.fxa_work_type_code IN (''NABCHQ_INWARDFV'',''NABCHQ_INWARDNFV'')
		AND v.fxa_inactive_flag = 0
		AND CAST(vt.transmission_date AS DATE) = @transmissiondate
	GROUP BY fr.[filename]
		,CONVERT(NVARCHAR(8),vt.transmission_date,108)
		,CAST(vt.transmission_date AS DATE)
		,rb.bank_group_code
		,rb.bank_name;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Inbound_IE] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the NAB Incoming Suspect Fraud Report
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_code AS ''Collecting FI Name''
		,'''' AS ''Collecting FI ACN''
		,'''' AS ''Collecting FI ABN''
		,'''' AS ''Collecting FI ARBN''
		,rb1.bank_code AS ''Drawee FI''
		,v.fxa_batch_number
		,CAST(v.fxa_tran_link_no AS BIGINT) AS TXN
		,v.fxa_drn AS DIN
		,v.fxa_extra_aux_dom AS EAD
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Cheque Amount''
		,t1.deposit_amount AS ''Deposit Total Amount''
		,v.folder_path
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		,v.fxa_processing_state
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	--INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
	--						    AND v.fxa_processing_state = rs.state_code
	LEFT JOIN dbo.ref_bank rb1 ON rb1.bsb = (LEFT(v.fxa_bsb,2)) OR rb1.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN  (SELECT SUM(CAST(v.fxa_amount AS MONEY)/100) AS deposit_amount
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_batch_number
					,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
				FROM [dbo].[voucher] v
				WHERE CAST(v.fxa_processing_date AS DATE)= @processdate
				AND v.fxa_classification = ''Cr''
				AND CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
															FROM [dbo].[voucher] v
															LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
															INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																			AND v.fxa_processing_state = rs.state_code
															WHERE v.fxa_suspect_fraud_flag = 1
															AND CAST(v.fxa_amount AS MONEY)/100 >= 200.00
															AND v.fxa_inactive_flag = 0
															AND v.fxa_classification = ''Dr''
															AND rb.bank_code IN (''NAB'',''BNZ'')
															AND rs.parent_fi =''''
															AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST'')
															AND CAST(v.fxa_processing_date AS DATE)= @processdate
																)
				AND v.fxa_account_number NOT IN (SELECT v.fxa_account_number
												FROM [dbo].[voucher] v
												LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
												INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																AND v.fxa_processing_state = rs.state_code
												WHERE v.fxa_suspect_fraud_flag = 1
												AND CAST(v.fxa_amount AS MONEY)/100 >= 200.00
												AND v.fxa_inactive_flag = 0
												AND v.fxa_classification = ''Dr''
												AND rb.bank_code IN (''NAB'',''BNZ'')
												AND rs.parent_fi =''''
												AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST'')
												AND CAST(v.fxa_processing_date AS DATE)= @processdate
												)
				GROUP BY CAST(v.fxa_tran_link_no AS BIGINT) 
					,v.fxa_batch_number
					,CAST(v.fxa_processing_date AS DATE)
					) t1 ON t1.fxa_batch_number = v.fxa_batch_number
						AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT) 
						AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)														
	WHERE v.fxa_suspect_fraud_flag = 1
		AND CAST(v.fxa_amount AS MONEY)/100 >= 200.00
		AND v.fxa_inactive_flag = 0
		AND v.fxa_classification = ''Dr''
		AND rb.bank_code IN (''NAB'',''BNZ'')
		--AND rs.parent_fi =''''
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST'')
	ORDER BY CAST(v.fxa_processing_date AS DATE)
		,v.fxa_batch_number
		,CAST(v.fxa_tran_link_no AS BIGINT)
		,v.fxa_drn;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Incoming_Suspect_Fraud] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Inward_FV_DR]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Inward_FV_DR]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ==================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the NAB_Inward_FV_DR Report
--EXEC [dbo].[usp_rpt_NAB_Inward_FV_DR]
--@processdate = ''20150928''
-- ==================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Inward_FV_DR] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_drn AS DIN
		,rb.bank_code AS WorkSource
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS Account
		,v.fxa_trancode AS Trancode
		,CAST(v.fxa_amount AS MONEY)/100 AS Amount
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		,v.fxa_processing_state
		,v.fxa_capture_bsb
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	LEFT JOIN dbo.ref_bank rb1 ON rb1.bsb = (LEFT(v.fxa_bsb,2)) OR rb1.bsb = (LEFT(v.fxa_bsb,3))
	WHERE (v.fxa_classification = ''Dr'' AND rb1.bank_code IN (''NAB'', ''BNZ'')) 
		AND v.fxa_work_type_code = ''NABCHQ_INWARDFV''
		AND vt.transmission_type = ''INWARD_FOR_VALUE''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Inward_FV_DR] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_IPOD_Errors]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_IPOD_Errors]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ============================================
-- Author:		James Honner
-- Create date: 23/06/2015
-- Description:	used for the NAB IPOD Errors
--EXEC [dbo].[usp_rpt_NAB_IPOD_Errors]
--@processdate = ''20151202''
-- ============================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_IPOD_Errors] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

SELECT * FROM (

--return all CR records that are not the NAB suspense acc, sort by fxa_drn DESC as highest fxa_drn appears in the summary section of the report--

	SELECT v.fxa_bsb AS ''Deposit BSB''
		,v.fxa_account_number AS ''Deposit Account Number''
		,CAST(CAST(v.fxa_pre_adjustment_amt AS MONEY)/100 AS NVARCHAR) AS ''Original Deposit Amount''
		,CAST(CAST(t.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjustment Amount''
		,CAST(CAST(v.fxa_amount AS MONEY)/100 AS NVARCHAR) AS ''Adjusted Deposit Amount''
		,v.fxa_adjustment_description AS ''Reason''
		,v.fxa_batch_type_code AS ''Batch Type Code''
		,v.folder_path AS ''Voucher Image Path''
		,CAST(v.fxa_processing_date AS DATE) AS ''Process Date''
		,v.fxa_drn 
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
		,'''' AS DIN
		,'''' AS NEGBSB
		,'''' AS EAD
		,'''' AS AD
		,'''' AS BSB
		,'''' AS ''Account No''
		,'''' AS TC
		,'''' AS ''DR/CR''
		,'''' AS Amount
	FROM [dbo].[voucher] v
	INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					,v.fxa_amount
				FROM [dbo].[voucher] v
				LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
						OR rb.bsb = (LEFT(v.fxa_bsb, 3))
				INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
						AND v.fxa_processing_state = rs.state_code
				WHERE v.fxa_inactive_flag = 0
					AND v.fxa_adjustment_flag = 1
					AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
					AND rb.bank_code IN (''NAB'',''BNZ'')
					AND rs.parent_fi = ''''
					AND v.fxa_account_number = ''899919946''
					AND LEFT(v.fxa_collecting_bsb,2) IN  (''05'', ''08'', ''75'' ,''78'' ,''20'')
					AND CAST(v.fxa_processing_date AS DATE) = @processdate
					) t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))						
	WHERE v.fxa_account_number <> ''899919946''
		AND v.fxa_classification = ''Cr''
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY v.fxa_bsb 
		,v.fxa_account_number 
		,CAST(t.fxa_amount AS MONEY)/100
		,CAST(v.fxa_pre_adjustment_amt AS MONEY)/100
		,CAST(v.fxa_amount AS MONEY)/100 
		,v.fxa_adjustment_description
		,v.fxa_batch_type_code 
		,v.folder_path 
		,CAST(v.fxa_processing_date AS DATE)
		,v.fxa_drn
		,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) 
				
	UNION ALL

--return all assocoiated transactions for the details section of the report--
		
		SELECT '''' AS ''Deposit BSB''
			,'''' AS ''Deposit Account Number''
			,'''' AS ''Original Deposit Amount''
			,'''' AS ''Adjustment Amount''
			,'''' AS ''Adjusted Deposit Amount''
			,'''' AS ''Reason''
			,'''' AS ''Batch Type Code''
			,'''' AS ''Voucher Image Path''
			,CAST(t1.fxa_processing_date AS DATE) AS ''Process Date''
			,'''' AS fxa_drn
			,t1.sug_key
			,t1.fxa_drn AS DIN
			,t1.fxa_collecting_bsb AS NEGBSB
			,t1.fxa_extra_aux_dom AS EAD
			,t1.fxa_aux_dom AS AD
			,t1.fxa_bsb AS BSB
			,t1.fxa_account_number AS ''Account No''
			,t1.fxa_trancode AS TC
			,t1.fxa_classification AS ''DR/CR''
			,CAST(CAST(t1.fxa_amount AS MONEY)/100 AS NVARCHAR) AS Amount
		FROM [dbo].[voucher] v
		INNER JOIN (SELECT CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
						,v.fxa_amount
					FROM [dbo].[voucher] v
					LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
							OR rb.bsb = (LEFT(v.fxa_bsb, 3))
					INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
					WHERE v.fxa_inactive_flag = 0
						AND v.fxa_adjustment_flag = 1
						AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
						AND rb.bank_code IN (''NAB'',''BNZ'')
						AND rs.parent_fi = ''''
						AND v.fxa_account_number = ''899919946''
						AND LEFT(v.fxa_collecting_bsb,2) IN  (''05'', ''08'', ''75'' ,''78'' ,''20'')
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
						)t ON t.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
		LEFT JOIN  (SELECT v.fxa_drn 
						,v.fxa_collecting_bsb
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom 
						,v.fxa_bsb 
						,v.fxa_account_number
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd'')) AS sug_key
					FROM [dbo].[voucher] v
					WHERE CAST(v.fxa_tran_link_no AS BIGINT) IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
																FROM [dbo].[voucher] v
																LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
																	OR rb.bsb = (LEFT(v.fxa_bsb, 3))
																INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																	AND v.fxa_processing_state = rs.state_code
																WHERE v.fxa_inactive_flag = 0
																	AND v.fxa_adjustment_flag = 1
																	AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
																	AND rb.bank_code IN (''NAB'',''BNZ'')
																	AND rs.parent_fi = ''''
																	AND v.fxa_account_number = ''899919946''
																	AND LEFT(v.fxa_collecting_bsb,2) IN  (''05'', ''08'', ''75'' ,''78'' ,''20'')
																	AND CAST(v.fxa_processing_date AS DATE) = @processdate
																)
						AND CAST(v.fxa_processing_date AS DATE) = @processdate
					GROUP BY v.fxa_drn 
						,v.fxa_collecting_bsb 
						,v.fxa_extra_aux_dom 
						,v.fxa_aux_dom  
						,v.fxa_bsb 
						,v.fxa_account_number 
						,v.fxa_trancode 
						,v.fxa_classification 
						,v.fxa_amount
						,v.fxa_processing_date
						,CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))
						) t1 ON t1.sug_key = CONCAT(v.fxa_batch_number,v.fxa_tran_link_no,FORMAT(v.fxa_processing_date, ''yyyyMMdd''))														
		WHERE v.fxa_account_number <> ''899919946''
			AND v.fxa_classification = ''Cr''
			AND CAST(v.fxa_processing_date AS DATE) = @processdate
		GROUP BY t1.fxa_drn
			,t1.fxa_collecting_bsb
			,t1.sug_key
			,t1.fxa_extra_aux_dom
			,t1.fxa_aux_dom
			,t1.fxa_bsb
			,t1.fxa_account_number 
			,t1.fxa_trancode 
			,t1.fxa_classification
			,CAST(t1.fxa_processing_date AS DATE)
			,CAST(t1.fxa_amount AS MONEY)/100
			) t1
	ORDER BY 11,12,10 DESC;

END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Locked_Box_Daily]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Locked_Box_Daily]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =========================================================================
-- Author:		James Honner
-- Create date: 25/05/2015
-- Description:	used for the NAB Locked Box Daily Processing Details Report
-- =========================================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Locked_Box_Daily] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT lb.customer_name AS ''Customer Name''
		,v.fxa_work_type_code AS ''Work Type''
		,SUM(CASE WHEN v.fxa_classification = ''Cr'' THEN 1 ELSE 0 END) AS ''Remittances Processed''
		,SUM(CASE WHEN v.fxa_classification = ''Dr'' THEN 1 ELSE 0 END) AS ''Cheques Processed''
		,SUM (CASE WHEN v.fxa_classification = ''Cr'' THEN CAST(v.fxa_amount AS MONEY)/100 ELSE 0 END) AS ''Total Item Value''
		,'''' AS ''Exception Items''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[ref_locked_box] lb ON lb.bsb = v.fxa_collecting_bsb
	WHERE v.fxa_work_type_code = ''NABCHQ_LBOX''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @processdate
	GROUP BY lb.customer_name, v.fxa_work_type_code;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Locked_Box_Daily] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Locked_Box_Monthly]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Locked_Box_Monthly]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================================
-- Author:		James Honner
-- Create date: 25/05/2015
-- Description:	used for the NAB Locked Box Monthly Processing Details Report
--EXEC [dbo].[usp_rpt_NAB_Locked_Box_Monthly]
-- ===========================================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Locked_Box_Monthly] @startdate DATE, @enddate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT lb.customer_name AS ''Customer Name''
		,v.fxa_work_type_code AS ''Work Type''
		,SUM(CASE WHEN v.fxa_classification = ''Cr'' THEN 1 ELSE 0 END) AS ''Remittances Processed''
		,SUM(CASE WHEN v.fxa_classification = ''Dr'' THEN 1 ELSE 0 END) AS ''Cheques Processed''
		,SUM (CASE WHEN v.fxa_classification = ''Cr'' THEN CAST(v.fxa_amount AS MONEY)/100 ELSE 0 END) AS ''Total Item Value''
		,'''' AS ''Exception Items''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[ref_locked_box] lb ON lb.bsb = v.fxa_collecting_bsb
	WHERE v.fxa_work_type_code = ''NABCHQ_LBOX''
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) BETWEEN @startdate AND @enddate
		GROUP BY lb.customer_name, v.fxa_work_type_code;
END







' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Locked_Box_Monthly] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- ==========================================================
-- Author:		James Honner
-- Create date: 19/05/2015
-- Description:	used for the NAB Merchant Substitutes Report
-- ==========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail] @processdate date
AS

BEGIN
	SET NOCOUNT ON;

SELECT v.fxa_drn AS DIN
	,v.fxa_collecting_bsb AS NEGBSB
	,CAST(v.fxa_amount AS MONEY)/100 AS Amount
	,v.fxa_processing_state
	,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
FROM [dbo].[voucher] v
LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							AND v.fxa_processing_state = rs.state_code
WHERE v.fxa_inactive_flag = 0
	AND v.fxa_processing_date = @processdate
	AND (v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125'')
		OR v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506''))
GROUP BY v.fxa_drn 
	,v.fxa_collecting_bsb 
	,CAST(v.fxa_amount AS MONEY)/100
	,v.fxa_processing_state
	,CAST(v.fxa_processing_date AS DATE) 
	,v.fxa_batch_number	
	,CAST(v.fxa_tran_link_no AS BIGINT)
ORDER BY v.fxa_processing_state, v.fxa_batch_number	,CAST(v.fxa_tran_link_no AS BIGINT), v.fxa_drn;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Merchant_Substitutes_Detail] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ==========================================================
-- Author:		James Honner
-- Create date: 19/05/2015
-- Description:	used for the NAB Merchant Substitutes Report
-- ==========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	WITH ms
	AS (
		SELECT RANK() OVER (ORDER BY v.fxa_batch_number	,v.fxa_tran_link_no) RANK
			,v.fxa_drn AS DIN
			,v.fxa_collecting_bsb AS NEGBSB
			,CAST(v.fxa_amount AS MONEY)/ 100 AS Amount
			,v.fxa_processing_state
			,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		FROM [dbo].[voucher] v
		LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb, 2))
			OR rb.bsb = (LEFT(v.fxa_bsb, 3))
		INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
			AND v.fxa_processing_state = rs.state_code
		WHERE v.fxa_inactive_flag = 0
			AND v.fxa_processing_date = @processdate
			AND (v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125'')
			OR v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506''))		
				)
	SELECT (RANK - 1) / 18 GroupID
		,SUM(Amount) AS Amount
		,COUNT(DIN) AS Envelopes
	FROM ms
	WHERE fxa_processing_date = @processdate
	GROUP BY ((RANK - 1) / 18);
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Merchant_Substitutes_Summary] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Agency]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Agency]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Agency] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_name AS ''Customer/FI''
		,v.fxa_work_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code 
	WHERE rs.parent_fi = ''NAB''
		AND v.fxa_work_type_code IN (''NABCHQ_POD'', ''NABCHQ_LBOX'', ''BQL_POD'')
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_name, v.fxa_work_type_code;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_Agency] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================

CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_name AS ''Customer/FI''
		,v.fxa_work_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
								AND v.fxa_processing_state = rs.state_code
	WHERE v.fxa_work_type_code = ''NABCHQ_APOST''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY rb.bank_name, v.fxa_work_type_code;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_AustPost] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================

CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_doc_retr_delivery_site AS ''Delivery Site''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND v.fxa_doc_retr_flag = 1
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'')
		AND v.fxa_doc_retr_date BETWEEN @startdate AND @enddate
	GROUP BY v.fxa_doc_retr_delivery_site;
END






' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_Delivery] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT lb.customer_name AS ''Customer/FI''
		,v.fxa_payment_type AS ''Payment Type''
		,COUNT (v.fxa_drn)  AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[ref_locked_box] lb ON lb.bsb = v.fxa_collecting_bsb
	WHERE v.fxa_work_type_code = ''NABCHQ_LBOX''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY lb.customer_name, v.fxa_payment_type;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_LockedBox] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_work_type_code AS ''Work Type''
		,COUNT (CASE WHEN ((v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125''))
				OR (v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506''))) THEN fxa_drn ELSE 0 END) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE v.fxa_inactive_flag = 0
		AND ((v.fxa_bsb = ''082012'' AND v.fxa_account_number  IN (''1200029'', ''1200125''))
				OR (v.fxa_bsb = ''083012'' AND v.fxa_account_number IN (''1234009'',''1236506'')))
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY v.fxa_work_type_code;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_Merchants] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 29/06/2015
-- Description:	used for the NAB Monthly Volumes Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD] @startdate DATE, @enddate DATE 
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_batch_type_code AS ''Work Type''
		,COUNT (v.fxa_drn) AS ''Number of Items Processed''
	FROM [dbo].[voucher] v
	WHERE  v.fxa_work_type_code = ''NABCHQ_POD''
		AND v.fxa_inactive_flag = 0
		AND v.fxa_processing_date BETWEEN @startdate AND @enddate
	GROUP BY v.fxa_work_type_code, v.fxa_batch_type_code;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Monthly_Volumes_NAB_POD] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outbound_IE]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outbound_IE]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ====================================================
-- Author:		James Honner
-- Create date: 02/06/2015
-- Description:	used for the NAB Outbound IE Report
-- ====================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Outbound_IE] @transmissiondate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT vt.[filename] AS ''File name or Reference''
		,CONVERT(NVARCHAR(8),vt.transmission_date,108) AS ''Time Exchanged''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0''	END) AS ''Debit Value''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN 1 ELSE 0 END) AS ''Debit Items''
		,SUM(CASE WHEN v.fxa_classification = ''Cr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0''	END) AS ''Credit Value''
		,SUM(CASE WHEN v.fxa_classification = ''Cr'' 
				  THEN 1 ELSE 0	END) AS ''Credit Items''
		,CAST(vt.transmission_date AS DATE) AS transmission_date
		,vt.target_end_point
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	WHERE vt.[status] = ''Completed''
		AND vt.transmission_type = ''IMAGE_EXCHANGE_OUTBOUND''
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST'')
		AND v.fxa_inactive_flag = 0
		AND CAST(vt.transmission_date AS DATE) = @transmissiondate
	GROUP BY vt.[filename]
		,CONVERT(NVARCHAR(8),vt.transmission_date,108)
		,CAST(vt.transmission_date AS DATE)
		,vt.target_end_point;
END' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Outbound_IE] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the NAB_Outgoing_Suspect_Fraud_Report
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT rb.bank_code AS ''Collecting FI Name''
		,'''' AS ''Collecting FI ACN''
		,'''' AS ''Collecting FI ABN''
		,'''' AS ''Collecting FI ARBN''
		,rb1.bank_code AS ''Drawee FI''
		,v.fxa_batch_number
		,CAST(v.fxa_tran_link_no AS BIGINT) AS TXN
		,v.fxa_drn AS DIN
		,v.fxa_extra_aux_dom AS EAD
		,v.fxa_aux_dom AS AD
		,v.fxa_bsb AS BSB
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Cheque Amount''
		,t1.deposit_amount AS ''Deposit Total Amount''
		,v.folder_path
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
		,v.fxa_processing_state
	FROM [dbo].[voucher] v
	LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_collecting_bsb,2)) OR rb.bsb = (LEFT(v.fxa_collecting_bsb,3))
	INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
							    AND v.fxa_processing_state = rs.state_code
	LEFT JOIN dbo.ref_bank rb1 ON rb1.bsb = (LEFT(v.fxa_bsb,2)) OR rb1.bsb = (LEFT(v.fxa_bsb,3))
	INNER JOIN (SELECT SUM(CAST(v.fxa_amount AS MONEY)/100) AS deposit_amount
					,CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
					,v.fxa_batch_number
					,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
				FROM [dbo].[voucher] v
				WHERE CAST(v.fxa_processing_date AS DATE)= @processdate
					AND v.fxa_classification = ''Cr'' 
					AND CAST(v.fxa_tran_link_no AS BIGINT)  IN (SELECT CAST(v.fxa_tran_link_no AS BIGINT) AS fxa_tran_link_no
											FROM [dbo].[voucher] v
											LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
											INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
															AND v.fxa_processing_state = rs.state_code
											WHERE v.fxa_suspect_fraud_flag = 1
												AND v.fxa_classification = ''Dr''
												AND v.fxa_inactive_flag = 0
												AND rb.bank_code NOT IN (''NAB'',''BNZ'')
												AND rs.parent_fi <>''''
												AND CAST(v.fxa_processing_date AS DATE) = @processdate
												)
				AND v.fxa_account_number NOT IN (SELECT v.fxa_account_number
												FROM [dbo].[voucher] v
												LEFT JOIN dbo.ref_bank rb ON rb.bsb = (LEFT(v.fxa_bsb,2)) OR rb.bsb = (LEFT(v.fxa_bsb,3))
												INNER JOIN dbo.ref_state rs ON rs.bank_code = rb.bank_code
																AND v.fxa_processing_state = rs.state_code
												WHERE v.fxa_suspect_fraud_flag = 1
													AND v.fxa_classification = ''Dr''
													AND v.fxa_inactive_flag = 0
													AND rb.bank_code NOT IN (''NAB'',''BNZ'')
													AND rs.parent_fi <>''''
													AND CAST(v.fxa_processing_date AS DATE) = @processdate
												)
				GROUP BY CAST(v.fxa_tran_link_no AS BIGINT) 
					,v.fxa_batch_number
					,CAST(v.fxa_processing_date AS DATE)
					) t1 ON t1.fxa_batch_number = v.fxa_batch_number
						AND CAST(t1.fxa_tran_link_no AS BIGINT) = CAST(v.fxa_tran_link_no AS BIGINT) 
						AND CAST(t1.fxa_processing_date AS DATE) = CAST(v.fxa_processing_date AS DATE)			
	WHERE v.fxa_suspect_fraud_flag = 1
		AND v.fxa_classification = ''Dr''
		AND v.fxa_inactive_flag = 0
		AND rb.bank_code NOT IN (''NAB'',''BNZ'')
		AND rs.parent_fi <>''''
		AND CAST(v.fxa_processing_date AS DATE) = @processdate;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Outgoing_Suspect_Fraud] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Outward_FV_CR]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Outward_FV_CR]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===================================================
-- Author:		James Honner
-- Create date: 7/05/2015
-- Description:	used for the NAB_Outward FV CR Report
-- ===================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Outward_FV_CR] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

		SELECT v.fxa_drn AS DIN
             ,CASE WHEN vt.target_end_point IN (''ANZ'', ''RBA'', ''BQL'') THEN vt.target_end_point
                      WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''245485472'') THEN ''CBA''
                      WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''823300223'') THEN ''WBC''
                      WHEN (vt.target_end_point = ''FIS'' AND v.fxa_account_number NOT IN (''823300223'', ''245485472'')) THEN vt.target_end_point ELSE '''' END AS ''Receiving Bank''
             ,v.fxa_extra_aux_dom AS EAD
             ,v.fxa_bsb AS BSB
             ,v.fxa_account_number AS Account
             ,v.fxa_trancode AS Trancode
             ,CAST(v.fxa_amount AS MONEY)/100 AS Amount
             ,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
             ,v.fxa_processing_state
       FROM [dbo].[voucher] v
       INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
       WHERE v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'', ''NABCHQ_APOST'')
             AND v.fxa_classification = ''Cr''
             AND v.fxa_for_value_type = ''Outward_For_Value''
             AND v.fxa_inactive_flag = 0
             AND vt.transmission_type like ''IMAGE_EXCHANGE_OUTBOUND%''
             AND CAST(v.fxa_processing_date AS DATE) = @processdate
             AND (vt.target_end_point IN (''ANZ'', ''RBA'', ''BQL'')
                    OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''245485472'')
                    OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number = ''823300223'')
                    OR (vt.target_end_point = ''FIS'' AND v.fxa_account_number NOT IN (''823300223'', ''245485472'')));
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Outward_FV_CR] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_Over_100K]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_Over_100K]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ============================================
-- Author:		James Honner
-- Create date: 03/06/2015
-- Description:	used for the NAB 100K Report
-- ============================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_Over_100K] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT v.fxa_drn AS ''DIN''
		,v.fxa_bsb AS ''Bank Branch''
		,v.fxa_aux_dom AS Serial
		,v.fxa_account_number AS ''Account Number''
		,CAST(v.fxa_amount AS MONEY)/100 AS ''Amount''
		,v.folder_path AS ''Voucher Image Path''
		,v.fxa_processing_state
		,v.fxa_trancode
		,CAST(v.fxa_processing_date AS DATE) AS fxa_processing_date
	FROM [dbo].[voucher] v
	WHERE NOT EXISTS (SELECT nb.bsb 				
				  FROM [dbo].[ref_non_bank_fi] nb
				  WHERE nb.bsb = v.fxa_bsb)
		AND v.fxa_inactive_flag = 0	
		AND v.fxa_unprocessable_item_flag = 0	
		AND v.fxa_high_value_flag = 0	
		AND v.fxa_classification = ''Dr''
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_APOST'')			
		AND v.fxa_amount > 10000000			
		AND v.fxa_trancode NOT IN (''111'', ''222'', ''444'', ''30'')			
		AND LEFT(v.fxa_bsb,2) = ''08''
		AND LEFT(v.fxa_account_number,4) <> ''8999'' 			
		AND v.fxa_account_number NOT IN (''823300223'', ''646877020'',''999999971'', ''999999947'', ''999999912'','''')	
		AND v.fxa_aux_dom <>''''		
		AND CAST(v.fxa_processing_date AS DATE) = @processdate			
ORDER BY v.fxa_processing_state				
	,v.fxa_drn;
END

' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_Over_100K] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_QA]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_QA]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ===========================================================
-- Author:		James Honner
-- Create date: 30/06/2015
-- Description:	used for the NAB QA Report
--EXEC [dbo].[usp_rpt_NAB_QA]
--@processdate = ''20151204''
-- ===========================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_QA] @processdate DATE
AS
BEGIN
	SET NOCOUNT ON;

		SELECT CAST(v.fxa_processing_date AS DATE) AS Processing_Date
			,v.fxa_drn AS DIN
			,CAST(v.fxa_amount AS MONEY)/100 AS Amount
			,CASE WHEN (v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 0) THEN ''Failed'' 
			      WHEN (v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 1) THEN ''Passed'' 
				  ELSE ''Not Assessed'' END AS Amount_QA_Result
			,v.fxa_extra_aux_dom AS EAD
			,v.fxa_aux_dom AS AD
			,v.fxa_bsb AS BSB
			,v.fxa_account_number AS ACCOUNT
			,v.fxa_trancode AS TC
			,CASE WHEN (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 0) THEN ''Failed'' 
			      WHEN (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 1) THEN ''Passed'' 
				  ELSE ''Not Assessed'' END AS Codeline_QA_Result
		FROM [dbo].[voucher] v
		LEFT JOIN (SELECT va.operator_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''dips'' 
						AND va.attribute_name = ''operator_name'') va ON va.i_chronicle_id = v.i_chronicle_id
																	AND va.Row_Num = 1
		LEFT JOIN (SELECT va.pre_value
						,va.post_value
						,va.operator_name
						,va.i_chronicle_id
						,va.subject_area 
						,va.attribute_name
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''qa'' 
						AND va.attribute_name = ''amt_accurate'') vb ON vb.i_chronicle_id = v.i_chronicle_id 
																	AND vb.Row_Num = 1
		LEFT JOIN (SELECT va.operator_name
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''cdc'' 
						AND va.attribute_name = ''acc'') vc ON vc.i_chronicle_id = v.i_chronicle_id
															AND vc.Row_Num = 1
		LEFT JOIN (SELECT va.pre_value
						,va.post_value
						,va.i_chronicle_id
						,ROW_NUMBER() OVER (PARTITION BY va.i_chronicle_id ORDER BY va.modified_date DESC) AS Row_Num
					FROM [dbo].[voucher_audit] va
					WHERE va.subject_area = ''qa'' 
						AND va.attribute_name = ''codeline_entry_accurate'') vd ON vd.i_chronicle_id = v.i_chronicle_id
																				AND vd.Row_Num = 1
		WHERE v.fxa_inactive_flag = 0
			AND ((v.fxa_ptqa_amt_complete_flag = 1 AND vb.post_value = 0) OR (v.fxa_ptqa_cdc_complete_flag = 1 AND vd.post_value = 0))
			AND (CAST(v.fxa_ptqa_amt_processed_date AS DATE) = @processdate OR CAST(v.fxa_ptqa_cdc_processed_date AS DATE) = @processdate)
		ORDER BY 2, 1;
END
' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_QA] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_rpt_NAB_VIF_Transmission]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_rpt_NAB_VIF_Transmission]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- ==================================================
-- Author:		James Honner
-- Create date: 28/04/2015
-- Description:	used for the VIF Transmission Report
--EXEC [dbo].[usp_rpt_NAB_VIF_Transmission] 
--@transmissiondate = ''20151124''
-- ==================================================
CREATE PROCEDURE [dbo].[usp_rpt_NAB_VIF_Transmission] @transmissiondate DATE
AS
BEGIN
	SET NOCOUNT ON;

	SELECT vt.[filename] AS ''File No''
		,SUM(CASE WHEN v.fxa_classification = ''Dr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0''	END) AS ''Total Debits''
		,SUM(CASE WHEN (v.fxa_classification = ''Dr'')
				  THEN 1 ELSE ''0'' END) AS ''Debit Count''
		,SUM(CASE WHEN v.fxa_classification = ''Cr''
				  THEN CAST(v.fxa_amount AS MONEY)/100 ELSE ''0'' END) AS ''Total Credits''
		,SUM(CASE WHEN (v.fxa_classification = ''Cr'')
				  THEN 1 ELSE ''0''END) AS ''Credit Count''
		,CAST(vt.transmission_date AS DATETIME) AS ''Run Time''
		,v.fxa_processing_state AS ''Site ID''
	FROM [dbo].[voucher] v
	INNER JOIN [dbo].[voucher_transfer] vt ON vt.v_i_chronicle_id = v.i_chronicle_id
	WHERE v.fxa_capture_bsb <> ''124001''
		AND vt.[status] = ''Sent''
		AND vt.transmission_type = ''VIF_OUTBOUND''
		AND v.fxa_classification NOT IN (''Bh'', ''Sp'')
		AND v.fxa_work_type_code IN (''NABCHQ_POD'',''NABCHQ_LBOX'',''NABCHQ_INWARDFV'',''NABCHQ_APOST'')
		AND v.fxa_inactive_flag = 0
		AND CAST(v.fxa_processing_date AS DATE) = @transmissiondate
	GROUP BY vt.[filename]
		,v.fxa_processing_state
		,CAST(vt.transmission_date AS DATETIME)
	ORDER BY vt.[filename];
END






' 
END
GO
GRANT EXECUTE ON [dbo].[usp_rpt_NAB_VIF_Transmission] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_Sequence_Number_Reset]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Sequence_Number_Reset]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[usp_Sequence_Number_Reset] @sequence_name NVARCHAR(50) = NULL
AS 
IF @sequence_name IS NULL
    BEGIN
        PRINT ''Sequence Name is null''
        RETURN 1
    END
IF @sequence_name NOT IN  (''AgencyBanksImageExchange'', ''TierOneBanksImageExchange'', ''ValueInstructionFile'')
    BEGIN
        PRINT ''Sequence Name does not match''
        RETURN 2
    END
    BEGIN
        UPDATE [dbo].[ref_sequence]
		SET reset_point = SYSDATETIME()
		,sequence_number = reset_sequence_number
		WHERE sequence_name = @sequence_name;
    END;







' 
END
GO
GRANT EXECUTE ON [dbo].[usp_Sequence_Number_Reset] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  StoredProcedure [dbo].[usp_Sequence_Number_Update]    Script Date: 13/01/2016 8:03:41 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Sequence_Number_Update]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

CREATE PROCEDURE [dbo].[usp_Sequence_Number_Update] @sequence_name NVARCHAR(50) = NULL
AS 
IF @sequence_name IS NULL
    BEGIN
        PRINT ''Sequence Name is null''
        RETURN 1
    END
IF @sequence_name NOT IN  (''AgencyBanksImageExchange'', ''TierOneBanksImageExchange'', ''ValueInstructionFile'')
    BEGIN
        PRINT ''Sequence Name does not match''
        RETURN 2
    END
    BEGIN
        UPDATE [dbo].[ref_sequence]
		SET [sequence_number] = [sequence_number] + 1
		FROM [dbo].[ref_sequence]
		WHERE [sequence_name] = @sequence_name;
    END;







' 
END
GO
GRANT EXECUTE ON [dbo].[usp_Sequence_Number_Update] TO [db_dataexecutor] AS [dbo]
GO
/****** Object:  Synonym [dbo].[dishonour]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'dishonour' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[dishonour] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_dishonour_letter_view]
GO
/****** Object:  Synonym [dbo].[document]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'document' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[document] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_document_sv]
GO
/****** Object:  Synonym [dbo].[file_receipt]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'file_receipt' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[file_receipt] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_file_receipt]
GO
/****** Object:  Synonym [dbo].[listing_view]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'listing_view' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[listing_view] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_listing_view]
GO
/****** Object:  Synonym [dbo].[reports]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'reports' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[reports] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_report_view]
GO
/****** Object:  Synonym [dbo].[voucher]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[voucher] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_voucher_view]
GO
/****** Object:  Synonym [dbo].[voucher_audit]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher_audit' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[voucher_audit] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_voucher_audit]
GO
/****** Object:  Synonym [dbo].[voucher_transfer]    Script Date: 13/01/2016 8:03:41 AM ******/
IF NOT EXISTS (SELECT * FROM sys.synonyms WHERE name = N'voucher_transfer' AND schema_id = SCHEMA_ID(N'dbo'))
CREATE SYNONYM [dbo].[voucher_transfer] FOR [VSQLS-NABDC\SQL1].[VDBS_NABDC].[dbo].[fxa_voucher_transfer_view]
GO
