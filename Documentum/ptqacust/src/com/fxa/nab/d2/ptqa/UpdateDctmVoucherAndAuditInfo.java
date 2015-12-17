package com.fxa.nab.d2.ptqa;

import com.documentum.fc.client.IDfCollection;

public class UpdateDctmVoucherAndAuditInfo {
	
	@Deprecated
	public boolean updateDctmVoucherAndAuditInfo(String chronicleId,String codelineResult,String amountResult){
		
		try{
				boolean amtOrCdcModified=false;
				CommonUtils.writeToLog("Inside Method updateDctmVoucherAndAuditInfo()", CommonUtils.debug, null);
				
				DctmActivities dctm=DctmActivities.getInstance();
				IDfCollection collection=null;
				
				if(amountResult.trim().equals("0") || amountResult.trim().equals("1")){
					String amountInsertQuery=String.format(PTQAProperties.amount_insert_query,chronicleId,amountResult);
					CommonUtils.writeToLog("Query to be executed to insert Amount result is ["+amountInsertQuery+"].", CommonUtils.debug, null);
					collection=dctm.executeQuery(amountInsertQuery);
					amtOrCdcModified=true;
				}else{
					CommonUtils.writeToLog("Amount Result is not provided. This voucher may not have Amount validation.", CommonUtils.debug, null);	
				}
				
				if(codelineResult.trim().equals("0") || codelineResult.trim().equals("1")){
				String codelineInsertQuery=String.format(PTQAProperties.codeline_insert_query,chronicleId,codelineResult);
				CommonUtils.writeToLog("Query to be executed to insert Codeline result is ["+codelineInsertQuery+"].", CommonUtils.debug, null);
					collection=dctm.executeQuery(codelineInsertQuery);
					amtOrCdcModified=true;
				}else{
					CommonUtils.writeToLog("Codeline Result is not provided. This voucher may not have Codeline validation.", CommonUtils.debug, null);	
				}	
				
				if(amtOrCdcModified){
					//update Voucher only if Amount or Codeline is updated. Otherwisee, updating them is invalid.
				String voucherUpdateQuery=String.format(PTQAProperties.voucher_update_query,chronicleId);
				
				CommonUtils.writeToLog("Query to be executed to update Voucher is ["+voucherUpdateQuery+"].", CommonUtils.debug, null);
				collection=dctm.executeQuery(voucherUpdateQuery);	
				}
		}catch(Exception e){
			CommonUtils.writeToLog("Error from Class: UpdateDctmVoucherAndAuditInfo; Method:updateDctmVoucherAndAuditInfo while processing update voucher query and insert audit table rows. ", CommonUtils.debug, null);
			return false;
		}
			CommonUtils.writeToLog("Leaving Method updateDctmVoucherAndAuditInfo()", CommonUtils.debug, null);
			return true;
	}
	
	public boolean updateDctmVoucherAndAuditInfoForAmt(String chronicleId,String amountResult){
		
		try{
				boolean isAmtModified=false;
				CommonUtils.writeToLog("Inside Method updateDctmVoucherAndAuditInfoForAmt()", CommonUtils.debug, null);
				
				DctmActivities dctm=DctmActivities.getInstance();
				IDfCollection collection=null;
				
				if(amountResult.trim().equals("0") || amountResult.trim().equals("1")){
					String amountInsertQuery=String.format(PTQAProperties.amount_insert_query,chronicleId,amountResult);
					CommonUtils.writeToLog("Query to be executed to insert Amount result is ["+amountInsertQuery+"].", CommonUtils.debug, null);
					collection=dctm.executeQuery(amountInsertQuery);
					isAmtModified=true;
				}else{
					CommonUtils.writeToLog("Amount Result is not provided. This voucher may not have Amount validation.", CommonUtils.debug, null);	
				}
				
				if(isAmtModified){
					//update Voucher only if Amount or Codeline is updated. Otherwisee, updating them is invalid.
				String voucherUpdateQuery=String.format(PTQAProperties.voucher_amt_update_query,chronicleId);
				
				CommonUtils.writeToLog("Query to be executed to update Voucher for Amount Validation is ["+voucherUpdateQuery+"].", CommonUtils.debug, null);
				collection=dctm.executeQuery(voucherUpdateQuery);	
				}
		}catch(Exception e){
			CommonUtils.writeToLog("Error from Class: UpdateDctmVoucherAndAuditInfo; Method:updateDctmVoucherAndAuditInfoForAmt while processing update voucher query and insert audit table rows. ", CommonUtils.debug, null);
			return false;
		}
			CommonUtils.writeToLog("Leaving Method updateDctmVoucherAndAuditInfoForAmt()", CommonUtils.debug, null);
			return true;
	}
	
	public boolean updateDctmVoucherAndAuditInfoForCdc(String chronicleId,String codelineResult){
		
		try{
				boolean isCdcModified=false;
				CommonUtils.writeToLog("Inside Method updateDctmVoucherAndAuditInfoForCdc()", CommonUtils.debug, null);
				
				DctmActivities dctm=DctmActivities.getInstance();
				IDfCollection collection=null;
				
				if(codelineResult.trim().equals("0") || codelineResult.trim().equals("1")){
				String codelineInsertQuery=String.format(PTQAProperties.codeline_insert_query,chronicleId,codelineResult);
				CommonUtils.writeToLog("Query to be executed to insert Codeline result is ["+codelineInsertQuery+"].", CommonUtils.debug, null);
					collection=dctm.executeQuery(codelineInsertQuery);
					isCdcModified=true;
				}else{
					CommonUtils.writeToLog("Codeline Result is not provided. This voucher may not have Codeline validation.", CommonUtils.debug, null);	
				}	
				
				if(isCdcModified){
					//update Voucher only if Amount or Codeline is updated. Otherwisee, updating them is invalid.
				String voucherUpdateQuery=String.format(PTQAProperties.voucher_cdc_update_query,chronicleId);
				
				CommonUtils.writeToLog("Query to be executed to update Voucher for Codeline Validation is ["+voucherUpdateQuery+"].", CommonUtils.debug, null);
				collection=dctm.executeQuery(voucherUpdateQuery);	
				}
		}catch(Exception e){
			CommonUtils.writeToLog("Error from Class: UpdateDctmVoucherAndAuditInfo; Method:updateDctmVoucherAndAuditInfoForCdc while processing update voucher query and insert audit table rows. ", CommonUtils.debug, null);
			return false;
		}
			CommonUtils.writeToLog("Leaving Method updateDctmVoucherAndAuditInfoForCdc()", CommonUtils.debug, null);
			return true;
	}	

}
