package com.fujixerox.aus.repository.api;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
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
	private static final String OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1 = "OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1";
	private static final String OUTPUT_JOB_FOR_FV = "OUTPUT_JOB_FOR_FV";
	private static final String OUTPUT_JOB_FOR_PENDING = "OUTPUT_JOB_FOR_PENDING";
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
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}

	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForMinAndNonImage() throws Exception {
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_IE_MIN_NON_VOUCHER, 2, -1, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForMaxAndTiffImage() throws Exception {
		queryDocumentum(ImageType.TIFF, OUTPUT_JOB_FOR_IE_MAX_TIFF_VOUCHER, 2, 4, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForIEForAllAndJpgImage() throws Exception {
		queryDocumentum(ImageType.JPEG, OUTPUT_JOB_FOR_IE_ALL_JPG_VOUCHER, 0, -1, "XYZ",
				DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs2() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_2, 0, 2, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs8000() throws Exception {	
		queryDocumentum(ImageType.NONE, "OUTPUT_JOB_FOR_VIF_MAXSIZE_8000", 0, 8000, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs7() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_7, 0, 7, "NAB", DocumentExchangeEnum.VIF_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIs17() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_17, 0, 17, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForVIFWhenMaxSizeIsMinusOne() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_VIF_MAXSIZE_MINUS_1, 0, -1, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForFV() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_FV, 0, -1, "XYZ", DocumentExchangeEnum.INWARD_FOR_VALUE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryDocumentumForPending() throws Exception {	
		queryDocumentum(ImageType.NONE, OUTPUT_JOB_FOR_PENDING, 0, 0, "XYZ", DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}
		
    private void queryDocumentum(ImageType imageType, String jobIdentifier,
    		int minSize, int maxSize, String targetEndPoint, DocumentExchangeEnum voucherTransition) throws Exception {
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(imageType, jobIdentifier, 
				minSize, maxSize, targetEndPoint, voucherTransition);
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetVouchersResponse response = service.query(request);
		assertEquals(response.getTargetEndPoint(), targetEndPoint);
		LogUtil.log("GetVouchersResponse voucher count = " + response.getVoucherCount(), LogUtil.DEBUG, null);
	}

	
}
