package com.fujixerox.aus.repository.api;

import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.getvouchersinformation.GetVouchersInformationResponse;
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
    public void shouldQueryVoucherInfoInDocumentumWithoutImage() throws Exception {
		queryVoucherInfoInDocumentum(ImageType.NONE, "OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_NO_IMAGE");
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithJpegImage() throws Exception {
		queryVoucherInfoInDocumentum(ImageType.JPEG, "OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_JPEG_IMAGE");
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldQueryVoucherInfoInDocumentumWithTiffImage() throws Exception {
		queryVoucherInfoInDocumentum(ImageType.TIFF, "OUTPUT_JOB_FOR_QUERY_VOUCHER_INFO_TIFF_IMAGE");
	}
	
	@Test
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
	}
	
	private void queryVoucherInfoInDocumentum(ImageType imageType, String jobIdentifier) throws Exception {		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
				
		Voucher voucher = new Voucher();
		voucher.setProcessingDate(new SimpleDateFormat(Constant.VOUCHER_DATE_FORMAT).parse("01092015"));
		
		VoucherProcess voucherProcess = new VoucherProcess();
		voucherProcess.setInactiveFlag(false);
				
		VoucherBatch voucherBatch = new VoucherBatch();		
		//voucherBatch.setBatchAccountNumber("232323");
		
		VoucherInformation voucherInfo = new VoucherInformation();
		voucherInfo.setVoucher(voucher);
		voucherInfo.setVoucherProcess(voucherProcess);
		voucherInfo.setVoucherBatch(voucherBatch);
		
		GetVouchersInformationRequest request = new GetVouchersInformationRequest();
		request.setImageRequired(imageType);
		request.setJobIdentifier(jobIdentifier);
		request.setVoucherInformation(voucherInfo);
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		GetVouchersInformationResponse response = service.queryVoucherInfo(request);
		response.getVoucherInformations();
	}
}
