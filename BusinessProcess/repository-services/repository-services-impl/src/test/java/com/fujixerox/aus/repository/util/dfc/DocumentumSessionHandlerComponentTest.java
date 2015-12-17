package com.fujixerox.aus.repository.util.dfc;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.documentum.fc.client.IDfSessionManager;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.documentum.fc.client.IDfBatchManager;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.AbstractComponentTest;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;
import com.fujixerox.aus.repository.util.exception.ACLException;

public class DocumentumSessionHandlerComponentTest implements AbstractComponentTest {
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldGetSession() throws DfException, ACLException {
		
		IDfBatchManager bMgr = mock(IDfBatchManager.class);
		IDfSession session = mock(IDfSession.class);
		when(session.getBatchManager()).thenReturn(bMgr);
		
		DocumentumSessionFactory documentumSessionFactory = mock(DocumentumSessionFactory.class);
		when(documentumSessionFactory.getSession()).thenReturn(session);
		
		DocumentumSessionHandler documentumSessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
		assertNotNull(documentumSessionHandler.getSession());
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldCommit() throws DfException, ACLException {
		
		IDfBatchManager bMgr = mock(IDfBatchManager.class);		
		IDfSession session = mock(IDfSession.class);
		when(session.getBatchManager()).thenReturn(bMgr);

		IDfSessionManager iDfSessionManager = mock(IDfSessionManager.class);
		when(session.getSessionManager()).thenReturn(iDfSessionManager);

		DocumentumSessionFactory documentumSessionFactory = mock(DocumentumSessionFactory.class);
		when(documentumSessionFactory.getSession()).thenReturn(session);
		
		DocumentumSessionHandler documentumSessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
		documentumSessionHandler.commit();
		
		verify(session).commitTrans();
		//verify(session).isTransactionActive();
	}
	
	@Test
    @Category(AbstractComponentTest.class)
    public void shouldRollback() throws DfException, ACLException {
		
		IDfBatchManager bMgr = mock(IDfBatchManager.class);		
		IDfSession session = mock(IDfSession.class);
		when(session.getBatchManager()).thenReturn(bMgr);
		when(session.isTransactionActive()).thenReturn(false, true);

		IDfSessionManager iDfSessionManager = mock(IDfSessionManager.class);
		when(session.getSessionManager()).thenReturn(iDfSessionManager);

		DocumentumSessionFactory documentumSessionFactory = mock(DocumentumSessionFactory.class);
		when(documentumSessionFactory.getSession()).thenReturn(session);
		
		DocumentumSessionHandler documentumSessionHandler = new DocumentumSessionHandler(documentumSessionFactory);
		documentumSessionHandler.rollback();
		
		//verify(session).abortTrans();
		//verify(session, times(2)).isTransactionActive();
		verify(iDfSessionManager).release(session);
	}
}
