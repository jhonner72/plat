package com.fxa.nab.d2.ptqa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.documentum.fc.common.DfException;

/**
 * Yogesh Jankin 
 * NAB Cheque Digitisation Project
 * 
 * Servlet implementation class BasePTQAServlet
 */
@WebServlet(name = "baseptqaservlet", urlPatterns = { "/baseptqaservlet" })
public class BasePTQAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String AMT_COUNTER="amt_counter";
	public static final String AMT_NO_OF_RECORDS="amt_no_of_records";
	public static final String AMT_XMLFILENAME="amt_xmlfilename";
	
	
	public static final String CDC_COUNTER="cdc_counter";
	public static final String CDC_NO_OF_RECORDS="cdc_no_of_records";
	public static final String CDC_XMLFILENAME="cdc_xmlfilename";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BasePTQAServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/*
	 * This method is used to set or reset PTQA Properties reading it from Servlet Context Params in web.xml
	 * 
	 * It is to make sure, that, if the PTQAProperties class is garbage collected due to inactivity, it is be resuscicated.
	 */
	public void setOrResetPTQAProperties() throws Exception{
		CommonUtils.writeToLog("Inside initAndValidate() method",CommonUtils.debug,null);

		try{
			//This properties are required to be reset for creating Dctm Session...
			PTQAProperties.docbroker_host=getServletContext().getInitParameter("docbroker_host");
			PTQAProperties.docbroker_port=getServletContext().getInitParameter("docbroker_port");
			PTQAProperties.dctm_login_user=getServletContext().getInitParameter("dctm_login_user");
			PTQAProperties.dctm_password_encrypted=getServletContext().getInitParameter("dctm_password_encrypted");
			PTQAProperties.repository_name=getServletContext().getInitParameter("repository_name");
			
			PTQAProperties.vouchers_per_set=getServletContext().getInitParameter("vouchers_per_set");
//			PTQAProperties.d2_drl=getServletContext().getInitParameter("d2_drl");
			PTQAProperties.xmlfilepath=getServletContext().getInitParameter("xmlfilepath");
			PTQAProperties.xmlnameprefixforamt=getServletContext().getInitParameter("xmlnameprefixforamt");
			PTQAProperties.xmlnameprefixforcdc=getServletContext().getInitParameter("xmlnameprefixforcdc");
			
			//instantiate DCTM 
			DctmActivities dctm=DctmActivities.getInstance();
			dctm.getSession();
			CommonUtils.writeToLog("Documentum connections tested successfully!!.Proceeding to process batches.", CommonUtils.debug, null);
		
		}catch(DfException e){
			CommonUtils.writeToLog(e.getMessage(), CommonUtils.error, e);
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			CommonUtils.writeToLog(e.getMessage(), CommonUtils.error, e);
			e.printStackTrace();
			throw e;
		}
	
	}	
	
	
	public void incrementCounter(String counterStr){
		Integer counterInteger=(Integer)getServletContext().getAttribute(counterStr);
		int counter=counterInteger.intValue()+1;
		getServletContext().setAttribute(counterStr,new Integer(counter));
	}
	
	public boolean isEndOfRecords(String counterStr,String noOfRecordsStr){
		int counterint=((Integer)getServletContext().getAttribute(counterStr)).intValue();
		int noOfRecords=((Integer)getServletContext().getAttribute(noOfRecordsStr)).intValue();
		CommonUtils.writeToLog("Value of ["+counterStr+"] is "+counterint+"; Value of ["+noOfRecordsStr+"] is "+noOfRecords, CommonUtils.debug, null);
		if(counterint > noOfRecords){
			CommonUtils.writeToLog("Resetting Servlet Context Params ["+counterStr+"] to 0 and ["+noOfRecordsStr+"] to -1", CommonUtils.debug, null);
			getServletContext().setAttribute(counterStr,new Integer(0));
			getServletContext().setAttribute(noOfRecordsStr,new Integer(-1));
			return true;
		}else{
			return false;
		}
	}	
	

}
