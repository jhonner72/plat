package com.fxa.nab.d2.ptqa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetNextPTQAVoucher
 */
@WebServlet(name = "getnextptqavoucher", urlPatterns = { "/getnextptqavoucher" })
public class UpdateCurrentAndGetNextPTQAVoucherServlet extends BasePTQAServlet{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateCurrentAndGetNextPTQAVoucherServlet() {    	
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
		
			CommonUtils.writeToLog("Inside doPost() method of GetNextPTQAVoucherServlet", CommonUtils.debug, null);

			try{
			//Initialize Documentum Sessions, if not.
				setOrResetPTQAProperties();
			}catch(Exception e){
				System.out.println(" PTQA Error Message  "+e.getMessage());	
				e.printStackTrace();
			}			
			

	    	if(getServletContext().getAttribute("counter") == null){
					CommonUtils.writeToLog("Admin should run query before results fetched by PTQA Screen", CommonUtils.error, null);
					PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
					ptqaVoucherObj.setI_chronicle_id("error");
				    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
					voucherObjList.add(ptqaVoucherObj);		
				    String json = new Gson().toJson(voucherObjList);

				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);					
			}else if(((Integer)getServletContext().getAttribute("counter")).intValue() == 0){
				CommonUtils.writeToLog("New query should be run to fetch results. Either all vouchers are processed or no results returned from previous query.", CommonUtils.error, null);
				PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
				ptqaVoucherObj.setI_chronicle_id("zero");
			    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
				voucherObjList.add(ptqaVoucherObj);		
			    String json = new Gson().toJson(voucherObjList);

			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);					
			}else if(isEndOfRecords()){
				CommonUtils.writeToLog("New query should be run to fetch results. Either all vouchers are processed or no results returned from previous query.", CommonUtils.error, null);
				PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
				ptqaVoucherObj.setI_chronicle_id("reset");
			    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
				voucherObjList.add(ptqaVoucherObj);		
			    String json = new Gson().toJson(voucherObjList);

			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);					
			}else{
				
				String chronicleId=request.getParameter("chronicleId");
				String codelineResult=request.getParameter("codelineResult");
				String amountResult=request.getParameter("amountResult");
				CommonUtils.writeToLog("Invoking method updateDctmVoucherAndAuditInfo("+chronicleId+","+codelineResult+","+amountResult+")",CommonUtils.debug,null);
				UpdateDctmVoucherAndAuditInfo udvaai=new UpdateDctmVoucherAndAuditInfo();
				udvaai.updateDctmVoucherAndAuditInfo(chronicleId, codelineResult, amountResult);
				
				synchronized(this){
				CommonUtils.writeToLog("counter value exist. ",CommonUtils.debug,null);
				ReturnPTQAVoucher obj=new ReturnPTQAVoucher((String)getServletContext().getAttribute(XMLFILENAME),PTQAProperties.getFilePath(),(Integer)getServletContext().getAttribute(COUNTER));
//				ReturnPTQAVoucher obj=new ReturnPTQAVoucher("ptqaresultobjects.xml","c:\\",new Integer(1));
				PTQAVoucherObject ptqaVoucherObj=obj.returnPTQAVoucher();	
				incrementCounter();
			    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
				voucherObjList.add(ptqaVoucherObj);		
			    String json = new Gson().toJson(voucherObjList);

			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);				
				}
			}	
	    	
	    	CommonUtils.writeToLog("Leaving doPost() method of GetNextPTQAVoucherServlet", CommonUtils.debug, null);
	}
	
	private void incrementCounter(){
		Integer counterInteger=(Integer)getServletContext().getAttribute("counter");
		int counter=counterInteger.intValue()+1;
		getServletContext().setAttribute("counter",new Integer(counter));
	}
	
	private boolean isEndOfRecords(){
		int counterint=((Integer)getServletContext().getAttribute("counter")).intValue();
		int noOfRecords=((Integer)getServletContext().getAttribute("no_of_records")).intValue();
		CommonUtils.writeToLog("Value is counter is "+counterint+"; Value of no_of_records is "+noOfRecords, CommonUtils.debug, null);
		if(counterint > noOfRecords){
			CommonUtils.writeToLog("Resetting Servlet Context Params counter to 0 and no_of_records to -1", CommonUtils.debug, null);
			getServletContext().setAttribute("counter",new Integer(0));
			getServletContext().setAttribute("no_of_records",new Integer(-1));
			return true;
		}else{
			return false;
		}
	}

}
