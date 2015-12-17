package com.fujixerox.aus.repository;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fujixerox.aus.lombard.outclearings.triggerworkflow.TriggerWorkflowRequest;
import com.fujixerox.aus.lombard.reporting.metadata.FormatType;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;

import com.fujixerox.aus.lombard.repository.storebatchvoucher.*;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreBatchAdjustmentLettersRequest;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.Constant;

/** 
 * Henry Niu
 * 25/03/2015
 */
public class RepositoryServiceTestHelper {

	public static StoreBatchVoucherRequest buildStoreBatchVoucherRequest(String batchNumber,
		String jobIdentifier, String processingDate, boolean unprocessable) throws ParseException {
		
		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();

		request.setVoucherBatch(buildVoucherBatch());
		request.setJobIdentifier(jobIdentifier);

		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (int i = 0; i < C.ACCOUNT_NUMBERS.length; i++) {
			storeVoucherRequests.add(buildStoreVoucherRequest(C.ACCOUNT_NUMBERS[i], C.AMOUNTS[i], C.BSBS[i], C.DRNS[i],
					processingDate, unprocessable, C.DOCUMENT_TYPES[i], C.AUX_DOMS[i], C.TARGET_END_POINTS[i],
					C.TRAN_LINK_NOS[i], C.LISTING_PAGE_NUMBERS[i], C.VOUCHER_DELAYED_INDICATORS[i], C.ADJUSTED_BY[i], C.ADJUSTED_FLAG[i],
					C.ADJUSTED_REASON_CODE[i], C.ADJUSTED_ON_HOLD[i], C.ADJUSTMENT_LETTER_REQUIRED[i], C.IS_GENERATED_VOUCHER[i], C.HIGH_VALUE_FLAGS[i], C.TPC_FAILED_FLAG[i],C.TPC_SUSPENSE_POOL_FLAG[i], C.UECD_RETURN_FLAG[i], C.MIXED_DEP_RETURN_FLAG[i]));

		}
		
		ReceivedFile receiptFile = new ReceivedFile();
		receiptFile.setFileIdentifier("test.txt");
		receiptFile.setReceivedDateTime(new Date());
		receiptFile.setTransmissionDateTime(new Date());
		
		request.setReceipt(receiptFile);

		return request;
	}

	public static StoreVoucherRequest buildStoreVoucherRequest(String accountNumber, String amount, String bsbNumber,
		   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
		   String targetEndPoint, String tranLinkNo, String listingPageNumber, String voucherDelayedIndicator, String adjustedBy, boolean adjustedFlag,
		   int adjustedReasonCode, boolean adjustedOnHold, boolean adjustmentLetterRequired, boolean isGeneratedVoucher, boolean isHighValueFlag,  boolean tpcFailedFlag,
															   boolean tpcSuspenceFlag, boolean uecdReturnFlag, boolean mixedDepReturnFlag) throws ParseException {

		Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
				docType, auxDom);

		StoreVoucherRequest storeVoucherRequest = new StoreVoucherRequest();
		
		VoucherProcess voucherProcess = buildVoucherProcess(unprocessable,
				tranLinkNo, listingPageNumber, voucherDelayedIndicator, adjustedBy, adjustedFlag,
				adjustedReasonCode, adjustedOnHold, adjustmentLetterRequired, isGeneratedVoucher, isHighValueFlag);
		
		storeVoucherRequest.setVoucherProcess(voucherProcess);
		storeVoucherRequest.setVoucher(voucher);
		storeVoucherRequest.getTransferEndpoints().addAll(buildEndpoint(targetEndPoint));
		storeVoucherRequest.getVoucherAudits().addAll(getVoucherAuditList());


