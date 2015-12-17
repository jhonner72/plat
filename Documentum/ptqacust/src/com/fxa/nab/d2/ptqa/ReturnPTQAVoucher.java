package com.fxa.nab.d2.ptqa;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReturnPTQAVoucher extends DefaultHandler{
	
	/*
	 * #) Get i_chroncile_id, codeline validation result, amount validation result - DONE LATER
	 * #) 
	 * #) Get counter,xmlfilename, xmlfilepath as input.
	 * #) Parse the xml and get the PTQAObject of the counter record
	 * #) pass that object as json
	 * #) 
	 * 
	 * SAMPLE XML OBJECT:
	 * <ptqaobject>
		<chronicleid>0901e24080012949</chronicleid>
		<objectid>0901e24080012949</objectid>
		<objectname>2450026785_r.tiff</objectname>
		<fxa_adjustment_flag>1</fxa_adjustment_flag>
		<fxa_for_value>type2</fxa_for_value>
		<fxa_ptqa_amt_flag>1</fxa_ptqa_amt_flag>
		<fxa_ptqa_code_line_flag>1</fxa_ptqa_code_line_flag>
		<dips_operator_name>au019473</dips_operator_name>
		<dips_amt>38000</dips_amt>
		<cdc_operator_name>au019475</cdc_operator_name>
		<cdc_ead>001120</cdc_ead>
		<cdc_ad>9913413</cdc_ad>
		<cdc_bsb>022350</cdc_bsb>
		<cdc_account>506288577</cdc_account>
		<cdc_tc>89</cdc_tc>
		</ptqaobject>
	 */
	
	
	public static final int debug=CommonUtils.debug;
	public static final int info=CommonUtils.info;
	public static final int warn=CommonUtils.warn;
	public static final int error=CommonUtils.error;
	
	private static String PTQA_OBJECT_ELEMENT="ptqaobject";
	
	private static String CHRONICLE_ELEMENT="chronicleid";
	private static String ID_ELEMENT="objectid";
	private static String NAME_ELEMENT="objectname";
	private static String ADJ_FLAG_ELEMENT="fxa_adjustment_flag";	
	private static String FOR_VALUE_ELEMENT="fxa_for_value";
	private static String AMT_FLAG_ELEMENT="fxa_ptqa_amt_flag";
	private static String CDC_FLAG_ELEMENT="fxa_ptqa_code_line_flag";
	private static String DIPS_OP_ELEMENT="dips_operator_name";
	private static String DIPS_AMT_ELEMENT="dips_amt";
	private static String CDC_OP_ELEMENT="cdc_operator_name";
	private static String CDC_EAD_ELEMENT="cdc_ead";
	private static String CDC_AD_ELEMENT="cdc_ad";
	private static String CDC_BSB_ELEMENT="cdc_bsb";
	private static String CDC_ACC_ELEMENT="cdc_account";
	private static String CDC_TC_ELEMENT="cdc_tc";
	
	private boolean isChronicleId=false;
	private boolean isObjectId=false;
	private boolean isObjectName=false;
	private boolean isAdjFlag=false;
	private boolean isForValue=false;
	private boolean isAmtFlag=false;
	private boolean isCdcFlag=false;
	private boolean isDipsOp=false;
	private boolean isDipsAmt=false;
	private boolean isCdcOp=false;
	private boolean isCdcEad=false;
	private boolean isCdcAd=false;
	private boolean isCdcBsb=false;
	private boolean isCdcAcc=false;
	private boolean isCdcTc=false;
	
	
	int countPTQAObjectInXML=0;
	int counter=0;
	String fileNameWithPath=null;
	boolean pickIt=false;
	PTQAVoucherObject ptqaVoucherObj=null;
	
	public ReturnPTQAVoucher(String xmlFileName,String xmlFilePath,Integer counterObj){
		fileNameWithPath=xmlFilePath+xmlFileName;
		CommonUtils.writeToLog("XML File with path is "+fileNameWithPath, debug, null);
		CommonUtils.writeToLog("Voucher at ["+counterObj.intValue()+"] will be picked up for processing.", debug, null);
		counter=counterObj.intValue();
		ptqaVoucherObj=new PTQAVoucherObject();
	}
	
	
	public PTQAVoucherObject returnPTQAVoucher(){
		
		CommonUtils.writeToLog("Inside ReturnPTQAVoucher(String xmlFileName,String xmlFilePath,String counter) method", debug, null);
		try{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		
		if(fileNameWithPath == null || fileNameWithPath.trim().length() < 1){
			CommonUtils.writeToLog("ERROR!! XML File with path is NULL", error, null);
			ptqaVoucherObj.setI_chronicle_id("error");
			return ptqaVoucherObj;
		}
	       saxParser.parse(fileNameWithPath, this);
	  	 
	     } catch (Exception e) {
	       e.printStackTrace();
	     }	
		
		
		CommonUtils.writeToLog("Leaving ReturnPTQAVoucher(String xmlFileName,String xmlFilePath,String counter) method", debug, null);
		return ptqaVoucherObj;
	   }
	 
	 
		public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
	 
			//System.out.println("Start Element :" + qName);
			if(qName.equals(PTQA_OBJECT_ELEMENT)){
				countPTQAObjectInXML++;
				if(counter==countPTQAObjectInXML){
					pickIt=true;
				}else{
					pickIt=false;
				}
			}
			
			if(pickIt){
				//CommonUtils.writeToLog("Current Element is ["+qName+"]", debug, null);				
				if(qName.equals(CHRONICLE_ELEMENT)){
					isChronicleId=true;
				}else{
					isChronicleId=false;
				}
				
				if(qName.equals(ID_ELEMENT)){
					isObjectId=true;
				}else{
					isObjectId=false;
				}
				
				
				if(qName.equals(NAME_ELEMENT)){
					isObjectName=true;
				}else{
					isObjectName=false;
				}
				
				if(qName.equals(ADJ_FLAG_ELEMENT)){
					isAdjFlag=true;
				}else{
					isAdjFlag=false;
				}	
				
				if(qName.equals(FOR_VALUE_ELEMENT)){
					isForValue=true;
				}else{
					isForValue=false;
				}
				
				if(qName.equals(AMT_FLAG_ELEMENT)){
					isAmtFlag=true;
				}else{
					isAmtFlag=false;
				}
				
				if(qName.equals(CDC_FLAG_ELEMENT)){
					isCdcFlag=true;
				}else{
					isCdcFlag=false;
				}
				
				if(qName.equals(DIPS_OP_ELEMENT)){
					isDipsOp=true;
				}else{
					isDipsOp=false;
				}
				
				if(qName.equals(DIPS_AMT_ELEMENT)){
					isDipsAmt=true;
				}else{
					isDipsAmt=false;
				}
				
				if(qName.equals(CDC_OP_ELEMENT)){
					isCdcOp=true;
				}else{
					isCdcOp=false;
				}
				
				if(qName.equals(CDC_EAD_ELEMENT)){
					isCdcEad=true;
				}else{
					isCdcEad=false;
				}
				
				if(qName.equals(CDC_AD_ELEMENT)){
					isCdcAd=true;
				}else{
					isCdcAd=false;
				}
				
				if(qName.equals(CDC_BSB_ELEMENT)){
					isCdcBsb=true;
				}else{
					isCdcBsb=false;
				}
				
				if(qName.equals(CDC_ACC_ELEMENT)){
					isCdcAcc=true;
				}else{
					isCdcAcc=false;
				}	
				
				if(qName.equals(CDC_TC_ELEMENT)){
					isCdcTc=true;
				}else{
					isCdcTc=false;
				}					
			}

		}
	 
		public void endElement(String uri, String localName,
			String qName) throws SAXException {
	 
			//System.out.println("End Element :" + qName);
	 
		}
	 
		public void characters(char ch[], int start, int length) throws SAXException {
			String value=null;
			if(pickIt){
				value=new String(ch, start, length);
				
				if (isChronicleId) {
					CommonUtils.writeToLog("ptqaVoucherObj.setI_chronicle_id("+value+")", debug, null);
					ptqaVoucherObj.setI_chronicle_id(value);
					isChronicleId=false;
				}
				
				if (isObjectId) {
					CommonUtils.writeToLog("ptqaVoucherObj.setR_object_id("+value+")", debug, null);
					ptqaVoucherObj.setR_object_id(value);
					isObjectId=false;
				}
				
				if (isObjectName) {
					CommonUtils.writeToLog("ptqaVoucherObj.setObject_name("+value+")", debug, null);
					ptqaVoucherObj.setObject_name(value);
					isObjectName=false;
				}
				
				if (isAdjFlag) {
					CommonUtils.writeToLog("ptqaVoucherObj.setFxa_adjustment_flag("+value+")", debug, null);
					ptqaVoucherObj.setFxa_adjustment_flag(value);
					isAdjFlag=false;
				}
				
				if (isForValue) {
					CommonUtils.writeToLog("ptqaVoucherObj.setFxa_for_value_type("+value+")", debug, null);
					ptqaVoucherObj.setFxa_for_value_type(value);
					isForValue=false;
				}
				
				if (isAmtFlag) {
					CommonUtils.writeToLog("ptqaVoucherObj.setFxa_ptqa_amt_flag("+value+")", debug, null);
					if(value.equals("1")){
						ptqaVoucherObj.setFxa_ptqa_amt_flag(true);
					}else{
						ptqaVoucherObj.setFxa_ptqa_amt_flag(false);
					}
					isAmtFlag=false;
				}
				
				if (isCdcFlag) {
					CommonUtils.writeToLog("ptqaVoucherObj.setFxa_ptqa_code_line_flag("+value+")", debug, null);
					if(value.equals("1")){					
						ptqaVoucherObj.setFxa_ptqa_code_line_flag(true);
					}else{
						ptqaVoucherObj.setFxa_ptqa_code_line_flag(false);
					}
					isCdcFlag=false;
				}
				
				if (isDipsOp) {
					CommonUtils.writeToLog("ptqaVoucherObj.setDips_operator_name("+value+")", debug, null);
					ptqaVoucherObj.setDips_operator_name(value);
					isDipsOp=false;
				}
				
				if (isDipsAmt) {
					CommonUtils.writeToLog("ptqaVoucherObj.setDips_amt("+value+")", debug, null);
					ptqaVoucherObj.setDips_amt(value);
					isDipsAmt=false;
				}
				
				if (isCdcOp) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_operator_name("+value+")", debug, null);
					ptqaVoucherObj.setCdc_operator_name(value);
					isCdcOp=false;
				}
				
				if (isCdcEad) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_ead("+value+")", debug, null);
					ptqaVoucherObj.setCdc_ead(value);
					isCdcEad=false;
				}
				
				if (isCdcAd) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_ad("+value+")", debug, null);
					ptqaVoucherObj.setCdc_ad(value);
					isCdcAd=false;
				}
				
				if (isCdcBsb) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_bsb("+value+")", debug, null);
					ptqaVoucherObj.setCdc_bsb(value);
					isCdcBsb=false;
				}
				
				if (isCdcAcc) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_account("+value+")", debug, null);
					ptqaVoucherObj.setCdc_account(value);
					isCdcAcc=false;
				}
				
				if (isCdcTc) {
					CommonUtils.writeToLog("ptqaVoucherObj.setCdc_tc("+value+")", debug, null);
					ptqaVoucherObj.setCdc_tc(value);
					isCdcTc=false;
				}
	 
			}
		}
