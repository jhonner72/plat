package com.fujixerox.aus.repository.util.dfc.recordextractor;

import java.util.List;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherField;
import com.fujixerox.aus.repository.util.dfc.FxaVoucherTransferField;
import com.fujixerox.aus.repository.util.dfc.recordextactor.DefaultVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.dfc.recordextactor.VoucherIdHolder;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultVoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	private static int countQuenryCount = 0;
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			5, 20, "XYZ", null);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		IDfCollection countCollection = mock(IDfCollection.class);
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);		
				
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(mock(DfQuery.class));
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		assertEquals(0, voucherIdHolders.size());
		assertFalse(extractor.vouchersRemaining());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.TIFF, "JOB_TEST", 
				2, 20, "XYZ", null);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		IDfCollection countCollection = mock(IDfCollection.class);
		when(countCollection.next()).thenReturn(true, false);
		when(countCollection.getInt(any(String.class))).thenReturn(5);
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);
						
		IDfCollection detailCollection = mock(IDfCollection.class);	
		when(detailCollection.next()).thenReturn(true, false);
		when(detailCollection.getString(FxaVoucherField.FULL_OBJECT_ID)).thenReturn("111111111111");
		when(detailCollection.getString(FxaVoucherTransferField.FULL_OBJECT_ID)).thenReturn("222222222222");

		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);		
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(voucherIdHolders);
		assertEquals(1, voucherIdHolders.size());
		
		VoucherIdHolder voucherIdHolder = voucherIdHolders.toArray(new VoucherIdHolder[]{})[0];
		assertEquals("111111111111", voucherIdHolder.getVoucherId());
		assertEquals("222222222222", voucherIdHolder.getVoucherTransferId());
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
		
		assertTrue(extractor.vouchersRemaining());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullIERecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			5, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(mock(IDfCollection.class));
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		assertEquals(0, voucherIdHolders.size());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractIERecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.TIFF, "JOB_TEST", 
				2, 20, "XYZ", DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		IDfCollection countCollection = mock(IDfCollection.class);
						
		when(countCollection.next()).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				if (countQuenryCount == 0) {
					countQuenryCount++;
					return Boolean.TRUE;
				} else {
					return Boolean.FALSE;
				}
			}
		});
		
		when(countCollection.getInt(any(String.class))).thenReturn(2);
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);		
		
		IDfCollection detailCollection = mock(IDfCollection.class);
		
		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);		
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(voucherIdHolders);
		
		Mockito.verify(countQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullFVRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			5, 20, "XYZ", DocumentExchangeEnum.INWARD_FOR_VALUE);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		IDfCollection countCollection = mock(IDfCollection.class);
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(mock(DfQuery.class));
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		assertEquals(0, voucherIdHolders.size());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractFVRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.TIFF, "JOB_TEST", 
				2, 20, "XYZ", DocumentExchangeEnum.INWARD_FOR_VALUE);
		request.setQueryLinkType(QueryLinkTypeEnum.NONE);

		IDfCollection detailCollection = mock(IDfCollection.class);
		
		IDfCollection countCollection = mock(IDfCollection.class);
		when(countCollection.next()).thenReturn(true, false);
		when(countCollection.getInt(any(String.class))).thenReturn(5);
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(countCollection);
		
		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);		
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		List<VoucherIdHolder> voucherIdHolders = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(voucherIdHolders);
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}

}