		return storeVoucherRequest;
	}
	
	public static VoucherInformation buildVoucherInformation(String accountNumber, String amount, String bsbNumber,
		   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
		   String targetEndPoint, String tranLinkNo, String listingPageNumber, String voucherDelayedIndicator, boolean isHighValueFlag) throws ParseException {

		Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
				docType, auxDom);
		
		VoucherProcess voucherProcess = buildVoucherProcess(unprocessable, listingPageNumber, listingPageNumber, voucherDelayedIndicator, listingPageNumber,
				unprocessable, 0, unprocessable, unprocessable, unprocessable, isHighValueFlag);
				
		VoucherBatch voucherBatch = buildVoucherBatch();		
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);
		voucherInfo.setVoucherBatch(voucherBatch);
		
		return voucherInfo;
	}
	
	public static StoreVoucher buildStoreVoucher(String accountNumber, String amount, String bsbNumber,
			   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
			   String targetEndPoint, String tranLinkNo, String listingPageNUmber, boolean isHighValueFlag) throws ParseException {

			Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
					docType, auxDom);
			
			VoucherProcess voucherProcess = buildVoucherProcess(unprocessable, tranLinkNo, listingPageNUmber, listingPageNUmber, listingPageNUmber,
					unprocessable, 0, unprocessable, unprocessable, unprocessable, isHighValueFlag);
			
			VoucherBatch voucherBatch = buildVoucherBatch();
			
			VoucherInformation voucherInfo = new VoucherInformation();
			voucherInfo.setVoucher(voucher);
			voucherInfo.setVoucherProcess(voucherProcess);	
			voucherInfo.setVoucherBatch(voucherBatch);
			
			StoreVoucher storeVoucher = new StoreVoucher();
			storeVoucher.setVoucherInformation(voucherInfo);
			storeVoucher.getTransferEndpoints().addAll(buildEndpoint(targetEndPoint));
			
			return storeVoucher;			
		}
	
	public static List<TransferEndpoint> buildEndpoint(String targetEndPoint) {
		
		List<TransferEndpoint> endpoints = new ArrayList<TransferEndpoint>();
		
		TransferEndpoint transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);

		endpoints.add(transferEndpoint);

		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);

		endpoints.add(transferEndpoint);
		
		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);

		endpoints.add(transferEndpoint);
		
		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
		transferEndpoint.setFilename("test.xml");
		transferEndpoint.setTransmissionDate(new Date());
		
		endpoints.add(transferEndpoint);
		
		return endpoints;
	}


	private static List<VoucherAudit> getVoucherAuditList() {

		List<VoucherAudit> voucherAudits = new ArrayList<VoucherAudit>();
		VoucherAudit voucherAudit = new VoucherAudit();
		voucherAudit.setOperator("o1");
		voucherAudit.setSubjectArea("dips");
		voucherAudit.setAttributeName("operator_name");
		voucherAudits.add(voucherAudit);

		VoucherAudit voucherAuditCaptureAmt = new VoucherAudit();
		voucherAuditCaptureAmt.setAttributeName("captured_amt");
		voucherAuditCaptureAmt.setSubjectArea("car");
		voucherAuditCaptureAmt.setPreValue("12");
		voucherAuditCaptureAmt.setPostValue("14");
		voucherAudits.add(voucherAuditCaptureAmt);

		VoucherAudit voucherAuditTimings = new VoucherAudit();
		voucherAuditTimings.setAttributeName("timings");
		voucherAuditTimings.setSubjectArea("car");
		Date dt = new Date();
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT);
		String date_to_string = dateformatyyyyMMdd.format(dt);
		System.out.println("date into yyyyMMdd format: " + date_to_string);
		voucherAuditTimings.setPreValue("28082015 15:05:22");
		voucherAuditTimings.setPostValue("28082015 15:05:22");
		voucherAudits.add(voucherAuditTimings);

		VoucherAudit voucherAuditAmtStatus = new VoucherAudit();
		voucherAuditAmtStatus.setAttributeName("amount_status");
		voucherAuditAmtStatus.setSubjectArea("cdv");
		voucherAuditAmtStatus.setPostValue(Boolean.toString(true));
		voucherAudits.add(voucherAuditAmtStatus);

		VoucherAudit voucherAuditTiming = new VoucherAudit();
		voucherAuditTiming.setAttributeName("timings");
		voucherAuditTiming.setSubjectArea("cdv");
		voucherAuditTiming.setPreValue("28082015 15:05:22");
		voucherAuditTiming.setPostValue("28082015 15:05:22");
		voucherAudits.add(voucherAuditTiming);

		VoucherAudit voucherAccount = new VoucherAudit();
		voucherAccount.setAttributeName("acc");
		voucherAccount.setSubjectArea("cdc");
		voucherAccount.setPreValue("10101010");
		voucherAccount.setPostValue("1625454");
		voucherAudits.add(voucherAccount);

		VoucherAudit voucherEad = new VoucherAudit();
		voucherEad.setAttributeName("ead");
		voucherEad.setSubjectArea("cdc");
		voucherEad.setPreValue("012");
		voucherEad.setPostValue("345");
		voucherAudits.add(voucherEad);

		VoucherAudit voucherAd = new VoucherAudit();
		voucherAd.setAttributeName("ad");
		voucherAd.setSubjectArea("cdc");
		voucherAd.setPreValue("990");
		voucherAd.setPostValue("088");
		voucherAudits.add(voucherAd);


		VoucherAudit voucherBsb = new VoucherAudit();
		voucherBsb.setAttributeName("bsb");
		voucherBsb.setSubjectArea("cdc");
		voucherBsb.setPreValue("9898");
		voucherBsb.setPostValue("7878");
		voucherAudits.add(voucherBsb);

		VoucherAudit voucherTc = new VoucherAudit();
		voucherTc.setAttributeName("tc");
		voucherTc.setSubjectArea("cdc");
		voucherTc.setPreValue("01");
		voucherTc.setPostValue("02");
		voucherAudits.add(voucherTc);

		VoucherAudit voucherAmt = new VoucherAudit();
		voucherAmt.setAttributeName("amt");
		voucherAmt.setSubjectArea("cdc");
		voucherAmt.setPreValue("45");
		voucherAmt.setPostValue("98");
		voucherAudits.add(voucherAmt);


		VoucherAudit voucherAuditTimin = new VoucherAudit();
		voucherAuditTimin.setAttributeName("timings");
		voucherAuditTimin.setSubjectArea("cdc");
		voucherAuditTimin.setPreValue("28082015 15:05:22");
		voucherAuditTimin.setPostValue("28082015 15:05:22");
		voucherAudits.add(voucherAuditTimin);

		VoucherAudit voucherAuditTim = new VoucherAudit();
		voucherAuditTim.setAttributeName("timings");
		voucherAuditTim.setSubjectArea("abal");
		voucherAuditTim.setPreValue("28082015 15:05:22");
		voucherAuditTim.setPostValue("28082015 15:05:22");
		voucherAudits.add(voucherAuditTim);

		VoucherAudit voucherAccount1 = new VoucherAudit();
		voucherAccount1.setAttributeName("acc");
		voucherAccount1.setSubjectArea("ebal");
		voucherAccount1.setPreValue("01235");
		voucherAccount1.setPostValue("98765");
		voucherAudits.add(voucherAccount1);



		VoucherAudit voucherEad1 = new VoucherAudit();
		voucherEad1.setAttributeName("ead");
		voucherEad1.setSubjectArea("ebal");
		voucherEad1.setPreValue("056");
		voucherEad1.setPostValue("098");
		voucherAudits.add(voucherEad1);


		VoucherAudit voucherAd1 = new VoucherAudit();
		voucherAd1.setAttributeName("ad");
		voucherAd1.setSubjectArea("ebal");
		voucherAd1.setPreValue("123");
		voucherAd1.setPostValue("345");
		voucherAudits.add(voucherAd1);

		VoucherAudit voucherBsb1 = new VoucherAudit();
		voucherBsb1.setAttributeName("bsb");
		voucherBsb1.setSubjectArea("ebal");
		voucherBsb.setPreValue("2345");
		voucherBsb.setPostValue("6789");
		voucherAudits.add(voucherBsb1);


		VoucherAudit voucherTc1 = new VoucherAudit();
		voucherTc1.setAttributeName("tc");
		voucherTc1.setSubjectArea("ebal");
		voucherTc1.setPreValue("78");
		voucherTc1.setPostValue("98");
		voucherAudits.add(voucherTc1);

		VoucherAudit voucherAmt1 = new VoucherAudit();
		voucherAmt1.setAttributeName("amt");
		voucherAmt1.setSubjectArea("ebal");
		voucherAmt1.setPreValue("12");
		voucherAmt1.setPostValue("450");
		voucherAudits.add(voucherAmt1);

		VoucherAudit voucherAuditTimings1 = new VoucherAudit();
		voucherAuditTimings1.setAttributeName("timings");
		voucherAuditTimings1.setSubjectArea("ebal");
		voucherAuditTimings1.setPreValue("28082015 15:05:22");
		voucherAuditTimings1.setPostValue("28082015 15:05:22");
		voucherAudits.add(voucherAuditTimings1);


		VoucherAudit voucherSuspectFraud = new VoucherAudit();
		voucherSuspectFraud.setAttributeName("susp_fraud");
		voucherSuspectFraud.setSubjectArea("ebal");
		voucherSuspectFraud.setPreValue(Boolean.toString(true));
		voucherSuspectFraud.setPostValue(Boolean.toString(false));
		voucherAudits.add(voucherSuspectFraud);


		VoucherAudit voucherTPCRequired = new VoucherAudit();
		voucherTPCRequired.setAttributeName("tpc_check_required");
		voucherTPCRequired.setSubjectArea("ebal");
		voucherTPCRequired.setPreValue(Boolean.toString(false));
		voucherTPCRequired.setPostValue(Boolean.toString(true));
		voucherAudits.add(voucherTPCRequired);


		VoucherAudit voucherTPCMixedDeposit = new VoucherAudit();
		voucherTPCMixedDeposit.setAttributeName("tpc_mixed_deposit_return");
		voucherTPCMixedDeposit.setSubjectArea("ebal");
		voucherTPCMixedDeposit.setPreValue(Boolean.toString(true));
		voucherTPCMixedDeposit.setPostValue(Boolean.toString(false));
		voucherAudits.add(voucherTPCMixedDeposit);


		VoucherAudit voucherSurplusItem = new VoucherAudit();
		voucherSurplusItem.setAttributeName("surplus_item_flag");
		voucherSurplusItem.setSubjectArea("ebal");
		voucherSurplusItem.setPreValue(Boolean.toString(false));
		voucherSurplusItem.setPostValue(Boolean.toString(true));
		voucherAudits.add(voucherSurplusItem);


		VoucherAudit voucherPTQAamount = new VoucherAudit();
		voucherPTQAamount.setAttributeName("ptqa_amount");
		voucherPTQAamount.setSubjectArea("ebal");
		voucherPTQAamount.setPreValue(Boolean.toString(true));
		voucherPTQAamount.setPostValue(Boolean.toString(false));
		voucherAudits.add(voucherPTQAamount);


		VoucherAudit voucherPTQAcodeline = new VoucherAudit();
		voucherPTQAcodeline.setAttributeName("ptqa_codeline");
		voucherPTQAcodeline.setSubjectArea("ebal");
		voucherPTQAcodeline.setPreValue(Boolean.toString(false));
		voucherPTQAcodeline.setPostValue(Boolean.toString(true));
		voucherAudits.add(voucherPTQAcodeline);

		return voucherAudits;
	}
	
	public static Voucher buildVoucher(String accountNumber, String amount, String bsbNumber, 
			String documentReferenceNumber, String processingDate, String docType, String auxDom) throws ParseException {
		
		Voucher voucher = new Voucher();
		voucher.setAccountNumber(accountNumber);
		voucher.setAmount(amount);
		voucher.setAuxDom(auxDom);
		voucher.setBsbNumber(bsbNumber);
		voucher.setDocumentReferenceNumber(documentReferenceNumber);
		voucher.setExtraAuxDom("test");
		voucher.setProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(processingDate));
		voucher.setTransactionCode("12");
		
		if (docType != null && !docType.equals("")) {
			voucher.setDocumentType(DocumentTypeEnum.fromValue(docType));
		}
		
		return voucher;
	}
	
	private static VoucherProcess buildVoucherProcess(boolean unprocessable,
			String tranLinkNo, String listingPageNumber, String voucherDelayedIndicator, String adjustedBy,
			boolean adjustedFlag, int adjustedReasonCode,
			boolean adjustedOnHold, boolean adjustmentLetterRequired,
            boolean isGeneratedVoucher, boolean isHighValueFlag) {
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setManualRepair(0);
		voucherProcess.setUnprocessable(unprocessable);		
		if (tranLinkNo != null) {
			voucherProcess.setTransactionLinkNumber(tranLinkNo.trim());
		}
		voucherProcess.setListingPageNumber(listingPageNumber);
		voucherProcess.setMicrFlag(true);
		voucherProcess.setPresentationMode("1");
		voucherProcess.setRawMICR("test");
		voucherProcess.setSuspectFraud(false);
		voucherProcess.setInactiveFlag(false);
		voucherProcess.setVoucherDelayedIndicator(voucherDelayedIndicator);

		voucherProcess.setAdjustedBy(adjustedBy);
		voucherProcess.setAdjustedFlag(adjustedFlag);
		voucherProcess.setAdjustmentReasonCode(adjustedReasonCode);
		voucherProcess.setAdjustmentsOnHold(adjustedOnHold);
		voucherProcess.setAdjustmentLetterRequired(adjustmentLetterRequired);

        voucherProcess.setIsGeneratedVoucher(isGeneratedVoucher);
        voucherProcess.setHighValueFlag(isHighValueFlag);
		return voucherProcess;
	}
	
	private static VoucherBatch buildVoucherBatch() {
		VoucherBatch voucherBatch = new VoucherBatch();
		
		voucherBatch.setBatchAccountNumber("234567");
		voucherBatch.setBatchType("test");
		voucherBatch.setCaptureBsb("456544");
		voucherBatch.setClient("NAB");
		voucherBatch.setCollectingBank("333222");
		voucherBatch.setProcessingState(StateEnum.NSW);
		voucherBatch.setScannedBatchNumber("343434");
		voucherBatch.setSource("WestPC");
		voucherBatch.setSubBatchType("test");
		voucherBatch.setUnitID("5434565");
		voucherBatch.setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);
	    
	    return voucherBatch;
	}
	
	public static IDfSysObject buildFxaVoucher() throws DfException, ParseException {
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);	
		
		Mockito.when(fxaVoucher.getChronicleId()).thenReturn(new DfId(C.CHRONICLE_ID));
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_account_number"))).thenReturn(C.ACCCOUT_NUMBER);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_amount"))).thenReturn(C.AMOUNT);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_aux_dom"))).thenReturn(C.AUX_DOM);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_bsb"))).thenReturn(C.BSB);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_extra_aux_dom"))).thenReturn(C.EXTRA_AUX_DOM);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_drn"))).thenReturn(C.DRN);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_trancode"))).thenReturn(C.TRANSACTION_CODE);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_processing_state"))).thenReturn(C.PROCESSING_STATE.value());
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_collecting_bsb"))).thenReturn(C.COLLECTING_BSB);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_manual_repair"))).thenReturn(Integer.toString(C.MANUAL_REPAIR));
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_batch_number"))).thenReturn(C.BATCH_NUMBER);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_target_end_point"))).thenReturn(C.TARGET_END_POINT);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_unit_id"))).thenReturn(C.UNIT_ID);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_capture_bsb"))).thenReturn(C.CAPTURE_BSB);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_classification"))).thenReturn(C.DOCUMENT_TYPE);
		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_voucher_delayed_id"))).thenReturn(C.VOUCHER_DELAYED_INDOCATOR);
        Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_for_value_type"))).thenReturn(C.FOR_VALUE_TYPE);

		Mockito.when(fxaVoucher.getString(Matchers.eq("fxa_listing_page_number"))).thenReturn(C.LISTING_PAGE_NUMBER);

		IDfTime idfTime = new DfTime(new SimpleDateFormat("ddMMyyyy").parse(C.PROCESSING_DATE));
		Mockito.when(fxaVoucher.getTime(Matchers.eq("fxa_processing_date"))).thenReturn(idfTime);
		
		return fxaVoucher;
	}	

	public static IDfSysObject buildFxaVoucherTransfer() throws DfException, ParseException {
		IDfSysObject fxaVoucherTransfer = Mockito.mock(IDfSysObject.class);	
		
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("v_i_chronicle_id"))).thenReturn(C.CHRONICLE_ID);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("status"))).thenReturn(C.STATUS);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("filename"))).thenReturn(C.FILENAME);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("transmission_type"))).thenReturn(C.TRANSMISSION_TYPE);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("target_end_point"))).thenReturn(C.TARGET_END_POINT);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("transfer_type"))).thenReturn(C.TRANSFER_TYPE);
		Mockito.when(fxaVoucherTransfer.getString(Matchers.eq("transaction_id"))).thenReturn(C.TRANSACTION_ID);
		
		IDfTime idfTime = new DfTime(new SimpleDateFormat("ddMMyyyy kk:mm:ss").parse(C.TRANSMISSION_DATE));
		Mockito.when(fxaVoucherTransfer.getTime(Matchers.eq("transmission_date"))).thenReturn(idfTime);
		
		return fxaVoucherTransfer;
	}
	
	public static IDfSysObject buildFxaFileReceipt() throws DfException, ParseException {
		IDfSysObject fxaFileReceipt = Mockito.mock(IDfSysObject.class);	
		
		String id = UUID.randomUUID().toString();
		Mockito.when(fxaFileReceipt.getString(Matchers.eq("file_id"))).thenReturn(id);
		Mockito.when(fxaFileReceipt.getString(Matchers.eq("filename"))).thenReturn("test.txt");
		Mockito.when(fxaFileReceipt.getTime(Matchers.eq("received_datetime"))).thenReturn(new DfTime(new Date()));
		Mockito.when(fxaFileReceipt.getTime(Matchers.eq("transmission_datetime"))).thenReturn(new DfTime(new Date()));
		Mockito.when(fxaFileReceipt.getString(Matchers.eq("exchange"))).thenReturn("this is a test message");
		
		return fxaFileReceipt;
	}

