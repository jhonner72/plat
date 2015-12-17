package com.fxa.nab.dctm.method;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfModule;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.methodserver.IDfMethod;

/**
 * Author: Ajit Dangal
 * Project: NAB Cheques
 *
 * Objective: Method will run on week days to synchronize users. 
 * Feed to receive from NAB and will be stored in DB
 * @Assumption : 1:1 mapping between NAB and Documentum user groups
 * @TFS story : 18594  
 * Version : 1.1 : by Ajit Insert and update modification
 * Version : 1.2 : 2-10-2015 : by Ajit Added logic to update user group - single user group 
 */	

public class UserSynchMethod implements IDfModule, IDfMethod {
	

	public static final String USER_KEY = "user_name";
	public static final String DOCBASE_KEY = "docbase_name";
	public static final String TICKET_KEY = "ticket";
	// App Specific
	// mapping
	public static final String RG_TABLE_MAPPING = "dbo.nabusergroupmapping";
	// Delta
	public static final String RG_TABLE_DELTA = "dbo.nabusersynch_delta";
	// docbase
	
	//custom value
	
	public static final int debug=0;
	public static final int error=1;
	public static final int warning=2;

	//private static final String PATH_SEP = "/";	
	
	private IDfSession session=null;
	private IDfSessionManager sMgr=null;
	
	// Input Params from Server...
	private String m_docbase = "NAB"; // default to
	private String m_userName = null;
	private String m_ticket = null;
	private PrintWriter m_writer = null;
	private Map<String,String> m_GroupMap = null;
	private ArrayList<String> m_updateUserSet = new ArrayList<String>();
	

	
	@SuppressWarnings("unchecked")
	@Override
	public int execute(@SuppressWarnings("rawtypes") Map arg_map, PrintWriter arg_writer) throws Exception {
		// TODO Auto-generated method stub
		m_writer=arg_writer;
		writeToLog("{UserSynchMethod}{execute}Executing Method...",debug,null);
		try{
			int iEligible = isEligible();
			if(iEligible <= 0 ){
				if(isValidDay()){
					this.readParamsAndValidate(arg_map);
					this.createSession(m_userName, m_ticket, null, m_docbase);
					this.getMapping();
					this.processDeltaInsert();
					this.processDeltaDelete();
					this.processDeltaUpdate();
				    
				}else{
					writeToLog("{UserSynchMethod}{execute}No run today as not valid day (sat/sun)",debug,null);
				}
			
			}else{
				writeToLog("{UserSynchMethod}{execute}Method is not executed",debug,null);
			}
		}catch(Exception eExc){
			writeToLog("{UserSynchMethod}{execute}Method failed to executed ?????????",error,null);
			eExc.printStackTrace();
		}finally{
			this.releaseSession();
		}
		return 0;
	}
/**
 * check flags - future use
 * @return
 */
	private int isEligible(){
		int iRet = 0;
		writeToLog("{UserSynchMethod}{isEligible} Return Reason Code{" + iRet + "}",debug,null);
		return iRet;
	}
	private void getMapping(){
		try{
			// First Get map from RTable
			m_GroupMap = this.fillMap();
		}catch(Exception eMap){
			
		}
	}

