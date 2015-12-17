package com.fxa.nab.d2.ptqa;

import java.util.ArrayList;
import java.util.HashMap;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfBatchManager;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;
import com.documentum.fc.tools.RegistryPasswordUtils;

public class DctmActivities {

	public static final int debug=CommonUtils.debug;
	public static final int info=CommonUtils.info;
	public static final int warn=CommonUtils.warn;
	public static final int error=CommonUtils.error;
	
	private static IDfACL folderACLObj=null;
	private static IDfACL voucherACLObj=null;
	
	private IDfSessionManager sMgr;
	private IDfSession session;
	
	private static DctmActivities dctm=new DctmActivities(); 
		
	private DctmActivities(){}
	
	public static DctmActivities getInstance(){
		return dctm;
	}

	   public IDfSession getSession() throws Exception{
		   if(session == null){
	
			   createSession(PTQAProperties.docbroker_host,PTQAProperties.docbroker_port,PTQAProperties.dctm_login_user, PTQAProperties.dctm_password_encrypted,null,PTQAProperties.repository_name);
	
			   if(session == null){
				    CommonUtils.writeToLog("Connection to Documentum couldn't be created. Check the details in Properties file and the network connectivity.", error, null);
					throw new FXAInitAndValidateException("ERROR!! Documentum Session object is null.");					
			   }else{
				   return session;
			   }
		   }else{
			   return session;
		   }
	   }	
	   
   
	   /**
	    * 
	    * This method is used to create a session for the repository.
	    * 
	    * @param  loginName as String,password as String,domain as String,docbaseName as String
	    * @return IDfSession
	    * @throws DfException
	    */
	    private IDfSession createSession(String docbrokerHost,String docbrokerPort,String loginName,String password,String domain,String docbaseName) throws Exception{
	    	CommonUtils.writeToLog("Inside 'createSession' method",debug,null);
	
	    	try{
			IDfClientX clientx = new DfClientX();
			IDfClient client = clientx.getLocalClient();
		
			sMgr = client.newSessionManager();
			IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
		
			loginInfoObj.setUser(loginName);
			String decryptPassword=RegistryPasswordUtils.decrypt(password);
			loginInfoObj.setPassword(decryptPassword);
			loginInfoObj.setDomain(domain);

            IDfTypedObject apiConfig = client.getClientConfig();			
	        if(docbrokerHost != null && docbrokerHost.trim().length() > 0 && docbrokerPort != null && docbrokerPort.trim().length() > 0){
                apiConfig.setString("primary_host", docbrokerHost);
                apiConfig.setString("primary_port", docbrokerPort);
                CommonUtils.writeToLog("Using docbroker_host ["+docbrokerHost+"] and docbroker_port ["+docbrokerPort+"] to create the connection.",debug,null);
           }else{
        	    CommonUtils.writeToLog("Using docbroker_host and docbroker_port from dfc.properties !!!",warn,null);
        	    CommonUtils.writeToLog("docbroker_host ["+apiConfig.getString("primary_host")+"] and docbroker_port ["+apiConfig.getString("primary_port")+"] to create the connection.",warn,null);
           }
			
			sMgr.setIdentity(docbaseName,loginInfoObj);
			session=sMgr.getSession(docbaseName);
			
			CommonUtils.writeToLog("leaving 'createSession' method",debug,null);
	    	}catch(DfException e){
				CommonUtils.writeToLog("Method createSession(): Exception is "+e.getMessage(), error, e);
				throw e;
			}
			return  session;// returns a IDfSession Object
	   }
	    
		public void releaseSession() {
			if(sMgr != null && session != null){
					sMgr.release(session);
					CommonUtils.writeToLog("Documentum Session released succesfully!",debug, null);
			}
		}	   
		
		
		public IDfCollection readQuery(String query) throws Exception {
			try{
				 IDfQuery q = new DfQuery();
				 q.setDQL(query);
				 CommonUtils.writeToLog("Method readQuery(): Query to be executed is [ "+query+" ] ", debug, null);
				 return q.execute(getSession(), DfQuery.DF_READ_QUERY);
			}catch(Exception e){
				CommonUtils.writeToLog("Method readQuery(String query): Exception is "+e.getMessage(), error, e);
				throw e;				
			}
		}
		
		public IDfCollection executeQuery(String query) throws Exception {
			try{
				 IDfQuery q = new DfQuery();
				 q.setDQL(query);
				 CommonUtils.writeToLog("Method executeQuery(): Query to be executed is [ "+query+" ] ", debug, null);
				 return q.execute(getSession(), DfQuery.DF_EXEC_QUERY);
			}catch(Exception e){
				CommonUtils.writeToLog("Method executeQuery(String query): Exception is "+e.getMessage(), error, e);
				throw e;				
			}			
		}
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DctmActivities dctm=new DctmActivities();
		try {
//			dctm.checkExistenseOfFolders("/Vouchers", "2012", "10", "01","1650");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
