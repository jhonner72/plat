package com.fujixerox.aus.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;

public class StoreBatchVoucherFactory {

	// we save the following 6 batches to Documentum for query and update purpose
		private static final String[] JOB_IDENTIFIERS = new String[]{"INPUT_JOB_FOR_IE_AND_VIF_01092015_123456", 
			"INPUT_JOB_FOR_IE_AND_VIF_01092015_234567", "INPUT_JOB_FOR_IE_AND_VIF_02092015_123456", 
			"INPUT_JOB_FOR_IE_AND_VIF_03092015_123456", "INPUT_JOB_FOR_IE_AND_VIF_03092015_234567", 
			"INPUT_JOB_FOR_IE_AND_VIF_03092015_345678"};
		private static final String[] BATCH_NUMBERS = new String[]{"121212", "232323", "343434", "454545", "565656", "676767"};
		private static final String[] PROCESSIENG_DATES = new String[]{"01092015", "01092015", "02092015", "03092015", "03092015", "03092015"};
		private static final String[] TRAN_LINK_NOS = new String[]{"123456", "234567", "123456", "123456", "234567", "345678"};
		private static final int[] RECORD_COUNT = new int[]{5, 10, 15, 1, 13, 7};
		
		// the following are the individual voucher meta data
		private static final String[] ACCOUNT_NUMBERS = new String[]{"21212121", "32323232", "43434343", "54545454", "65656565",
			"21267121", "32387632", "43234543", "54987754", "6565443465",
			"21267541", "3277732", "43234333", "54232354", "6563333465"};
		private static final String[] AMOUNTS = new String[]{"234.56", "345.67", "24.56", "6345.67", "234.66",
			"1234.56", "2345.67", "3324.56", "46345.67", "76234.66",
			"884.56", "995.67", "4534.56", "6665.67", "73334.66"};
		private static final String[] BSBS = new String[]{"063844", "054434", "063769", "043234", "065423",
			"063444", "057834", "06666", "049898", "066753","063878", "055656", "068765", "042222", "063333"};	
		private static final String[] DRNS = new String[]{"00000000", "11111111", "22222222", "33333333", "44444444", 
			"55555555",	"66666666", "77777777", "88888888", "99999999", 
			"12121212", "23232323", "34343434", "45454545", "56565656"};
		private static final String[] DOCUMENT_TYPES = new String[]{"Dr", "Cr", "Dr", "Cr", "Dr", 
			"Cr", "Dr", "Cr", "Dr", "Cr", "Dr", "Cr", "Dr", "Cr", "Dr"};
		private static final String[] AUX_DOMS = new String[]{"Test0", "Test1", "Test2", "Test3", "Test4", 
			"Test5", "Test6", "Test7", "Test8", "Test9", "Test10", "Test11", "Test12", "Test13", "Test14"};
		private static final String[] TARGET_END_POINTS = new String[]{"XYZ", "XYZ", "XYZ", "XYZ", "XYZ", 
			"XYZ", "XYZ", "XYZ", "XYZ", "XYZ", "XYZ", "XYZ", "XYZ", "XYZ", "XYZ"};
		private static final boolean[] UNPROCESSABLES = new boolean[]{false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false, false};
		public static final String[] FOR_VALUE_TYPES = new String[]{"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_Non_For_Value", 
			"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", 
			"Inward_For_Value"};
		private static final String[] LISTING_PAGE_NUMBERS = new String[]{"10", "11", "12", "13", "14", "15", "16", "17",
				"18", "19", "20", "21", "22", "23", "24"};
		
	public static List<StoreBatchVoucherRequest> buildStoreBatchVoucherRequests() throws ParseException{
		List<StoreBatchVoucherRequest> storeBatchVoucherRequest = new ArrayList<StoreBatchVoucherRequest>();
		for (int i = 0; i < JOB_IDENTIFIERS.length; i++) {
			StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(
					BATCH_NUMBERS[i], JOB_IDENTIFIERS[i], PROCESSIENG_DATES[i], TRAN_LINK_NOS[i], UNPROCESSABLES[i], RECORD_COUNT[i]);

			storeBatchVoucherRequest.add(request);
		}
		return storeBatchVoucherRequest;
	}
	
	public static StoreBatchVoucherRequest buildStoreBatchVoucherRequest(String batchNumber, String jobIdentifier, 
			String processingDate, String tranLinkNo, boolean unprocessable, int recordCount) throws ParseException {
		
		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		VoucherBatch voucherBatch = new VoucherBatch();
		request.setVoucherBatch(voucherBatch);
		voucherBatch.setScannedBatchNumber(batchNumber);
		voucherBatch.setProcessingState(StateEnum.VIC);
		request.setJobIdentifier(jobIdentifier);
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (int i = 0; i < recordCount; i++) {
			storeVoucherRequests.add(buildStoreVoucherRequest(ACCOUNT_NUMBERS[i], 
				AMOUNTS[i], BSBS[i], DRNS[i], processingDate, unprocessable, DOCUMENT_TYPES[i], AUX_DOMS[i],
				TARGET_END_POINTS[i], tranLinkNo, LISTING_PAGE_NUMBERS[i]));
		}

		return request;
	}