/*	public InwardImageExchangeVoucher buildInwardImageExchangeVoucher() throws ParseException {
		
		InwardImageExchangeVoucher voucher = new InwardImageExchangeVoucher();
		voucher.setAccountNumber(C.ACCCOUT_NUMBER);
		voucher.setAmount(C.AMOUNT);
		voucher.setAuxDom(C.AUX_DOM);
		voucher.setBsb(C.BSB);
		voucher.setTransactionId(C.DRN);
		voucher.setExtraAuxDom(C.EXTRA_AUX_DOM);
		voucher.setTransmissionDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(C.PROCESSING_DATE));
		voucher.setTransactionCode(C.TRANSACTION_CODE);
		
		return voucher;
	}*/
	
	public static void compareVoucher(Voucher voucher) {
		assertEquals(C.ACCCOUT_NUMBER, voucher.getAccountNumber());
		assertEquals(C.AMOUNT, voucher.getAmount());
		assertEquals(C.AUX_DOM, voucher.getAuxDom());
		assertEquals(C.BSB, voucher.getBsbNumber());
		assertEquals(C.EXTRA_AUX_DOM, voucher.getExtraAuxDom());
		assertEquals(C.DRN, voucher.getDocumentReferenceNumber());
		assertEquals(C.TRANSACTION_CODE, voucher.getTransactionCode());

		String processingDate = new SimpleDateFormat("ddMMyyyy").format(voucher.getProcessingDate());
		assertEquals(C.PROCESSING_DATE, processingDate);
	}
	
