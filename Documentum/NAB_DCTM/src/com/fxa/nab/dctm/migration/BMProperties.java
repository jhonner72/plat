package com.fxa.nab.dctm.migration;

import java.util.HashMap;
import java.util.ResourceBundle;

public class BMProperties {

	public static boolean SERVICE_ON=false;
	
//	public static final String PROP_FILE="ingestion";
	public static final String batch_per_cycle="50";
	public static final String CHECKSUM_TYPE="MD5";
	
	public static final int NO_OF_COLS_IN_CSV=20;
	
	public static final String PATH_SEP="\\";	
	public static final String DCTM_PATH_SEP="/";
	
	public static final String DCTM_INGEST_STATUS="COMPLETED"; //shouldn't be configurable 	

	public static final String docbroker_host_key="docbroker_host";
	public static final String docbroker_port_key="docbroker_port";	
	public static final String login_user_key="dctm_login_user";
	public static final String password_encrypted_key="dctm_password_encrypted";	
	public static final String repository_name_key="repository_name";

	public static final String target_dctm_location_key="target_dctm_location";
	public static final String doc_type_key="doc_object_type";
	public static final String folder_type_key="folder_object_type";
	public static final String folder_acl_key="folder_acl_name";
	public static final String folder_acl_domain_key="folder_acl_domain";	
	public static final String doc_acl_key="doc_acl_name";
	public static final String doc_acl_domain_key="doc_acl_domain";
	
	public static final String dbinstancename_key="dbinstancename";
	public static final String dbname_key="dbname";
	public static final String dbport_key="dbport";
	public static final String dbusername_key="dbusername";
	public static final String dbpassword_key="dbpassword";
	
	public static final String batch_pickup_query_key="batch_pickup_query";
	public static final String batch_pickup_status_key="batch_pickup_status";
	public static final String batch_pickup_reconcile_status_key="batch_pickup_reconcile_status";
	public static final String batch_upload_complete_status_key="batch_upload_complete_status";
	public static final String batch_upload_fail_status_key="batch_upload_fail_status";
	public static final String batch_upload_inprogress_status_key="batch_upload_inprogress_status";
	
//	public static final String log_location_key="log_location";
	public static final String log_dctm_location_key="log_dctm_location";	

	public static final String total_batch_to_process_key="total_batch_to_process";
	
	public static final String batch_status_to_process_key="batch_status_to_process";
	
	public static final String check_agency_bank_key="check_agency_bank"; ////story21701 - Yogesh Jankin
	public static final String targetEndPointsRequiringCredit_key="targetEndPointsRequiringCredit";//story21701 - Yogesh Jankin
	
	
	public static final String AGENCY_BANK_BSBS="122-123-124-125-126-127-180-181-182-183-184-185-186-187-188-189-917-211-212-213-214-215-216-217-218-219-915-240-241-" +
			"242-243-244-245-246-247-248-249-801-802-803-804-805-806-807-512-514-313-642-812-813-814-815-817";//story21701 - Yogesh Jankin
			/* Voucher Transfer object not required for BSBs with NAB as target end point -340-341-342-343-344-345-346-347-348-349-452-453-" +
			"200-201-202-203-204-205-206-207-208-209-610-633-639-653-819-942-952-985";//story21701 - Yogesh Jankin */
	
	public static String targetEndPointsRequiringCredit=null; //story21701 - Yogesh Jankin
	
	public static final String voucher_transfer_object_type="fxa_voucher_transfer";//story21701 - Yogesh Jankin
	
	public static final String CSV_SEP=",";
	public static final String CSV_EXT=".csv";	
	
	public static String docbroker_host=null;
	public static String docbroker_port=null;
	public static String dctm_login_user=null;
	public static String dctm_password_encrypted=null;	
	public static String repository_name=null;

	public static String target_dctm_location=null;
	public static String doc_object_type=null;
	public static String folder_object_type=null;
	
	public static String folder_acl_name=null;	
	public static String folder_acl_domain=null;	
	public static String doc_acl_name=null;	
	public static String doc_acl_domain=null;
	
