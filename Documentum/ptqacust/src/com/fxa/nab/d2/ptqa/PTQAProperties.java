package com.fxa.nab.d2.ptqa;

import java.io.File;
import java.util.Date;
import java.util.ResourceBundle;

import com.documentum.fc.common.DfException;

public class PTQAProperties {
	
//	private static String PropertiesFileName="ptqa";
//	public static final int debug=CommonUtils.debug;
//	public static final int info=CommonUtils.info;
//	public static final int warn=CommonUtils.warn;
//	public static final int error=CommonUtils.error;
//	
//	public static final String docbroker_host_key="docbroker_host";
//	public static final String docbroker_port_key="docbroker_port";	
//	public static final String login_user_key="dctm_login_user";
//	public static final String password_encrypted_key="dctm_password_encrypted";	
//	public static final String repository_name_key="repository_name";
//
//	public static final String vouchers_per_set_key="vouchers_per_set";
//	
//	public static final String dips="dips";
//	public static final String cdc="cdc";
	
	/*should be used later
	public static final String voucher_pickup_query_key="voucher_pickup_query";
	public static final String amount_pickup_query_key="amount_pickup_query";
	public static final String codeline_pickup_query_key="codeline_pickup_query";

	public static String voucher_pickup_query=null;	
	public static String amount_pickup_query=null;
	public static String codeline_pickup_query=null;	
	*/	
	
	//WORST HARD CODING BELOW... FIX IT ASAP
//	public static String docbroker_host="DctmMelb";
//	public static String docbroker_port="1489";
//	public static String dctm_login_user="fxa_voucher";
//	public static String dctm_password_encrypted="AAAAEL7KfJCck2TCcLASAPhGJDnPZjgJANa7gWR8K8OqZJgR";	
//	public static String repository_name="NAB";
	
	public final static String DEFAULT_FILE_PATH="c://";
	//SIT DETAILS
	//Below variables get their values from Servlet Context Parameters that are mentioned in web.xml
	public static String docbroker_host=null;
	public static String docbroker_port=null;
	public static String dctm_login_user=null;
	public static String dctm_password_encrypted=null;
	public static String repository_name=null;	

	public static String vouchers_per_set=null;
	public static String d2_drl=null;
	public static String xmlfilepath=null;
	@Deprecated
	public static String xmlnameprefix=null;
	
	public static String xmlnameprefixforamt=null;	
	public static String xmlnameprefixforcdc=null;	
	
	private static String vouchers_per_set_default="200";
	
	@Deprecated
	public static String voucher_pickup_query="select object_name,r_object_id,i_chronicle_id,fxa_processing_date,fxa_ptqa_amt_flag,fxa_ptqa_code_line_flag,"
			+ "fxa_adjustment_flag,fxa_for_value_type from fxa_voucher where (fxa_processing_date >= DATE('%s','dd/mm/yyyy') and "
			+ "fxa_processing_date < DATEADD(day,1,DATE('%s','dd/mm/yyyy'))) and (fxa_ptqa_amt_flag = true or fxa_ptqa_code_line_flag = true) and (fxa_ptqa_amt_complete_flag = false) enable (return_top %s)";
	
	public static String voucher_amt_pickup_query="select object_name,r_object_id,i_chronicle_id,fxa_processing_date,fxa_ptqa_amt_flag,fxa_ptqa_code_line_flag,"
			+ "fxa_adjustment_flag,fxa_for_value_type from fxa_voucher where (fxa_processing_date >= DATE('%s','dd/mm/yyyy') and "
			+ "fxa_processing_date < DATEADD(day,1,DATE('%s','dd/mm/yyyy'))) and (fxa_ptqa_amt_flag = true) and (fxa_ptqa_amt_complete_flag = false) enable (return_top %s)";
	
	public static String voucher_cdc_pickup_query="select object_name,r_object_id,i_chronicle_id,fxa_processing_date,fxa_ptqa_amt_flag,fxa_ptqa_code_line_flag,"
			+ "fxa_adjustment_flag,fxa_for_value_type from fxa_voucher where (fxa_processing_date >= DATE('%s','dd/mm/yyyy') and "
			+ "fxa_processing_date < DATEADD(day,1,DATE('%s','dd/mm/yyyy'))) and (fxa_ptqa_code_line_flag = true) and (fxa_ptqa_cdc_complete_flag = false) enable (return_top %s)";
	