//	 
//		chronicleId=voucherResults.getString("i_chronicle_id");
//		object.setI_chronicle_id(chronicleId);
//		object.setR_object_id(voucherResults.getString("r_object_id"));
//		object.setObject_name(voucherResults.getString("object_name"));
//		object.setFxa_adjustment_flag(voucherResults.getString("fxa_adjustment_flag"));
//		object.setFxa_for_value_type(voucherResults.getString("fxa_for_value_type"));
//		object.setFxa_processing_date(voucherResults.getString("fxa_processing_date"));
//		object.setFxa_ptqa_amt_flag(voucherResults.getBoolean("fxa_ptqa_amt_flag"));
//		object.setFxa_ptqa_code_line_flag(voucherResults.getBoolean("fxa_ptqa_code_line_flag"));
//		
//		object.setDips_operator_name(amountResults.getString("post_value"));
//		object.setDips_amt(amountResults.getString("post_value"));		
//		
//		object.setCdc_operator_name(codeLineResults.getString("post_value"));
//		object.setCdc_ead(codeLineResults.getString("post_value"));
//		object.setCdc_ad(codeLineResults.getString("post_value"));
//		object.setCdc_bsb(codeLineResults.getString("post_value"));
//		object.setCdc_account(codeLineResults.getString("post_value"));
//		object.setCdc_tc(codeLineResults.getString("post_value"));	
		
		public static void main(String[] args){
			
			ReturnPTQAVoucher obj=new ReturnPTQAVoucher("ptqaresultobjects.xml","c:\\",new Integer(2));
			obj.returnPTQAVoucher();
		}


}
