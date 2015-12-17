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
@WebServlet(name = "getnextptqavoucherforamt", urlPatterns = { "/getnextptqavoucherforamt" })
public class UpdateCurrentAndGetNextPTQAVoucherForAmtServlet extends BasePTQAServlet{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateCurrentAndGetNextPTQAVoucherForAmtServlet() {    	
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
		
			CommonUtils.writeToLog("Inside doPost() method of UpdateCurrentAndGetNextPTQAVoucherForAmtServlet", CommonUtils.debug, null);

			try{
			//Initialize Documentum Sessions, if not.
				setOrResetPTQAProperties();
			}catch(Exception e){
				System.out.println(" PTQA Error Message  "+e.getMessage());	
				e.printStackTrace();
			}	
			
			String chronicleId=request.getParameter("chronicleId");
			String amountResult=request.getParameter("amountResult");
			
			//IMPORTANT: HANDLE NUMBER FORMAT EXCEPTION FOR CDC_COUNTER
			
	    	if(getServletContext().getAttribute(AMT_COUNTER) == null){
					CommonUtils.writeToLog("Admin should run query before results fetched by PTQA Screen", CommonUtils.error, null);
					PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
					ptqaVoucherObj.setI_chronicle_id("error");
				    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
					voucherObjList.add(ptqaVoucherObj);		
				    String json = new Gson().toJson(voucherObjList);

				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);					
			}else if(((Integer)getServletContext().getAttribute(AMT_COUNTER)).intValue() == 0){
				CommonUtils.writeToLog("New query should be run to fetch results. Either all vouchers are processed or no results returned from previous query.", CommonUtils.error, null);
				PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
				ptqaVoucherObj.setI_chronicle_id("zero");
			    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
				voucherObjList.add(ptqaVoucherObj);		
			    String json = new Gson().toJson(voucherObjList);

			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);
			    
			}else if(isEndOfRecords(AMT_COUNTER,AMT_NO_OF_RECORDS)){//update, don't fetch scenario
				
				if(chronicleId != null && chronicleId.trim().length() == 16){ 			
					CommonUtils.writeToLog("Invoking method updateDctmVoucherAndAuditInfoForAmt("+chronicleId+","+amountResult+")",CommonUtils.debug,null);
					UpdateDctmVoucherAndAuditInfo udvaai=new UpdateDctmVoucherAndAuditInfo();
					udvaai.updateDctmVoucherAndAuditInfoForAmt(chronicleId,  amountResult);
				}
				
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

				if(chronicleId != null && chronicleId.trim().length() == 16){ //update and fetch scenario;				
					CommonUtils.writeToLog("Invoking method updateDctmVoucherAndAuditInfoForAmt("+chronicleId+","+amountResult+")",CommonUtils.debug,null);
					UpdateDctmVoucherAndAuditInfo udvaai=new UpdateDctmVoucherAndAuditInfo();
					udvaai.updateDctmVoucherAndAuditInfoForAmt(chronicleId,  amountResult);
				}else{ //No update but fetch scenario
					CommonUtils.writeToLog("Null or Invalid ChronicleID. No update to Repository.",CommonUtils.debug,null);
				}
				
				synchronized(this){
					CommonUtils.writeToLog("counter value exist. ",CommonUtils.debug,null);
					ReturnPTQAVoucher obj=new ReturnPTQAVoucher((String)getServletContext().getAttribute(AMT_XMLFILENAME),PTQAProperties.getFilePath(),(Integer)getServletContext().getAttribute(AMT_COUNTER));
	//				ReturnPTQAVoucher obj=new ReturnPTQAVoucher("ptqaresultobjects.xml","c:\\",new Integer(1));
					PTQAVoucherObject ptqaVoucherObj=obj.returnPTQAVoucher();	
					incrementCounter(AMT_COUNTER);
				    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
					voucherObjList.add(ptqaVoucherObj);		
				    String json = new Gson().toJson(voucherObjList);
	
				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);				
				}
			}	
	    	
	    	CommonUtils.writeToLog("Leaving doPost() method of UpdateCurrentAndGetNextPTQAVoucherForAmtServlet", CommonUtils.debug, null);
	}


}