	public static String dbinstancename=null;
	public static String dbname=null;
	public static String dbport=null;	
	public static String dbusername=null;	
	public static String dbpassword=null;
	
	public static String batch_pickup_query=null;	
	public static String batch_pickup_status=null;
	public static String batch_pickup_reconcile_status=null;
	public static String batch_upload_complete_status=null;
	public static String batch_upload_fail_status=null;
	public static String batch_upload_inprogress_status=null;
	public static boolean check_agency_bank=false;  //story21701 - Yogesh Jankin
	
//	public static String log_location=null;
	public static String log_dctm_location=null;

	public static String total_batch_to_process=null;
	
	public static HashMap<String, String> agencyBsbsTargetEndpointsMapping=new HashMap<String, String>(); //story21701 - Yogesh Jankin

	
	private static String total_batch_to_process_default="200";
	private static String dbport_default="1433";
	private static String log_dctm_location_default="/Temp";
	private static String batch_pickup_query_default="select top %d ManifestID,Day,Entry,OutputPath,Created,BatchName from Manifest where Status='%s' order by ManifestID";
	private static String batch_pickup_status_default="USB-PAYLOAD-INGESTION-COMPLETE";
	private static String batch_pickup_reconcile_status_default="USB-PAYLOAD-RECONCILE-COMPLETE";	
	private static String batch_upload_complete_status_default="USB-DCTM-INGESTION-COMPLETE";
	private static String batch_upload_fail_status_default="USB-DCTM-INGESTION-FAILED";
	private static String batch_upload_inprogress_status_default="USB-DCTM-INGESTION-INPROGRESS";
	private static boolean check_agency_bank_default=false; //story21701 - Yogesh Jankin
	private static String targetEndPointsRequiringCredit_default="BQL-CIT-ARB-CUS"; //story21701 - Yogesh Jankin
	
	
	public static ResourceBundle loadPropsFileAndLogLocation(String propFile) throws Exception{

		//DON'T USE COMMONUTILS INSIDE THIS METHOD!!!		
		ResourceBundle bundle=ResourceBundle.getBundle(propFile);
		if(bundle == null ){			
			String msg="ERROR!!! No properties file found with the given name '"+propFile+".properties'. Cannot proceed further.";
			System.out.println(msg);
			return null;
		}
		return bundle;
	}
	
