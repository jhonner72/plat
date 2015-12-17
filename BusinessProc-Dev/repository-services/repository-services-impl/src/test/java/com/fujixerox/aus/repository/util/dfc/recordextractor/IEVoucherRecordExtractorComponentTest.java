package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.recordextactor.IEVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IEVoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	private static int countQuenryCount = 0;
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			5, 20, "XYZ", DocumentExchangeEnum.VIF_OUTBOUND);
		
		DfQuery countQuery = mock(DfQuery.class);
		when(countQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(mock(IDfCollection.class));
		
		IEVoucherRecordExtractor extractor = new IEVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		IDfCollection IDfCollection = extractor.extractRecords(request, mock(IDfSession.class));
		assertNull(IDfCollection);
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.TIFF, "JOB_TEST", 
				2, 20, "XYZ", DocumentExchangeEnum.IMAGE_EXCHANGE_OUTBOUND);
		
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
		
		IEVoucherRecordExtractor extractor = new IEVoucherRecordExtractor();
		extractor.setCountQuery(countQuery);
		extractor.setDetailQuery(detailQuery);
		IDfCollection IDfCollection = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(IDfCollection);
		
		Mockito.verify(countQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}

}
