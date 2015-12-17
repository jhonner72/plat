package com.fujixerox.aus.repository.api;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.Voucher;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceQueryIntegrationTest implements AbstractIntegrationTest {
	
	public static final String[] FOR_VALUE_TYPES = new String[]{"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_Non_For_Value", 
		"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", 
		"Inward_For_Value"};
	private static final String OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER = "OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER";
	private static final String OUTPUT_JOB_FOR_IE_MAX_TIFF_VOUCHER = "OUTPUT_JOB_FOR_IE_MAX_TIFF_VOUCHER";
	private static final String OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER = "OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_2 = "OUTPUT_JOB_FOR_VIF_MAXSIZE_2";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_7 = "OUTPUT_JOB_FOR_VIF_MAXSIZE_7";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_17 = "OUTPUT_JOB_FOR_VIF_MAXSIZE_17";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_17_CUSTOMER_LINK = "OUTPUT_JOB_FOR_VIF_MAXSIZE_17_CUSTOMER_LINK";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1 = "OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1";
	private static final String OUTPUT_JOB_FOR_FV = "OUTPUT_JOB_FOR_FV";
	private static final String OUTPUT_JOB_FOR_PENDING = "OUTPUT_JOB_FOR_PENDING";
	
	private static final String OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER_WILDCARD = "OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER_WILDCARD";
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_17_WILDCARD = "OUTPUT_JOB_FOR_VIF_MAXSIZE_17_WILDCARD";
	private static final String OUTPUT_JOB_FOR_FV_WILDCARD = "OUTPUT_JOB_FOR_FV_WILDCARD";

	//the following is used to test listing
	public static final String LISTING_PROCESSING_DATE = "07072015";
	public static final String BATCH_NUMBER = "68200027";
	public static final WorkTypeEnum WORK_TYPE = WorkTypeEnum.NABCHQ_LISTINGS;
	public static final String BATCH_TYPE = "OTC_Listings";
	public static final String OPERATOR= "a";
	public static final String UNIT_ID = "083";
	public static final String CAPTURE_BSB = "083029";
	public static final String COLLECTING_BSB = "083042";
	public static final String TRANSACTION_CODE = "22";
	public static final String AUX_DOM = "6590";
	public static final String EX_AUX_DOM = "6880";
	public static final String ACCOUNT_NUMBER = "0000000090";
	public static final String DRN = "A83000018";
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForNotEnoughIERecords() throws Exception {
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER, 200, -1, "XYZ", 
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, QueryLinkTypeEnum.NONE);
	}

	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForMinAndNonImage() throws Exception {
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER, 2, -1, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForMaxAndTiffImage() throws Exception {
		queryDocumentum(ImageType.TIFF, OUTPUT_JOB_FOR_IE_MAX_TIFF_VOUCHER, 2, 4, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForAllAndJpgImage() throws Exception {
		queryDocumentum(ImageType.JPEG, OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER, 0, -1, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForAllAndJpgImageWithWildcardSearch() throws Exception {
		queryDocumentum(ImageType.JPEG, OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER_WILDCARD, 0, -1, "AB",
				DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs2() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_2, 0, 2, "XYZ", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs8000() throws Exception {	
		queryDocumentum(ImageType.NONE, "OUTPUT_JOB_FOR_VIF_MAXSIZE_8000", 0, 8000, "XYZ", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs7() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_7, 0, 7, "NAB", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs17() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_17, 0, 17, "XYZ", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs17WithWildcardSearch() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_17_WILDCARD, 0, 17, "Y", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIsMinusOne() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1, 0, -1, "XYZ", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForFV() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_FV, 0, -1, "XYZ", 
				DocumentExchangeEnum.INWARD_FOR_VALUE, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForFVWildcard() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_FV_WILDCARD, 0, -1, "Z", 
				DocumentExchangeEnum.INWARD_FOR_VALUE, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForPending() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_PENDING, 0, 0, "XYZ", 
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND, QueryLinkTypeEnum.NONE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForCustomerLink() throws Exception {	
		//saveToDocumentumForQuery("00000000", "28092015", "CUSTLINK_001");
		//saveToDocumentumForQuery("11111111", "28092015", "CUSTLINK_001");
		//saveToDocumentumForQuery("22222222", "29092015", "CUSTLINK_002");
		//saveToDocumentumForQuery("33333333", "29092015", "CUSTLINK_002");
				
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_17_CUSTOMER_LINK, 0, 17, "Y", 
				DocumentExchangeEnum.VIF_OUTBOUND, QueryLinkTypeEnum.CUSTOMER_LINK_NUMBER);
	}
		
    private void queryDocumentum(ImageType imageType, String jobIdentifier, int minSize, int maxSize, 
		String targetEndPoint, DocumentExchangeEnum voucherTransition, QueryLinkTypeEnum queryLinkType) throws Exception {
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(imageType, jobIdentifier, 
				minSize, maxSize, targetEndPoint, voucherTransition);
		request.setQueryLinkType(queryLinkType);
		request.setVoucherStatusTo(VoucherStatus.IN_PROGRESS);
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetVouchersResponse response = service.query(request);
		assertEquals(response.getTargetEndPoint(), targetEndPoint);
		LogUtil.log("GetVouchersResponse voucher count = " + response.getVoucherCount(), LogUtil.DEBUG, null);
	}
    
    private void saveToDocumentumForQuery(String drn, String processingDateString, String customerLinkNumber) throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		
		StoreBatchVoucherRequest batchRequest = new StoreBatchVoucherRequest();
		batchRequest.setJobIdentifier("INPUT_JOB_FOR_SAVE_FOR_CUSTOMER_LINK");

		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setScannedBatchNumber("10062015");
		voucherBatch.setProcessingState(StateEnum.VIC);			
		batchRequest.setVoucherBatch(voucherBatch);
			
		batchRequest.setOrigin(DocumentExchangeEnum.VIF_OUTBOUND);
		StoreVoucherRequest storeVoucherRequest = new StoreVoucherRequest();
		
		Voucher voucher = new Voucher();
		voucher.setProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse(processingDateString));		
		voucher.setDocumentReferenceNumber(drn);
		voucher.setAmount("2000");
		voucher.setDocumentType(DocumentTypeEnum.CR);
		storeVoucherRequest.setVoucher(voucher);
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setCustomerLinkNumber(customerLinkNumber);
		storeVoucherRequest.setVoucherProcess(voucherProcess);
		
		batchRequest.getVouchers().add(storeVoucherRequest);
		
		TransferEndpoint transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint("XYZ");
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.VIF_OUTBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
		storeVoucherRequest.getTransferEndpoints().add(transferEndpoint);
		
		transferEndpoint = new TransferEndpoint();
		transferEndpoint.setEndpoint("XYZ");
		transferEndpoint.setDocumentExchange(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND);
		transferEndpoint.setVoucherStatus(VoucherStatus.NEW);
		storeVoucherRequest.getTransferEndpoints().add(transferEndpoint);
		
		ImageHelper.prepare(ctx, batchRequest.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(batchRequest);
		
		ctx.close();
	}

	
}
