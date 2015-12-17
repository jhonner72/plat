package com.fujixerox.aus.repository.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

/** 
 * Zaka Lei
 * 31/07/2015
 */
public class RepositoryServiceSaveSimpleIntegrationTest implements AbstractIntegrationTest {
	
	// we save the following 1 batch to Documentum for query and update purpose
	private static final String JOB_IDENTIFIER = "INPUT_JOB_FOR_IE_AND_VIF_01092015_123456";
	private static final String BATCH_NUMBER = "01012016";
	private static final String PROCESSIENG_DATE = "04012016";
	private static final String TRAN_LINK_NO = "345678";
	
	// the following are the individual voucher meta data
	private static final String ACCOUNT_NUMBER = "000004";
	private static final String AMOUNT = "99.99";
	private static final String BSB = "063844";	
	private static final String DRN = "87654111";
	private static final String DOCUMENT_TYPE = "Sp";
	private static final String AUX_DOM = "Test0";
	private static final String TARGET_END_POINT = "XYZ";
	private static final boolean UNPROCESSABLE = false;
	public static final String FOR_VALUE_TYPE = "Inward_Non_For_Value";
	private static final String LISTING_PAGE_NUMBER = "10";
	private static final String VOUCHER_DELAYED_INDICATOR = "D";

	private static final String ADJUSTED_BY = "adj1";
	private static final boolean ADJUSTED_FLAG = true;
	private static final int ADJUSTED_REASON_CODE = 9;
	private static final boolean ADJUSTED_ON_HOLD = true;
	private static final boolean ADJUSTMENT_LETTER_REQUIRED = true;

	private static final boolean TPC_FAILED_FLAG = true;
	private static final boolean TPC_SUSPENSE_POOL_FLAG = true;
	private static final boolean UECD_RETURN_FLAG = true;
	private static final boolean MIXED_DEP_RETURN_FLAG = false;
	
	private static final boolean SURPLUS_ITEM_FLAG = false;

    private static final boolean IS_GENERATED_VOUCHER = true;
    private static final boolean HIGH_VALUE_FLAG = true;
    
    private static final boolean POST_TRANSMISSION_QA_AMOUNT_FLAG = true;
    private static final boolean POST_TRANSMISSION_QA_CODELINE_FLAG = true;

	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequest() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		request.setJobIdentifier(JOB_IDENTIFIER);

		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
		voucherBatch.setProcessingState(StateEnum.VIC);
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		
		StoreVoucherRequest storeVoucherRequest = RepositoryServiceTestHelper.buildStoreVoucherRequest(ACCOUNT_NUMBER, 
				AMOUNT, BSB, DRN, PROCESSIENG_DATE, UNPROCESSABLE, DOCUMENT_TYPE, AUX_DOM,
				TARGET_END_POINT, TRAN_LINK_NO, LISTING_PAGE_NUMBER, VOUCHER_DELAYED_INDICATOR, ADJUSTED_BY, ADJUSTED_FLAG,
				ADJUSTED_REASON_CODE, ADJUSTED_ON_HOLD, ADJUSTMENT_LETTER_REQUIRED, IS_GENERATED_VOUCHER, HIGH_VALUE_FLAG, TPC_FAILED_FLAG, TPC_SUSPENSE_POOL_FLAG, UECD_RETURN_FLAG, MIXED_DEP_RETURN_FLAG);
		
		VoucherProcess voucherProcess = storeVoucherRequest.getVoucherProcess();
		voucherProcess.setPostTransmissionQaAmountFlag(POST_TRANSMISSION_QA_AMOUNT_FLAG);
		voucherProcess.setPostTransmissionQaCodelineFlag(POST_TRANSMISSION_QA_CODELINE_FLAG);
		
		//Set TPC fields
		voucherProcess.setThirdPartyCheckFailed(TPC_FAILED_FLAG);
		voucherProcess.setThirdPartyPoolFlag(TPC_SUSPENSE_POOL_FLAG);
		voucherProcess.setUnencodedECDReturnFlag(UECD_RETURN_FLAG);
		voucherProcess.setThirdPartyMixedDepositReturnFlag(MIXED_DEP_RETURN_FLAG);
		
		//Set Surplus fields
		voucherProcess.setSurplusItemFlag(SURPLUS_ITEM_FLAG);
		
		storeVoucherRequests.add(storeVoucherRequest);
		
		ReceivedFile receiptFile = new ReceivedFile();
		receiptFile.setFileIdentifier(UUID.randomUUID().toString() + ".txt");
		receiptFile.setReceivedDateTime(new Date());
		receiptFile.setTransmissionDateTime(new Date());
		
		request.setVoucherBatch(voucherBatch);
		request.setReceipt(receiptFile);
		request.setOrigin(DocumentExchangeEnum.VOUCHER_INBOUND);

		ImageHelper.prepare(ctx, request.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}
	
}
