package com.fujixerox.aus.repository.util.dfc;

import com.documentum.fc.client.IDfBatchManager;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.util.LogUtil;

public class DocumentumSessionHandler {
	
	private IDfBatchManager bMgr = null;
	private IDfSession session = null;
	private boolean batchStartsHere = false;
	private boolean txStartsHere = false;
	
	public DocumentumSessionHandler(DocumentumSessionFactory documentumSessionFactory) throws DfException {
		
		session = documentumSessionFactory.getSession();
		bMgr = session.getBatchManager();
		if (!bMgr.isBatchActive()) {
			bMgr.openBatch();
			batchStartsHere = true;
		}
		if (!session.isTransactionActive()) {
			session.beginTrans();
			txStartsHere = true; 
		}
	}
	
	public IDfSession getSession() {
		return session;
	}
	
	public void cleanup() throws DfException {
		
		if (txStartsHere) {
			try {
				session.commitTrans();
				txStartsHere = false;
			} catch (DfException ex) {
				LogUtil.log("Could not commit transaction.", LogUtil.ERROR, ex);
				throw ex;
			}				
		}
		
		if (batchStartsHere) {
			try {
				bMgr.closeBatch();
				batchStartsHere = false;
			} catch (DfException ex) {
				LogUtil.log("Could not close batch.", LogUtil.ERROR, ex);
				throw ex;
			}	 				
		}
		
		try {
			if (txStartsHere && session.isTransactionActive()) {
				session.abortTrans();
			}
		} catch (DfException ex) {
			LogUtil.log("Could not abort session.", LogUtil.ERROR, ex);
			throw ex;
		}
		
		try {
			if (batchStartsHere && bMgr.isBatchActive()) {
				bMgr.abortBatch();
			}
		    if (session != null) {
		        session.disconnect();
		    }
		} catch (DfException ex) {
			LogUtil.log("ERROR! Failed to close Batch or Session while saving Documentum.", LogUtil.ERROR, ex);
			throw ex;
		}
	}
}
