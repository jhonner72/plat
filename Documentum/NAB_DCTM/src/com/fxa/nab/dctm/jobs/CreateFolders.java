package com.fxa.nab.dctm.jobs;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.GregorianCalendar;
import java.util.Set;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.methodserver.IDfMethod;

/**
 * Author: Yogesh Jankin
 * Project: NAB Cheques
 *
 * Objective: Create the folders for years, months and days at multiple locations inside repository to store the vouchers, reports etc., processed on those date. The folders
 * will be created for the next 30 days (including the day the job is running).
 * 
 * Algorithm:
 * #) Read the default session related params and in addition, get the following parameters from Documentum:
 * 	 @daysFolderPathsSepByComma - Folders for which year, month and days should be created.
 *   @monthFolderPathSepByComma - Folders for which year and month should be created.
 *   @startingDateAsYYYYMMDD - Optional, date as String from which the folder will be created for the next 30 days. 
 *   
 * #) For given @daysFolderPathsSepByComma, create folder structure accordingly.
 *   
 */	

public class CreateFolders implements IDfModule, IDfMethod {
	
//	public static final int MAX_DAYS=31; //The noOfDays cannot be greater than MAX_DAYS. And this variable has nothing to do with days of month and can be any number.

	public static final String USER_KEY = "user_name";
	public static final String DOCBASE_KEY = "docbase_name";
	public static final String TICKET_KEY = "ticket";
	
	//custom value
	

	// Default values
	private static String FOLDER_TYPE_DEFAULT = "fxa_folder";
	private static String ACL_DOMAIN_DEFAULT = "dm_dbo";
	private static int NO_OF_DAYS=31;
	private static int NO_OF_MONTHS=3;
	
	public static final int debug=0;
	public static final int error=1;
	public static final int warning=2;

	private static final String PATH_SEP = "/";	
	
	private IDfSession session=null;
	private IDfSessionManager sMgr=null;
	
	// Input Params from Server...
	private String m_docbase = null;
	private String m_userName = null;
	private String m_ticket = null;
	private PrintWriter m_writer = null;
	
	//Custom params
	private String daysFolderPathsSepByComma=null;
	private String monthFolderPathSepByComma=null;
	private String startingDateAsYYYYMMDD=null;
//	private int noOfDays;
	private String folderObjType=null; //default value. It can be overridden by the parameter passed
	private String folderACLName=null;
	private String folderACLDomain=null;
	boolean aclParamsProvided=false;
	IDfACL folderACLObj=null;
	
	
	@Override
	public int execute(Map arg_map, PrintWriter arg_writer) throws Exception {
		// TODO Auto-generated method stub
		m_writer=arg_writer;
		this.readParamsAndValidate(arg_map);
		this.createSession(m_userName, m_ticket, null, m_docbase);
	    this.processFolders(daysFolderPathsSepByComma,monthFolderPathSepByComma,startingDateAsYYYYMMDD);
		return 0;
	}

	
	private void readParamsAndValidate(Map<String, String[]> params) throws DfException {
		writeToLog("Inside readParams(Map<String,String[]> params) method.......",debug,null);

		Set<String> keys = params.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			if ((key == null) || (key.length() == 0)) {
				continue;
			}
			writeToLog("Received Key  "+key,debug,null);
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
			else if (key.equalsIgnoreCase("daysFolderPathsSepByComma")) {
				daysFolderPathsSepByComma = (value.length > 0) ? value[0] : null;
			} else if (key.equalsIgnoreCase("monthFolderPathSepByComma")) {
				monthFolderPathSepByComma = (value.length > 0) ? value[0] : null;
			} else if (key.equalsIgnoreCase("startingDateAsYYYYMMDD")) {
				startingDateAsYYYYMMDD = (value.length > 0) ? value[0] : null;
			} else if (key.equalsIgnoreCase("folderACLName")) {
				folderACLName = (value.length > 0) ? value[0] : null;
			} else if (key.equalsIgnoreCase("folderACLDomain")) {
				folderACLDomain = (value.length > 0) ? value[0] : null;
			} else if (key.equalsIgnoreCase("folderObjType")) {
				folderObjType = (value.length > 0) ? value[0] : null;
			} 
		}// end of while
		