	private void processDeltaInsert() throws Exception {
		try{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String strToday = sdf.format(cal.getTime());
			writeToLog("{UserSynchMethod}{processDeltaInsert} Processing Delta !! for Date {" + 
						strToday +"}",debug,null);
			String qry = "select * from "+ RG_TABLE_DELTA + " where data_date ='"+ 
								strToday +"' and ops_type ='INSERT' ORDER BY user_id";
	    	writeToLog("{UserSynchMethod}{processDeltaInsert}Query {" + qry + "}",debug,null);	

	    			IDfCollection coll = null;
	    	    	try{		
	        			
	    	    		String strUserId = "";
	    		    	IDfQuery query = new DfQuery();
		    			query.setDQL(qry);
	    	    		coll = query.execute(session, IDfQuery.DF_QUERY);
	    	    		int iRecordCount = 0 ;
	    	    		while (coll.next())
	    	    			{
	    	    				strUserId = coll.getString("user_id");	    	    				
	    	    				if(strUserId != null ){
	    	    					writeToLog("{UserSynchMethod}{processDeltaInsert}Nab User ID {" + strUserId +
	    	    							"}",debug,null);
    	    						try{
    	    							if(!this.isUserExists(strUserId)){
	    	    							this.createUser(coll.getString("user_id"),    	    						
		    	    								coll.getString("first_name"),coll.getString("last_name"),
		    	    								coll.getString("user_email"),coll.getString("user_group"));
	    	    							
	    	    							iRecordCount++;
	    	    							writeToLog("{UserSynchMethod}{processDeltaInsert}User {" + strUserId + 
		    	    							"} User created successfully in documentum",debug,null);
    	    							}	
	    	    					}catch(Exception eIndUserError){
	    	    						writeToLog("{UserSynchMethod}{processDeltaInsert} ** FATAL ERROR ** User {" + strUserId + 
	    	    								"} Unable to create user in documentum, please check the log",debug,null);
	    	    					}
	    	    					
	    	    				}else{
	    	    					writeToLog("{UserSynchMethod}{processDeltaInsert}Nab User ID is null or empty{" + 
	    	    								strUserId + "} Please check Delta table has values",debug,null);
	    	    				}
	    	    			
	    	    			}
	    	    			
	    	    			writeToLog("{UserSynchMethod}{processDeltaInsert} Process Complete number of records {" + 
	    	    						iRecordCount +"}",debug,null);
	    	    			
	    	    	}catch(Exception eDql){
	    	    		writeToLog("{UserSynchMethod}{processDeltaInsert}Unable to Insert record "  ,error,null);
	    	    		eDql.printStackTrace();
	    	    		//throw eDql;
	    	    		
	    	    	}finally{
	    	    			
	    	    			coll.close();
	    	    			writeToLog("{UserSynchMethod}{processDeltaInsert}closed collection "  ,debug,null);
	    	    	}   		
	    			
		}catch(Exception eDeltaInsert){
			writeToLog("{UserSynchMethod}{processDeltaInsert} Error creating users record "  ,error,null);
			eDeltaInsert.printStackTrace();
			throw eDeltaInsert;
		}
	}
	private void processDeltaUpdate() throws Exception {
		try{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String strToday = sdf.format(cal.getTime());
			writeToLog("{UserSynchMethod}{processDeltaUpdate} Processing Delta !! for Date {" + 
						strToday +"}",debug,null);
			String qry = "select * from "+ RG_TABLE_DELTA + " where data_date ='"+ 
								strToday +"' and ops_type ='UPDATE' ORDER BY user_id";
	    	writeToLog("{UserSynchMethod}{processDeltaUpdate}Query {" + qry + "}",debug,null);	

	    			IDfCollection coll = null;
	    	    	try{		
	        			
	    	    		String strUserId = "";
	    		    	IDfQuery query = new DfQuery();
		    			query.setDQL(qry);
	    	    		coll = query.execute(session, IDfQuery.DF_QUERY);
	    	    		int iRecordCount = 0;
	    	    		while (coll.next())
	    	    			{
	    	    				strUserId = coll.getString("user_id");	    	    				
	    	    				if(strUserId != null ){
	    	    					writeToLog("{UserSynchMethod}{processDeltaUpdate}Nab User ID {" + strUserId + 
	    	    							"}",debug,null);
	    	    					if(isUserExists(strUserId)){
	    	    						writeToLog("{UserSynchMethod}{processDeltaUpdate}User {" + strUserId + 
	    	    								"} does exists in documentum",debug,null);
	    	    						if(updateUser(coll.getString("user_id"),
	    	    								coll.getString("first_name"),coll.getString("last_name"),
	    	    								coll.getString("user_email"),coll.getString("user_group"))){
	    	    							iRecordCount ++;
	    	    							writeToLog("{UserSynchMethod}{processDeltaUpdate}User {" + strUserId + 
	    	    									"} User updated successfully in documentum",debug,null);
	    	    							
	    	    						}else{
	    	    							writeToLog("{UserSynchMethod}{processDeltaUpdate}User {" + strUserId + 
	    	    									"} Unable to update user in documentum, please check the log",debug,null);
	    	    						}
	    	    					}else{
	    	    						writeToLog("{UserSynchMethod}{processDeltaUpdate}User {" + strUserId + 
	    	    								"} does not exists in documentum - No furher processing",debug,null);
	    	    						
	    	    					}
	    	    			
	    	    				}
	    	    			}// end of while
	    	    			
	    	    			writeToLog("{UserSynchMethod}{processDeltaUpdate} Process Complete number of records {"+
	    	    						iRecordCount +"}",debug,null);
	    	    			
	    	    	}catch(Exception eDql){
	    	    		writeToLog("{UserSynchMethod}{processDeltaUpdate}Unable to Update record "  ,debug,null);
	    	    		eDql.printStackTrace();
	    	    		throw eDql;
	    	    		
	    	    	}finally{
	    	    			
	    	    			coll.close();
	    	    			writeToLog("{UserSynchMethod}{processDeltaUpdate}closed collection "  ,debug,null);
	    	    	}   		
	    			
		}catch(Exception eDeltaUpdate){
			throw eDeltaUpdate;
		}
	}
	private void processDeltaDelete() throws Exception {
		try{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String strToday = sdf.format(cal.getTime());
			writeToLog("{UserSynchMethod}{processDeltaDelete} Processing Delta !! for Date {" + strToday +"}",debug,null);
			String qry = "select * from "+ RG_TABLE_DELTA + " where data_date ='"+ 
								strToday +"' and ops_type ='DELETE' ORDER BY user_id";
	    	writeToLog("{UserSynchMethod}{processDeltaDelete}Query {" + qry + "}",debug,null);	

	    			IDfCollection coll = null;
	    	    	try{		
	        			
	    	    		String strUserId = "";
	    		    	IDfQuery query = new DfQuery();
		    			query.setDQL(qry);
	    	    		coll = query.execute(session, IDfQuery.DF_QUERY);
	    	    		int iRecordCount = 0;
	    	    		while (coll.next())
	    	    			{
	    	    				strUserId = coll.getString("user_id");	    	    				
	    	    				if(strUserId != null ){
	    	    					writeToLog("{UserSynchMethod}{processDeltaDelete}Nab User ID {" + strUserId + "}",debug,null);
	    	    					if(isUserExists(strUserId)){
	    	    						//this.checkGroups();
	    	    						writeToLog("{UserSynchMethod}{processDeltaDelete}User {" + strUserId + 
	    	    								"} does exists in documentum",debug,null);
	    	    						if(changeUserState("deactivate",coll.getString("user_id"))){
	    	    							iRecordCount ++;
	    	    							writeToLog("{UserSynchMethod}{processDeltaDelete}User {" + strUserId + 
	    	    									"} User deactivated successfully in documentum",debug,null);
	    	    							
	    	    						}else{
	    	    							writeToLog("{UserSynchMethod}{processDeltaDelete}User {" + strUserId + 
	    	    									"} Unable to create user in documentum, please check the log",debug,null);
	    	    						}
	    	    					}else{
	    	    						writeToLog("{UserSynchMethod}{processDeltaDelete}User {" + strUserId + 
	    	    								"} does not exists in documentum - No furher processing",debug,null);
	    	    					}
	    	    				}else{
	    	    					writeToLog("{UserSynchMethod}{processDeltaDelete}Nab User ID is null or empty{" + strUserId + 
	    	    							"} Please check Delta table has values",debug,null);
	    	    				}
	    	    			
	    	    			}
	    	    			
	    	    			writeToLog("{UserSynchMethod}{processDeltaDelete} Process Complete Record count{"+ iRecordCount +"}",debug,null);
	    	    			
	    	    	}catch(Exception eDql){
	    	    		writeToLog("{UserSynchMethod}{processDeltaDelete}Unable to Update record "  ,debug,null);
	    	    		eDql.printStackTrace();
	    	    		throw eDql;
	    	    		
	    	    	}finally{
	    	    			
	    	    			coll.close();
	    	    			writeToLog("{UserSynchMethod}{processDeltaDelete}closed collection "  ,debug,null);
	    	    	}   		
	    			
		}catch(Exception eDeltaDelete){
			throw eDeltaDelete;
		}
	}
	private Map<String,String> fillMap() throws Exception{
	try{
		// empty first
		m_GroupMap = new HashMap <String,String>();
		String qry = "select nab_user_group,dctm_user_group from "+ RG_TABLE_MAPPING + " ORDER BY nab_user_group";
    	writeToLog("{UserSynchMethod}{fillMap}Query {" + qry + "}",debug,null);		
    	IDfCollection coll = null;
    	try{		
    			
    		String strNabUsrGrp = "";
    		String strDctmUsrGrp = "";
    		IDfQuery query = new DfQuery();
			query.setDQL(qry);
			coll = query.execute(session, IDfQuery.DF_QUERY);
    		while (coll.next())
    			{
    				strNabUsrGrp = coll.getString("nab_user_group");
    				//writeToLog("{UserSynchMethod}{fillMap}Nab User Group {" + strNabUsrGrp + "}",debug,null);
    				strDctmUsrGrp = coll.getString("dctm_user_group");
    				writeToLog("{UserSynchMethod}{fillMap}NAB Dctm User Group Mapping {" +
    						strNabUsrGrp +"}={"+ strDctmUsrGrp + "}",debug,null);
    				if(strNabUsrGrp != null ){
    					if(strDctmUsrGrp!= null ){
    						m_GroupMap.put(strNabUsrGrp, strDctmUsrGrp);
    					}else{
    						writeToLog("{UserSynchMethod}{fillMap}Dctm User Group is null or empty{" + strNabUsrGrp + 
    								"} Please check registered table",debug,null);	
    					}
    				}else{
    					writeToLog("{UserSynchMethod}{fillMap}Nab User Group is null or empty{" + 
    								strNabUsrGrp + "} Please check registered table",debug,null);
    				}
    			
    			}
    			
    			writeToLog("{UserSynchMethod}{fillMap}Size of Map {" + m_GroupMap.size() + "}",debug,null);
    			
    	}catch(Exception eDql){
    		writeToLog("{UserSynchMethod}{fillMap}Unable to Update record "  ,debug,null);
    		eDql.printStackTrace();
    		throw eDql;
    		
    	}finally{
    			
    			coll.close();
    			writeToLog("{UserSynchMethod}{fillMap}closed collection "  ,debug,null);
    	}    	
		
	}catch(Exception eMap){
		throw eMap;		
	}
		
		return m_GroupMap;
	}
	
