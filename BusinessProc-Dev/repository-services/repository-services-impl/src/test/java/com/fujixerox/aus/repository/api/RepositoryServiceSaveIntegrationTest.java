package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fujixerox.aus.lombard.repository.storebatchvoucher.VoucherAudit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.storelisting.StoreListingRequest;
import com.fujixerox.aus.lombard.reporting.metadata.FormatType;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreBatchRepositoryReportsRequest;
import com.fujixerox.aus.lombard.reporting.storerepositoryreports.StoreRepositoryReportsRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceSaveIntegrationTest implements AbstractIntegrationTest {
	
	// we save the following 6 batches to Documentum for query and update purpose
	private static final String[] JOB_IDENTIFIERS = new String[]{"INPUT_JOB_FOR_IE_AND_VIF_01092015_123456", 
		"INPUT_JOB_FOR_IE_AND_VIF_01092015_234567", "INPUT_JOB_FOR_IE_AND_VIF_02092015_123456", 
		"INPUT_JOB_FOR_IE_AND_VIF_03092015_123456", "INPUT_JOB_FOR_IE_AND_VIF_03092015_234567", 
		"INPUT_JOB_FOR_IE_AND_VIF_03092015_345678"};
	private static final String[] BATCH_NUMBERS = new String[]{"121212", "232323", "343434", "454545", "565656", "520520"};
	private static final String[] PROCESSIENG_DATES = new String[]{"01092015", "01092015", "02092015", "03092015", "03092015", "03092015"};
	private static final String[] TRAN_LINK_NOS = new String[]{"123456", "234567", "123456", "123456", "234567", "345678"};
	private static final int[] RECORD_COUNT = new int[]{5, 10, 15, 1, 13, 7};
	
	// the following are the individual voucher meta data
	private static final String[] ACCOUNT_NUMBERS = new String[]{"123123123", "32323232", "43434343", "54545454", "65656565",
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
	private static final String[] VOUCHER_DELAYED_INDICATOR = new String[]{"D", "N", "D", "D", "D", "D", "D", "D",
			"D", "D", "N", "N", "N", " ", " "};

	private static final String[] ADJUSTED_BY = new String[]{"adj1", "adj2", "adj3", "adj4", "adj5", "adj6", "adj7", "adj8",
			"adj9", "adj10", "adj11", "adj12", "adj13", "adj14", "adj15"};
	private static final boolean[] ADJUSTED_FLAG = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};
	private static final int[] ADJUSTED_REASON_CODE = new int[]{1, 2, 3, 2, 4, 3, 5, 7,
			2, 4, 5, 6, 7, 8, 9};
	private static final boolean[] ADJUSTED_ON_HOLD = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};
	private static final boolean[] ADJUSTMENT_LETTER_REQUIRED = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};

    private static final boolean[] IS_GENERATED_VOUCHER = new boolean[]{false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false};
    
    private static final boolean[] HIGH_VALUE_FLAGS = new boolean[]{false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false};

	private static final boolean[] TPC_FAILED_FLAG = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};
	private static final boolean[] TPC_SUSPENSE_POOL_FLAG = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};
	private static final boolean[] UECD_RETURN_FLAG = new boolean[]{false, false, true, false, false, false, false, false,
			false, false, false, false, false, false, false};
	private static final boolean[] MIXED_DEP_RETURN_FLAG = new boolean[]{false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};



	//the following is used to test listing
	private static final String JOB_IDENTIFIER_LISTING = "INPUT_JOB_FOR_LISTING";
	public static final String LISTING_PROCESSING_DATE = "09092015";
	public static final String BATCH_NUMBER = "68200027";
	public static final WorkTypeEnum WORK_TYPE = WorkTypeEnum.NABCHQ_LISTINGS;
	public static final String BATCH_TYPE = "OTC_Listings";
	public static final String OPERATOR= "a";
	public static final String UNIT_ID = "083";
	public static final String CAPTURE_BSB = "083029";
	public static final String COLLECTING_BSB = "081999";
	public static final String TRANSACTION_CODE = "22";
	public static final String AUX_DOM = "6590";
	public static final String EX_AUX_DOM = "6880";
	public static final String ACCOUNT_NUMBER = "0000000090";
	public static final String DRN = "A83000018";
	private static final String[] LISTING_DRNS = new String[]{"A83000019"};

	//the following is used to test Reports
	private static final String JOB_IDENTIFIER_REPORT = "INPUT_JOB_FOR_REPORTING";
	private static final String[] REPORT_OUTPUT_FILE_NAMES = new String[]{"PROD.NAB.RPT.AIR.20150619.2.zip", "PROD.BQL.RPT.AIR.20150619.3.dat",
			"PROD.NAB.RPT.AIR.20150618.4.csv", "PROD.NAB.RPT.AIR.20150617.5.txt", "PROD.NAB.RPT.AIR.20150616.5.doc", "PROD.NAB.RPT.AIR.20150623.4.xlsx"};
	private static final String[] REPORT_PROCESSING_DATES = new String[]{"20150619", "20150619", "20150618", "20150617", "20150616", "20150623"};
	private static final FormatType[] REPORT_FORMAT_TYPES = new FormatType[]{FormatType.ZIP, FormatType.DAT, FormatType.CSV, FormatType.TXT, FormatType.DOC, FormatType.XLSX};
	private static final String[] REPORT_TYPES = new String[]{"Dishonours", "Australia Post", "Image Exchange Inbound", "Image Exchange Outbound", "Adjustments", "Dishonours"};
	private static final int REPORT_COUNT = 6;

	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequest() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		for (int i = 0; i < JOB_IDENTIFIERS.length; i++) {
			StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(
					BATCH_NUMBERS[i], JOB_IDENTIFIERS[i], PROCESSIENG_DATES[i], TRAN_LINK_NOS[i], UNPROCESSABLES[i], RECORD_COUNT[i]);

			ImageHelper.prepare(ctx, request.getJobIdentifier());
			
			DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
			
			RepositoryServiceImpl service = new RepositoryServiceImpl();
			service.setFileUtil(new FileUtil(C.LOCKER_PATH));
			service.setDocumentumSessionFactory(documentumSessionFactory);
			service.save(request);
		}
		
		ctx.close();
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequestWithoutImage() throws Exception {	
		StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(
				"BATCH_20150824", "JOB_TEST", "24082015", "123456", false, 2);

		request.getVoucherBatch().setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);			
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingFile() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		request.setJobIdentifier("INPUT_JOB_FOR_IE_AND_VIF_USING_FILE");

		ImageHelper.prepare(ctx, request.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close(); 
	}

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldSaveToDocumentumUsingFileWithAdjustments() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");

		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		request.setJobIdentifier("INPUT_JOB_FOR_IE_AND_VIF_USING_FILE_ADJUSTMENTS");

		ImageHelper.prepare(ctx, request.getJobIdentifier());

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);

		ctx.close();
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequestParsedFromFile() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, "03062015-4CDD-422B-9AAF-SSSS67500131");
		
		File jsonFile = new File("target/03062015-4CDD-422B-9AAF-SSSS67500131/vouchers_485000012.JSON");
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());		 
		StoreBatchVoucherRequest request = mapper.readValue(jsonFile, StoreBatchVoucherRequest.class);		

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldSaveToDocumentumUsingRequestParsedFromFileWithAdjustments() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");
		ImageHelper.prepare(ctx, "10062015-D379-4073-BA7C-SSSS68500861");

		File jsonFile = new File("target/10062015-D379-4073-BA7C-SSSS68500861/vouchers_385000012.JSON");
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		StoreBatchVoucherRequest request = mapper.readValue(jsonFile, StoreBatchVoucherRequest.class);

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);

		ctx.close();
	}

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldSaveListingsToDocumentum() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");

		StoreListingRequest request = buildStoreListingRequest();
		ImageHelper.prepare(ctx, request.getJobIdentifier());

		FileUtil fileUtil = new FileUtil();
		fileUtil.setLockerPath(C.LOCKER_PATH);

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(fileUtil);
		service.saveListings(request);

		ctx.close();
	}

	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequestForInwardFVWithoutImage() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(C.BATCH_NUMBER, "INPUT_JOB_FOR_INWARD_IE_WITHOUT_IMAGE", 
				C.PROCESSING_DATE, C.TRAN_LINK_NO, false, 1);
		
		request.getVoucherBatch().setWorkType(WorkTypeEnum.NABCHQ_INWARDFV);
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequestForAdjustmentsOnHoldWithoutImage() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(C.BATCH_NUMBER, "INPUT_JOB_FOR_INWARD_IE_ADJUSTMENT_ONHOLD_WITHOUT_IMAGE", 
				C.PROCESSING_DATE, C.TRAN_LINK_NO, false, 1);
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (StoreVoucherRequest storeVoucherRequest : storeVoucherRequests) {
			storeVoucherRequest.getVoucherProcess().setAdjustmentsOnHold(true);
		}
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequestForHeader() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		
		StoreBatchVoucherRequest request = buildStoreBatchVoucherRequest(C.BATCH_NUMBER, "INPUT_JOB_FOR_IE_AND_VIF_HEADER", 
				"01082015", null, false, 1);
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (StoreVoucherRequest storeVoucherRequest : storeVoucherRequests) {
			storeVoucherRequest.getVoucher().setDocumentType(DocumentTypeEnum.BH);
		}
		
		ImageHelper.prepare(ctx, request.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}

	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldSaveReportsToDocumentum() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");

		StoreBatchRepositoryReportsRequest storeBatchRepositoryReportsRequest = buildStoreBatchRepositoryReportsRequest(JOB_IDENTIFIER_REPORT, REPORT_COUNT);

		ImageHelper.prepare(ctx, storeBatchRepositoryReportsRequest.getJobIdentifier());

		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.saveReports(storeBatchRepositoryReportsRequest);

		ctx.close();
	}

	private StoreBatchRepositoryReportsRequest buildStoreBatchRepositoryReportsRequest(String jobIdentifierReport, int reportCount) throws ParseException{
		StoreBatchRepositoryReportsRequest request = new StoreBatchRepositoryReportsRequest();
		request.setJobIdentifier(jobIdentifierReport);

		List<StoreRepositoryReportsRequest> storeRepositoryReportsRequest = request.getReports();
		for (int i = 0; i < reportCount; i++) {
			storeRepositoryReportsRequest.add(RepositoryServiceTestHelper.buildStoreRepositoryReportsRequest(REPORT_OUTPUT_FILE_NAMES[i],
					REPORT_PROCESSING_DATES[i], REPORT_FORMAT_TYPES[i], REPORT_TYPES[i]));
		}
		return request;
	}
		
	private StoreBatchVoucherRequest buildStoreBatchVoucherRequest(String batchNumber, String jobIdentifier, 
			String processingDate, String tranLinkNo, boolean unprocessable, int recordCount) throws ParseException {
		
		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		request.setJobIdentifier(jobIdentifier);

		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setScannedBatchNumber(batchNumber);
		voucherBatch.setProcessingState(StateEnum.VIC);
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		for (int i = 0; i < recordCount; i++) {
			storeVoucherRequests.add(RepositoryServiceTestHelper.buildStoreVoucherRequest(ACCOUNT_NUMBERS[i], 
				AMOUNTS[i], BSBS[i], DRNS[i], processingDate, unprocessable, DOCUMENT_TYPES[i], AUX_DOMS[i],
				TARGET_END_POINTS[i], tranLinkNo, LISTING_PAGE_NUMBERS[i], VOUCHER_DELAYED_INDICATOR[i], ADJUSTED_BY[i], ADJUSTED_FLAG[i],
				ADJUSTED_REASON_CODE[i], ADJUSTED_ON_HOLD[i], ADJUSTMENT_LETTER_REQUIRED[i], IS_GENERATED_VOUCHER[i], HIGH_VALUE_FLAGS[i], TPC_FAILED_FLAG[i], TPC_SUSPENSE_POOL_FLAG[i], UECD_RETURN_FLAG[i], MIXED_DEP_RETURN_FLAG[i]));
		}
		
		ReceivedFile receiptFile = new ReceivedFile();
		receiptFile.setFileIdentifier(UUID.randomUUID().toString() + ".txt");
		receiptFile.setReceivedDateTime(new Date());
		receiptFile.setTransmissionDateTime(new Date());
		
		request.setVoucherBatch(voucherBatch);
		request.setReceipt(receiptFile);
		request.setOrigin(DocumentExchangeEnum.VIF_OUTBOUND);
		
		return request;
	}
    
	private StoreListingRequest buildStoreListingRequest() throws ParseException {
		StoreListingRequest request = new StoreListingRequest();

		ScannedListingBatchHeader scannedListingBatchHeader = buildScannedListingBatchHeader();

		for (int i = 0; i < LISTING_DRNS.length; i++) {

			ScannedListing scannedListing = new ScannedListing();
			scannedListing.setDocumentReferenceNumber(LISTING_DRNS[i]);
			scannedListingBatchHeader.getListingPages().add(scannedListing);
		}

		request.setScannedListing(scannedListingBatchHeader);
		request.setJobIdentifier(JOB_IDENTIFIER_LISTING);

		return request;
	}

	private ScannedListingBatchHeader buildScannedListingBatchHeader() throws ParseException {

		ScannedListingBatchHeader scannedListingBatchHeader = new ScannedListingBatchHeader();

		scannedListingBatchHeader.setOperator(OPERATOR);
		scannedListingBatchHeader.setBatchType(BATCH_TYPE);
		scannedListingBatchHeader.setWorkType(WORK_TYPE);
		scannedListingBatchHeader.setBatchNumber(BATCH_NUMBER);
		scannedListingBatchHeader.setDocumentReferenceNumber(DRN);
		scannedListingBatchHeader.setExtraAuxDom(EX_AUX_DOM);
		scannedListingBatchHeader.setAuxDom(AUX_DOM);
		scannedListingBatchHeader.setUnitId(UNIT_ID);
		scannedListingBatchHeader.setCollectingBsb(COLLECTING_BSB);
		scannedListingBatchHeader.setCaptureBsb(CAPTURE_BSB);
		scannedListingBatchHeader.setTransactionCode(TRANSACTION_CODE);
		scannedListingBatchHeader.setAccountNumber(ACCOUNT_NUMBER);
		scannedListingBatchHeader.setListingProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(LISTING_PROCESSING_DATE));

		return scannedListingBatchHeader;
	}
	

}
