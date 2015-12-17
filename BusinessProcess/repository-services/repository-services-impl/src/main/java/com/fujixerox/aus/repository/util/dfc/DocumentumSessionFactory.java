package com.fujixerox.aus.repository.util.dfc;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.fujixerox.aus.repository.util.LogUtil;
import com.fujixerox.aus.repository.util.RepositoryProperties;

/** 
 * Henry Niu
 * 25/03/2015
 */ 
public class DocumentumSessionFactory {

	private String repositoryUsername;
	private String repositoryPassword;
	private String repositoryHost;
	private String repositoryPort;

	public IDfSession getSession() throws DfException {

		LogUtil.log("Inside 'createSession' method", LogUtil.DEBUG, null);

		IDfSession session = createSession();
		   
		if (session == null) {
		   LogUtil.log("Connection to database couldn't be created. Check the details in Properties file and the network connectivity.",
					   LogUtil.ERROR, null);
			throw new DfException("ERROR!! Documentum Session object is null.");
	   }

		LogUtil.log("leaving 'createSession' method", LogUtil.DEBUG, null);
		return session;
	 }
	
	/**
    * 
    * This method is used to create a session for the repository.
    * 
    * @return IDfSession
    * @throws DfException
    */
    private IDfSession createSession() throws DfException  {
    	IDfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();

		IDfSessionManager sMgr = client.newSessionManager();
		
		IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
		loginInfoObj.setUser(repositoryUsername);
		loginInfoObj.setPassword(repositoryPassword);
		loginInfoObj.setDomain(null);

		sMgr.setIdentity(RepositoryProperties.repository_name, loginInfoObj);
		return sMgr.newSession(RepositoryProperties.repository_name);
   }

	public void setRepositoryUsername(String repositoryUsername) {
		this.repositoryUsername = repositoryUsername;
	}

	public void setRepositoryPassword(String repositoryPassword) {
		this.repositoryPassword = repositoryPassword;
	}

	public void setRepositoryHost(String repositoryHost) {
		this.repositoryHost = repositoryHost;
	}

	public void setRepositoryPort(String repositoryPort) {
		this.repositoryPort = repositoryPort;
	}
}
