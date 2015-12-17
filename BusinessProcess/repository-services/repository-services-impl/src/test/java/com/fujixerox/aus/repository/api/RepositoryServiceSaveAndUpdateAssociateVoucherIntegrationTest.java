package com.fujixerox.aus.repository.api;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersResponse;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.DocumentTypeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.TransferEndpoint;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertNotNull;

/** 
 * Henry Niu
 * 10/09/2015
 */
public class RepositoryServiceSaveAndUpdateAssociateVoucherIntegrationTest implements AbstractIntegrationTest {
	
	private static final String JOB_IDENTIFIER = "INPUT_JOB_FOR_ASSOCIATE";
	public static final String PROCESSING_DATE = "09092015";
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

	@Test 
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveAndUpdateAssociateVoucher() throws Exception {	
		AssociateVouchersRequest request = buildAssociateVouchersRequest();
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, request.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.saveAndUpdateAssociateVoucher(request);

		ctx.close();
	}
		
	private AssociateVouchersRequest buildAssociateVouchersRequest() throws ParseException {
		
		AssociateVouchersRequest request = new AssociateVouchersRequest();
		request.setJobIdentifier(JOB_IDENTIFIER);

		List<VoucherDetail> insertVoucherDetails = request.getInsertVouchers();
		
		VoucherDetail insertVoucherDetail = new VoucherDetail();

		VoucherInformation voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation("121212", "567", "047732",
				   "22222222", "10062015", false, DocumentTypeEnum.CR.value(), null,  null, null, null, null, false);		
		insertVoucherDetail.setVoucher(voucherInfo);
				
		insertVoucherDetail.getTransferEndpoints().add(
			buildTransferEndpoint(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND, "NAB", VoucherStatus.NEW));
		insertVoucherDetail.getTransferEndpoints().add(
				buildTransferEndpoint(DocumentExchangeEnum.INWARD_FOR_VALUE, "NAB", VoucherStatus.NEW));
		
		insertVoucherDetails.add(insertVoucherDetail);
		
		List<VoucherDetail> updateVoucherDetails = request.getUpdateVouchers();
		
		VoucherDetail updateVoucherDetail = new VoucherDetail();
		
		voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation("131313", "456", "041234",
				   "11111111", "10062015", false, DocumentTypeEnum.CR.value(), null,  null, null, null, null, false);		
		updateVoucherDetail.setVoucher(voucherInfo);
				
		updateVoucherDetail.getTransferEndpoints().add(
			buildTransferEndpoint(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND, "NAB", VoucherStatus.IN_PROGRESS));
		updateVoucherDetail.getTransferEndpoints().add(
				buildTransferEndpoint(DocumentExchangeEnum.INWARD_FOR_VALUE, "NAB", VoucherStatus.ON_HOLD));
		
		updateVoucherDetails.add(updateVoucherDetail);
		
		return request;
	}
	
	private TransferEndpoint buildTransferEndpoint(DocumentExchangeEnum documentExchangeEnum, String endPoint,
			VoucherStatus voucherStatus) {	
		TransferEndpoint transferEndpoint = new TransferEndpoint();
		transferEndpoint.setDocumentExchange(documentExchangeEnum);
		transferEndpoint.setEndpoint(endPoint);
		transferEndpoint.setTransmissionDate(new Date());
		transferEndpoint.setVoucherStatus(voucherStatus);
		
		return transferEndpoint;
	}

	/*
	This test is a payload driven integration test.

	Note: This test has a data dependency on the update. In the .json, the updated vouchers (11111111 & 22222222 in the
	current file) need to exist for test to pass. So treat this as an integration test and not a unit test!
	 */
	@Test
	@Category(AbstractIntegrationTest.class)
	public void shouldAssociateBulkCreditedItems() throws Exception {
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
//		Resource resource = new DefaultResourceLoader().getResource("classpath:/request/UpdateBulkVouchersInformationRequest.json");
		Resource resource = new DefaultResourceLoader().getResource("classpath:/request/StoreAssociatedBulkCredit.json");
		AssociateVouchersRequest request = mapper.readValue(resource.getFile(), AssociateVouchersRequest.class);

		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));

		AssociateVouchersResponse response = service.saveAndUpdateAssociateVoucher(request);
		assertNotNull(response);
	}

}