	public static String amount_pickup_query="select i_chronicle_id,subject_area,attribute_name,post_value from dm_dbo.fxa_voucher_audit where subject_area='dips' "
			+ "and attribute_name in ('operator','amt') and i_chronicle_id in (%s)";
	
	public static String codeline_pickup_query="select i_chronicle_id,subject_area,attribute_name,post_value from dm_dbo.fxa_voucher_audit where subject_area='cdc' "
			+ "and attribute_name in ('operator','ead','ad','bsb','account','tc') and i_chronicle_id in (%s)";
	
	@Deprecated
	public static String voucher_update_query="update fxa_voucher object set fxa_ptqa_complete_flag=true set fxa_ptqa_processed_date=DATE(NOW) where i_chronicle_id in ('%s')";
	
	public static String voucher_amt_update_query="update fxa_voucher object set fxa_ptqa_amt_complete_flag=true set fxa_ptqa_amt_processed_date=DATE(NOW) where i_chronicle_id in ('%s')";
	
	public static String voucher_cdc_update_query="update fxa_voucher object set fxa_ptqa_cdc_complete_flag=true set fxa_ptqa_cdc_processed_date=DATE(NOW) where i_chronicle_id in ('%s')";
	
	public static String amount_insert_query="insert into dm_dbo.fxa_voucher_audit(i_chronicle_id,subject_area,attribute_name,post_value) values('%s','qa','amt_accurate','%s')";
	
	public static String codeline_insert_query="insert into dm_dbo.fxa_voucher_audit(i_chronicle_id,subject_area,attribute_name,post_value) values('%s','qa','codeline_entry_accurate','%s')";
	
	
	public static String getFilePath(){
		CommonUtils.writeToLog("Inside getFilePath() method",CommonUtils.debug,null);
		
		if(xmlfilepath != null && xmlfilepath.trim().length() > 0){
			File filePath=new File(xmlfilepath);
			if(filePath.isDirectory()){
				CommonUtils.writeToLog(xmlfilepath+" is a valid directory. The resulting XML file will be stored here.", CommonUtils.debug, null);
				return xmlfilepath; //NOTE: returning String not File object..
			}else{
				CommonUtils.writeToLog(xmlfilepath+" is NOT a valid directory. The resulting XML file will be stored at default path : ["+DEFAULT_FILE_PATH+"]", CommonUtils.warn, null);
				return DEFAULT_FILE_PATH;
			}
		}else{
			CommonUtils.writeToLog(xmlfilepath+" is NULL or EMPTY String. The resulting XML file will be stored at default path : ["+DEFAULT_FILE_PATH+"]", CommonUtils.warn, null);
			return DEFAULT_FILE_PATH;
		}
	}
	
	/*
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
		

		vouchers_per_set=bundle.getString(vouchers_per_set_key);

		if(vouchers_per_set == null || vouchers_per_set.length() == 0){
			vouchers_per_set=vouchers_per_set_default;
			CommonUtils.writeToLog(vouchers_per_set_key +" doesn't have a value in the properties file. Default value "+vouchers_per_set_default+" set for it.",CommonUtils.warn,null);
		}
		
		try{
			Integer.parseInt(vouchers_per_set);
			
		}catch(NumberFormatException e){
			String message="Value for key '"+vouchers_per_set_key+"' is not an integer. Assigning default value...";
			CommonUtils.writeToLog(message,CommonUtils.warn,null);
			vouchers_per_set=vouchers_per_set_default;
		}
		
		CommonUtils.writeToLog("total_batch_to_process :"+vouchers_per_set,CommonUtils.debug,null);	
		
		
		if(!result){
			CommonUtils.writeToLog("Cannot proceed further. Check the log file for error info",CommonUtils.error,null);
		}
		
		return result;		
	} */
	

}
