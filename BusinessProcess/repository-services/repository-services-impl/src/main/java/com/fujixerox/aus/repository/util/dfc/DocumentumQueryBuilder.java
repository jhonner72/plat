package com.fujixerox.aus.repository.util.dfc;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.repository.util.MappingEntry;
import com.fujixerox.aus.repository.util.MappingEntryObjectProcessor;
import com.fujixerox.aus.repository.util.MappingEntryQueryProcessor;
import com.fujixerox.aus.repository.util.MappingSAXHandler;
import com.fujixerox.aus.repository.util.MappingSAXHandlerFactory;

public class DocumentumQueryBuilder {
	
	public String buildSearchQuery(List<Criteria> list) throws DfException  {
		
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM fxa_voucher WHERE ");
		
	    try {
	    	MappingEntryQueryProcessor mappingEntryQueryProcessor = new MappingEntryQueryProcessor();
			MappingSAXHandler handler = MappingSAXHandlerFactory.getHandler();
		    for ( Criteria criteria : list) {
		    	MappingEntry mappingEntry = handler.getMappingEntryByValue(criteria.getName());
		    	queryBuffer.append(mappingEntryQueryProcessor.process(criteria, mappingEntry) + "\nAND ");
		    }
		} catch (Exception e) {
			throw new DfException(e);
		}
	    
	    String query = queryBuffer.toString();
	    if (query.trim().endsWith("AND")) {
			query = query.substring(0, query.length() - "AND ".length());
		}
		
		return query;
	}
	
