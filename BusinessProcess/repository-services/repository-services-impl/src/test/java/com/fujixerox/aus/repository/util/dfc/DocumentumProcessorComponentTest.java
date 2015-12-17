package com.fujixerox.aus.repository.util.dfc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumProcessorComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldUpdate() throws DfException, ACLException {
		
		IDfSysObject idfSysObject = mock(IDfSysObject.class);
		when(idfSysObject.isCheckedOut()).thenReturn(false);

		new ConcreteDocumentumProcessor().update(idfSysObject);
		
		Mockito.verify(idfSysObject).checkout();
		Mockito.verify(idfSysObject, times(3)).setString(ArgumentCaptor.forClass(String.class).capture(), 
				ArgumentCaptor.forClass(String.class).capture());
	}
	
	class ConcreteDocumentumProcessor extends DocumentumProcessor {

		@Override
		public void setUpdateCriteria(IDfSysObject idfSysObject) throws DfException {
			idfSysObject.setString("field1",  "value1");
			idfSysObject.setString("field2",  "value2");
			idfSysObject.setString("field3",  "value3");			
		}		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldBulkUpdateVoucherStatus() throws DfException, ACLException {
		
		DfQuery dfQuery = mock(DfQuery.class);
		
		List<VoucherIdHolder> voucherIdHolders = new ArrayList<VoucherIdHolder>();
		for (int i = 0; i < 53; i++) {
			VoucherIdHolder holder = new VoucherIdHolder("VOUCHER_" + i, "VOUCHER_TRANSFER_" + i);
			voucherIdHolders.add(holder);
		}

		DocumentumProcessor documentumProcessor = new DocumentumProcessor();
		documentumProcessor.setDfQuery(dfQuery);
		documentumProcessor.updateVoucherStatus(mock(IDfSession.class), voucherIdHolders, VoucherStatus.IN_PROGRESS);
		
		verify(dfQuery).setDQL(ArgumentCaptor.forClass(String.class).capture());
		Mockito.verify(dfQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
	}

}
