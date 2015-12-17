package com.fxa.nab.dctm.utils;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.fxa.nab.dctm.migration.CommonUtils;
import com.documentum.fc.tools.RegistryPasswordUtils;

public class SessionEncryptTest {

	private IDfSessionManager sMgr;
	private IDfSession session;	
	
    private IDfSession createSession() throws Exception{
//    	CommonUtils.writeToLog("Inside 'createSession' method",debug,null);

    	try{
		IDfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();
	
		sMgr = client.newSessionManager();
		IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
	
		loginInfoObj.setUser("NABDCPECM_SVC_E");
		String password=RegistryPasswordUtils.decrypt("AAAAELVaPFoeyoxCUhkXbyjyDe4hQY7ILjefk9YgIi8Wz03A");
		loginInfoObj.setPassword(password);
		loginInfoObj.setDomain("");
	
		sMgr.setIdentity("NAB",loginInfoObj);
		session=sMgr.getSession("NAB");
		System.out.println("Session Created");
//		CommonUtils.writeToLog("leaving 'createSession' method",debug,null);
    	}catch(DfException e){
//			CommonUtils.writeToLog("Method createSession(): Exception is "+e.getMessage(), error, e);
			e.printStackTrace();
			System.out.println("Session NOT Created");
			throw e;
		}
    	
		
		return  session;// returns a IDfSession Object
   }

    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			System.out.println("Begin");
		SessionEncryptTest test=new SessionEncryptTest();
		test.createSession();
		System.out.println("The End");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	

}