	private boolean isUserExistsAndActive(String userId) throws Exception{
		boolean bRet = false;
		String qry = "select user_login_name from dm_user where user_login_name='"+ userId.trim() + "' and user_state=0";
    	writeToLog("{UserSynchMethod}{isUserExistsAndActive}Query {" + qry + "}",debug,null);	    	
    	IDfCollection coll = null;
    	try{		
    			
    		IDfQuery query = new DfQuery();
			query.setDQL(qry);
			coll = query.execute(session, IDfQuery.DF_QUERY);
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UserSynchMethod}{isUserExistsAndActive}Active user exists returning true {" + userId + "}",debug,null);
    		}else{
    			writeToLog("{UserSynchMethod}{isUserExistsAndActive} User is not active {" + userId + "}",debug,null);
    		}
    	}catch(Exception eUser){
    		writeToLog("{UserSynchMethod}{isUserExistsAndActive} Error checking user",error,null);
    		eUser.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
	}
	private boolean isUserExists(String userId) throws Exception{
		boolean bRet = false;
		String qry = "select user_login_name from dm_user where user_login_name='"+ userId.trim() + "'";
    	writeToLog("{UserSynchMethod}{isUserExists}Query {" + qry + "}",debug,null);	    	
    	IDfCollection coll = null;
    	try{		
    			
    		IDfQuery query = new DfQuery();
			query.setDQL(qry);
			coll = query.execute(session, IDfQuery.DF_QUERY);
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UserSynchMethod}{isUserExists}User Exists returning true {" + userId + "}",debug,null);
    		}else{
    			writeToLog("{UserSynchMethod}{isUserExists}User Does not Exists returning false {" + userId + "}",debug,null);
    		}
    	}catch(Exception eUser){
    		writeToLog("{UserSynchMethod}{isUserExists} Error checking user",error,null);
    		eUser.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
	}

	private boolean changeUserState(String strActive , String user_login)throws Exception{
    	
		String strQuery = null;
		if(strActive.equalsIgnoreCase("activate")){
			strQuery = "update dm_user object set user_state=0 where user_login_name='" + user_login +"'";
    	}else{
    		strQuery = "update dm_user object set user_state=1 where user_login_name='" + user_login +"'";
    	}
    			
		boolean bRet = false;
    	IDfCollection coll = null;
		
    	try{	    	
    		IDfQuery query = new DfQuery();
    		writeToLog("{UserSynchMethod}{deactivateUser}Query {" + strQuery + "}",debug,null);	
    		query.setDQL(strQuery);
    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UserSynchMethod}{deactivateUser}User Deactivated {" + user_login + "}",debug,null);
    		}else{
    			writeToLog("{UserSynchMethod}{deactivateUser}Unable to deactivate user {" + user_login + "}",debug,null);
    		}
    	}catch(Exception eUser){
    		writeToLog("{UserSynchMethod}{deactivateUser} Error Deactivating User",error,null);
    		eUser.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
	}
	private boolean removeUserFromGroups(String user_name){
		boolean bRet = false;
		try{
			if(user_name != null){
				String strGetGroupsQry = "select distinct group_name from dm_group"+
							" where any users_names = '" + user_name + "'";
		    	IDfCollection coll = null;
		    	Set<String> setGrpName = new HashSet<String>();
				try{	    	
		    		IDfQuery query = new DfQuery();
		    		writeToLog("{UserSynchMethod}{removeUserFromGroups}Query get all groups{" + strGetGroupsQry + "}",debug,null);	
		    		query.setDQL(strGetGroupsQry);
		    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
		    		while(coll.next()){
		    			String strTmpGrp = coll.getString("group_name");
		    			writeToLog("{UserSynchMethod}{removeUserFromGroups}User Group {" + strTmpGrp + "}",debug,null);
		    			try{
		    				setGrpName.add(coll.getString("group_name"));
		    			}catch(Exception eAdd){
		    				writeToLog("{UserSynchMethod}{removeUserFromGroups} Error occured while adding group to set" +
		    						" - User from this "+ strTmpGrp + 
		    						" group needs to be removed manually or next day may be",error,null);
		    			}
		    			
		    		}
		    		

		    	}catch(Exception eUser){
		    		writeToLog("{UserSynchMethod}{removeUserFromGroups} Error Deactivating User",error,null);
		    		eUser.printStackTrace();
		    	}finally{
		    		coll.close();
		    	}
	    		if(setGrpName.size()>0){
	    			Iterator<String> iterator = setGrpName.iterator();
	    			
	    			while(iterator.hasNext()){
	    			  String strGrp = (String) iterator.next();
	    			  String strRemoveQry = "ALTER GROUP '"+strGrp.trim()+ "' DROP '"+ user_name+"'";
	    			  writeToLog("{UserSynchMethod}{removeUserFromGroups} Query to remove user {" +
	    					  strRemoveQry +"}",debug,null);
	    			  IDfCollection collRem = null;   
	    			  try{
	    				IDfQuery query = new DfQuery();
	  		    		//writeToLog("{UserSynchMethod}{removeUserFromGroups}Query remove{" + strRemoveQry + "}",debug,null);	
	  		    		query.setDQL(strRemoveQry);
	  		    		collRem = query.execute(session, IDfQuery.DF_QUERY);	    			
	  		    		if(collRem.next()){	  		    			
	  		    			writeToLog("{UserSynchMethod}{removeUserFromGroups}User removed from {" + strGrp + "}",debug,null);
	  		    			
	  		    		}
	    				  
	    			  }catch(Exception eRemove){
	    				  writeToLog("{UserSynchMethod}{removeUserFromGroups}Unable to remove from {" + strGrp + "}",error,eRemove);
	    				  eRemove.printStackTrace();
	    			  }finally{
	    				  collRem.close();
	    			  }
	    			  
	    			}// end of while
	    			
	    		}else{		    		
	    			writeToLog("{UserSynchMethod}{removeUserFromGroups}No Groups found for user {" +
	    					user_name + "}",debug,null);
	    		}
				
				
				
			}else{
				writeToLog("{UserSynchMethod}{removeUserFromGroups} User Name string is NULL",error,null);
			}
		}catch(Exception eMuser){
			eMuser.printStackTrace();
		}
		return bRet;
	}
	private boolean addUserMultipleGroup(String grps,String user_name){
		boolean bRet = false;
		try{
			if(grps != null){
				
			}else{
				writeToLog("{UserSynchMethod}{addUserMultipleGroup} Group string is NULL",error,null);
			}
		}catch(Exception eMuser){
			eMuser.printStackTrace();
		}
		return bRet;
	}
	private boolean updateUser(String user_login,String fname, String lname,String email,String nabGrps)throws Exception{
		boolean bRet = false;
		writeToLog("{UserSynchMethod}{updateUser} Update request for user {" + user_login +"}",debug,null);	
		IDfCollection coll = null;
		IDfQuery query = new DfQuery();
		String dctmMapGrpName = "";
			
	    	try{	    	
	    		
	    		
	    		
	    		if(!isUserExistsAndActive(user_login)){
	    			this.changeUserState("activate", user_login);
				}
	    		String strUserName = fname+" "+lname;

	    		// update default
	    		try{
		    		dctmMapGrpName = this.getDefaultMappedGroup(nabGrps);
		    		String qry = "UPDATE dm_user objects set user_group_name ='"+ dctmMapGrpName +
		    						"' where user_name='"+strUserName+"'";
		    		
		    		writeToLog("{UserSynchMethod}{updateUser}Query {" + qry + "}",debug,null);	
		    		query.setDQL(qry);
		    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
		    		if(coll.next()){
		    			bRet = true;
		    			writeToLog("{UserSynchMethod}{updateUser}Users default group updated {" + user_login + "}",debug,null);
		    		}else{
		    			writeToLog("{UserSynchMethod}{updateUser}Unable to update user {" + user_login + "}",debug,null);
		    		}	    		
	    		}catch(Exception eDefaultUpdate){
	    			writeToLog("{UserSynchMethod}{updateUser}Unable to update user default group",error,null);
	    			eDefaultUpdate.printStackTrace();
	    		}
	    		//  Remove 
	    		try{
	    			if(!m_updateUserSet.contains(user_login)){
	    				this.removeUserFromGroups(strUserName);
	    			}else{
	    				writeToLog("{UserSynchMethod}{updateUser} User repeated adding it to more groups",error,null);
	    			}
	    		}catch(Exception eRemove){
	    			writeToLog("{UserSynchMethod}{updateUser}Unable to remove user from group",error,null);
	    			eRemove.printStackTrace();
	    		}
	    		// update other groups
	    		String strDctmGrpString = (String)m_GroupMap.get(nabGrps.trim());	    				
	    		if(strDctmGrpString !=null && strDctmGrpString.contains("|")){
		    		String formattedString = this.getOtherGrpString(strDctmGrpString);
		    		String qryAllGrp = "UPDATE dm_group OBJECTS APPEND users_names = '" + fname.trim() + " " +
		    					lname.trim() +"' WHERE group_name IN ("+ formattedString +")";
		    		writeToLog("{UserSynchMethod}{updateUser}Multiple Group Query{" + qryAllGrp +"}",debug,null);
		    		query.setDQL(qryAllGrp);
		    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
		    		if(coll.next()){
		    			bRet = true;
		    			writeToLog("{UserSynchMethod}{updateUser}User updated for other groups{" + user_login + "}",debug,null);
		    		}else{
		    			writeToLog("{UserSynchMethod}{updateUser}Unable to update user {" + user_login + "}",debug,null);
		    		}	
	    		}else{
	    			writeToLog("{UserSynchMethod}{updateUser}User is not part of multiple group only default group set ",debug,null);
		    		
		    		String qryAllGrp = "UPDATE dm_group OBJECTS APPEND users_names = '" + fname.trim() + " " +
		    					lname.trim() +"' WHERE group_name IN ('"+ strDctmGrpString +"')";
		    		writeToLog("{UserSynchMethod}{updateUser}Single Group Query{" + qryAllGrp +"}",debug,null);
		    		query.setDQL(qryAllGrp);
		    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
		    		if(coll.next()){
		    			bRet = true;
		    			writeToLog("{UserSynchMethod}{updateUser}User updated for Single group{" + user_login + "}",debug,null);
		    		}else{
		    			writeToLog("{UserSynchMethod}{updateUser}Unable to update user - Single Group {" + user_login + "}",debug,null);
		    		}
	    		}
	    		// store in array
	    		m_updateUserSet.add(user_login);
	    		
	    	}catch(Exception eUser){
	    		writeToLog("{UserSynchMethod}{updateUser} Error",error,null);
	    		eUser.printStackTrace();
	    	}finally{
	    		coll.close();
	    	}
		
		return bRet;
	}
	private boolean createUser(String user_login,String fname, String lname,String email,String grps)throws Exception{
		boolean bRet = false;
	    try{
	    	
	    	String strDefaultGrp = this.getDefaultMappedGroup(grps);
	    	IDfUser user = (IDfUser) session.newObject("dm_user");
		    user.setUserName(fname.trim()+ " " +lname.trim());
		    user.setUserLoginName(user_login.trim());
		    user.setUserPassword("p@ssw0rd");
		    user.setUserAddress(email.trim());
		    user.setDescription(grps);
		    user.setUserSourceAsString("inline password");
		    user.setClientCapability(2);
		    user.setHomeDocbase(m_docbase);
		    user.setFailedAuthenticationAttempts(-1);
		    user.setUserGroupName(strDefaultGrp.trim());
		    user.setUserPrivileges(IDfUser.DF_PRIVILEGE_NONE);
		    user.setUserXPrivileges(32);
		    user.setWorkflowDisabled(false);
		    user.save();	 
		    writeToLog("{UserSynchMethod}{createUser}User Created {" + user_login + "}",debug,null);
			
		}catch(Exception eUser){
			writeToLog("{UserSynchMethod}{createUser} Error while creating or save user",error,null);
			eUser.printStackTrace();
			writeToLog("{UserSynchMethod}{createUser} Trying creating using Query now",error,null);
			bRet = createUserByQuery(user_login,fname, lname,email,grps);
			writeToLog("{UserSynchMethod}{createUser} User Created Flag" + bRet,error,null);
		}
	    // Add into multiple group if
	    try{
	    	bRet = this.updateUser(user_login,fname, lname,email,grps);
	    }catch(Exception eUserMultiple){
	    	writeToLog("{UserSynchMethod}{createUser} Error adding user to multiple groups",error,null);
	    }
		return bRet;
	}
	private boolean createUserByQuery(String user_login,String fname, String lname,String email,String grps)throws Exception{
		boolean bRet = false;
		StringBuffer strBuf = null;
		String strDefaultGrp = this.getDefaultMappedGroup(grps);
		strBuf = new StringBuffer("create dm_user object ");
		strBuf.append("set user_name='");
		strBuf.append((fname != null ?fname.trim():""));
		strBuf.append(" ");
		strBuf.append((lname != null ?lname.trim():""));
		strBuf.append("' ");	
		strBuf.append("set description='");
		strBuf.append(grps);
		strBuf.append("' ");
		strBuf.append("set user_address='");
		strBuf.append(email.trim());
		strBuf.append("' ");
		strBuf.append("set home_docbase='");
		strBuf.append(m_docbase);
		strBuf.append("' ");
		strBuf.append("set acl_domain='");
		strBuf.append("dm_dbo");
		strBuf.append("' ");		
		strBuf.append("set user_group_name='");
		strBuf.append(strDefaultGrp);
		strBuf.append("' ");	
		strBuf.append("set user_login_name='");
		strBuf.append(user_login); 
		strBuf.append("' ");
		strBuf.append("set user_source= 'inline password'");
		strBuf.append(" set user_password= 'p@ssw0rd'");
		strBuf.append(" set client_capability=2 ");
		strBuf.append("set user_privileges=0 ");
		strBuf.append("set user_xprivileges=32 ");
		strBuf.append("set workflow_disabled=0 ");
		
		
    	IDfCollection coll = null;
    			
    	try{	    	
    		IDfQuery query = new DfQuery();
    		String qry = strBuf.toString();
    		writeToLog("{UserSynchMethod}{createUserByQuery}Query {" + qry + "}",debug,null);	
    		query.setDQL(qry);
    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UserSynchMethod}{createUserByQuery}User Created {" + user_login + "}",debug,null);
    		}else{
    			writeToLog("{UserSynchMethod}{createUserByQuery}Unable to create user {" + user_login + "}",debug,null);
    		}
    	}catch(Exception eUser){
    		writeToLog("{UserSynchMethod}{createUserByQuery} Error",error,null);
    		eUser.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
	}
	private void readParamsAndValidate(Map<String, String[]> params) throws DfException {
		writeToLog("{UserSynchMethod}{readParamsAndValidate} Start",debug,null);

		Set<String> keys = params.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if ((key == null) || (key.length() == 0)) {
				continue;
			}
			writeToLog("{UserSynchMethod}{createSession}Received Key  "+key,debug,null);
			String[] value = (String[]) params.get(key);

			if (key.equalsIgnoreCase(USER_KEY)) {
				m_userName = (value.length > 0) ? value[0] : "";
				writeToLog("m_userName : " + m_userName,debug,null);
			} else if (key.equalsIgnoreCase(DOCBASE_KEY)) {
				m_docbase = (value.length > 0) ? value[0] : "";
				writeToLog("m_docbase  : " + m_docbase,debug,null);
			} else if (key.equalsIgnoreCase(TICKET_KEY)) {
				m_ticket = (value.length > 0) ? value[0] : "";
				writeToLog("m_ticket  : " + m_ticket,debug,null);
			} else if (key.equalsIgnoreCase("password")){
				m_ticket = value[0];
				writeToLog("m_ticket  : " + m_ticket,debug,null);
			}
	 
		}// end of while
			
		writeToLog("{UserSynchMethod}{readParamsAndValidate}End",debug,null);
	}
	
	private IDfSession createSession(String loginName, String password,	String domain, String docbaseName) throws DfException {

		writeToLog("{UserSynchMethod}{createSession}Creating Session for {"+ loginName + "}",debug,null);

		try{
		IDfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();

		sMgr = client.newSessionManager();
		IDfLoginInfo loginInfoObj = clientx.getLoginInfo();

		loginInfoObj.setUser(loginName);
		loginInfoObj.setPassword(password);

		if (domain != null) {
			loginInfoObj.setDomain(domain);
		}

		sMgr.setIdentity(docbaseName, loginInfoObj);

		session = sMgr.getSession(docbaseName);
		writeToLog("{UserSynchMethod}{createSession}Session id {"+ session.getSessionId() + "}",debug,null);
		} catch (DfException e) { 
			writeToLog("{UserSynchMethod}{createSession}Error from  createSession(...) method. Cannot continue further.",error,null);
			writeToLog(e.getMessage(),error,null);
			throw e;
		}
		
		writeToLog("{UserSynchMethod}{createSession}Session Successful for {"+ loginName + "}",debug,null);
		return session; // returns a IDfSession Object
	}	
	private void releaseSession(){
		try{
			if(sMgr != null){
				if(session != null && session.isConnected()){
					String strSessionId = session.getSessionId();
					sMgr.release(session);
					writeToLog("{UserSynchMethod}{releaseSession}Session Released id {"+ strSessionId + "}",debug,null);
				}else{
					writeToLog("{UserSynchMethod}{createSession}Session Null",debug,null);
				}
			}else{
				writeToLog("{UserSynchMethod}{createSession}Session Manager Null",debug,null);
			}
		}catch(DfException dfe){
			dfe.printStackTrace();
		}
	}
	
	private String getDefaultMappedGroup(String strNabGrp)throws Exception{
		writeToLog("{UserSynchMethod}{getDefaultMappedGroup} Group {"+strNabGrp + "}",debug,null); 
		String strRet = null;
		if(m_GroupMap == null){ 
			m_GroupMap = this.fillMap();
		}
		if(m_GroupMap.size() >0){
			if(m_GroupMap.containsKey(strNabGrp.trim())){
				String strTmp = (String)m_GroupMap.get(strNabGrp.trim());
				int iFound = strTmp.indexOf("|");
				writeToLog("{UserSynchMethod}{getDefaultMappedGroup} String found index {"+iFound + "}",debug,null); 
				if(iFound > 0){
					strRet = strTmp.substring(0,iFound);
				}else{
					strRet = strTmp;
									}
				writeToLog("{UserSynchMethod}{getDefaultMappedGroup} String found {"+strRet + "}",debug,null); 
			}else{
				writeToLog("{UserSynchMethod}{getDefaultMappedGroup} *** FATAL ERROR - NO mapping found  for {"+ 
							strNabGrp + "} Please check RG table has values",error,null);
			}
		}else{
			writeToLog("{UserSynchMethod}{getDefaultMappedGroup} *** FATAL ERROR - NO mapping found " +
						", Please check RG table has values",error,null);
		}
		return strRet.trim();
	}
	private boolean isValidDay(){
		boolean bRet = false;
		try{
			Calendar calToday = Calendar.getInstance();
			int iSwitch = calToday.get(Calendar.DAY_OF_WEEK);
			writeToLog("{UserSynchMethod}{getToday} Today {" + iSwitch+ "}",debug,null);
			// test only
			//iSwitch = 6;
			if(iSwitch ==1 || iSwitch == 7){
				writeToLog("{UserSynchMethod}{getToday} Return false{" + iSwitch+ "}",debug,null);
				bRet = false;
			}else{
				writeToLog("{UserSynchMethod}{getToday} Return true{" + iSwitch+ "}",debug,null);
				bRet = true;
			}
		
		}catch(Exception eToday){
			writeToLog("{UserSynchMethod}{getToday} Error Getting Date{" + eToday.getMessage() + "}",error,null);
			eToday.printStackTrace();
		}	
		return bRet;
	}
	
	private String getOtherGrpString(String grps){
		String strRet = null;
		try{
			if(grps != null){
				StringTokenizer st = new StringTokenizer(grps, "|");				 
				boolean bFirst = true;
				while (st.hasMoreElements()) {
					String tmpToken = st.nextToken().trim();					
					if(!tmpToken.isEmpty()){
						if(bFirst){
							strRet = "'" + tmpToken + "',";
							bFirst = false;
						}else{
							strRet = strRet + "'" + tmpToken + "',";
						}
					}
				}// end of while
				// remove comma at the end 
				if(!bFirst){
					int ilength = strRet.length();
					strRet = strRet.substring(0,ilength-1);
				}
			}

			
		}catch(Exception eFormat){
			eFormat.printStackTrace();
		}
		
		return strRet;
	}
	private void writeToLog(String message,int level,Throwable e) {
		if(level==debug){
			DfLogger.debug(this, message+"\n", null, e);}
		if(level==error){
			DfLogger.error(this, message+"\n", null, e);}
		if(level==warning){
			DfLogger.warn(this, message+"\n", null, e);}
		
		System.out.println(message+"\n");
		if (m_writer != null) {
			m_writer.print(message+"\n");
		}
	}
	
	public static void main_t(String[] args) {
		try {

			UserSynchMethod handler = new UserSynchMethod();
			System.out.println(handler.getOtherGrpString(" "));
		} catch (Exception e) {
			System.out.println("Exception Message is " + e.getMessage());
		}

	}
	public static void main(String[] args) {
		try {
			UserSynchMethod handler = new UserSynchMethod();
			HashMap<String, String[]> map = new HashMap<String, String[]>();
			map.put(UserSynchMethod.USER_KEY,
					new String[] { "dmadmin" });
			map.put(UserSynchMethod.DOCBASE_KEY,
					new String[] { "NAB" });
			map.put(UserSynchMethod.TICKET_KEY,
					new String[] { "dm@dmin01" });
			OutputStream oStream = new FileOutputStream("C:\\test\\userSynch.log");
			PrintWriter writer=new PrintWriter(oStream);			
			handler.execute(map, writer);

		} catch (Exception e) {
			System.out.println("Exception Message is " + e.getMessage());
		}

	}

}
