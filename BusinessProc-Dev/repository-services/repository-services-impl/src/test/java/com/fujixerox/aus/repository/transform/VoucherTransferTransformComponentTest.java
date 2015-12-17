package com.fujixerox.aus.repository.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Matchers;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;

/** 
 * Henry Niu
 * 28/4/2015
 */
public class VoucherTransferTransformComponentTest implements AbstractComponentTest {
	
	private static final String STATUS = "New";
	private static final String FILENAME = "Test.xml";
	private static final String TRANSMISSION_DATE = "27042015 08:33:56";
	private static final String TRANSMISSION_TYPE = "VIF";
	private static final String TARGET_END_POINT = "NAB";
	private static final String TRANSFER_TYPE = "Inbound";
	private static final int TRANSACTION_ID = 11111111;

	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {		
		
		IDfSysObject fxaVoucherTransfer = buildFxaVoucherTransfer();
		IDfSession session = mock(IDfSession.class);
		when(session.newObject(Matchers.any(String.class))).thenReturn(fxaVoucherTransfer);		
				
		IDfSysObject fxaVoucher = mock(IDfSysObject.class);
		when(fxaVoucher.getChronicleId()).thenReturn(new DfId("4444444444"));
		when(fxaVoucher.getInt(Matchers.eq(FxaVoucherField.DRN))).thenReturn(TRANSACTION_ID);
		
		IDfSysObject actualFxaVoucherTransfer = new VoucherTransferTransform().transform(session, fxaVoucher, STATUS, 
				TRANSMISSION_TYPE, TARGET_END_POINT, TRANSFER_TYPE, null, null);		
		
		assertEquals(FILENAME, actualFxaVoucherTransfer.getString(FxaVoucherTransferField.FILENAME));
		assertEquals(STATUS, actualFxaVoucherTransfer.getString(FxaVoucherTransferField.STATUS));
		assertEquals(TARGET_END_POINT, actualFxaVoucherTransfer.getString(FxaVoucherTransferField.TARGET_END_POINT));
		assertEquals(TRANSFER_TYPE, actualFxaVoucherTransfer.getString(FxaVoucherTransferField.TRANSFER_TYPE));
		assertEquals(TRANSMISSION_TYPE, actualFxaVoucherTransfer.getString(FxaVoucherTransferField.TRANSMISSION_TYPE));
		assertEquals(new DfId("4444444444"), actualFxaVoucherTransfer.getId(FxaVoucherTransferField.V_I_CHRONICLE_ID));
    }
	
	private IDfSysObject buildFxaVoucherTransfer() throws DfException, ParseException {
		IDfSysObject fxaVoucherTransfer = mock(IDfSysObject.class);	
		
		when(fxaVoucherTransfer.getId(Matchers.eq("v_i_chronicle_id"))).thenReturn(new DfId("4444444444"));
		when(fxaVoucherTransfer.getString(Matchers.eq("status"))).thenReturn(STATUS);
		when(fxaVoucherTransfer.getString(Matchers.eq("filename"))).thenReturn(FILENAME);
		when(fxaVoucherTransfer.getString(Matchers.eq("transmission_type"))).thenReturn(TRANSMISSION_TYPE);
		when(fxaVoucherTransfer.getString(Matchers.eq("target_end_point"))).thenReturn(TARGET_END_POINT);
		when(fxaVoucherTransfer.getString(Matchers.eq("transfer_type"))).thenReturn(TRANSFER_TYPE);
		when(fxaVoucherTransfer.getInt(Matchers.eq("transaction_id"))).thenReturn(TRANSACTION_ID);
		
		IDfTime idfTime = new DfTime(new SimpleDateFormat("ddMMyyyy").parse(TRANSMISSION_DATE));
		when(fxaVoucherTransfer.getTime(Matchers.eq("transmission_date"))).thenReturn(idfTime);

				
		return fxaVoucherTransfer;
	}
}
