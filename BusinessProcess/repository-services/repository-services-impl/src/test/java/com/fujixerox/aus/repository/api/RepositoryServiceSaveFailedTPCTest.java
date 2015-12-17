package com.fujixerox.aus.repository.api;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfQuery;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherProcess;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucherRequest;
import com.fujixerox.aus.repository.*;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionHandler;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/** 
 * Zaka Lei
 * 31/07/2015
 */
public class RepositoryServiceSaveFailedTPCTest implements AbstractIntegrationTest {

	// we save the following 1 batch to Documentum for query and update purpose
	private static final String JOB_IDENTIFIER = "INPUT_JOB_FOR_IE_AND_VIF_01092015_123456";
	private static final String BATCH_NUMBER = "091120155";
	private static final String PROCESSIENG_DATE = "09112015";
	private static final String TRAN_LINK_NO = "3456";

	// the following are the individual voucher meta data
	private static final String ACCOUNT_NUMBER = "000002";
	private static final String AMOUNT = "99.99";
	private static final String BSB = "063844";
	private static final String DRN = "00000012";
	private static final String DOCUMENT_TYPE = "Dr";	//Set document type to "Bh" or "HDR" to skip saving to Voucher Transfer table
	private static final String AUX_DOM = "321";
	private static final String TARGET_END_POINT = "ABC";
	private static final boolean UNPROCESSABLE = false;
	public static final String FOR_VALUE_TYPE = "Inward_Non_For_Value";
	private static final String LISTING_PAGE_NUMBER = "10";
	private static final String VOUCHER_DELAYED_INDICATOR = "D";
	private static final String TRAN_CODE = "66";

	private static final String ADJUSTED_BY = "adj1";
	private static final boolean ADJUSTED_FLAG = true;
	private static final int ADJUSTED_REASON_CODE = 9;
	private static final boolean ADJUSTED_ON_HOLD = false;
	private static final boolean ADJUSTMENT_LETTER_REQUIRED = false;

	//Set one of the TPC flags to true to skip saving to Voucher Transfer table
	private static final boolean TPC_FAILED_FLAG = true;
	private static final boolean UECD_RETURN_FLAG = false;
	private static final boolean MIXED_DEP_RETURN_FLAG = false;

	
	private static final boolean TPC_SUSPENSE_POOL_FLAG = false;
	private static final boolean SURPLUS_ITEM_FLAG = false;

    private static final boolean IS_GENERATED_VOUCHER = true;
    private static final boolean HIGH_VALUE_FLAG = false;
    
    private static final boolean POST_TRANSMISSION_QA_AMOUNT_FLAG = false;
    private static final boolean POST_TRANSMISSION_QA_CODELINE_FLAG = false;
    
    private static final String ALT_EX_TRAN_CODE = "999";
    private static final boolean creditNoteFlag = true;
    
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldSaveToDocumentumUsingRequest() throws Exception {	
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		

		StoreBatchVoucherRequest request = new StoreBatchVoucherRequest();
		request.setJobIdentifier(JOB_IDENTIFIER);

		VoucherBatch voucherBatch = new VoucherBatch();
		voucherBatch.setScannedBatchNumber(BATCH_NUMBER);
		voucherBatch.setProcessingState(StateEnum.VIC);
		voucherBatch.setBatchType("B01");
		voucherBatch.setSubBatchType("SB01");
		
		List<StoreVoucherRequest> storeVoucherRequests = request.getVouchers();
		
		StoreVoucherRequest storeVoucherRequest = RepositoryServiceTestHelper.buildStoreVoucherRequest(ACCOUNT_NUMBER, 
				AMOUNT, BSB, DRN, PROCESSIENG_DATE, UNPROCESSABLE, DOCUMENT_TYPE, AUX_DOM,
				TARGET_END_POINT, TRAN_LINK_NO, LISTING_PAGE_NUMBER, VOUCHER_DELAYED_INDICATOR, ADJUSTED_BY, ADJUSTED_FLAG,
				ADJUSTED_REASON_CODE, ADJUSTED_ON_HOLD, ADJUSTMENT_LETTER_REQUIRED, IS_GENERATED_VOUCHER, HIGH_VALUE_FLAG, TPC_FAILED_FLAG, TPC_SUSPENSE_POOL_FLAG, UECD_RETURN_FLAG, MIXED_DEP_RETURN_FLAG);
		
		//Set TransactionCode(fxa_trancode(3)) again. the test above set it to 4 chars
		storeVoucherRequest.getVoucher().setTransactionCode(TRAN_CODE);
		
		//Set PTQA flags
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
		
		//Set Alternate fields for LockedBox
		voucherProcess.setAlternateAccountNumber(ACCOUNT_NUMBER);
		voucherProcess.setAlternateBsbNumber(BSB);
		voucherProcess.setAlternateAuxDom(AUX_DOM);
		voucherProcess.setAlternateExAuxDom(AUX_DOM);
		voucherProcess.setAlternateTransactionCode(ALT_EX_TRAN_CODE);
		
		//Set CreditNoteFlag field for LockedBox
		voucherProcess.setCreditNoteFlag(creditNoteFlag);
		
		
		storeVoucherRequests.add(storeVoucherRequest);
		
		ReceivedFile receiptFile = new ReceivedFile();
		receiptFile.setFileIdentifier(UUID.randomUUID().toString() + ".txt");
//		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		Date date = format.parse("2015/09/22 23:59:59");
		Date date = new Date();
		receiptFile.setReceivedDateTime(date);
		receiptFile.setTransmissionDateTime(date);
		
		request.setVoucherBatch(voucherBatch);
		request.setReceipt(receiptFile);
		request.setOrigin(DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND);
		// --------------- End Building StoreBatchVoucherRequest ------------

		ImageHelper.prepare(ctx, request.getJobIdentifier());
		
		DocumentumSessionFactory documentumSessionFactory = DocumentumSessionHelper.getDocumentumSessionFactory();
		
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.setDocumentumSessionFactory(documentumSessionFactory);
		service.save(request);
		
		ctx.close();
	}
}
