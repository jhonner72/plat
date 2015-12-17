package com.fujixerox.aus.repository.util.dfc.recordextractor;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;
import com.fujixerox.aus.repository.util.dfc.recordextactor.DefaultVoucherRecordExtractor;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultVoucherRecordExtractorComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractNullRecords() throws NonRetriableException, DfException {
		
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.NONE, "JOB_TEST", 
			5, 20, "XYZ", null);
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setDetailQuery(mock(DfQuery.class));
		IDfCollection IDfCollection = extractor.extractRecords(request, mock(IDfSession.class));
		assertNull(IDfCollection);
	}
	
	@Test
    @Category(AbstractComponentTest.class)
	public void shouldExtractRecords() throws NonRetriableException, DfException {
				
		GetVouchersRequest request = RepositoryServiceTestHelper.buildGetVouchersRequest(ImageType.TIFF, "JOB_TEST", 
				2, 20, "XYZ", null);
						
		IDfCollection detailCollection = mock(IDfCollection.class);
		
		DfQuery detailQuery = mock(DfQuery.class);
		when(detailQuery.execute(any(IDfSession.class), any(Integer.class))).thenReturn(detailCollection);		
		
		DefaultVoucherRecordExtractor extractor = new DefaultVoucherRecordExtractor();
		extractor.setDetailQuery(detailQuery);
		IDfCollection IDfCollection = extractor.extractRecords(request, mock(IDfSession.class));
		
		assertNotNull(IDfCollection);
		
		Mockito.verify(detailQuery).execute(ArgumentCaptor.forClass(IDfSession.class).capture(), 
				ArgumentCaptor.forClass(Integer.class).capture());		
	}

}
