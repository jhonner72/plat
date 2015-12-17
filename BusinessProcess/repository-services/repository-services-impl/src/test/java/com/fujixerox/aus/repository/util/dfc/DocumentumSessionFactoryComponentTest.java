package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Matchers;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.DocumentumSessionHelper;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumSessionFactoryComponentTest implements AbstractComponentTest {
	
	@Test(expected = Exception.class)
    @Category(AbstractComponentTest.class)
    public void shouldRaiseExceptionIfNoConnectionParam() throws DfException, ACLException {
		DocumentumSessionFactory factory = new DocumentumSessionFactory();
		factory.getSession();
	}
	
//	@Test
    @Category(AbstractComponentTest.class)
    public void shouldGetSession() throws DfException, ACLException {
		IDfSession session = mock(IDfSession.class);		
		IDfSessionManager sMgr = mock(IDfSessionManager.class);
		when(sMgr.getSession(Matchers.any(String.class))).thenReturn(session);
		
		DocumentumSessionFactory factory = DocumentumSessionHelper.getDocumentumSessionFactory();

		assertNotNull(factory.getSession());
	}
}
