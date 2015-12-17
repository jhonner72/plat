package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.DocumentumQuery;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumQueryComponentTest implements AbstractComponentTest {
			
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldHaveValues() throws DfException, ACLException {				
		assertNotNull(DocumentumQuery.GET_OBJECT_BASE_WITHOUT_TARGET_END_POINTS);
		assertNotNull(DocumentumQuery.GET_OBJECT_BASE);		
		assertNotNull(DocumentumQuery.GET_OBJECT_COUNT);
		assertNotNull(DocumentumQuery.GET_ALL_OBJECT_ID_QUERY_WITHOUT_TARGET_END_POINTS);
		assertNotNull(DocumentumQuery.GET_ALL_OBJECT_ID_QUERY);
		assertNotNull(DocumentumQuery.GET_OBJECT_ID_QUERY);
		assertNotNull(DocumentumQuery.GET_VIF_OBJECT_COUNT_QUERY);
		assertNotNull(DocumentumQuery.GET_VIF_OBJECT_QUERY);
		assertNotNull(DocumentumQuery.UPDATE_VOUCHER_STATUS_QUAL);
		assertNotNull(DocumentumQuery.INSERT_FILE_RECEIPT);

	}
}