	public static boolean readProps(ResourceBundle bundle,String propFile) throws FXAInitAndValidateException{
		boolean result=true;

		CommonUtils.writeToLog("Reading Properties File :"+propFile+".properties",CommonUtils.debug,null);
//		CommonUtils.writeToLog(log_location_key + " :  "+log_location,CommonUtils.debug,null);

		docbroker_host=bundle.getString(docbroker_host_key);
		if(docbroker_host == null || docbroker_host.trim().length() == 0){
			CommonUtils.writeToLog(docbroker_host_key +" doesn't have a value in the properties file. Note: Program will use the values from dfc.properties!!!",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(docbroker_host_key + " :  "+docbroker_host,CommonUtils.debug,null); }
		
		docbroker_port=bundle.getString(docbroker_port_key);
		if(docbroker_port == null || docbroker_port.trim().length() == 0){
			CommonUtils.writeToLog(docbroker_port_key +" doesn't have a value in the properties file. Note: Program will use the values from dfc.properties!!!",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(docbroker_port_key + " :  "+docbroker_port,CommonUtils.debug,null); }
		
		dctm_login_user=bundle.getString(login_user_key);
		if(dctm_login_user == null || dctm_login_user.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(login_user_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(login_user_key + " :  "+dctm_login_user,CommonUtils.debug,null); }
		
		
		dctm_password_encrypted=bundle.getString(password_encrypted_key);
		if( dctm_password_encrypted== null || dctm_password_encrypted.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(password_encrypted_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(password_encrypted_key + " :  "+dctm_password_encrypted,CommonUtils.debug,null); }
		
		
		repository_name=bundle.getString(repository_name_key);
		if(repository_name == null || repository_name.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(repository_name_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog( repository_name_key + " :  "+repository_name,CommonUtils.debug,null); }
		
		
		target_dctm_location=bundle.getString(target_dctm_location_key);
		if(target_dctm_location == null || target_dctm_location.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(target_dctm_location_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(target_dctm_location_key + " :  "+target_dctm_location,CommonUtils.debug,null); }

		
		doc_object_type=bundle.getString(doc_type_key);
		if(doc_object_type == null || doc_object_type.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(doc_type_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(doc_type_key + " :  "+doc_object_type,CommonUtils.debug,null); }
		
		
		folder_object_type=bundle.getString(folder_type_key);
		if(folder_object_type == null || folder_object_type.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(folder_type_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(folder_type_key + " :  "+folder_object_type,CommonUtils.debug,null); }
		
		folder_acl_name=bundle.getString(folder_acl_key);
		if(folder_acl_name == null || folder_acl_name.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(folder_acl_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(folder_acl_key + " :  "+folder_acl_name,CommonUtils.debug,null); }
		
		folder_acl_domain=bundle.getString(folder_acl_domain_key);
		if(folder_acl_domain == null || folder_acl_domain.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(folder_acl_domain_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(folder_acl_domain_key + " :  "+folder_acl_domain,CommonUtils.debug,null); }
		
		doc_acl_name=bundle.getString(doc_acl_key);
		if( doc_acl_name== null || doc_acl_name.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(doc_acl_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(doc_acl_key + " :  "+doc_acl_name,CommonUtils.debug,null); }
		
		doc_acl_domain=bundle.getString(doc_acl_domain_key);
		if(doc_acl_domain == null || doc_acl_domain.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(doc_acl_domain_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(doc_acl_domain_key + " :  "+doc_acl_domain,CommonUtils.debug,null); }
		
		dbinstancename=bundle.getString(dbinstancename_key);
		if(dbinstancename == null || dbinstancename.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog( dbinstancename_key+" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog( dbinstancename_key + " :  "+dbinstancename,CommonUtils.debug,null); }
		
		dbname=bundle.getString(dbname_key);
		if(dbname == null || dbname.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(dbname_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(dbname_key + " :  "+dbname,CommonUtils.debug,null); }
		
		dbport=bundle.getString(dbport_key);
		if(dbport == null || dbport.trim().length() == 0){
			dbport=dbport_default;
			CommonUtils.writeToLog(dbport_key +" doesn't have a value in the properties file. Default value "+dbport_default+" set for it.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(dbport_key + " :  "+dbport,CommonUtils.debug,null); }
		
		dbusername=bundle.getString(dbusername_key);
		if(dbusername == null || dbusername.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(dbusername_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(dbusername_key + " :  "+dbusername,CommonUtils.debug,null); }
		
		dbpassword=bundle.getString(dbpassword_key);
		if(dbpassword == null || dbpassword.trim().length() == 0){
			result=false;
			CommonUtils.writeToLog(dbpassword_key +" doesn't have a value in the properties file.",CommonUtils.error,null);
		}else{
		CommonUtils.writeToLog(dbpassword_key + " :  "+dbpassword,CommonUtils.debug,null); }
		
		
		batch_pickup_query=bundle.getString(batch_pickup_query_key);
		if(batch_pickup_query == null || batch_pickup_query.trim().length() == 0){
			batch_pickup_query=batch_pickup_query_default;
			CommonUtils.writeToLog(batch_pickup_query_key +" doesn't have a value in the properties file. Default value '"+batch_pickup_query_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_pickup_query_key + " :  "+batch_pickup_query,CommonUtils.debug,null); }		
		
		batch_pickup_status=bundle.getString(batch_pickup_status_key);
		if(batch_pickup_status == null || batch_pickup_status.trim().length() == 0){
			batch_pickup_status=batch_pickup_status_default;
			CommonUtils.writeToLog(batch_pickup_status_key +" doesn't have a value in the properties file. Default value '"+batch_pickup_status_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_pickup_status_key + " :  "+batch_pickup_status,CommonUtils.debug,null); }

		batch_pickup_reconcile_status=bundle.getString(batch_pickup_reconcile_status_key);
		if(batch_pickup_reconcile_status == null || batch_pickup_reconcile_status.trim().length() == 0){
			batch_pickup_reconcile_status=batch_pickup_reconcile_status_default;
			CommonUtils.writeToLog(batch_pickup_reconcile_status_key +" doesn't have a value in the properties file. Default value '"+batch_pickup_reconcile_status_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_pickup_reconcile_status_key + " :  "+batch_pickup_reconcile_status,CommonUtils.debug,null); }
		
		batch_upload_complete_status=bundle.getString(batch_upload_complete_status_key);
		if(batch_upload_complete_status == null || batch_upload_complete_status.trim().length() == 0){
			batch_upload_complete_status=batch_upload_complete_status_default;
			CommonUtils.writeToLog(batch_upload_complete_status_key +" doesn't have a value in the properties file. Default value '"+batch_upload_complete_status_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_upload_complete_status_key + " :  "+batch_upload_complete_status,CommonUtils.debug,null); }

		batch_upload_fail_status=bundle.getString(batch_upload_fail_status_key);
		if(batch_upload_fail_status == null || batch_upload_fail_status.trim().length() == 0){
			batch_upload_fail_status=batch_upload_fail_status_default;
			CommonUtils.writeToLog(batch_upload_fail_status_key +" doesn't have a value in the properties file. Default value '"+batch_upload_fail_status_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_upload_fail_status_key + " :  "+batch_upload_fail_status,CommonUtils.debug,null); }
		
		batch_upload_inprogress_status=bundle.getString(batch_upload_inprogress_status_key);
		if(batch_upload_inprogress_status == null || batch_upload_inprogress_status.trim().length() == 0){
			batch_upload_inprogress_status=batch_upload_inprogress_status_default;
			CommonUtils.writeToLog(batch_upload_inprogress_status_key +" doesn't have a value in the properties file. Default value '"+batch_upload_inprogress_status_default+"' is used.",CommonUtils.warn,null);
		}else{
		CommonUtils.writeToLog(batch_upload_inprogress_status_key + " :  "+batch_upload_inprogress_status,CommonUtils.debug,null); }
		
		log_dctm_location=bundle.getString(log_dctm_location_key);
		if(log_dctm_location == null || log_dctm_location.trim().length() == 0){
			log_dctm_location=log_dctm_location_default;
			CommonUtils.writeToLog(log_dctm_location_key +" doesn't have a value in the properties file. Default value "+log_dctm_location_default+" set for it.",CommonUtils.warn,null);
		}else {CommonUtils.writeToLog(log_dctm_location_key + " :  "+log_dctm_location,CommonUtils.debug,null);}
		
		
		total_batch_to_process=bundle.getString(total_batch_to_process_key);

		if(total_batch_to_process == null || total_batch_to_process.length() == 0){
			total_batch_to_process=total_batch_to_process_default;
			CommonUtils.writeToLog(total_batch_to_process_key +" doesn't have a value in the properties file. Default value "+total_batch_to_process_default+" set for it.",CommonUtils.warn,null);
		}
		
		
		try{
			Integer.parseInt(total_batch_to_process);
			
		}catch(NumberFormatException e){
			String message="Value for key '"+total_batch_to_process_key+"' is not an integer. Assigning default value...";
			CommonUtils.writeToLog(message,CommonUtils.warn,null);
			total_batch_to_process=total_batch_to_process_default;
		}
		
		CommonUtils.writeToLog("total_batch_to_process :"+total_batch_to_process,CommonUtils.debug,null);		
		
		/* start - Code added by Yogesh for 21701 - Agency Bank cheque*/
		String check_agency_bank_str=bundle.getString(check_agency_bank_key);

		if(check_agency_bank_str == null || check_agency_bank_str.length() == 0){
			check_agency_bank=false;
			CommonUtils.writeToLog(check_agency_bank_key +" doesn't have a value in the properties file. Default value "+check_agency_bank_default+" set for it.",CommonUtils.warn,null);
		}else if (check_agency_bank_str.equals("T")){
			check_agency_bank=true;
			CommonUtils.writeToLog("check_agency_bank :"+check_agency_bank,CommonUtils.debug,null);
		}else{
			check_agency_bank=false;
			CommonUtils.writeToLog("check_agency_bank :"+check_agency_bank,CommonUtils.debug,null);
		}
		
		
		targetEndPointsRequiringCredit=bundle.getString(targetEndPointsRequiringCredit_key);
		if(targetEndPointsRequiringCredit == null || targetEndPointsRequiringCredit.trim().length() == 0){
			targetEndPointsRequiringCredit=targetEndPointsRequiringCredit_default;
			CommonUtils.writeToLog(targetEndPointsRequiringCredit_key +" doesn't have a value in the properties file. Default value "+targetEndPointsRequiringCredit_default+" set for it.",CommonUtils.warn,null);
		}else {CommonUtils.writeToLog(targetEndPointsRequiringCredit_key + " :  "+targetEndPointsRequiringCredit,CommonUtils.debug,null);}		
		/* end - Code added by Yogesh for 21701 - Agency Bank cheque*/
	
		
		
		if(!result){
			CommonUtils.writeToLog("Cannot proceed further. Check the log file for error info",CommonUtils.error,null);
		}
		
		return result;		
	}
	
	
	/* start - Code added by Yogesh for 21701 - Agency Bank cheque*/
	public static HashMap<String, String> getBsbsTargetEndpointsMapping(){
		if(agencyBsbsTargetEndpointsMapping.size() == 0){
			return generateBsbsTargetEndpointsMapping();
		}else{
			return agencyBsbsTargetEndpointsMapping;
		}
	}
	
	
	private static HashMap<String, String> generateBsbsTargetEndpointsMapping(){
		agencyBsbsTargetEndpointsMapping.put("122", "BQL");
		agencyBsbsTargetEndpointsMapping.put("123", "BQL");
		agencyBsbsTargetEndpointsMapping.put("124", "BQL");
		agencyBsbsTargetEndpointsMapping.put("125", "BQL");
		agencyBsbsTargetEndpointsMapping.put("126", "BQL");
		agencyBsbsTargetEndpointsMapping.put("127", "BQL");
		agencyBsbsTargetEndpointsMapping.put("180", "MQG");
		agencyBsbsTargetEndpointsMapping.put("181", "MQG");
		agencyBsbsTargetEndpointsMapping.put("182", "MQG");
		agencyBsbsTargetEndpointsMapping.put("183", "MQG");
		agencyBsbsTargetEndpointsMapping.put("184", "MQG");
		agencyBsbsTargetEndpointsMapping.put("185", "MQG");
		agencyBsbsTargetEndpointsMapping.put("186", "MQG");
		agencyBsbsTargetEndpointsMapping.put("187", "MQG");
		agencyBsbsTargetEndpointsMapping.put("188", "MQG");
		agencyBsbsTargetEndpointsMapping.put("189", "MQG");
		agencyBsbsTargetEndpointsMapping.put("917", "ARB");
		agencyBsbsTargetEndpointsMapping.put("211", "JPM");
		agencyBsbsTargetEndpointsMapping.put("212", "JPM");
		agencyBsbsTargetEndpointsMapping.put("213", "JPM");
		agencyBsbsTargetEndpointsMapping.put("214", "JPM");
		agencyBsbsTargetEndpointsMapping.put("215", "JPM");
		agencyBsbsTargetEndpointsMapping.put("216", "JPM");
		agencyBsbsTargetEndpointsMapping.put("217", "JPM");
		agencyBsbsTargetEndpointsMapping.put("218", "JPM");
		agencyBsbsTargetEndpointsMapping.put("219", "JPM");
		agencyBsbsTargetEndpointsMapping.put("915", "JPM");
		agencyBsbsTargetEndpointsMapping.put("240", "CIT");
		agencyBsbsTargetEndpointsMapping.put("241", "CIT");
		agencyBsbsTargetEndpointsMapping.put("242", "CIT");
		agencyBsbsTargetEndpointsMapping.put("243", "CIT");
		agencyBsbsTargetEndpointsMapping.put("244", "CIT");
		agencyBsbsTargetEndpointsMapping.put("245", "CIT");
		agencyBsbsTargetEndpointsMapping.put("246", "CIT");
		agencyBsbsTargetEndpointsMapping.put("247", "CIT");
		agencyBsbsTargetEndpointsMapping.put("248", "CIT");
		agencyBsbsTargetEndpointsMapping.put("249", "CIT");
		agencyBsbsTargetEndpointsMapping.put("801", "CUS");
		agencyBsbsTargetEndpointsMapping.put("802", "CUS");
		agencyBsbsTargetEndpointsMapping.put("803", "CUS");
		agencyBsbsTargetEndpointsMapping.put("804", "CUS");
		agencyBsbsTargetEndpointsMapping.put("805", "CUS");
		agencyBsbsTargetEndpointsMapping.put("806", "CUS");
		agencyBsbsTargetEndpointsMapping.put("807", "CUS");
		agencyBsbsTargetEndpointsMapping.put("512", "CUS");
		agencyBsbsTargetEndpointsMapping.put("514", "CUS");
		agencyBsbsTargetEndpointsMapping.put("313", "CUS");
		agencyBsbsTargetEndpointsMapping.put("642", "CUS");
		agencyBsbsTargetEndpointsMapping.put("812", "CUS");
		agencyBsbsTargetEndpointsMapping.put("813", "CUS");
		agencyBsbsTargetEndpointsMapping.put("814", "CUS");
		agencyBsbsTargetEndpointsMapping.put("815", "CUS");
		agencyBsbsTargetEndpointsMapping.put("817", "CUS");
		/** story21701 - Yogesh Jankin
		Voucher Transfer object not required for BSBs with NAB as target end point -
		agencyBsbsTargetEndpointsMapping.put("340", "NAB");
		agencyBsbsTargetEndpointsMapping.put("341", "NAB");
		agencyBsbsTargetEndpointsMapping.put("342", "NAB");
		agencyBsbsTargetEndpointsMapping.put("343", "NAB");
		agencyBsbsTargetEndpointsMapping.put("344", "NAB");
		agencyBsbsTargetEndpointsMapping.put("345", "NAB");
		agencyBsbsTargetEndpointsMapping.put("346", "NAB");
		agencyBsbsTargetEndpointsMapping.put("347", "NAB");
		agencyBsbsTargetEndpointsMapping.put("348", "NAB");
		agencyBsbsTargetEndpointsMapping.put("349", "NAB");
		agencyBsbsTargetEndpointsMapping.put("452", "NAB");
		agencyBsbsTargetEndpointsMapping.put("453", "NAB");
		agencyBsbsTargetEndpointsMapping.put("200", "NAB");
		agencyBsbsTargetEndpointsMapping.put("201", "NAB");
		agencyBsbsTargetEndpointsMapping.put("202", "NAB");
		agencyBsbsTargetEndpointsMapping.put("203", "NAB");
		agencyBsbsTargetEndpointsMapping.put("204", "NAB");
		agencyBsbsTargetEndpointsMapping.put("205", "NAB");
		agencyBsbsTargetEndpointsMapping.put("206", "NAB");
		agencyBsbsTargetEndpointsMapping.put("207", "NAB");
		agencyBsbsTargetEndpointsMapping.put("208", "NAB");
		agencyBsbsTargetEndpointsMapping.put("209", "NAB");
		agencyBsbsTargetEndpointsMapping.put("610", "NAB");
		agencyBsbsTargetEndpointsMapping.put("633", "NAB");
		agencyBsbsTargetEndpointsMapping.put("639", "NAB");
		agencyBsbsTargetEndpointsMapping.put("653", "NAB");
		agencyBsbsTargetEndpointsMapping.put("819", "NAB");
		agencyBsbsTargetEndpointsMapping.put("942", "NAB");
		agencyBsbsTargetEndpointsMapping.put("952", "NAB");
		agencyBsbsTargetEndpointsMapping.put("985", "NAB");	*/
		
		return agencyBsbsTargetEndpointsMapping;

	}
	
	public static boolean includeCredit(String targetEndPoint){
		if(targetEndPointsRequiringCredit.indexOf(targetEndPoint) > -1){
			return true;
		}
		
		return false;
	}
	
	/* end - Code added by Yogesh for 21701 - Agency Bank cheque*/

}
