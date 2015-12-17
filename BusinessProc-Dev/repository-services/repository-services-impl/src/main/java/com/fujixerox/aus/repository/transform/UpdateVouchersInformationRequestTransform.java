package com.fujixerox.aus.repository.transform;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.MappingEntry;
import com.fujixerox.aus.repository.util.MappingEntryFxaProcessor;
import com.fujixerox.aus.repository.util.MappingSAXHandler;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;

/**
 * Transform a StoreVocuherRequest (from queue) to IDfSysObject (Documentum)
 * Henry Niu
 * 15/07/2015
 */
public class UpdateVouchersInformationRequestTransform {  
	
	public void transform(VoucherInformation voucherInfo, IDfSysObject fxaVoucher) throws DfException  {
				
		Voucher voucher = voucherInfo.getVoucher();
	    LogUtil.log("UpdateVouchersInformationRequestTransform  :" + voucher.getDocumentReferenceNumber(), LogUtil.INFO, null);
	    
	    ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		try {
			LogUtil.log("voucherInfo = \n" + mapper.writeValueAsString(voucherInfo), LogUtil.INFO, null);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	    
	    /*try {
		    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		    MappingSAXHandler handler = new MappingSAXHandler();	    
			//parser.parse(ClassLoader.getSystemResourceAsStream("mapping/voucher_information_mapping.xml"), handler);			
			parser.parse(this.getClass().getResourceAsStream("/mapping/voucher_information_mapping.xml"), handler);
			
		    for ( MappingEntry entry : handler.getEntryList()) {
		      new MappingEntryFxaProcessor().process(voucherInfo, fxaVoucher, entry);
		    }
		} catch (Exception e) {
			throw new DfException(e);
		}*/
	 	
/*		fxaVoucher.setContentType("tiff"); //remove this hardcode later

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
		}	*/	

		VoucherProcess voucherProcess = voucherInfo.getVoucherProcess();
		try {
			LogUtil.log("voucherProcess = \n" + mapper.writeValueAsString(voucherProcess), LogUtil.INFO, null);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		if (voucherProcess != null) {			
			/*fxaVoucher.setBoolean(FxaVoucherField.UNPROCESSABLE_ITEM_FLAG, voucherProcess.isUnprocessable());
			fxaVoucher.setString(FxaVoucherField.TRAN_LINK_NO, voucherProcess.getTransactionLinkNumber());
			fxaVoucher.setString(FxaVoucherField.LISTING_PAGE_NUMBER, voucherProcess.getListingPageNumber());
			fxaVoucher.setString(FxaVoucherField.PRESENTATATION_MODE, voucherProcess.getPresentationMode());
			fxaVoucher.setBoolean(FxaVoucherField.MICR_FLAG, voucherProcess.isMicrFlag());
			fxaVoucher.setString(FxaVoucherField.RAW_MICR, voucherProcess.getRawMICR());
	        fxaVoucher.setString(FxaVoucherField.REPOST_DRN, voucherProcess.getRepostFromDRN());
			
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
			fxaVoucher.setString(FxaVoucherField.ADJUSTMENT_REASON_CODE, Integer.toString(voucherProcess.getAdjustmentReasonCode()));*/
			
			LogUtil.log("Updating fxa_adjustment_onhold_flag to " + voucherProcess.isAdjustmentsOnHold()  
					+ " for voucher i_chronicle_id = " + fxaVoucher.getId(FxaVoucherField.CHRONICLE_ID), LogUtil.INFO, null);			
			     
			fxaVoucher.setBoolean(FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG, voucherProcess.isAdjustmentsOnHold());
			/*fxaVoucher.setBoolean(FxaVoucherField.ADJUSTMENT_LETTER_REQ_FLAG, voucherProcess.isAdjustmentLetterRequired());
			fxaVoucher.setString(FxaVoucherField.ADJUSTMENT_DESCRIPTION, voucherProcess.getAdjustmentDescription());
			fxaVoucher.setBoolean(FxaVoucherField.SURPLUS_ITEM_FLAG, voucherProcess.isSurplusItemFlag());
            fxaVoucher.setBoolean(FxaVoucherField.IS_GENERATED_VOUCHER_FLAG, voucherProcess.isIsGeneratedVoucher());		*/
		}
	}	
	
}
