package com.fujixerox.aus.repository.api;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.repostvouchers.RepostVouchersRequest;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreVoucher;
import com.fujixerox.aus.repository.AbstractIntegrationTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.FileUtil;

/** 
 * Henry Niu
 * 19/05/2015
 */
public class RepositoryServiceRepostIntegrationTest implements AbstractIntegrationTest {
	
	private static final String[] TRAN_LINK_NOS = new String[]{"123456", "234567", "123456", "123456", "234567", "345678"};
	// the following are the individual voucher meta data
	private static final String[] ACCOUNT_NUMBERS = new String[]{"789789789", "32323232", "43434343", "54545454", "65656565",
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
	public static final String[] FOR_VALUE_TYPES = new String[]{"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_Non_For_Value", 
		"Inward_Non_For_Value", "Inward_Non_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", "Inward_For_Value", 
		"Inward_For_Value"};
	private static final String[] LISTING_PAGE_NUMBERS = new String[]{"10", "11", "12", "13", "14", "15", "16", "17",
			"18", "19", "20", "21", "22", "23", "24"};
	private static final String[] VOUCHER_DELAYED_INDICATORS = new String[]{"D", "N", "D", "D", "D", "D", "D", "D",
			"D", "D", "D", "D", "D", "D", "D"};

	private static final String INPUT_JOB_FOR_REPOST_VOUCHER = "INPUT_JOB_FOR_REPOST_VOUCHER";
	
	// the following is used for test repost
	private static final String PROCESSIENG_DATE = "01092015";	
	private static final int QUERY_SIZE = 3;
	
	@Test
    @Category(AbstractIntegrationTest.class)
    public void shouldRepostDocumentum() throws Exception { 
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, INPUT_JOB_FOR_REPOST_VOUCHER);

		RepostVouchersRequest request = new RepostVouchersRequest();
		
		List<StoreVoucher> storeVouchers = request.getInsertVouchers();	
		for (int i = 0; i < QUERY_SIZE; i++) {
			storeVouchers.add(buildStoreVoucher(i));			
		}
				
		request.setTransitionDate(new Date());
		request.setJobIdentifier(INPUT_JOB_FOR_REPOST_VOUCHER);
		request.setVoucherStatus(VoucherStatus.COMPLETED);
		request.setVoucherTransition(DocumentExchangeEnum.INWARD_FOR_VALUE);	
						
		RepositoryServiceImpl service = new RepositoryServiceImpl();
		service.setDocumentumSessionFactory(DocumentumSessionHelper.getDocumentumSessionFactory());
		service.setFileUtil(new FileUtil(C.LOCKER_PATH));
		service.repost(request);
	}
     
	private StoreVoucher buildStoreVoucher(int i) throws ParseException {		
		VoucherInformation voucherInfo = RepositoryServiceTestHelper.buildVoucherInformation(ACCOUNT_NUMBERS[i], 
				AMOUNTS[i], BSBS[i], DRNS[i], PROCESSIENG_DATE, false, DOCUMENT_TYPES[i], AUX_DOMS[i],
				TARGET_END_POINTS[i], TRAN_LINK_NOS[i], LISTING_PAGE_NUMBERS[i], VOUCHER_DELAYED_INDICATORS[i], true);
		
		StoreVoucher storeVoucher = new StoreVoucher();
		storeVoucher.setVoucherInformation(voucherInfo);
		storeVoucher.getTransferEndpoints().addAll(RepositoryServiceTestHelper.buildEndpoint(C.TARGET_END_POINT));
			
		return storeVoucher;
	}

}
