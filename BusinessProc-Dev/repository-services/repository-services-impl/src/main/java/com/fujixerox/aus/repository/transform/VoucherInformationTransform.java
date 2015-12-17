package com.fujixerox.aus.repository.transform;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.ForValueTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;   
import com.fujixerox.aus.repository.util.MappingEntry;
import com.fujixerox.aus.repository.util.MappingEntryObjectProcessor;
import com.fujixerox.aus.repository.util.MappingSAXHandler;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;

/** 
 * Henry Niu
 * 19/5/2015
 */ 
public class VoucherInformationTransform {
	
	public VoucherInformation transform(IDfSysObject fxaVoucher) throws DfException   {
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(new Voucher());
		voucherInfo.setVoucherBatch(new VoucherBatch());
		voucherInfo.setVoucherProcess(new VoucherProcess());
		
		try {
		    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		    MappingSAXHandler handler = new MappingSAXHandler();	    
			//parser.parse(ClassLoader.getSystemResourceAsStream("mapping/voucher_information_mapping.xml"), handler);			
			parser.parse(this.getClass().getResourceAsStream("/mapping/voucher_information_mapping.xml"), handler);
			
		    for ( MappingEntry entry : handler.getEntryList()) {
		      new MappingEntryObjectProcessor().process(fxaVoucher, voucherInfo, entry);
		    }
		} catch (Exception e) {
			throw new DfException(e);
		}
		
		return voucherInfo;
		
		/*Voucher voucher = new VoucherTransform().transform(fxaVoucher);
		
		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setCaptureBsb(fxaVoucher.getString(FxaVoucherField.CAPTURE_BSB));
		voucherBatch.setCollectingBank(fxaVoucher.getString(FxaVoucherField.COLLECTING_BSB));
		voucherBatch.setScannedBatchNumber(fxaVoucher.getString(FxaVoucherField.BATCH_NUMBER));
		voucherBatch.setUnitID(fxaVoucher.getString(FxaVoucherField.UNIT_ID));
		voucherBatch.setBatchAccountNumber(fxaVoucher.getString(FxaVoucherField.BATCH_ACCOUNT_NUMBER));
		
		String processingState = fxaVoucher.getString(FxaVoucherField.PROCESSING_STATE);
		if (processingState != null && !processingState.equals("")) {
			voucherBatch.setProcessingState(StateEnum.valueOf(processingState));
		}
		
		String workType = fxaVoucher.getString(FxaVoucherField.WORK_TYPE_CODE);
		if (workType != null && !workType.equals("")) {
			voucherBatch.setWorkType(WorkTypeEnum.fromValue(workType));
		}	
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setVoucherDelayedIndicator(fxaVoucher.getString(FxaVoucherField.VOUCHER_DELAYED_ID));
		voucherProcess.setListingPageNumber(fxaVoucher.getString(FxaVoucherField.LISTING_PAGE_NUMBER));
		voucherProcess.setMicrFlag(fxaVoucher.getBoolean(FxaVoucherField.MICR_FLAG));
		voucherProcess.setPresentationMode(fxaVoucher.getString(FxaVoucherField.PRESENTATATION_MODE));
		voucherProcess.setRawMICR(fxaVoucher.getString(FxaVoucherField.RAW_MICR));
		voucherProcess.setUnprocessable(fxaVoucher.getBoolean(FxaVoucherField.UNPROCESSABLE_ITEM_FLAG));
		voucherProcess.setSuspectFraud(fxaVoucher.getBoolean(FxaVoucherField.SUSPECT_FRAUD_FLAG));
		voucherProcess.setInactiveFlag(fxaVoucher.getBoolean(FxaVoucherField.INACTIVE_FLAG));
        voucherProcess.setSurplusItemFlag(fxaVoucher.getBoolean(FxaVoucherField.SURPLUS_ITEM_FLAG));
        voucherProcess.setPreAdjustmentAmount(fxaVoucher.getString(FxaVoucherField.PRE_ADJUSTMENT_AMT));
        voucherProcess.setIsGeneratedVoucher(fxaVoucher.getBoolean(FxaVoucherField.IS_GENERATED_VOUCHER_FLAG));

        String forValueType = fxaVoucher.getString(FxaVoucherField.FOR_VALUE_TYPE);
        if (forValueType != null && !forValueType.equals("")) {
        	voucherProcess.setForValueType(ForValueTypeEnum.fromValue(forValueType));
        }

		String tranLinkNo = fxaVoucher.getString(FxaVoucherField.TRAN_LINK_NO);
		if (tranLinkNo != null && !tranLinkNo.equals("")) {
			voucherProcess.setTransactionLinkNumber(tranLinkNo);
		}
		
		int manualRepair = 0;
		if (fxaVoucher.getBoolean(FxaVoucherField.MANUAL_REPAIR)) {
			manualRepair = 1;
		}
		voucherProcess.setManualRepair(manualRepair);
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherBatch(voucherBatch);
		voucherInfo.setVoucherProcess(voucherProcess);

		return voucherInfo;*/
	}

}
