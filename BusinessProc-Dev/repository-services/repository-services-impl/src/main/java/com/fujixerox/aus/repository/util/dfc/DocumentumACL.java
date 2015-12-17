package com.fujixerox.aus.repository.util.dfc;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.repository.util.RepositoryProperties;
import com.fujixerox.aus.repository.util.exception.ACLException;
import com.fujixerox.aus.repository.util.exception.FileException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 * Henry Niu
 * 26/03/2015 Initial version
 * 27/08/2015 Modified to handle multiple object and folder ACLs
 */ 
public class DocumentumACL {	
			
	private static IDfACL voucherACLObj = null;
    private static IDfACL reportACLObj = null;
    private static IDfACL adjustmentLetterACLObj = null;
    
    private static IDfACL voucherFolderACLObj = null;
    private static IDfACL reportFolderACLObj = null;
    private static IDfACL adjustmentLetterFolderACLObj = null;
    
    private static Map<String, IDfACL> aclNameMap;
    private static Map<String, IDfACL> folderACLNameMap;
    
    static {
		init();
    }
    
    private static void init() {
    	aclNameMap = new HashMap<String, IDfACL>();
    	aclNameMap.put(RepositoryProperties.doc_acl_name, voucherACLObj);
    	aclNameMap.put(RepositoryProperties.doc_acl_report_name, reportACLObj);
    	aclNameMap.put(RepositoryProperties.doc_acl_adjustment_letter_name, adjustmentLetterACLObj);
    	
    	folderACLNameMap = new HashMap<String, IDfACL>();
    	folderACLNameMap.put(RepositoryProperties.folder_acl_name, voucherFolderACLObj);
    	folderACLNameMap.put(RepositoryProperties.folder_acl_report_name, reportFolderACLObj);
    	folderACLNameMap.put(RepositoryProperties.folder_acl_adjustment_letter_name, adjustmentLetterFolderACLObj);    
    }
	
	public IDfACL getACL(IDfSession session, String aclName) throws DfException, ACLException {	
		IDfACL thisACL = aclNameMap.get(aclName);
				
		if (thisACL == null) {
			thisACL = getACLObj(session, RepositoryProperties.doc_acl_domain, aclName);
			aclNameMap.put(aclName, thisACL);
		} 
		
		return thisACL;
	}

	public String checkFolderExist(IDfSession session, String folderACLName, String parentFolderPath, Date processingDate)
	    		throws ACLException, DfException, FileException {
			
		if (parentFolderPath == null || parentFolderPath.trim().length() == 0){
			throw new FileException("No folder exist with the given path '" + parentFolderPath + "'.");
		}
			
		IDfFolder parentFolderObj = (IDfFolder)session.getFolderByPath(parentFolderPath);				
		if (parentFolderObj == null) {				
			throw new FileException("No folder exist with the given path '" + parentFolderPath + "'.");
		}

        String yearFolder = new SimpleDateFormat("yyyy").format(processingDate);
        checkEachAndCreate(session, folderACLName, yearFolder, parentFolderPath);			
        
		String yearFolderPath = parentFolderPath.endsWith(RepositoryProperties.repository_image_path_sep) ? 
				parentFolderPath + yearFolder:parentFolderPath + RepositoryProperties.repository_image_path_sep + yearFolder;

        String monthFolder = new SimpleDateFormat("MM").format(processingDate);
		checkEachAndCreate(session, folderACLName, monthFolder, yearFolderPath);			
		
		String monthFolderPath = yearFolderPath + RepositoryProperties.repository_image_path_sep + monthFolder;
		
        String dayFolder = new SimpleDateFormat("dd").format(processingDate);
		checkEachAndCreate(session, folderACLName, dayFolder, monthFolderPath);			
		
		String dayFolderPath = monthFolderPath + RepositoryProperties.repository_image_path_sep + dayFolder;
	
		return dayFolderPath;
	}
	
	private void checkEachAndCreate(IDfSession session, String folderACLName, String folderName, String parentFolderPath) 
			throws ACLException, DfException  {
		
		  String thisFolderPath = parentFolderPath.endsWith(RepositoryProperties.repository_image_path_sep) ? parentFolderPath + folderName:parentFolderPath
				  + RepositoryProperties.repository_image_path_sep + folderName;
		
		  IDfFolder thisFolderPathObj = (IDfFolder)session.getFolderByPath(thisFolderPath);
		  if (thisFolderPathObj == null) {
			createFolder(session, folderACLName, parentFolderPath, folderName);
		  } 
	}		
	
    private void createFolder(IDfSession session, String folderACLName, String parentFolder, String folderToBeCreated) 
    		throws ACLException, DfException {
		
		IDfFolder folder = (IDfFolder)session.newObject(RepositoryProperties.folder_object_type);
		folder.setObjectName(folderToBeCreated);
		
		if(getFolderACL(session, folderACLName) != null){
			folder.setACL(getFolderACL(session, folderACLName));				
		}
		  
		folder.link(parentFolder);				
		folder.save();
	}

   private IDfACL getFolderACL(IDfSession session, String folderACLName) throws DfException, ACLException {	
	   IDfACL thisFolderACL = folderACLNameMap.get(folderACLName);
		
		if (thisFolderACL == null) {
			thisFolderACL = getACLObj(session, RepositoryProperties.folder_acl_domain, folderACLName);
			folderACLNameMap.put(folderACLName, thisFolderACL);
		} 
		
		return thisFolderACL;
	}

	private IDfACL getACLObj(IDfSession session, String aclDomain, String aclName) throws ACLException, DfException  {			
		IDfACL acl = null;
		
		if (aclDomain != null && aclName != null) {				
			acl = session.getACL(aclDomain.trim(), aclName.trim());
			if (acl == null) {
				throw new ACLException("ERROR : There is no ACL with the name " + aclName + " and domain " + aclDomain
						+ ". Set correct acl and domain in properties file.");
		    }
		}
		
		return acl;	
    }	   
	
	// this method is used to clean the static variables for unit test
	public void clean() {
		voucherACLObj = null;
	    reportACLObj = null;
	    adjustmentLetterACLObj = null;
	    
	    voucherFolderACLObj = null;
	    reportFolderACLObj = null;
	    adjustmentLetterFolderACLObj = null;
	    
	    init();
	}
		   
}