	public static StoreBatchVoucherRequest buildStoreBatchVoucherRequest(String batchNumber,
																		 String jobIdentifier, String processingDate, String[] drn, 
																		 boolean unprocessable, int voucherCount) throws ParseException {
		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();

		VoucherBatch voucherBatch = new VoucherBatch();
		request.setVoucherBatch(voucherBatch);
		
		//new fields/missing
//		voucherBatch.setBatchAccountNumber("11112222");
//		voucherBatch.setBatchType("batchType");
//		voucherBatch.setCaptureBsb("captureBSB");
		
		voucherBatch.setScannedBatchNumber(batchNumber);
		voucherBatch.setProcessingState(StateEnum.VIC);
		request.setJobIdentifier(jobIdentifier);

		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (int i = 0; i < C.ACCOUNT_NUMBERS.length && i < voucherCount; i++) {
			System.out.printf("DRN no is - %s" , drn[i]);
			
			storeVoucherRequests.add(buildStoreVoucherRequest(C.ACCOUNT_NUMBERS[i], C.AMOUNTS[i], C.BSBS[i], drn[i],
					processingDate, unprocessable, C.DOCUMENT_TYPES[i], C.AUX_DOMS[i], C.TARGET_END_POINTS[i],
					C.TRAN_LINK_NOS[i], C.LISTING_PAGE_NUMBERS[i]));
		}

		return request;
	}

	public static StoreVoucherRequest buildStoreVoucherRequest(String accountNumber, String amount, String bsbNumber,
		   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
		   String targetEndPoint, String tranLinkNo, String listingPageNumber) throws ParseException {

		Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
				docType, auxDom);

		StoreVoucherRequest storeVoucherRequest = new StoreVoucherRequest();
		VoucherProcess voucherProcess = new VoucherProcess();
		storeVoucherRequest.setVoucherProcess(voucherProcess);
		voucherProcess.setManualRepair(0);
		voucherProcess.setUnprocessable(unprocessable);
		voucherProcess.setTransactionLinkNumber(tranLinkNo);
		voucherProcess.setListingPageNumber(listingPageNumber);
		storeVoucherRequest.setVoucher(voucher);

		storeVoucherRequest.getTransferEndpoints().addAll(buildEndpoint(targetEndPoint));
		
		return storeVoucherRequest;
	}
	
	public static VoucherInformation buildVoucherInformation(String accountNumber, String amount, String bsbNumber,
		   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
		   String targetEndPoint, String tranLinkNo, String listingPageNumber) throws ParseException {

		Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
				docType, auxDom);
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setManualRepair(0);
		voucherProcess.setUnprocessable(unprocessable);
		voucherProcess.setTransactionLinkNumber(tranLinkNo);
		voucherProcess.setListingPageNumber(listingPageNumber);

		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);	
		return voucherInfo;
	}
	
	public static StoreVoucher buildStoreVoucher(String accountNumber, String amount, String bsbNumber,
			   String documentReferenceNumber, String processingDate, boolean unprocessable, String docType, String auxDom,
			   String targetEndPoint, String tranLinkNo, String listingPageNUmber) throws ParseException {

			Voucher voucher = buildVoucher(accountNumber, amount, bsbNumber, documentReferenceNumber, processingDate,
					docType, auxDom);
			
			VoucherProcess voucherProcess = new VoucherProcess();
			voucherProcess.setManualRepair(0);
			voucherProcess.setUnprocessable(unprocessable);
			voucherProcess.setTransactionLinkNumber(tranLinkNo);
		    voucherProcess.setListingPageNumber(listingPageNUmber);
			
			VoucherInformation voucherInfo = new VoucherInformation();
			voucherInfo.setVoucher(voucher);
			voucherInfo.setVoucherProcess(voucherProcess);	
				
			StoreVoucher storeVoucher = new StoreVoucher();
			storeVoucher.setVoucherInformation(voucherInfo);
			storeVoucher.getTransferEndpoints().addAll(buildEndpoint(targetEndPoint));
			
			return storeVoucher;			
		}
	
	public static List<TransferEndpoint> buildEndpoint(String targetEndPoint) {
		
		List<TransferEndpoint> endpoints = new ArrayList<TransferEndpoint>();
		
		TransferEndpoint transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);

		endpoints.add(transferEndpoint);

		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);

		endpoints.add(transferEndpoint);
		
		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.INWARD_FOR_VALUE);

		endpoints.add(transferEndpoint);
		
		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint(targetEndPoint);
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.INWARD_NON_FOR_VALUE);

		endpoints.add(transferEndpoint);
		
		return endpoints;
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
}
