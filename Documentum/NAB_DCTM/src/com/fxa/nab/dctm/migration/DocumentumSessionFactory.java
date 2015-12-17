package com.fxa.nab.dctm.migration;

import java.util.Enumeration;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.rsa.cryptoj.o.lv.e;

public class DocumentumSessionFactory {

	
		private String repositoryUsername;
		private String repositoryPassword;
		private String repositoryHost;
		private String repositoryPort;

		public IDfSession getSession() throws DfException {

//		CommonUtils.writeToLog("Inside 'createSession' method", CommonUtils.debug, null);

		IDfSession session = createSession();

		if (session == null) {
//		   CommonUtils.writeToLog("Connection to database couldn't be created. Check the details in Properties file and the network connectivity.",
//		   CommonUtils.error, null);
		   throw new DfException("ERROR!! Documentum Session object is null.");
		}

//		CommonUtils.writeToLog("leaving 'createSession' method", CommonUtils.debug, null); 
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
		IDfClientX clientx = new DfClientX(); IDfClient client = clientx.getLocalClient();

		IDfTypedObject cfg = client.getClientConfig(); 
//		cfg.setString("dfc.docbroker.host", "svme-9612dcp"); 
//		cfg.setInt("dfc.docbroker.port", 1489);
//		cfg.setString("dfc.globalregistry.repository", "NABDocbase"); 
//		cfg.setString("dfc.globalregistry.username", "dm_bof_registry");
//		cfg.setString("dfc.globalregistry.password", "dm_bof_registry"); 
		
		cfg.setString("primary_host", "svme-9612dcp"); 
		cfg.setInt("primary_port", 1489);

//		cfg.setString(arg0, arg1);
//		Enumeration<e> enum01=cfg.enumAttrs();
//		int i=0;
//		while(enum01.hasMoreElements()){
//			Object obj=enum01.nextElement();
//			System.out.println(obj);
//			System.out.println(cfg.getAttr(i++));
//			
//		}

		IDfSessionManager sMgr = client.newSessionManager();

		IDfLoginInfo loginInfoObj = clientx.getLoginInfo(); 
		loginInfoObj.setUser("fxa_voucher");
		loginInfoObj.setPassword("fxa_voucher");
		loginInfoObj.setDomain(null);

		sMgr.setIdentity("NAB", loginInfoObj); return sMgr.getSession("NAB");
	   }

		public void setRepositoryUsername(String repositoryUsername) { this.repositoryUsername = repositoryUsername; }

		public void setRepositoryPassword(String repositoryPassword) { this.repositoryPassword = repositoryPassword; }

		public void setRepositoryHost(String repositoryHost) { this.repositoryHost = repositoryHost; }

		public void setRepositoryPort(String repositoryPort) { this.repositoryPort = repositoryPort; } 
		
		
	    private void createFolder(String parentFolder,String folderToBeCreated) throws Exception{
	    	System.out.println("inside createfolder...");
			try{
//				CommonUtils.writeToLog("Inside createFolder() method...", CommonUtils.debug,null);
					IDfFolder folder = (IDfFolder) getSession().newObject("dm_folder");
					folder.setObjectName(folderToBeCreated);
					
//					if(getFolderACL() != null){
//					 folder.setACL(getFolderACL());				
//					}
					  
					folder.link(parentFolder);				
					folder.save();
//					CommonUtils.writeToLog(folderToBeCreated + " created under " + parentFolder,CommonUtils.debug,null);
			}catch(DfException e){
//				CommonUtils.writeToLog("Exception from 'createFolder(...)' method. Exception message is : \n "+e.getMessage(), CommonUtils.error,null);
				e.printStackTrace();
				
			}
			
		}		
		

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			try{
				System.out.println("Now Starting...");
			// TODO Auto-generated method stub
			DocumentumSessionFactory dctm=new DocumentumSessionFactory();
//			IDfSession session=dctm.getSession();
			System.out.println("calling createfolder...");
			dctm.createFolder("/Temp", "test01");
			System.out.println("Ending...");
			}catch(Exception e){
				System.out.println("Exception is "+e.getMessage());
			}

		}


}
