package com.fujixerox.aus.repository.api;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersstatus.UpdateVouchersStatusResponse;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceUpdateIntegrationTest implements AbstractIntegrationTest {
	
	public static final String[] FOR_VALUE_TYPES = new String[]{"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_Non_For_Value", 
		"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", 
		"Inward_For_Value"};
	private static final String INPUT_JOB_FOR_IE_UPDATE_VOUCHER = "INPUT_JOB_FOR_IE_UPDATE_VOUCHER";
	private static final String INPUT_JOB_FOR_VIF_UPDATE_VOUCHER = "INPUT_JOB_FOR_VIF_UPDATE_VOUCHER";
	
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
    public void shouldUpdateDocumentumForIE() throws Exception {	
		updateDocumentum(INPUT_JOB_FOR_IE_UPDATE_VOUCHER, DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldUpdateDocumentumForVIF() throws Exception {
		updateDocumentum(INPUT_JOB_FOR_VIF_UPDATE_VOUCHER, DocumentExchangeEnum.VIF_OUTBOUND);
	}	
	    
    private void updateDocumentum(String jobIdentifier, DocumentExchangeEnum voucherTransition) throws Exception {
		
    	ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, jobIdentifier);
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		UpdateVouchersStatusRequest request = new UpdateVouchersStatusRequest();
		request.setFilename("Test.txt");
		request.setTransitionDate(new Date());
		request.setJobIdentifier(jobIdentifier);
		request.setVoucherStatus(VoucherStatus.COMPLETED);
		request.setVoucherTransition(voucherTransition);
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		UpdateVouchersStatusResponse response = service.update(request);
		assertNotNull(response);
	}

}
