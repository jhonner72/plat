package com.fujixerox.aus.repository.api;

import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.Criteria;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.ResponseType;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.Constant;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.SimpleDateFormat;
import java.util.List;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceQueryVoucherInfoIntegrationTest implements AbstractIntegrationTest {
	
	public static final String[] FOR_VALUE_TYPES = new String[]{"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_Non_For_Value", 
		"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", 
		"Inward_For_Value"};
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
    public void shouldQueryVoucherInfoWithoutImage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_1", "01/09/2015",
				ImageType.NONE, ResponseType.FILE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoWithJpegImage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_2",  "01/09/2015",
				ImageType.JPEG, ResponseType.FILE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoWithTiffImage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_3",  "01/09/2015",
				ImageType.TIFF, ResponseType.FILE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoWithoutImageWithImageInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_4",  "01/09/2015",
				ImageType.NONE, ResponseType.MESSAGE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithJpegImageWithImageInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_5",  "01/09/2015",
				ImageType.JPEG, ResponseType.MESSAGE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithTiffImageWithImageInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_6",  "01/09/2015",
				ImageType.TIFF, ResponseType.MESSAGE, ResponseType.FILE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoWithoutImageWithMetadataInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_7",  "01/09/2015",
				ImageType.NONE, ResponseType.FILE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithJpegImageWithMetadataInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_8",  "01/09/2015",
				ImageType.JPEG, ResponseType.FILE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithTiffImageWithMetadataInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_9",  "01/09/2015",
				ImageType.TIFF, ResponseType.FILE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoWithoutImageWithBothInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_10",  "01/09/2015",
				ImageType.NONE, ResponseType.MESSAGE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithJpegImageWithBothInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_11",  "01/09/2015",
				ImageType.JPEG, ResponseType.MESSAGE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithTiffImageWithBothInMessage() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_12",  "01/09/2015",
				ImageType.TIFF, ResponseType.MESSAGE, ResponseType.MESSAGE);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithUpdate() throws Exception {
		queryVoucherInfoInDocumentum("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_13",  "10/06/2015",
				ImageType.TIFF, ResponseType.MESSAGE, ResponseType.MESSAGE, true);
	}
	
	/*@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithComplicateQuery() throws Exception {
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		request.setImageRequired(ImageType.TIFF);
		request.setJobIdentifier("OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_TIFF_IMAGE");
		request.setVoucherInformation(RepositoryServiceTestHelper.buildVoucherInformation(
				C.ACCCOUT_NUMBER, C.AMOUNT, C.BSB, C.DRN, C.PROCESSING_DATE, 
				false, C.DOCUMENT_TYPE, C.AUX_DOM, C.TARGET_END_POINT, C.TRAN_LINK_NO, C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, false));
						
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetVouchersInformationResponse response = service.queryVoucherInfo(request);
		response.getVoucherInformations();
	}*/
	
	private void queryVoucherInfoInDocumentum(String jobIdentifier, String processingDate, ImageType imageType, ResponseType imageResponseType,
			ResponseType metadataResponseType) throws Exception {
		
		queryVoucherInfoInDocumentum(jobIdentifier, processingDate, imageType, imageResponseType, metadataResponseType, false);
	}
	
	private void queryVoucherInfoInDocumentum(String jobIdentifier, String processingDate, ImageType imageType, ResponseType imageResponseType,
			ResponseType metadataResponseType, boolean buildUpdateCriteria) throws Exception {
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
						
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		request.setJobIdentifier(jobIdentifier);
		request.setImageRequired(imageType);
		request.setImageResponseType(imageResponseType);
		request.setMetadataResponseType(metadataResponseType);
		
		List<Criteria> searchCriteria = request.getSearchCriterias();
		searchCriteria.add(RepositoryServiceTestHelper.buildCriteria("voucher.processingDate", processingDate));
		searchCriteria.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.inactiveFlag", "false"));
		
		if (buildUpdateCriteria) {
			List<Criteria> updateCriteria = request.getUpdateCriterias();
			updateCriteria.add(RepositoryServiceTestHelper.buildCriteria("voucher.amount", "3000"));
			updateCriteria.add(RepositoryServiceTestHelper.buildCriteria("voucherBatch.collectingBank", "888888"));
			updateCriteria.add(RepositoryServiceTestHelper.buildCriteria("voucherProcess.manualRepair", "true"));
		}
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetVouchersInformationResponse response = service.queryVoucherInfo(request);
		response.getVoucherInformations();
	}
}
