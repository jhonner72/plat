package com.fxa.nab.d2.ptqa;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RunPTQAQueryAndStoreResultsToXML {

	/**
	#) Read Properties file.
	#) Create a session with NAB Repo
	#) Run the query to get all the vouchers with the processing date,fxa_ptqa_amt_flag,fxa_ptqa_code_line_flag
	#) Run multiple queries in fxa_voucher_audit_table for the given set of chronicle id.
	#) Keep populating the values in PTQA Object (put every object in a hash map with 'i_chronicle_id' as the key.
	#) Store it in Singleton object. 
	*/

	public static final int debug=CommonUtils.debug;
	public static final int info=CommonUtils.info;
	public static final int warn=CommonUtils.warn;
	public static final int error=CommonUtils.error;	
	
//	CommonUtils utils=null;
	DctmActivities dctm=DctmActivities.getInstance();

	//Create a HashMap entry to store 'chronicle id' as key and PTQAObject as value	
	HashMap<String,PTQAVoucherObject> resultsMap=new HashMap<String,PTQAVoucherObject>();
	
	
	public RunPTQAQueryAndStoreResultsToXML(){
	   
	}	
	
	@Deprecated
	public int retrieveAndStorePTQAVouchers(String procDate) throws Exception{
		CommonUtils.writeToLog("Inside retrieveAndStorePTQAVouchers(String procDate) method",debug,null);		
		
		//Read Vouchers from Documentum
		String voucherPickupQuery=String.format(PTQAProperties.voucher_pickup_query, procDate,procDate,PTQAProperties.vouchers_per_set);
		CommonUtils.writeToLog("voucherPickupQuery : "+voucherPickupQuery,debug,null);
		IDfCollection voucherResults=dctm.readQuery(voucherPickupQuery);
		
		PTQAVoucherObject object=null;
		String chronicleId=null;
		StringBuffer whereClauseBuffer=new StringBuffer();
		String objectId=null;
		
		if(voucherResults == null){
			CommonUtils.writeToLog("The IDfCollection [voucherResults] is null.", CommonUtils.error,null);
			CommonUtils.writeToLog("ERROR!! No XML file generated having the results!!", CommonUtils.error,null);
			return -1;
		}
		
		while(voucherResults.next()){
			chronicleId=null;
			objectId=null;
			//Create PTQAObjects for each record from the result			
			object=new PTQAVoucherObject();
			
			chronicleId=voucherResults.getString("i_chronicle_id");
			object.setI_chronicle_id(chronicleId);
			
			objectId=voucherResults.getString("r_object_id");
			object.setR_object_id(objectId);
//			object.setDrl(String.format(PTQAProperties.d2_drl,objectId)); d2_drl is not confirmed
			
			object.setObject_name(voucherResults.getString("object_name"));
			object.setFxa_adjustment_flag(voucherResults.getString("fxa_adjustment_flag"));
			object.setFxa_for_value_type(voucherResults.getString("fxa_for_value_type"));
			object.setFxa_processing_date(voucherResults.getString("fxa_processing_date"));
			object.setFxa_ptqa_amt_flag(voucherResults.getBoolean("fxa_ptqa_amt_flag"));
			object.setFxa_ptqa_code_line_flag(voucherResults.getBoolean("fxa_ptqa_code_line_flag"));
			
			
			whereClauseBuffer.append("'"+chronicleId+"',");
			//put the object in the Hashmap as chronicleId as the key
			resultsMap.put(chronicleId, object);			
		}
		
		if(resultsMap.size() == 0){
			CommonUtils.writeToLog("The IDfCollection [voucherResults] retured no results. Try with different processing date.", CommonUtils.warn,null);
			CommonUtils.writeToLog("WARNING!! No XML file generated having the results!!", CommonUtils.warn,null);
			return 0;
		}		
		
		String whereClause=whereClauseBuffer.substring(0,whereClauseBuffer.length()-1);
		CommonUtils.writeToLog(whereClause, debug, null);
		
		//Read amount related query from fxa_voucher_audit table
		getAmountData(whereClause);
		
		//Read Code Line related query from fxa_voucher_audit table
		getCodelineData(whereClause);
		
		CommonUtils.writeToLog("Leaving retrieveAndStorePTQAVouchers(String procDate) method",debug,null);
		return resultsMap.size();
		
	}
	
	public int retrieveAndStorePTQAVouchers(String procDate,boolean isAmtOtherwiseCdc) throws Exception{
		CommonUtils.writeToLog("Inside retrieveAndStorePTQAVouchers(String procDate) method",debug,null);		
		
		//Read Vouchers from Documentum
		String voucherPickupQuery=null;
		if(isAmtOtherwiseCdc){
			voucherPickupQuery=String.format(PTQAProperties.voucher_amt_pickup_query, procDate,procDate,PTQAProperties.vouchers_per_set);
		}else{
			voucherPickupQuery=String.format(PTQAProperties.voucher_cdc_pickup_query, procDate,procDate,PTQAProperties.vouchers_per_set);
		}
		CommonUtils.writeToLog("voucherPickupQuery : "+voucherPickupQuery,debug,null);
		IDfCollection voucherResults=dctm.readQuery(voucherPickupQuery);
		
		PTQAVoucherObject object=null;
		String chronicleId=null;
		StringBuffer whereClauseBuffer=new StringBuffer();
		String objectId=null;
		
		if(voucherResults == null){
			CommonUtils.writeToLog("The IDfCollection [voucherResults] is null.", CommonUtils.error,null);
			CommonUtils.writeToLog("ERROR!! No XML file generated having the results!!", CommonUtils.error,null);
			return -1;
		}
		
		while(voucherResults.next()){
			chronicleId=null;
			objectId=null;
			//Create PTQAObjects for each record from the result			
			object=new PTQAVoucherObject();
			
			chronicleId=voucherResults.getString("i_chronicle_id");
			object.setI_chronicle_id(chronicleId);
			
			objectId=voucherResults.getString("r_object_id");
			object.setR_object_id(objectId);
//			object.setDrl(String.format(PTQAProperties.d2_drl,objectId)); d2_drl is not confirmed
			
			object.setObject_name(voucherResults.getString("object_name"));
			object.setFxa_adjustment_flag(voucherResults.getString("fxa_adjustment_flag"));
			object.setFxa_for_value_type(voucherResults.getString("fxa_for_value_type"));
			object.setFxa_processing_date(voucherResults.getString("fxa_processing_date"));
			object.setFxa_ptqa_amt_flag(voucherResults.getBoolean("fxa_ptqa_amt_flag"));
			object.setFxa_ptqa_code_line_flag(voucherResults.getBoolean("fxa_ptqa_code_line_flag"));
			
			
			whereClauseBuffer.append("'"+chronicleId+"',");
			//put the object in the Hashmap as chronicleId as the key
			resultsMap.put(chronicleId, object);			
		}
		
		if(resultsMap.size() == 0){
			CommonUtils.writeToLog("The IDfCollection [voucherResults] retured no results. Try with different processing date.", CommonUtils.warn,null);
			CommonUtils.writeToLog("WARNING!! No XML file generated having the results!!", CommonUtils.warn,null);
			return 0;
		}		
		
		String whereClause=whereClauseBuffer.substring(0,whereClauseBuffer.length()-1);
		CommonUtils.writeToLog(whereClause, debug, null);
		
		if(isAmtOtherwiseCdc){
			//Read amount related query from fxa_voucher_audit table
			getAmountData(whereClause);
		}else{
			//Read Code Line related query from fxa_voucher_audit table
			getCodelineData(whereClause);
		}
		
		CommonUtils.writeToLog("Leaving retrieveAndStorePTQAVouchers(String procDate) method",debug,null);
		return resultsMap.size();
		
	}

	
	//Read amount related query from fxa_voucher_audit table
	private void getAmountData(String whereClause) throws Exception{
		CommonUtils.writeToLog("Inside getAmountData() method",debug,null);
		
		String amountPickupQuery=String.format(PTQAProperties.amount_pickup_query, whereClause);
		IDfCollection amountResults=dctm.readQuery(amountPickupQuery);
		PTQAVoucherObject object=null;
		
		String chronicleId=null;
		String attrName=null;
		
		while(amountResults.next()){
			attrName=null;
			chronicleId=null;object=null;
			chronicleId=amountResults.getString("i_chronicle_id");
			
			//Update PTQA object with those details using 'i_chronicle_id'
			object=resultsMap.get(chronicleId);
			
			if(object!=null){

				attrName=amountResults.getString("attribute_name");
//				CommonUtils.writeToLog("attrName  : "+attrName,debug,null);				
				if(attrName==null || attrName.trim().length() == 0){
					CommonUtils.writeToLog("Attribute Name is null",error,null);
				}else if(attrName.equals("operator")){
					object.setDips_operator_name(amountResults.getString("post_value"));
				}else if(attrName.equals("amt")){
					object.setDips_amt(amountResults.getString("post_value"));
				}else{
					CommonUtils.writeToLog("Strange!! Invalid column name for fxa_voucher_audit table. Check the query you run.",error,null);
				}
				
			}else{
				
				CommonUtils.writeToLog("No object found for Chronicle ID [ "+chronicleId+" ] in HashMap [resultsMap] received from fxa_voucher",error,null);
			}			
		}		
		
		CommonUtils.writeToLog("Leaving getAmountData() method",debug,null);
	}

	
	//Read amount related query from fxa_voucher_audit table
	private void getCodelineData(String whereClause) throws Exception{
		
		CommonUtils.writeToLog("Inside getCodelineData() method",debug,null);
		
		String codelinePickupQuery=String.format(PTQAProperties.codeline_pickup_query, whereClause);
		IDfCollection codeLineResults=dctm.readQuery(codelinePickupQuery);
		PTQAVoucherObject object=null;
		
		String chronicleId=null;
		String attrName=null;		
		
		while(codeLineResults.next()){
			chronicleId=null;
			object=null;
			attrName=null;
			chronicleId=codeLineResults.getString("i_chronicle_id");
			
			//Update PTQA object with those details using 'i_chronicle_id'
			object=resultsMap.get(chronicleId);
			
			if(object!=null){
				//'operator','ead','ad','bsb','account','tc'
				attrName=codeLineResults.getString("attribute_name");
//				CommonUtils.writeToLog("attrName  : "+attrName,debug,null);					
				
				if(attrName==null || attrName.trim().length() == 0){
					CommonUtils.writeToLog("Attribute Name is null",error,null);
				}else if(attrName.equals("operator")){
					object.setCdc_operator_name(codeLineResults.getString("post_value"));
				}else if(attrName.equals("ead")){
					object.setCdc_ead(codeLineResults.getString("post_value"));
				}else if(attrName.equals("ad")){
					object.setCdc_ad(codeLineResults.getString("post_value"));
				}else if(attrName.equals("bsb")){
					object.setCdc_bsb(codeLineResults.getString("post_value"));
				}else if(attrName.equals("account")){
					object.setCdc_account(codeLineResults.getString("post_value"));
				}else if(attrName.equals("tc")){
					object.setCdc_tc(codeLineResults.getString("post_value"));
				}else{
					CommonUtils.writeToLog("Strange!! Invalid Attribute Name found. Check the query you run.",error,null);
				}
				
			}else{
				
				CommonUtils.writeToLog("No object found for Chronicle ID [ "+chronicleId+" ]in HashMap [resultsMap] received from fxa_voucher",error,null);
			}			
		}		
		
		CommonUtils.writeToLog("Leaving getCodelineData() method",debug,null);
	}

	@Deprecated
	public String writeToXML(String procDate){
		CommonUtils.writeToLog("Inside writeToXML() method",debug,null);
		
		GregorianCalendar cal=new GregorianCalendar(); //creates Gregorian Calendar object with CURRENT time.
		String fileName=PTQAProperties.xmlnameprefix+cal.getTimeInMillis()+".xml";
		CommonUtils.writeToLog("XML File "+fileName+" created at "+cal.getTime(), debug, null);
		
		  try {
			  
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("ptqa");
				doc.appendChild(rootElement);
		 
				// staff elements
				Element result_for_date = doc.createElement("result_for_date");
				result_for_date.appendChild(doc.createTextNode(procDate));
				rootElement.appendChild(result_for_date);
				
				Element no_of_results = doc.createElement("no_of_results");
				no_of_results.appendChild(doc.createTextNode(Integer.toString(resultsMap.size())));
				rootElement.appendChild(no_of_results);				
		 
				Element element=null;
				Element objectElement=null;
				for(PTQAVoucherObject object : resultsMap.values()){
					
					objectElement = doc.createElement("ptqaobject");
					rootElement.appendChild(objectElement);
					
					element=doc.createElement("chronicleid");
					element.appendChild(doc.createTextNode(object.getI_chronicle_id() == null?"":object.getI_chronicle_id()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("objectid");
					element.appendChild(doc.createTextNode(object.getR_object_id() == null?"":object.getR_object_id()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("drl");
					element.appendChild(doc.createTextNode(object.getDrl() == null?"":object.getDrl()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("objectname");
					element.appendChild(doc.createTextNode(object.getObject_name() == null?"":object.getObject_name()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("fxa_adjustment_flag");
					element.appendChild(doc.createTextNode(object.getFxa_adjustment_flag() == null?"":object.getFxa_adjustment_flag()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("fxa_for_value");
					element.appendChild(doc.createTextNode(object.getFxa_for_value_type() == null?"":object.getFxa_for_value_type()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("fxa_ptqa_amt_flag");
					element.appendChild(doc.createTextNode(object.isFxa_ptqa_amt_flag() ?"1":"0"));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("fxa_ptqa_code_line_flag");
					element.appendChild(doc.createTextNode(object.isFxa_ptqa_code_line_flag()?"1":"0"));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("dips_operator_name");
					element.appendChild(doc.createTextNode(object.getDips_operator_name() == null?"":object.getDips_operator_name()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("dips_amt");
					element.appendChild(doc.createTextNode(object.getDips_amt() == null?"":object.getDips_amt()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_operator_name");
					element.appendChild(doc.createTextNode(object.getCdc_operator_name() == null?"":object.getCdc_operator_name()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_ead");
					element.appendChild(doc.createTextNode(object.getCdc_ad() == null?"":object.getCdc_ad()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_ad");
					element.appendChild(doc.createTextNode(object.getCdc_ead() == null?"":object.getCdc_ead()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_bsb");
					element.appendChild(doc.createTextNode(object.getCdc_bsb() == null?"":object.getCdc_bsb()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_account");
					element.appendChild(doc.createTextNode(object.getCdc_account() == null?"":object.getCdc_account()));
					objectElement.appendChild(element);
					
					
					element=doc.createElement("cdc_tc");
					element.appendChild(doc.createTextNode(object.getCdc_tc() == null?"":object.getCdc_tc()));
					objectElement.appendChild(element);  
				
				}
		 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(PTQAProperties.getFilePath())+fileName);
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved!");
		 
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
						
		
		CommonUtils.writeToLog("Leaving writeToXML() method",debug,null);
		return fileName;
	}

	public String writeToXML(String procDate,boolean isAmtOtherwiseCdc,String fileNamePrefix){
		CommonUtils.writeToLog("Inside writeToXML() method",debug,null);
		
		GregorianCalendar cal=new GregorianCalendar(); //creates Gregorian Calendar object with CURRENT time.
		String formattedProcDate=procDate.replace('/', '-');
		
		String fileName=fileNamePrefix+formattedProcDate+"_"+cal.getTimeInMillis()+".xml";
		CommonUtils.writeToLog("XML File "+fileName+" created at "+cal.getTime(), debug, null);
		
		  try {
			  
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("ptqa");
				doc.appendChild(rootElement);
		 
				// staff elements
				Element result_for_date = doc.createElement("result_for_date");
				result_for_date.appendChild(doc.createTextNode(procDate));
				rootElement.appendChild(result_for_date);
				
				Element no_of_results = doc.createElement("no_of_results");
				no_of_results.appendChild(doc.createTextNode(Integer.toString(resultsMap.size())));
				rootElement.appendChild(no_of_results);				
		 
				Element element=null;
				Element objectElement=null;
				for(PTQAVoucherObject object : resultsMap.values()){
					objectElement = doc.createElement("ptqaobject");
					rootElement.appendChild(objectElement);
					
					element=doc.createElement("chronicleid");
					element.appendChild(doc.createTextNode(object.getI_chronicle_id() == null?"":object.getI_chronicle_id()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("objectid");
					element.appendChild(doc.createTextNode(object.getR_object_id() == null?"":object.getR_object_id()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("drl");
					element.appendChild(doc.createTextNode(object.getDrl() == null?"":object.getDrl()));
					objectElement.appendChild(element);					
					
					element=doc.createElement("objectname");
					element.appendChild(doc.createTextNode(object.getObject_name() == null?"":object.getObject_name()));
					objectElement.appendChild(element);					
				
					
					if(isAmtOtherwiseCdc){ //create elements for codeline related attributes.
						
						element=doc.createElement("fxa_adjustment_flag");
						element.appendChild(doc.createTextNode(object.getFxa_adjustment_flag() == null?"":object.getFxa_adjustment_flag()));
						objectElement.appendChild(element);
						
						element=doc.createElement("fxa_ptqa_amt_flag");
						element.appendChild(doc.createTextNode(object.isFxa_ptqa_amt_flag() ?"1":"0"));
						objectElement.appendChild(element);
						
						element=doc.createElement("dips_operator_name");
						element.appendChild(doc.createTextNode(object.getDips_operator_name() == null?"":object.getDips_operator_name()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("dips_amt");
						element.appendChild(doc.createTextNode(object.getDips_amt() == null?"":object.getDips_amt()));
						objectElement.appendChild(element);
						
					}else{ //create elements for codeline related attributes.
						
						element=doc.createElement("fxa_for_value");
						element.appendChild(doc.createTextNode(object.getFxa_for_value_type() == null?"":object.getFxa_for_value_type()));
						objectElement.appendChild(element);					
						
						element=doc.createElement("fxa_ptqa_code_line_flag");
						element.appendChild(doc.createTextNode(object.isFxa_ptqa_code_line_flag()?"1":"0"));
						objectElement.appendChild(element);
						
						element=doc.createElement("cdc_operator_name");
						element.appendChild(doc.createTextNode(object.getCdc_operator_name() == null?"":object.getCdc_operator_name()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("cdc_ead");
						element.appendChild(doc.createTextNode(object.getCdc_ad() == null?"":object.getCdc_ad()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("cdc_ad");
						element.appendChild(doc.createTextNode(object.getCdc_ead() == null?"":object.getCdc_ead()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("cdc_bsb");
						element.appendChild(doc.createTextNode(object.getCdc_bsb() == null?"":object.getCdc_bsb()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("cdc_account");
						element.appendChild(doc.createTextNode(object.getCdc_account() == null?"":object.getCdc_account()));
						objectElement.appendChild(element);						
						
						element=doc.createElement("cdc_tc");
						element.appendChild(doc.createTextNode(object.getCdc_tc() == null?"":object.getCdc_tc()));
						objectElement.appendChild(element);  
						
					}//end of else.
				
				}
		 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(PTQAProperties.getFilePath())+fileName);
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				CommonUtils.writeToLog("File saved!",debug,null);
		 
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }
						
		
		CommonUtils.writeToLog("Leaving writeToXML() method",debug,null);
		return fileName;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		RunPTQAQueryAndStoreResultsToXML obj=new RunPTQAQueryAndStoreResultsToXML();
		try{
			obj.retrieveAndStorePTQAVouchers("28/07/2015",true);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		System.out.println("I am here");
	}	
	

}
