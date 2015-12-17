package com.fujixerox.aus.repository.transform;

import java.text.ParseException;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfTime;
import com.fujixerox.aus.lombard.outclearings.storeadjustmentletters.StoreAdjustmentLettersRequest;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.RepositoryServiceTestHelper;

/** 
 * Henry Niu
 * 22/07/2015
 */
public class StoreBatchAdjustmentLettersRequestTransformComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldTransform() throws DfException, ParseException {		
		StoreAdjustmentLettersRequest request = RepositoryServiceTestHelper.buildStoreAdjustmentLettersRequest("11111111",
				"test.txt", "JOB_1111111", new Date());
		
		IDfSysObject fxaAdjustmentLetter = mock(IDfSysObject.class);
		IDfSession session = mock(IDfSession.class);
		when(session.newObject(any(String.class))).thenReturn(fxaAdjustmentLetter);
		
		new StoreAdjustmentLettersRequestTransform().transform(session, request);
		
		verify(fxaAdjustmentLetter, times(4)).setString(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(String.class).capture());
		verify(fxaAdjustmentLetter).setTime(ArgumentCaptor.forClass(String.class).capture(),
				ArgumentCaptor.forClass(IDfTime.class).capture());
		
    }
}