		boolean isDaysFolderPathsSepByCommaEmpty=false;
		boolean isMonthFolderPathSepByCommaEmpty=false;
		
		if(daysFolderPathsSepByComma == null || daysFolderPathsSepByComma.trim().length() == 0){
			writeToLog("param 'daysFolderPathsSepByComma' is either null or empty string.", warning,null);
			isDaysFolderPathsSepByCommaEmpty=true;
		}else{
			writeToLog("param 'daysFolderPathsSepByComma' : "+daysFolderPathsSepByComma,debug,null);
		}
		
		
		if(monthFolderPathSepByComma == null || monthFolderPathSepByComma.trim().length() == 0){
			writeToLog("param 'monthFolderPathSepByComma' is either null or empty string.", warning,null);
			isMonthFolderPathSepByCommaEmpty=true;
		}else{
			writeToLog("param 'monthFolderPathSepByComma' : "+monthFolderPathSepByComma,debug,null);
		}
		
		if(isDaysFolderPathsSepByCommaEmpty && isMonthFolderPathSepByCommaEmpty){
			writeToLog("Mandatory parameters 'monthFolderPathSepByComma' and 'daysFolderPathsSepByComma' were not provided. Cannot continue further.",error,null);
			throw new DfException("Mandatory parameters 'monthFolderPathSepByComma' and 'daysFolderPathsSepByComma' are either empty or not provided. Cannot continue further.");			
		}
		
		if(startingDateAsYYYYMMDD == null || startingDateAsYYYYMMDD.trim().length() == 0){
			writeToLog("param 'startingDateAsYYYYMMDD' is either null or empty string. Today's date will be used for creating folders.", warning,null);
		}else{
			writeToLog("param 'startingDateAsYYYYMMDD' : "+startingDateAsYYYYMMDD,debug,null);
		}		
		
		if(folderACLName == null || folderACLName.trim().length() == 0){
			writeToLog("ACL to set for folders is not provided. ACL will NOT be set for folders 'explicitly'. It will inherit from its parent folder.",debug,null);			
		}else{
			writeToLog("ACL to set for folders is :"+folderACLName,debug,null);
			aclParamsProvided=true;
			writeToLog("aclParamsProvided is set to true",debug,null);
		}
		
		if(folderACLDomain == null || folderACLDomain.trim().length() == 0){
			writeToLog("ACL domain not provided. Setting '"+ACL_DOMAIN_DEFAULT+"' as the domain",warning,null);
			folderACLDomain=ACL_DOMAIN_DEFAULT;
		}else{
			writeToLog("ACL domain is :"+folderACLDomain,debug,null);
		}

		if(folderObjType == null || folderObjType.trim().length() == 0){
			folderObjType= FOLDER_TYPE_DEFAULT;
			writeToLog("Default Value "+FOLDER_TYPE_DEFAULT+" set for folderObjType as it is not provided or empty",debug,null);			
		}else{
			writeToLog("param 'folderObjType' : "+folderObjType,debug,null);
		}
		
