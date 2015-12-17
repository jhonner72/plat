package com.fujixerox.aus.repository.util.dfc;

import java.util.Properties;

import com.documentum.fc.client.IDfBatchManager;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.util.LogUtil;

public class DocumentumSessionHandler {

	private IDfSession session = null;

	public DocumentumSessionHandler(DocumentumSessionFactory documentumSessionFactory) throws DfException {

		session = documentumSessionFactory.getSession();
		session.beginTrans();
	}
	
	public IDfSession getSession() {
		return session;
	}
		
	public void commit() throws DfException {
				
		try {
			session.commitTrans();
			LogUtil.log("Session Transaction committed.", LogUtil.INFO, null);

		} catch (DfException ex) {
			LogUtil.log("Could not commit transaction.", LogUtil.ERROR, ex);
			throw ex;
		} finally {
		    if (session != null) {
				session.getSessionManager().release(session);
		    }
		}
	}
	
	public void rollback() throws DfException {
		try {
			if ( session.isTransactionActive()) {
				session.abortTrans();
				LogUtil.log("Session Transaction aborted!!!.", LogUtil.INFO, null);
			}

		} catch (DfException ex) {
			LogUtil.log("Could not abort session.", LogUtil.ERROR, ex);
			throw ex;
		} finally {
		    if (session != null) {
				session.getSessionManager().release(session);
		    }
		}
	}
}
