package com.fujixerox.aus.repository.api;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.*;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationRequest;
import com.fujixerox.aus.lombard.repository.updatevouchersinformation.UpdateVouchersInformationResponse;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertNotNull;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceUpdateVoucherInfoIntegrationTest implements AbstractIntegrationTest {
	
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
    public void shouldUpdateVoucherInfoInDocumentumForAdjustmentOnHold() throws Exception {	
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		UpdateVouchersInformationRequest request = new UpdateVouchersInformationRequest();
		//request.setVoucherTransferStatusFrom(VoucherStatus.ADJUSTMENT_ON_HOLD);
		request.setVoucherTransferStatusTo(VoucherStatus.NEW);
		request.getVoucherInformations().add(RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("385300213", "10062015"));
		request.getVoucherInformations().add(RepositoryServiceTestHelper.buildVoucherInformationForAdjustment("385300214", "10062015"));
				
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		UpdateVouchersInformationResponse response = service.updateVoucherInfo(request);
		assertNotNull(response);
	}
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldUpdateVoucherInfoInDocumentumForAdjustmentOnHoldUsingFile() throws Exception {	
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());	
		File inputFile = new File("target/test-classes/request/UpdateVouchersInformationRequest.json");
		UpdateVouchersInformationRequest request = mapper.readValue(inputFile, UpdateVouchersInformationRequest.class);
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		
		UpdateVouchersInformationResponse response = service.updateVoucherInfo(request);
		assertNotNull(response);
	}
 

}