/*	public static void compareImageExchangeVoucher(ImageExchangeVoucher ieVoucher) {
		RepositoryServiceTestHelper.compareVoucher(ieVoucher.getVoucher());			

		VoucherBatch voucherBatch = ieVoucher.getVoucherBatch();
		
		assertEquals(C.COLLECTING_BSB, voucherBatch.getCollectingBank());
		assertEquals(C.CAPTURE_BSB, voucherBatch.getCaptureBsb());
		assertEquals(C.PROCESSING_STATE, StateEnum.valueOf(voucherBatch.getProcessingState().value()));
		assertEquals(C.BATCH_NUMBER, voucherBatch.getScannedBatchNumber());
		assertEquals(C.UNIT_ID, voucherBatch.getUnitID());

		VoucherProcess voucherProcess = ieVoucher.getVoucherProcess();
		
		assertEquals(C.MANUAL_REPAIR, voucherProcess.getManualRepair());
		// assertEquals(C.TARGET_END_POINT, voucherProcess.getTargetEndPoint());
		assertEquals(C.VOUCHER_DELAYED_INDOCATOR, voucherProcess.getVoucherDelayedIndicator());
	}*/
	
	public static void compareVoucherInformation(VoucherInformation voucherInfo) {
		RepositoryServiceTestHelper.compareVoucher(voucherInfo.getVoucher());			

		VoucherBatch voucherBatch = voucherInfo.getVoucherBatch();
		
		assertEquals(C.COLLECTING_BSB, voucherBatch.getCollectingBank());
		assertEquals(C.CAPTURE_BSB, voucherBatch.getCaptureBsb());
		assertEquals(C.PROCESSING_STATE, StateEnum.valueOf(voucherBatch.getProcessingState().value()));
		assertEquals(C.BATCH_NUMBER, voucherBatch.getScannedBatchNumber());
		assertEquals(C.UNIT_ID, voucherBatch.getUnitID());

		VoucherProcess voucherProcess = voucherInfo.getVoucherProcess();
		
		assertEquals(C.MANUAL_REPAIR, voucherProcess.getManualRepair());
		//assertEquals(C.TARGET_END_POINT, voucherProcess.getTargetEndPoint());
		assertEquals(C.VOUCHER_DELAYED_INDOCATOR, voucherProcess.getVoucherDelayedIndicator());
		assertEquals(C.LISTING_PAGE_NUMBER, voucherProcess.getListingPageNumber());
	}
	
	public static GetVouchersRequest buildGetVouchersRequest(ImageType imageType, String jobIdentifier, int minSize, int maxSize,
			String targetEndPoint, DocumentExchangeEnum vouchertransition) {
		
		GetVouchersRequest request = new GetVouchersRequest();
		request.setImageType(imageType);
		request.setJobIdentifier(jobIdentifier);
		request.setMinReturnSize(minSize);
		request.setMaxReturnSize(maxSize);
		request.setTargetEndPoint(targetEndPoint);
		request.setVoucherTransfer(vouchertransition);
		
		return request;
	}

	public static StoreListingRequest buildStoreListingRequest() throws ParseException {
		StoreListingRequest request = new StoreListingRequest();

		ScannedListingBatchHeader scannedListingBatchHeader = buildScannedListingBatchHeader();

		for (int i = 0; i < C.LT_DRNS.length; i++) {

			ScannedListing scannedListing = new ScannedListing();
			scannedListing.setDocumentReferenceNumber(C.LT_DRNS[i]);
			scannedListingBatchHeader.getListingPages().add(scannedListing);
		}

		request.setScannedListing(scannedListingBatchHeader);
		request.setJobIdentifier(C.JOB_IDENTIFIER_LISTING);

		return request;
	}

	public static ScannedListingBatchHeader buildScannedListingBatchHeader() throws ParseException {

		ScannedListingBatchHeader scannedListingBatchHeader = new ScannedListingBatchHeader();

		scannedListingBatchHeader.setOperator(C.LT_OPERATOR);
		scannedListingBatchHeader.setBatchType(C.LT_BATCH_TYPE);
		scannedListingBatchHeader.setWorkType(C.LT_WORK_TYPE);
		scannedListingBatchHeader.setBatchNumber(C.LT_BATCH_NUMBER);
		scannedListingBatchHeader.setDocumentReferenceNumber(C.LT_DRN);
		scannedListingBatchHeader.setExtraAuxDom(C.LT_EX_AUX_DOM);
		scannedListingBatchHeader.setAuxDom(C.LT_AUX_DOM);
		scannedListingBatchHeader.setUnitId(C.LT_UNIT_ID);
		scannedListingBatchHeader.setCollectingBsb(C.LT_COLLECTING_BSB);
		scannedListingBatchHeader.setCaptureBsb(C.LT_CAPTURE_BSB);
		scannedListingBatchHeader.setTransactionCode(C.LT_TRANSACTION_CODE);
		scannedListingBatchHeader.setListingProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(C.LT_PROCESSING_DATE));

		return scannedListingBatchHeader;
	}

	public static IDfSysObject buildFxaListing() throws DfException, ParseException {

		IDfSysObject fxaListing = Mockito.mock(IDfSysObject.class);

		Mockito.when(fxaListing.getString(Matchers.eq("fxa_batch_number"))).thenReturn(C.LT_BATCH_NUMBER);
		Mockito.when(fxaListing.getString(Matchers.eq("work_type"))).thenReturn(C.LT_WORK_TYPE.value());
		Mockito.when(fxaListing.getString(Matchers.eq("batch_type_name"))).thenReturn(C.LT_BATCH_TYPE);
		Mockito.when(fxaListing.getString(Matchers.eq("operator_name"))).thenReturn(C.LT_OPERATOR);
		Mockito.when(fxaListing.getString(Matchers.eq("workstation_no"))).thenReturn(C.LT_UNIT_ID);
		Mockito.when(fxaListing.getString(Matchers.eq("capture_bsb"))).thenReturn(C.LT_CAPTURE_BSB);
		Mockito.when(fxaListing.getString(Matchers.eq("collecting_bsb"))).thenReturn(C.LT_COLLECTING_BSB);
		Mockito.when(fxaListing.getString(Matchers.eq("tc"))).thenReturn(C.LT_TRANSACTION_CODE);
		Mockito.when(fxaListing.getString(Matchers.eq("ad"))).thenReturn(C.LT_AUX_DOM);
		Mockito.when(fxaListing.getString(Matchers.eq("ead"))).thenReturn(C.LT_EX_AUX_DOM);
		Mockito.when(fxaListing.getString(Matchers.eq("drn"))).thenReturn(C.LT_DRN);

		IDfTime idfTime = new DfTime(new SimpleDateFormat("ddMMyyyy").parse(C.LT_PROCESSING_DATE));
		Mockito.when(fxaListing.getTime(Matchers.eq("processing_date"))).thenReturn(idfTime);

		return fxaListing;
	}

	public static IDfSysObject buildFxaReport() throws DfException, ParseException {

		IDfSysObject fxaReport = Mockito.mock(IDfSysObject.class);

		Mockito.when(fxaReport.getString(Matchers.eq("object_name"))).thenReturn(C.REPORT_OUTPUT_FILE_NAME);
		Mockito.when(fxaReport.getString(Matchers.eq("fxa_report_type"))).thenReturn(C.REPORT_TYPE);

		IDfTime idfTime = new DfTime(new SimpleDateFormat(Constant.REPORT_DATE_FORMAT).parse(C.REPORT_PROCESSING_DATE));
		Mockito.when(fxaReport.getTime(Matchers.eq("fxa_processing_date"))).thenReturn(idfTime);

		return fxaReport;
	}
	
	public static IDfSysObject buildFxaReportForAdjustmentLetter() throws DfException, ParseException {

		IDfSysObject fxaReport = Mockito.mock(IDfSysObject.class);

		Mockito.when(fxaReport.getString(Matchers.eq("object_name"))).thenReturn("test.txt");
		Mockito.when(fxaReport.getString(Matchers.eq("fxa_report_type"))).thenReturn(C.ADJUSTMENT_LETTER_REPORT_TYPE);
		IDfTime idfTime = new DfTime(new SimpleDateFormat(Constant.REPORT_DATE_FORMAT).parse(C.REPORT_PROCESSING_DATE));
		Mockito.when(fxaReport.getTime(Matchers.eq("fxa_processing_date"))).thenReturn(idfTime);

		return fxaReport;
	}

	public static StoreRepositoryReportsRequest buildStoreRepositoryReportsRequest(String reportOutputFileName,
																				   String reportProcessingDate,
																				   FormatType reportFormatType,
																				   String reportType) throws ParseException{

		StoreRepositoryReportsRequest storeRepositoryReportsRequest = new StoreRepositoryReportsRequest();
		storeRepositoryReportsRequest.setFormatType(reportFormatType);
		storeRepositoryReportsRequest.setReportOutputFilename(reportOutputFileName);
		storeRepositoryReportsRequest.setReportProcessingDate(new SimpleDateFormat(Constant.REPORT_DATE_FORMAT).parse(reportProcessingDate));
		storeRepositoryReportsRequest.setReportType(reportType);
		return storeRepositoryReportsRequest;

	}

	public static TriggerWorkflowRequest buildTriggerWorkFlowRequest(String[] workflowNames, String businessDay) throws ParseException {

		TriggerWorkflowRequest triggerWorkflowRequest = new TriggerWorkflowRequest();
		for(int i=0; i < workflowNames.length ; i++){
			triggerWorkflowRequest.getWorkflowNames().add(workflowNames[i]);
		}
		triggerWorkflowRequest.setBusinessDay(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(businessDay));

		return triggerWorkflowRequest;
	}
	
	public static VoucherInformation buildVoucherInformationForAdjustment(String drn, String processingDateString) throws ParseException {
		
		Voucher voucher = new Voucher();
		voucher.setDocumentReferenceNumber(drn);
		voucher.setProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(processingDateString));
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setAdjustmentsOnHold(false);					
			
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);
			
		return voucherInfo;
	}	
	
	public static StoreAdjustmentLettersRequest buildStoreAdjustmentLettersRequest(String drn, String fileName, 
			String jobIdentifier, Date processingDate) {		
		StoreAdjustmentLettersRequest request = new StoreAdjustmentLettersRequest();
		request.setDocumentReferenceNumber(drn);
		request.setFilename(fileName);
		request.setJobIdentifier(jobIdentifier);
		request.setProcessingDate(processingDate);
		request.setScannedBatchNumber("BATCH_1111");
		request.setTransactionLinkNumber("TRANLINK_1111");
		
		return request;		
	}
}
	

