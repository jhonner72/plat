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
@WebServlet(name = "getnextptqavoucherforcdc", urlPatterns = { "/getnextptqavoucherforcdc" })
public class UpdateCurrentAndGetNextPTQAVoucherForCdcServlet extends BasePTQAServlet{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateCurrentAndGetNextPTQAVoucherForCdcServlet() {    	
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
		
			CommonUtils.writeToLog("Inside doPost() method of UpdateCurrentAndGetNextPTQAVoucherForCdcServlet", CommonUtils.debug, null);

			try{
				//Initialize Documentum Sessions, if not.
				setOrResetPTQAProperties();
			}catch(Exception e){
				System.out.println(" PTQA Error Message  "+e.getMessage());	
				e.printStackTrace();
			}	
			
			
			String chronicleId=request.getParameter("chronicleId");
			String codelineResult=request.getParameter("codelineResult");
			
//			try{
//				if(((Integer)getServletContext().getAttribute(CDC_COUNTER)).intValue() == 0){
//				    response.setContentType("application/json");
//				    response.setCharacterEncoding("UTF-8");
//				    response.getWriter().write(json);
//				}
//			}catch(Exception e){
//				
//			}
			//IMPORTANT: HANDLE NUMBER FORMAT EXCEPTION FOR CDC_COUNTER
			

	    	if(getServletContext().getAttribute(CDC_COUNTER) == null){ //no update and no fetch
					CommonUtils.writeToLog("Admin should run query before results fetched by PTQA Screen", CommonUtils.error, null);
					PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
					ptqaVoucherObj.setI_chronicle_id("error");
				    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
					voucherObjList.add(ptqaVoucherObj);		
				    String json = new Gson().toJson(voucherObjList);

				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);	
				    
			}else if(((Integer)getServletContext().getAttribute(CDC_COUNTER)).intValue() == 0){ //no update and no fetch
				CommonUtils.writeToLog("New query should be run to fetch results. Either all vouchers are processed or no results returned from previous query.", CommonUtils.error, null);
				PTQAVoucherObject ptqaVoucherObj=new PTQAVoucherObject();
				ptqaVoucherObj.setI_chronicle_id("zero");
			    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
				voucherObjList.add(ptqaVoucherObj);		
			    String json = new Gson().toJson(voucherObjList);

			    response.setContentType("application/json");
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().write(json);
			    
			}else if(isEndOfRecords(CDC_COUNTER,CDC_NO_OF_RECORDS)){ //update, don't fetch scenario
				
				if(chronicleId != null && chronicleId.trim().length() == 16){ 
					CommonUtils.writeToLog("Invoking method updateDctmVoucherAndAuditInfoForCdc("+chronicleId+","+codelineResult+")",CommonUtils.debug,null);
					UpdateDctmVoucherAndAuditInfo udvaai=new UpdateDctmVoucherAndAuditInfo();
					udvaai.updateDctmVoucherAndAuditInfoForCdc(chronicleId, codelineResult);
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
					CommonUtils.writeToLog("Invoking method updateDctmVoucherAndAuditInfoForCdc("+chronicleId+","+codelineResult+")",CommonUtils.debug,null);
					UpdateDctmVoucherAndAuditInfo udvaai=new UpdateDctmVoucherAndAuditInfo();
					udvaai.updateDctmVoucherAndAuditInfoForCdc(chronicleId, codelineResult);
				}else{ //No update but fetch scenario
					CommonUtils.writeToLog("Null or Invalid ChronicleID. No update to Repository.",CommonUtils.debug,null);
				}
				
				synchronized(this){
					CommonUtils.writeToLog("counter value exist. ",CommonUtils.debug,null);
					ReturnPTQAVoucher obj=new ReturnPTQAVoucher((String)getServletContext().getAttribute(CDC_XMLFILENAME),PTQAProperties.getFilePath(),(Integer)getServletContext().getAttribute(CDC_COUNTER));
	//				ReturnPTQAVoucher obj=new ReturnPTQAVoucher("ptqaresultobjects.xml","c:\\",new Integer(1));
					PTQAVoucherObject ptqaVoucherObj=obj.returnPTQAVoucher();	
					incrementCounter(CDC_COUNTER);
				    List<PTQAVoucherObject> voucherObjList = new ArrayList<PTQAVoucherObject>();
					voucherObjList.add(ptqaVoucherObj);		
				    String json = new Gson().toJson(voucherObjList);
	
				    response.setContentType("application/json");
				    response.setCharacterEncoding("UTF-8");
				    response.getWriter().write(json);				
				}
			}	
	    	
	    	CommonUtils.writeToLog("Leaving doPost() method of UpdateCurrentAndGetNextPTQAVoucherForCdcServlet", CommonUtils.debug, null);
	}


}
