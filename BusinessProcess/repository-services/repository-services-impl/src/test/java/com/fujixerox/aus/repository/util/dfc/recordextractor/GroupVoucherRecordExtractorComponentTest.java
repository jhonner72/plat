package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.dfc.recordextactor.CustomerLinkVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.GroupVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.QueryGroupFieldHolder;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GroupVoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	static int countQuenryCount = 0;
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			0, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.CUSTOMER_LINK_NUMBER);
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(mock(IDfCollection.class));
		
		ConcreteVoucherRecordExtractor extractor = new ConcreteVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		assertTrue(voucherIdHolders.isEmpty());

	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
				0, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.CUSTOMER_LINK_NUMBER);

		IDfCollection countCollection = mock(IDfCollection.class);						
		when(countCollection.next()).thenReturn(true, false); 
		
		when(countCollection.getInt(any(String.class))).thenReturn(2);
		when(countCollection.getTime(any(String.class))).thenReturn(new DfTime());
		when(countCollection.getString(any(String.class))).thenReturn("TRAN_LINK_111");
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);		
		
		IDfCollection detailCollection = mock(IDfCollection.class);
		when(detailCollection.next()).thenReturn(true, false);
		when(detailCollection.getString(FxaVoucherField.FULL_OBJECT_ID)).thenReturn("333333333333");
		when(detailCollection.getString(FxaVoucherTransferField.FULL_OBJECT_ID)).thenReturn("444444444444");
				
		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);

		ConcreteVoucherRecordExtractor extractor = new ConcreteVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(voucherIdHolders);
		assertEquals(1, voucherIdHolders.size());
		
		VoucherIdHolder voucherIdHolder = voucherIdHolders.toArray(new VoucherIdHolder[]{})[0];
		assertEquals("333333333333", voucherIdHolder.getVoucherId());
		assertEquals("444444444444", voucherIdHolder.getVoucherTransferId());
		
		Mockito.verify(countQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}
	
	class ConcreteVoucherRecordExtractor extends GroupVoucherRecordExtractor {

		@Override
		public String buildCountQueryString(GetVouchersRequest request) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String buildQueryString(GetVouchersRequest request,
				QueryGroupFieldHolder holder) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public QueryGroupFieldHolder buildQueryGroupFieldHolder(GetVouchersRequest request, IDfCollection collection)
				throws DfException {
			String batchNumber = collection.getString(FxaVoucherField.BATCH_NUMBER);
			IDfTime processingDate = collection.getTime(FxaVoucherField.PROCESSING_DATE);
			String tranLinkNo = collection.getString(FxaVoucherField.TRAN_LINK_NO);
			
			return new QueryGroupFieldHolder(tranLinkNo, processingDate, batchNumber);
		}

		@Override
		public boolean skipGroup(IDfCollection collection) throws DfException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
