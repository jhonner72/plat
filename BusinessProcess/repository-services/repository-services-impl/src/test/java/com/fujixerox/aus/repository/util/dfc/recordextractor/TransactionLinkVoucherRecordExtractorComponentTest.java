package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.recordextactor.TransactionLinkVoucherRecordExtractor;
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

public class TransactionLinkVoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	static int countQuenryCount = 0;
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			0, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);

		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(mock(IDfCollection.class));
		
		TransactionLinkVoucherRecordExtractor extractor = new TransactionLinkVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		assertTrue(voucherIdHolders.isEmpty());

	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
				0, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.TRANSACTION_LINK_NUMBER);

		IDfCollection countCollection = mock(IDfCollection.class);
		when(countCollection.next()).thenReturn(true, false);
		
		when(countCollection.getInt(any(String.class))).thenReturn(2);
		when(countCollection.getTime(any(String.class))).thenReturn(new DfTime());
		when(countCollection.getString(any(String.class))).thenReturn("TRAN_LINK_111");
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);		
		
		IDfCollection detailCollection = mock(IDfCollection.class);
		
		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);

		when(detailCollection.getString(any(String.class))).thenReturn("1234");
		
		TransactionLinkVoucherRecordExtractor extractor = new TransactionLinkVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(voucherIdHolders);
		
		Mockito.verify(countQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}
}