		writeToLog("Leaving readParams(Map<String,String[]> params) method.......",debug,null);
	}

	private IDfSession createSession(String loginName, String password,	String domain, String docbaseName) throws DfException {

		writeToLog("Inside createSession method.......",debug,null);

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
		} catch (DfException e) { 
			writeToLog("Error from  createSession(...) method. Cannot continue further.",error,null);
			writeToLog(e.getMessage(),error,null);
			throw e;
		}
		
		writeToLog("returning from createSession method.......",debug,null);
		return session; // returns a IDfSession Object
	}	

	private boolean aclExist() throws DfException{
		
	try{
		if(folderACLName != null && folderACLDomain != null){
			
			  folderACLObj=session.getACL(folderACLDomain,folderACLName);
			  if(folderACLObj != null){
					writeToLog("ACL for the folder is set as "+folderACLName+" and the ACL domain is "+folderACLDomain+".",debug,null);
					return true;
				}else{
					writeToLog("ERROR : There is no ACL with the name "+folderACLName+" and domain "+folderACLDomain+".",debug,null);
					return false;
				}
			}
		return false;
	 }catch(DfException e){
			writeToLog("Exception from method aclExist(). Exception message is "+e.getMessage(),error,null);
			throw e;
	 }		
	}
	
	private void processFolders(String daysFolderPathsSepByComma,String monthFolderPathSepByComma,String startingDateAsYYYYMMDD) throws DfException{
		writeToLog("Inside processFolders",debug,null);
		String[] dayFolders={};
		String[] monthOnlyFolders={};
		int year,month,day;
		GregorianCalendar startingDate;

		try{
		//check if we have a valid ACL for the given ACL parameters, if provided
		if(aclParamsProvided){
			if(!aclExist()){
				String errorMessage="ERROR: ACL doesn't exist for the given ACL Parameters, ACL Name : "+folderACLName+" and ACL Domain : "+folderACLDomain+". Cannot proceed further...";
				writeToLog(errorMessage,error,null);
				throw new DfException(errorMessage);
			}
		 }else{
			 writeToLog("Skipping ACL Exist test as ACL Params are not provided",debug,null); 
		 }
		 }	catch(DfException e){
			writeToLog("Exception from method processFolders(). Exception message is "+e.getMessage(),error,null);
			throw e;
		}
		
		if(daysFolderPathsSepByComma != null && daysFolderPathsSepByComma.trim().length() != 0)
		dayFolders=daysFolderPathsSepByComma.split(",");
		
		writeToLog("dayFolders.length  : "+dayFolders.length,debug,null);		

		if(monthFolderPathSepByComma != null && monthFolderPathSepByComma.trim().length() != 0)
		monthOnlyFolders=monthFolderPathSepByComma.split(",");

		writeToLog("monthFolders.length  : "+monthOnlyFolders.length,debug,null);
		
		//get the date parameter, if null, then create it with today's date
		if(startingDateAsYYYYMMDD != null && startingDateAsYYYYMMDD.trim().length() == 8){
		  try{
			year= Integer.parseInt(startingDateAsYYYYMMDD.substring(0,4));
			this.writeToLog("Year is "+year, debug, null);
			
			month=Integer.parseInt(startingDateAsYYYYMMDD.substring(4,6));
			this.writeToLog("Month is "+month, debug, null);
			
			day=Integer.parseInt(startingDateAsYYYYMMDD.substring(6));
			this.writeToLog("Day is "+month, debug, null);
			
			startingDate=new GregorianCalendar(year,month-1,day);
		  }catch(Exception e){
				writeToLog("Invalid Date paramater "+startingDateAsYYYYMMDD,warning,null);
				writeToLog("Using Today's date as the starting date.",debug,null);
				startingDate=new GregorianCalendar();
		   }
			
		}else{
			writeToLog("Starting Date 'startingDateAsYYYYMMDD' is either not provided or invalid : "+startingDateAsYYYYMMDD,debug,null);
			writeToLog("Using Today's date as the starting date.",debug,null);
			startingDate=new GregorianCalendar();
		}
		
		writeToLog("Starting Date is "+formatDate(startingDate),debug,null);
		
		writeToLog("\n\n\nProcessing dayfolders.....",debug,null);
		
		for(int i=0;i < NO_OF_DAYS;i++){
			
			String yearFolder=Integer.toString(startingDate.get(Calendar.YEAR));
			
			String monthFolder=Integer.toString(startingDate.get(Calendar.MONTH)+1);
			if(startingDate.get(Calendar.MONTH)+1 < 10){
				monthFolder="0"+monthFolder;
			}		
			
			String dayFolder=Integer.toString(startingDate.get(Calendar.DAY_OF_MONTH));
			if(startingDate.get(Calendar.DAY_OF_MONTH) < 10){
				dayFolder="0"+dayFolder;
			}			
			
			for(String parentFolder : dayFolders){ 			
				checkExistenseOfFolders(parentFolder,yearFolder,monthFolder,dayFolder,true);			
			}
			
			startingDate.add(Calendar.DAY_OF_MONTH, 1);		
			writeToLog("\n The date after adding one day is "+formatDate(startingDate),debug,null);
		}
		
		
		
		//Removing the NO_OF_DAYS to go back to the start for processing month
		startingDate.add(Calendar.DAY_OF_MONTH, -NO_OF_DAYS);
		writeToLog("\n The date after subtracting "+NO_OF_DAYS+" is "+formatDate(startingDate),debug,null);
		
		
		writeToLog("\n\n\nProcessing monthOnlyFolders.....",debug,null);	
		
		for(int i=0;i < NO_OF_MONTHS;i++){	
			
			String yearFolder=Integer.toString(startingDate.get(Calendar.YEAR));
			
			String monthFolder=Integer.toString(startingDate.get(Calendar.MONTH)+1);
			if(startingDate.get(Calendar.MONTH)+1 < 10){
				monthFolder="0"+monthFolder;
			}		
			
			String dayFolder=Integer.toString(startingDate.get(Calendar.DAY_OF_MONTH));
			if(startingDate.get(Calendar.DAY_OF_MONTH) < 10){
				dayFolder="0"+dayFolder;
			}
			
			for(String parentFolder : monthOnlyFolders){ 
				
				checkExistenseOfFolders(parentFolder,yearFolder,monthFolder,dayFolder,false);}
			
			startingDate.add(Calendar.MONTH, 1);	
			writeToLog("The date after adding one month is "+formatDate(startingDate),debug,null);			
		}
	}
	
	
	private void checkExistenseOfFolders(String parentFolderPath,String yearFolder,String monthFolder,String dayFolder,boolean isDayFoldersRequired){
		
		try{
			if(parentFolderPath == null || parentFolderPath.trim().length() == 0){
			writeToLog("No folder exist with the given path "+parentFolderPath,error,null);
			return;
			}
			
			//get the parentFolder object. 
			IDfFolder parentFolderObj = (IDfFolder) session.getFolderByPath(parentFolderPath);
			
			//Log error if not and return
			if(parentFolderObj == null){
			writeToLog("No folder exist with the given path "+parentFolderPath,error,null);
			return;
			}
			
			//try to get parentFolderPath+yearfolder.
			String yearFolderPath=parentFolderPath.endsWith(PATH_SEP)?parentFolderPath+yearFolder:parentFolderPath+PATH_SEP+yearFolder;
			
			IDfFolder yearFolderPathObj= (IDfFolder) session.getFolderByPath(yearFolderPath);
			if(yearFolderPathObj == null){
				//if not exist, create it
				writeToLog("The folder with year "+yearFolderPath+" doesn't exist. Creating it.",debug,null);
				createFolder(parentFolderPath,yearFolder);
			}else{
				//if exist, proceed
				writeToLog("The folder with year "+yearFolderPath+" already exist.",debug,null);
			}
			
	
			//try to get yearFolderPath+monthFolder.
			String monthFolderPath=yearFolderPath+PATH_SEP+monthFolder;
			IDfFolder monthFolderPathObj= (IDfFolder) session.getFolderByPath(monthFolderPath);
			if(monthFolderPathObj == null){
				//if not exist, create it
				writeToLog("The folder with year "+monthFolderPath+" doesn't exist. Creating it.",debug,null);
				createFolder(yearFolderPath,monthFolder);
			}else{
				//if exist, proceed
				writeToLog("The folder with year "+monthFolderPath+" already exist.",debug,null);
			}		
	
			if(isDayFoldersRequired){
				//try to get monthFolderPath+dayFolder.
				String dayFolderPath=monthFolderPath+PATH_SEP+dayFolder;
				IDfFolder dayFolderPathObj= (IDfFolder) session.getFolderByPath(dayFolderPath);
				if(dayFolderPathObj == null){
					//if not exist, create it
					writeToLog("The folder with year "+dayFolderPath+" doesn't exist. Creating it.",debug,null);
					createFolder(monthFolderPath,dayFolder);
				}else{
					//if exist, proceed
					writeToLog("The folder with year "+dayFolderPath+" already exist.",debug,null);
				}
			}
		
		}catch(DfException e){
			writeToLog("Exception from 'checkExistenseOfFolders(....)' method. Exception message is : \n "+e.getMessage(), error,null);
		}
						
	}	
	
	
	private void createFolder(String parentFolder,String folderToBeCreated){
		
		try{
				IDfFolder folder = (IDfFolder) session.newObject(folderObjType);
				folder.setObjectName(folderToBeCreated);
				
				if(this.aclParamsProvided && folderACLObj != null){
				 folder.setACL(folderACLObj);				
				}
				  
				folder.link(parentFolder);				
				folder.save();
				writeToLog(folderToBeCreated + " created under " + parentFolder,debug,null);
		}catch(DfException e){
			writeToLog("Exception from 'createFolder(...)' method. Exception message is : \n "+e.getMessage(), error,null);
		}
		
	}
	
	private String formatDate(java.util.GregorianCalendar date){
		String month=Integer.toString(date.get(Calendar.MONTH)+1);
		if(date.get(Calendar.MONTH)+1 < 10){
			month="0"+month;
		}
		String day=Integer.toString(date.get(Calendar.DAY_OF_MONTH));
		if(date.get(Calendar.DAY_OF_MONTH) < 10){
			day="0"+day;
		}
		return date.get(Calendar.YEAR)+PATH_SEP+month+PATH_SEP+day+"(yyyy/mm/dd)";
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
	
	
	public static void main(String[] args) {
		try {
			CreateFolders handler = new CreateFolders();
			HashMap<String, String[]> map = new HashMap<String, String[]>();
			map.put(CreateFolders.USER_KEY,
					new String[] { "NABDCPECM_SVC_E" });
			map.put(CreateFolders.DOCBASE_KEY,
					new String[] { "NAB" });
			map.put(CreateFolders.TICKET_KEY,
					new String[] { "N@BDCP3CM_SVC_3" });
//			map.put("daysFolderPathsSepByComma",
//					new String[] { "/Inbound Dishonours/Dishonour Letters" });
			map.put("daysFolderPathsSepByComma",new String[] { "/Vouchers" });
//			map.put("monthFolderPathSepByComma",
//					new String[] { "/Dishonours/Dishonour Logs,/Dishonours/Dishonour Reports,/Dishonours/Mailhouse File,/Dishonours/Processing Reports" });
			map.put("startingDateAsYYYYMMDD",new String[]{"20160501"}); 
			map.put("folderObjType", new String[]{"fxa_folder"});
			map.put("folderACLName", new String[]{"fxa_voucher_folder_acl"});
//			map.put("folderACLDomain", new String[]{"dm_dbo"});

//			DA Parameters -folderObjType "fxa_folder" -monthFolderPathSepByComma  "/Dishonours/Dishonour Logs,/Dishonours/Dishonour Reports,/Dishonours/Mailhouse Archive,/Dishonours/Processing Reports"
// 			-daysFolderPathsSepByComma "/Dishonours/Dishonour Letters" -docbase_name "NAB" -user_name "NABDctm"
			OutputStream oStream = new FileOutputStream("C:\\output\\ServMeth_log.txt");
			PrintWriter writer=new PrintWriter(oStream);
			
			handler.execute(map, writer);

		} catch (Exception e) {
			System.out.println("Exception Message is " + e.getMessage());
		}

	}

}
