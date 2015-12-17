package com.fujixerox.aus.repository.transform;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.MappingEntry;
import com.fujixerox.aus.repository.util.MappingEntryFxaProcessor;
import com.fujixerox.aus.repository.util.MappingSAXHandler;
import com.fujixerox.aus.repository.util.MappingSAXHandlerFactory;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;

/**
 * Transform a StoreVocuherRequest (from queue) to IDfSysObject (Documentum)
 * Henry Niu
 * 25/03/2015
 */
public class StoreVoucherRequestTransform {  
				
	public IDfSysObject transform(IDfSession session, VoucherInformation voucherInfo, String fileId)	
			throws DfException  {
		
	    LogUtil.log("Processing Document  :" + voucherInfo.getVoucher().getDocumentReferenceNumber(), LogUtil.INFO, null);

	    IDfSysObject fxaVoucher = (IDfSysObject)session.newObject(RepositoryProperties.doc_object_type);
		fxaVoucher.setContentType("tiff"); //remove this hardcode later		
		fxaVoucher.setString(FxaVoucherField.FXA_FILE_RECEIPT_ID, fileId);

		String lastEntryMapped = "";
	    try {
	    	MappingEntryFxaProcessor mappingEntryFxaProcessor = new MappingEntryFxaProcessor();
	    	MappingSAXHandler handler = MappingSAXHandlerFactory.getHandler();
		    for ( MappingEntry entry : handler.getEntryList()) {
				lastEntryMapped = entry.getKey();
		    	mappingEntryFxaProcessor.process(voucherInfo, fxaVoucher, entry);
		    }
		} catch (Exception e) {
			LogUtil.log("Exception while storing key:" + lastEntryMapped, LogUtil.DEBUG, e);
			throw new DfException(e);
			/*
			This may seem a little pedantic to track last error, but it is probably better than doing nothing (and
			save the time involved is doing it via a debugger). I also don't mind setting off alarms here as it seems
			strange to reclassifying non-documentum exceptions to a documentum one (in the hope of identifying the
			cause).
			 */
		}
		
		return fxaVoucher;
		
		/*IDfSysObject fxaVoucher = (IDfSysObject)session.newObject(RepositoryProperties.doc_object_type);
		
		Voucher voucher = voucherInfo.getVoucher();
	 	
		fxaVoucher.setContentType("tiff"); //remove this hardcode later

		fxaVoucher.setString(FxaVoucherField.EXTRA_AUX_DOM, voucher.getExtraAuxDom());
		fxaVoucher.setString(FxaVoucherField.AUX_DOM, voucher.getAuxDom());
		fxaVoucher.setString(FxaVoucherField.BSB, voucher.getBsbNumber());
		fxaVoucher.setString(FxaVoucherField.ACCOUNT_NUMBER, voucher.getAccountNumber());
		fxaVoucher.setString(FxaVoucherField.AMOUNT, voucher.getAmount());
		fxaVoucher.setString(FxaVoucherField.DRN, voucher.getDocumentReferenceNumber());
		fxaVoucher.setString(FxaVoucherField.TRANCODE, voucher.getTransactionCode());

		Date processingDate = voucher.getProcessingDate();
		if (processingDate != null) {
			IDfTime processTime = new DfTime(voucher.getProcessingDate());			
			String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
			IDfTime timeValue = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14); 
			fxaVoucher.setTime(FxaVoucherField.PROCESSING_DATE, timeValue);
		}

        DocumentTypeEnum documentTypeEnum = voucher.getDocumentType();
		if (documentTypeEnum != null) {
			fxaVoucher.setString(FxaVoucherField.CLASSIFICATION, documentTypeEnum.value());
		}	
		
		fxaVoucher.setString(FxaVoucherField.FXA_FILE_RECEIPT_ID, fileId);
		
		VoucherBatch voucherBatch = voucherInfo.getVoucherBatch();
		if (voucherBatch != null) {
			fxaVoucher.setString(FxaVoucherField.MIGRATION_BATCH_NO, voucherBatch.getScannedBatchNumber());
			fxaVoucher.setString(FxaVoucherField.BATCH_NUMBER, voucherBatch.getScannedBatchNumber());
			fxaVoucher.setString(FxaVoucherField.UNIT_ID, voucherBatch.getUnitID());
			fxaVoucher.setString(FxaVoucherField.CAPTURE_BSB, voucherBatch.getCaptureBsb());
			fxaVoucher.setString(FxaVoucherField.BATCH_ACCOUNT_NUMBER, voucherBatch.getBatchAccountNumber());
			fxaVoucher.setString(FxaVoucherField.COLLECTING_BSB, voucherBatch.getCollectingBank());
			
			StateEnum stateEnum = voucherBatch.getProcessingState();
			if (stateEnum != null) {
				fxaVoucher.setString(FxaVoucherField.PROCESSING_STATE, stateEnum.value());
			}
			
			WorkTypeEnum workTypeEnum = voucherBatch.getWorkType();
			if (workTypeEnum != null) {
				fxaVoucher.setString(FxaVoucherField.WORK_TYPE_CODE, workTypeEnum.value());
			}	
		}		

		VoucherProcess voucherProcess = voucherInfo.getVoucherProcess();
		if (voucherProcess != null) {
			fxaVoucher.setBoolean(FxaVoucherField.UNPROCESSABLE_ITEM_FLAG, voucherProcess.isUnprocessable());
			fxaVoucher.setString(FxaVoucherField.TRAN_LINK_NO, voucherProcess.getTransactionLinkNumber());
			fxaVoucher.setString(FxaVoucherField.LISTING_PAGE_NUMBER, voucherProcess.getListingPageNumber());
			fxaVoucher.setString(FxaVoucherField.PRESENTATATION_MODE, voucherProcess.getPresentationMode());
			fxaVoucher.setBoolean(FxaVoucherField.MICR_FLAG, voucherProcess.isMicrFlag());
			fxaVoucher.setString(FxaVoucherField.RAW_MICR, voucherProcess.getRawMICR());
	        fxaVoucher.setString(FxaVoucherField.REPOST_DRN, voucherProcess.getRepostFromDRN());
	        fxaVoucher.setString(FxaVoucherField.VOUCHER_DELAYED_ID, voucherProcess.getVoucherDelayedIndicator());
			fxaVoucher.setString(FxaVoucherField.PRE_ADJUSTMENT_AMT, voucherProcess.getPreAdjustmentAmount());
			fxaVoucher.setBoolean(FxaVoucherField.HIGH_VALUE_FLAG, voucherProcess.isHighValueFlag());

	        Date repostProcessingDate = voucherProcess.getRepostFromProcessingDate();
	        if (repostProcessingDate != null) {
	            IDfTime processTime = new DfTime(voucherProcess.getRepostFromProcessingDate());
	            String dateFormat = processTime.getDay() + "/" + processTime.getMonth() + "/" + processTime.getYear() + " 12:00:00";
	            IDfTime timeValue = new DfTime(dateFormat, IDfTime.DF_TIME_PATTERN14);
	            fxaVoucher.setTime(FxaVoucherField.REPOST_PROCESSING_DATE, timeValue);
	        }
	        
	        ForValueTypeEnum forValueTypeEnum = voucherProcess.getForValueType();
			if (forValueTypeEnum != null) {
				fxaVoucher.setString(FxaVoucherField.FOR_VALUE_TYPE, forValueTypeEnum.value());
			}		
			
			int manualRepair = voucherProcess.getManualRepair();
			if (manualRepair == 0) {
				fxaVoucher.setBoolean(FxaVoucherField.MANUAL_REPAIR, false);
			} else {
				fxaVoucher.setBoolean(FxaVoucherField.MANUAL_REPAIR, true);
			}
			
			fxaVoucher.setBoolean(FxaVoucherField.SUSPECT_FRAUD_FLAG, voucherProcess.isSuspectFraud());
			fxaVoucher.setBoolean(FxaVoucherField.INACTIVE_FLAG, voucherProcess.isInactiveFlag());
			
			fxaVoucher.setString(FxaVoucherField.ADJUSTED_BY_ID, voucherProcess.getAdjustedBy());
			fxaVoucher.setBoolean(FxaVoucherField.ADJUSTMENT_FLAG, voucherProcess.isAdjustedFlag());
			fxaVoucher.setString(FxaVoucherField.ADJUSTMENT_REASON_CODE, Integer.toString(voucherProcess.getAdjustmentReasonCode()));
			fxaVoucher.setBoolean(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, voucherProcess.isAdjustmentsOnHold());
			fxaVoucher.setBoolean(FxaVoucherField.ADJUSTMENT_LETTER_REQ_FLAG, voucherProcess.isAdjustmentLetterRequired());
			fxaVoucher.setString(FxaVoucherField.ADJUSTMENT_DESCRIPTION, voucherProcess.getAdjustmentDescription());
			fxaVoucher.setBoolean(FxaVoucherField.SURPLUS_ITEM_FLAG, voucherProcess.isSurplusItemFlag());
            fxaVoucher.setBoolean(FxaVoucherField.IS_GENERATED_VOUCHER_FLAG, voucherProcess.isIsGeneratedVoucher());
        }
		
		return fxaVoucher;*/
	}	
	
}
