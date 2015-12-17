package com.fujixerox.aus.repository.transform;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.repository.associatevouchers.AssociateVouchersRequest;
import com.fujixerox.aus.lombard.repository.associatevouchers.VoucherDetail;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.C;
import com.fujixerox.aus.repository.ImageHelper;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaFileReceiptField;
import com.fujixerox.aus.repository.util.exception.FileException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** 
 * Henry Niu
 * 13/4/2015
 */
public class StoreVoucherRequestTransformComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {
		
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);				
		IDfSession session = Mockito.mock(IDfSession.class);
		Mockito.when(session.newObject(Matchers.any(String.class))).thenReturn(fxaVoucher);
		
		StoreBatchVoucherRequest batchRequest = RepositoryServiceTestHelper.buildStoreBatchVoucherRequest(C.BATCH_NUMBER,
			"JOB_INWARD_NON_FOR_VALUE", "24052105", false);
		VoucherInformation voucherInfo = RepositoryServiceTestHelper.
			buildVoucherInformation(C.ACCCOUT_NUMBER, C.AMOUNT, C.BSB, C.DRN, "24052105", false, "CRT", "Test1", 
			null, "1", C.LISTING_PAGE_NUMBER, C.VOUCHER_DELAYED_INDICATOR, true);
		 
		IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();
		
	    /*fxaVoucher = new StoreVoucherRequestTransform().transform(session, batchRequest.getVoucherBatch(), voucherInfo.getVoucher(), 
	    		voucherInfo.getVoucherProcess(), fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID));*/
	    
	    fxaVoucher = new StoreVoucherRequestTransform().transform(session, voucherInfo, fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID));
			
		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> acValue = ArgumentCaptor.forClass(String.class);
	
		Mockito.verify(fxaVoucher, Mockito.times(27)).setString(acField.capture(), acValue.capture());
		
		String[] fields = acField.getAllValues().toArray(new String[]{});
		String[] values = acValue.getAllValues().toArray(new String[]{});
				
		for (int i = 0; i < fields.length; i++) {
			switch (fields[i]) {
				case "fxa_account_number":
					assertEquals(C.ACCCOUT_NUMBER, values[i]);
					break;
				case "fxa_amount":
					assertEquals(C.AMOUNT, values[i]);
					break;
				case "fxa_bsb":
					assertEquals(C.BSB, values[i]);
					break;
				case "fxa_drn":
					assertEquals(C.DRN, values[i]);
					break;
				default:
					break;				
			}
		}			
    }
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldSaveAndUpdateAssociateVouchersRequestParsedFromFile() throws Exception {
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/repository-services-integration-test.xml");		
		ImageHelper.prepare(ctx, "UNIT_TEST_FOR_TRANSFORM");

		File jsonFile = new File("target/UNIT_TEST_FOR_TRANSFORM/AssociateVouchersRequest.JSON");
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());		 
		AssociateVouchersRequest request = mapper.readValue(jsonFile, AssociateVouchersRequest.class);
		
		List<VoucherDetail> insertVouchers = request.getInsertVouchers();
		for (VoucherDetail voucherDetail : insertVouchers) {
			VoucherInformation voucherInfo = voucherDetail.getVoucher();
			testStoreVoucherRequestTransform(voucherInfo);
		}
		
		List<VoucherDetail> updateVouchers = request.getUpdateVouchers();
		for (VoucherDetail voucherDetail : updateVouchers) {
			VoucherInformation voucherInfo = voucherDetail.getVoucher();
			testStoreVoucherRequestTransform(voucherInfo);
		}
    }
	
	private void testStoreVoucherRequestTransform(VoucherInformation voucherInfo) throws DfException, ParseException {
		IDfSysObject fxaVoucher = Mockito.mock(IDfSysObject.class);				
		IDfSession session = Mockito.mock(IDfSession.class);
		Mockito.when(session.newObject(Matchers.any(String.class))).thenReturn(fxaVoucher);
				 
		//IDfSysObject fxaFileReceipt = RepositoryServiceTestHelper.buildFxaFileReceipt();	    
	    //fxaVoucher = new StoreVoucherRequestTransform().transform(session, voucherInfo, fxaFileReceipt.getString(FxaFileReceiptField.FILE_ID));
		
	    fxaVoucher = new StoreVoucherRequestTransform().transform(session, voucherInfo, "FILE_ID");
			
		ArgumentCaptor<String> acField = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> acValue = ArgumentCaptor.forClass(String.class);
	
		//Mockito.verify(fxaVoucher, Mockito.times(36)).setString(acField.capture(), acValue.capture());
		
		/*String[] fields = acField.getAllValues().toArray(new String[]{});
		String[] values = acValue.getAllValues().toArray(new String[]{});
				
		for (int i = 0; i < fields.length; i++) {
			switch (fields[i]) {
				case "fxa_account_number":
					assertEquals(C.ACCCOUT_NUMBER, values[i]);
					break;
				case "fxa_amount":
					assertEquals(C.AMOUNT, values[i]);
					break;
				case "fxa_bsb":
					assertEquals(C.BSB, values[i]);
					break;
				case "fxa_drn":
					assertEquals(C.DRN, values[i]);
					break;
				default:
					break;				
			}
		}	*/
	}
	
}
