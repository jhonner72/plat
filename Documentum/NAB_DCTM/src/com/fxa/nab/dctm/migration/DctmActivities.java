package com.fxa.nab.dctm.migration;

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
	
	    public String checkExistenseOfFolders(String parentFolderPath,String yearFolder,String monthFolder,String dayFolder,String entryNumber) throws Exception{
		
			try{
				if(parentFolderPath == null || parentFolderPath.trim().length() == 0){
				CommonUtils.writeToLog("The path to store in Documentum is either null or empty string. In properties file, check the key '"+BMProperties.target_dctm_location_key+"' has a valid folder path.",error,null);
				throw new FXAException("Exception from 'checkExistenseOfFolders(....)' method. No folder exist with the given path '"+parentFolderPath+"'. In properties file, check the key '"+BMProperties.target_dctm_location_key+"' has a valid folder path.");
				}
				
				//get the parentFolder object. 
				IDfFolder parentFolderObj = (IDfFolder) getSession().getFolderByPath(parentFolderPath);
				
				//Log error if not and return
				if(parentFolderObj == null){
					CommonUtils.writeToLog("No folder exist with the given path "+parentFolderPath,error,null);
					throw new FXAException("Exception from 'checkExistenseOfFolders(....)' method. No folder exist with the given path '"+parentFolderPath+"'. In properties file, check the key '"+BMProperties.target_dctm_location_key+"' has a valid folder path.");
				}
				
				checkEachAndCreate(yearFolder,parentFolderPath);
				String yearFolderPath=parentFolderPath.endsWith(BMProperties.DCTM_PATH_SEP)?parentFolderPath+yearFolder:parentFolderPath+BMProperties.DCTM_PATH_SEP+yearFolder;
				checkEachAndCreate(monthFolder,yearFolderPath);
				String monthFolderPath=yearFolderPath+BMProperties.DCTM_PATH_SEP+monthFolder;
				checkEachAndCreate(dayFolder,monthFolderPath);			
				String dayFolderPath=monthFolderPath+BMProperties.DCTM_PATH_SEP+dayFolder;
				checkEachAndCreate(entryNumber,dayFolderPath);	
				String entryFolderPath=dayFolderPath+BMProperties.DCTM_PATH_SEP+entryNumber;
				
				return entryFolderPath;
			
			}catch(DfException e){
				CommonUtils.writeToLog("DfException from 'checkExistenseOfFolders(....)' method. DfException message is : \n "+e.getMessage(), error,null);
				e.printStackTrace();
				throw e;
			}catch(Exception e){
				CommonUtils.writeToLog("Exception from 'checkExistenseOfFolders(....)' method. Exception message is : \n "+e.getMessage(), error,null);
				e.printStackTrace();
				throw e;
			}						
		}
	
		private void checkEachAndCreate(String folderName,String parentFolderPath) throws Exception{
			
			try{
			  String thisFolderPath=parentFolderPath.endsWith(BMProperties.DCTM_PATH_SEP)?parentFolderPath+folderName:parentFolderPath+BMProperties.DCTM_PATH_SEP+folderName;
			
			  IDfFolder thisFolderPathObj= (IDfFolder) getSession().getFolderByPath(thisFolderPath);
			  if(thisFolderPathObj == null){
					//if not exist, create it
				CommonUtils.writeToLog("The folder with path "+thisFolderPath+" doesn't exist. Creating it.",debug,null);
					createFolder(parentFolderPath,folderName);
			  }else{
					//if exist, proceed
				CommonUtils.writeToLog("The folder with year "+thisFolderPath+" already exist.",debug,null);
			  }
			}catch(DfException e){
				CommonUtils.writeToLog("DfException from 'checkEachAndCreate(....)' method. DfException message is : \n "+e.getMessage(), error,null);
				e.printStackTrace();
				throw e;
			}catch(Exception e){
				CommonUtils.writeToLog("Exception from 'checkEachAndCreate(....)' method. Exception message is : \n "+e.getMessage(), error,null);
				e.printStackTrace();
				throw e;
			}
		}
	
	
	    private void createFolder(String parentFolder,String folderToBeCreated) throws Exception{
			
			try{
					IDfFolder folder = (IDfFolder) getSession().newObject(BMProperties.folder_object_type);
					folder.setObjectName(folderToBeCreated);
					
					if(getFolderACL() != null){
					 folder.setACL(getFolderACL());				
					}
					  
					folder.link(parentFolder);				
					folder.save();
					CommonUtils.writeToLog(folderToBeCreated + " created under " + parentFolder,debug,null);
			}catch(DfException e){
				CommonUtils.writeToLog("Exception from 'createFolder(...)' method. Exception message is : \n "+e.getMessage(), error,null);
			}			
		}

	   private IDfACL getFolderACL() throws Exception{
			
		try{
			if(folderACLObj != null){
				return folderACLObj;
			}else{
				String folderACLName=BMProperties.folder_acl_name;
				String folderACLDomain=BMProperties.folder_acl_domain;
				
				if(folderACLName != null && folderACLDomain != null){
					
					  folderACLObj=getACLObj(folderACLDomain,folderACLName);
					  if(folderACLObj != null){
						  	CommonUtils.writeToLog("ACL for the folder is set as "+folderACLName+" and the ACL domain is "+folderACLDomain+".",debug,null);
							
						}else{
							CommonUtils.writeToLog("ERROR : There is no ACL with the name "+folderACLName+" and domain "+folderACLDomain+".",debug,null);
							throw new FXAException("ERROR : There is no ACL with the name "+folderACLName+" and domain "+folderACLDomain+". Set correct folder acl and domain in properties file.");
						}
					}
				return folderACLObj;
			}
			
		 }catch(Exception e){
			 CommonUtils.writeToLog("Exception from method getFolderACL(). Exception message is "+e.getMessage(),error,null);
			 throw e;
		 }		
	    }
		
		public IDfACL getVoucherACL() throws Exception{
			
		try{
			if(voucherACLObj != null){
				return voucherACLObj;
			}else{
				String voucherACLName=BMProperties.doc_acl_name;
				String voucherACLDomain=BMProperties.doc_acl_domain;
				
				if(voucherACLName != null && voucherACLDomain != null){
					
					  voucherACLObj=getACLObj(voucherACLDomain,voucherACLName);
					  if(voucherACLObj != null){
						  	CommonUtils.writeToLog("ACL for the vouchers is set as "+voucherACLName+" and the ACL domain is "+voucherACLDomain+".",debug,null);
							
						}else{
							CommonUtils.writeToLog("ERROR : There is no ACL with the name "+voucherACLName+" and domain "+voucherACLDomain+".",debug,null);
							throw new FXAException("ERROR : There is no ACL with the name "+voucherACLName+" and domain "+voucherACLDomain+". Set correct doc acl and domain in properties file.");
						}
					}
				return voucherACLObj;
			}
			
		 }catch(Exception e){
			 CommonUtils.writeToLog("Exception from method getVoucherACL(). Exception message is "+e.getMessage(),error,null);
			 throw e;
		 }		
	    }		

		public IDfACL getACLObj(String aclDomain,String aclName) throws Exception{
			
		try{
				IDfACL aclObj=null;
				if(aclDomain != null && aclName != null){
					
					aclObj=getSession().getACL(aclDomain,aclName);
					  if(aclObj != null){
						  return aclObj;
					  }
					}
				
				return null;
			
		 }catch(Exception e){
			 CommonUtils.writeToLog("Exception from method getACLObj(). Exception message is "+e.getMessage(),error,null);
			 throw e;
		 }		
	    }
		   
	   public IDfSession getSession() throws Exception{
		   if(session == null){

			   createSession(BMProperties.docbroker_host,BMProperties.docbroker_port,BMProperties.dctm_login_user, BMProperties.dctm_password_encrypted,null,BMProperties.repository_name);

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
//				e.printStackTrace();
				throw e;
			}
	    	
			
			return  session;// returns a IDfSession Object
	   }
	    
		public void releaseSession() {
			if(sMgr != null && session != null){
//				try {
					sMgr.release(session);
					CommonUtils.writeToLog("Documentum Session released succesfully!",debug, null);
//				} catch (Exception e) {
//					CommonUtils.writeToLog("Exception while releasing the Documentum Session : "+e.getMessage(),debug, null);
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
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
//				e.printStackTrace();
				throw e;				
			}
		}
	    
		
		//Upload Images	
		public boolean uploadVouchersInABatch(String batchName,String batchStoragePath,ArrayList<FxaVoucher> vouchersList,HashMap<String,String> transactionAndDRN,String folderPath){
			CommonUtils.writeToLog("Inside uploadVouchersInABatch() method",debug,null);
			
			IDfBatchManager bMgr=null;
//			RollingFileAppender appender=null;
			
			try{
					
			CommonUtils.writeToLog("Processing "+batchName, info, null);
			
			FxaVoucher chequeObj=null;
			
			bMgr=getSession().getBatchManager();
			bMgr.openBatch();
			
			for(int count=0;count<vouchersList.size();count++){	
				chequeObj=(FxaVoucher)vouchersList.get(count);

				
				
			IDfSysObject fxa_voucher=(IDfSysObject)getSession().newObject(BMProperties.doc_object_type);

			    CommonUtils.writeToLog("Processing Document  :"+chequeObj.getObject_name(),info,null);
			 	
			    fxa_voucher.setObjectName(chequeObj.getObject_name());		
				fxa_voucher.setContentType("tiff"); //remove this hardcode later
				fxa_voucher.setString("fxa_migration_batch_no",batchName);
				
				fxa_voucher.setFile(batchStoragePath+BMProperties.PATH_SEP+chequeObj.getObject_name());
				
				fxa_voucher.setString("fxa_extra_aux_dom",chequeObj.getFxa_extra_aux_dom());	
				fxa_voucher.setString("fxa_aux_dom",chequeObj.getFxa_aux_dom());	
				fxa_voucher.setString("fxa_bsb",chequeObj.getFxa_bsb());
				fxa_voucher.setString("fxa_account_number",chequeObj.getFxa_account_number());	
				fxa_voucher.setString("fxa_trancode",chequeObj.getFxa_trancode());
				fxa_voucher.setString("fxa_amount",chequeObj.getFxa_amount());
				fxa_voucher.setString("fxa_drn",chequeObj.getFxa_drn());
				fxa_voucher.setString("fxa_classification",chequeObj.getFxa_classification());
				fxa_voucher.setString("fxa_collecting_bsb",chequeObj.getFxa_collecting_bsb());	
				fxa_voucher.setString("fxa_m_entry_number",chequeObj.getFxa_m_entry_number());
				fxa_voucher.setString("fxa_m_batch_number",chequeObj.getFxa_m_batch_number());	
				
				fxa_voucher.setString("fxa_batch_number",chequeObj.getFxa_m_entry_number()+chequeObj.getFxa_m_batch_number()); //// changes for story 22878. Old comment: changes for story 20346 by Yogesh Jankin
				
				fxa_voucher.setString("fxa_m_bal_seq_for_deposit",chequeObj.getFxa_m_bal_seq_for_deposit());
				
				fxa_voucher.setString("fxa_tran_link_no",chequeObj.getFxa_m_bal_seq_for_deposit()); // changes for story 22878 by Yogesh Jankin
				
				fxa_voucher.setString("fxa_m_balanced_sequence",chequeObj.getFxa_m_balanced_sequence());
				fxa_voucher.setString("fxa_checksum",chequeObj.getFxa_checksum());			
				fxa_voucher.setString("fxa_checksum_type",chequeObj.getFxa_checksum_type());
				
				fxa_voucher.setString("fxa_capture_bsb","082082");  // changes for story 22878 by Yogesh Jankin	
						
				if(transactionAndDRN.get(chequeObj.getTransaction()) != null){
					 fxa_voucher.setString("fxa_m_cr_drn",transactionAndDRN.get(chequeObj.getTransaction()));
					 //fxa_voucher.setString("fxa_tran_link_no",transactionAndDRN.get(chequeObj.getTransaction())); // changes for story 22878. Old comment: changes for story 20346 by Yogesh Jankin
				}/* Below code added for story 20346 by Yogesh Jankin */
				else{
					
					fxa_voucher.setString("fxa_m_cr_drn",chequeObj.getFxa_drn());
					//fxa_voucher.setString("fxa_tran_link_no",chequeObj.getFxa_drn());		// changes for story 22878 by Yogesh Jankin			
				}/* End of code added for story 20346 by Yogesh Jankin */
				 
				IDfTime processTime=new DfTime(parseDate(chequeObj.getFxa_processing_date()),IDfTime.DF_TIME_PATTERN14);
				fxa_voucher.setTime("fxa_processing_date",processTime);
				if(BMProperties.check_agency_bank){	
					setFXAWorkTypeCode(fxa_voucher,chequeObj);
				}else{
					CommonUtils.writeToLog("Agency Bank Processing not required. No need to set fxa_work_type_code. ",debug,null);
				}
			 
				fxa_voucher.link(folderPath);
				fxa_voucher.setACL(getVoucherACL());
				
				fxa_voucher.save();
				CommonUtils.writeToLog("Voucher "+chequeObj.getObject_name()+" saved in Documentum", debug,null);				
				
				
				/* start - Code added by Yogesh for 21701 - Agency Bank cheque*/
				if(BMProperties.check_agency_bank){	
					CommonUtils.writeToLog("<< Beginning Agency Bank Processing................. ",debug,null);
					String bsbStarting3Chars=chequeObj.getFxa_bsb().substring(0, 3);
					String targetEndPoint=BMProperties.getBsbsTargetEndpointsMapping().get(bsbStarting3Chars);
					
					if(targetEndPoint == null || targetEndPoint.trim().length() == 0){
						CommonUtils.writeToLog("There is no targetEndPoint for given bsb starting with "+bsbStarting3Chars,debug,null);
						CommonUtils.writeToLog("Voucher transfer object will NOT be created for this voucher.",debug,null);
					}else{
						String chronicleId=fxa_voucher.getChronicleId().toString();

						CommonUtils.writeToLog("There is a targetEndPoint ["+targetEndPoint+"] for given bsb starting with "+bsbStarting3Chars,debug,null);
						String classification=chequeObj.getFxa_classification();
						if(classification.equalsIgnoreCase("CR")){
							CommonUtils.writeToLog("It is a Credit Voucher",debug,null);
							if(BMProperties.includeCredit(targetEndPoint)){
								CommonUtils.writeToLog("This credit voucher requires Voucher Transfer Object.",debug,null);
								createVoucherTransferObject(chronicleId, targetEndPoint,folderPath);
							}else{
								CommonUtils.writeToLog("This credit voucher doesn't require Voucher Transfer Object.",debug,null);
							}
						}else{
							CommonUtils.writeToLog("This debit voucher requires Voucher Transfer Object.",debug,null);
							createVoucherTransferObject(chronicleId, targetEndPoint,folderPath);
						}							
						
					}
					
					CommonUtils.writeToLog("........End of Agency Bank Processing. >>",debug,null);					
				}else{
					CommonUtils.writeToLog("Agency Bank Processing not required.",debug,null);
				}
				/* end - Code added by Yogesh for 21701 - Agency Bank cheque*/	
				
			   }
				
			    bMgr.commitBatch();
			    bMgr.closeBatch();
			    CommonUtils.writeToLog("SUCCESS!!!  Batch "+batchName+ " processed successfully!!!",debug,null);
			    CommonUtils.writeToLog("Returning 'true' from 'uploadVouchersInABatch(..)' method to set the batch status to sucess.",debug,null);		   
			    return true;	
			}catch(Exception e){
				CommonUtils.writeToLog("ERROR!!!! Method uploadVouchersInABatch(): Exception is "+e.getMessage(), error, null);
				CommonUtils.writeToLog("ERROR!!!! Failed to process batch "+batchName, error, null);
				e.printStackTrace();
				    try{
					 if(bMgr != null) bMgr.abortBatch();
					}catch(DfException ex){
						CommonUtils.writeToLog("ERROR!!!! Failed to abort Batch while processing batch "+batchName,error,ex);
						CommonUtils.writeToLog("ERROR!!!! Failed to abort Batch while processing batch "+batchName,error,null);
					}
				    CommonUtils.writeToLog("Returning 'false' from 'uploadVouchersInABatch(..)' method to set the batch status to fail. ",error,null);
				    return false;
			}/*finally{
				try{
				if(bMgr != null) 
				}catch(DfException e){
					CommonUtils.writeToLog("ERROR!!!! Failed to close Batch while processing batch "+batchName,error,e);
					CommonUtils.writeToLog("ERROR!!!! Failed to close Batch while processing batch "+batchName,error,null);
				}
			}*/
		}
		
		private void setFXAWorkTypeCode(IDfSysObject fxa_voucher,FxaVoucher chequeObj) throws DfException{
			CommonUtils.writeToLog("Inside setFXAWorkTypeCode() method ",debug,null);
			String bsbStarting3Chars=chequeObj.getFxa_bsb().substring(0, 3);
			String targetEndPoint=BMProperties.getBsbsTargetEndpointsMapping().get(bsbStarting3Chars);
			
			if(targetEndPoint == null || targetEndPoint.trim().length() == 0){
				CommonUtils.writeToLog("fxa_work_type_code WON'T be set.",debug,null);
			}else{
				String classification=chequeObj.getFxa_classification();
				if(classification.equalsIgnoreCase("CR")){//it is a credit voucher
					if(BMProperties.includeCredit(targetEndPoint)){
						fxa_voucher.setString("fxa_work_type_code","NABCHQ_POD");
						CommonUtils.writeToLog("fxa_work_type_code set to [NABCHQ_POD]",debug,null);						
					}
				}else{//it is a debit voucher
					fxa_voucher.setString("fxa_work_type_code","NABCHQ_POD");
					CommonUtils.writeToLog("fxa_work_type_code set to [NABCHQ_POD]",debug,null);					
				}
			}
			CommonUtils.writeToLog("Leaving setFXAWorkTypeCode() method ",debug,null);				
		}
		
		private String parseDate(String rawDate){
			String formattedDate=rawDate.substring(6)+"/"+rawDate.substring(4,6)+"/"+rawDate.substring(0,4);
	        return formattedDate;
		}	
		
		/* start - Code added by Yogesh for 21701 - Agency Bank cheque*/
		private void createVoucherTransferObject(String chronicleId,String targetEndPoint,String folderPath){
			
			CommonUtils.writeToLog("Inside createVoucherTransferObject() method ",debug,null);
			
			try{
			IDfSysObject fxa_voucher_transfer=(IDfSysObject)getSession().newObject(BMProperties.voucher_transfer_object_type);
			fxa_voucher_transfer.setString("transmission_type","IMAGE_EXCHANGE_OUTBOUND");
			fxa_voucher_transfer.setString("status","New");			
			fxa_voucher_transfer.setString("target_end_point",targetEndPoint);
			fxa_voucher_transfer.setString("v_i_chronicle_id",chronicleId);
			fxa_voucher_transfer.link(folderPath);
			fxa_voucher_transfer.save();
			CommonUtils.writeToLog("Voucher Transfer Object created for Chronicle ID  ["+chronicleId+"]",debug,null);
			
			}catch(Exception e){
				CommonUtils.writeToLog("ERROR!!!! Method createVoucherTransferObject(): Exception is "+e.getMessage(), error, null);
				CommonUtils.writeToLog("ERROR!!!! Failed to create voucher transfer object for  chronicleId ["+chronicleId+"]", error, null);
				e.printStackTrace();
			}
			CommonUtils.writeToLog("Leaving createVoucherTransferObject() method ",debug,null);
		}
		/* end - Code added by Yogesh for 21701 - Agency Bank cheque*/
	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DctmActivities dctm=new DctmActivities();
		try {
			dctm.checkExistenseOfFolders("/Vouchers", "2012", "10", "01","1650");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