	/*public static String buildSearchQuery(List<Criteria> list) {
		
		String query = "SELECT * FROM fxa_voucher WHERE ";
		
		Voucher voucher = list.getVoucher();
		
		if (voucher != null && voucher.getAccountNumber() != null) {
			query += FxaVoucherField.ACCOUNT_NUMBER + " = '" + voucher.getAccountNumber() + "' \nAND ";
		}
		if (voucher != null && voucher.getAmount() != null) {
			query += FxaVoucherField.AMOUNT + " = '" + voucher.getAmount() + "' \nAND ";
		}
		if (voucher != null && voucher.getAuxDom() != null) {
			query += FxaVoucherField.AUX_DOM + " = '" + voucher.getAuxDom() + "' \nAND ";
		}
		if (voucher != null && voucher.getBsbNumber() != null) {
			query += FxaVoucherField.BSB + " = '" + voucher.getBsbNumber() + "' \nAND ";
		}
		if (voucher != null && voucher.getDocumentReferenceNumber() != null) {
			query += FxaVoucherField.DRN + " = '" + voucher.getDocumentReferenceNumber() + "' \nAND ";
		}
		if (voucher != null && voucher.getDocumentType() != null) {
			query += FxaVoucherField.CLASSIFICATION + " = '" + voucher.getDocumentType().value() + "' \nAND ";
		}
		if (voucher != null && voucher.getExtraAuxDom() != null) {
			query += FxaVoucherField.EXTRA_AUX_DOM + " = '" + voucher.getExtraAuxDom() + "' \nAND ";
		}
		if (voucher != null && voucher.getProcessingDate() != null) {
			String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(voucher.getProcessingDate());
			query += FxaVoucherField.PROCESSING_DATE + " = date('" + procegssingDateString + "', '" 
					+  Constant.DOCUMENTUM_DATE_FORMAT + "') \nAND ";
		}
		if (voucher != null && voucher.getTransactionCode() != null) {
			query += FxaVoucherField.TRANCODE + " = '" + voucher.getTransactionCode() + "' \nAND ";
		}
		
		VoucherBatch voucherBatch = list.getVoucherBatch();
		
		if (voucherBatch != null && voucherBatch.getBatchAccountNumber() != null) {
			query += FxaVoucherField.BATCH_ACCOUNT_NUMBER + " = '" + voucherBatch.getBatchAccountNumber() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getCaptureBsb() != null) {
			query += FxaVoucherField.CAPTURE_BSB + " = '" + voucherBatch.getCaptureBsb() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getCollectingBank() != null) {
			query += FxaVoucherField.COLLECTING_BSB + " = '" + voucherBatch.getCollectingBank() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getProcessingState() != null) {
			query += FxaVoucherField.PROCESSING_STATE + " = '" + voucherBatch.getProcessingState().value() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getScannedBatchNumber() != null) {
			query += FxaVoucherField.MIGRATION_BATCH_NO + " = '" + voucherBatch.getScannedBatchNumber() + "' \nAND ";
			query += FxaVoucherField.BATCH_NUMBER + " = '" + voucherBatch.getScannedBatchNumber() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getUnitID() != null) {
			query += FxaVoucherField.UNIT_ID + " = '" + voucherBatch.getUnitID() + "' \nAND ";
		}
		if (voucherBatch != null && voucherBatch.getWorkType() != null) {
			query += FxaVoucherField.WORK_TYPE_CODE + " = '" + voucherBatch.getWorkType().value() + "' \nAND ";
		}
		
		VoucherProcess voucherProcess = list.getVoucherProcess();
		
		if (voucherProcess != null && voucherProcess.getAdjustedBy() != null) {
			query += FxaVoucherField.ADJUSTED_BY_ID + " = '" + voucherProcess.getAdjustedBy() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getAdjustmentDescription() != null) {
			query += FxaVoucherField.ADJUSTMENT_DESCRIPTION + " = '" + voucherProcess.getAdjustmentDescription() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getAdjustmentReasonCode() != 0) {
			query += FxaVoucherField.ADJUSTMENT_REASON_CODE + " = '" + voucherProcess.getAdjustmentReasonCode() + "' \nAND ";
		}
		if (voucherProcess != null && voucherProcess.getForValueType() != null) {
			query += FxaVoucherField.FOR_VALUE_TYPE + " = '" + voucherProcess.getForValueType().value() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getListingPageNumber() != null) {
			query += FxaVoucherField.LISTING_PAGE_NUMBER + " = '" + voucherProcess.getListingPageNumber() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getManualRepair() == 1) {
			query += FxaVoucherField.MANUAL_REPAIR + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.getPresentationMode() != null) {
			query += FxaVoucherField.PRESENTATATION_MODE + " = '" + voucherProcess.getPresentationMode() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getRawMICR() != null) {
			query += FxaVoucherField.RAW_MICR + " = '" + voucherProcess.getRawMICR() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getRepostFromDRN() != null) {
			query += FxaVoucherField.REPOST_DRN + " = '" + voucherProcess.getRepostFromDRN() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getRepostFromProcessingDate() != null) {
			String procegssingDateString = new SimpleDateFormat(Constant.DM_PROCESSING_DATE_FORMAT).format(voucherProcess.getRepostFromProcessingDate());
			query += FxaVoucherField.REPOST_PROCESSING_DATE + " = date('" + procegssingDateString + "', '" 
					+  Constant.DOCUMENTUM_DATE_FORMAT + "') \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getTransactionLinkNumber() != null) {
			query += FxaVoucherField.TRAN_LINK_NO + " = '" + voucherProcess.getTransactionLinkNumber() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.getVoucherDelayedIndicator() != null) {
			query += FxaVoucherField.VOUCHER_DELAYED_ID + " = '" + voucherProcess.getVoucherDelayedIndicator() + "' \nAND ";
		}		
		if (voucherProcess != null && voucherProcess.isAdjustedFlag()) {
			query += FxaVoucherField.ADJUSTMENT_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isAdjustmentLetterRequired()) {
			query += FxaVoucherField.ADJUSTMENT_LETTER_REQ_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isAdjustmentsOnHold()) {
			query += FxaVoucherField.ADJUSTMENT_ON_HOLD_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isHighValueFlag()) {
			query += FxaVoucherField.HIGH_VALUE_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isInactiveFlag()) {
			query += FxaVoucherField.INACTIVE_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isMicrFlag()) {
			query += FxaVoucherField.MICR_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isSurplusItemFlag()) {
			query += FxaVoucherField.SURPLUS_ITEM_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isSuspectFraud()) {
			query += FxaVoucherField.SUSPECT_FRAUD_FLAG + " = true \nAND ";
		}
		if (voucherProcess != null && voucherProcess.isUnprocessable()) {
			query += FxaVoucherField.UNPROCESSABLE_ITEM_FLAG + " = true \nAND ";
		}
        if (voucherProcess != null && voucherProcess.isIsGeneratedVoucher()) {
            query += FxaVoucherField.IS_GENERATED_VOUCHER_FLAG + " = true \nAND ";
        }
        if (voucherProcess != null && voucherProcess.isPostTransmissionQaAmountFlag()) {
            query += FxaVoucherField.PTQA_AMOUNT_FLAG + " = true \nAND ";
        }
        if (voucherProcess != null && voucherProcess.isPostTransmissionQaCodelineFlag()) {
            query += FxaVoucherField.PTQA_CODELINE_FLAG + " = true \nAND ";
        }

		if (query.trim().endsWith("AND")) {
			query = query.substring(0, query.length() - "AND ".length());
		}
		
		return query;		
	}*/
	


}
