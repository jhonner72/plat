package com.fujixerox.aus.repository.util.dfc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumProcessorComponentTest implements AbstractComponentTest {
		
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldUpdate() throws DfException, ACLException {
		
		IDfSysObject idfSysObject = mock(IDfSysObject.class);
		when(idfSysObject.isCheckedOut()).thenReturn(false);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("field1",  "value1");
		params.put("field2",  "value2");
		params.put("field3",  "value3");

		new DocumentumProcessor().update(idfSysObject, params);
		
		Mockito.verify(idfSysObject).checkout();
		Mockito.verify(idfSysObject, times(3)).setString(ArgumentCaptor.forClass(String.class).capture(), 
				ArgumentCaptor.forClass(String.class).capture());
	}

}
