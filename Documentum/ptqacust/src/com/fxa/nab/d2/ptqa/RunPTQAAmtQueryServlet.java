package com.fxa.nab.d2.ptqa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RunPTQAQuery
 */
@WebServlet(name = "runptqaamtquery", urlPatterns = { "/runptqaamtquery" })
public class RunPTQAAmtQueryServlet extends BasePTQAServlet {
	
	public static final String SUCCESS="1";
	public static final String FAIL_NO_RESULTS_GEN="-1";
	public static final String FAIL_NO_XML_GEN="-2";
//	public static final String FAIL_UNKNOWN_ERR="-3";
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RunPTQAAmtQueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CommonUtils.writeToLog("Inside doPost() method of RunPTQAAmtQueryServlet", CommonUtils.debug, null);
		// TODO Auto-generated method stub
		String date=request.getParameter("proc_date");
		boolean isAmtOtherwiseCodeline=true; //IMPORTANT: DON'T CHANGE THIS VALUE. IT MUST BE TRUE FOR AMOUNT PROCESSING
		
		CommonUtils.writeToLog("Processing Date is "+date,CommonUtils.debug,null);	
		
		try{
			//Set/Reset all properties file and initialize Documentum Sessions, if not.
			setOrResetPTQAProperties();
			
			RunPTQAQueryAndStoreResultsToXML gpvastx=new RunPTQAQueryAndStoreResultsToXML(); //Important: new instance must be created always
			int no_of_records=gpvastx.retrieveAndStorePTQAVouchers(date,isAmtOtherwiseCodeline );		
			
		    response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
		    response.setCharacterEncoding("UTF-8"); 
		    
		    
			if(no_of_records > 0){			
				//Write the output to XML
				String fileName=gpvastx.writeToXML(date,isAmtOtherwiseCodeline,PTQAProperties.xmlnameprefixforamt);
				
				if(fileName != null && fileName.trim().length() > 0){

					CommonUtils.writeToLog("Setting ServletContext param ["+AMT_XMLFILENAME+"] value to ["+fileName+"]", CommonUtils.debug, null);	
					getServletContext().setAttribute(AMT_XMLFILENAME, fileName);
					
					CommonUtils.writeToLog("Setting ServletContext param ["+AMT_COUNTER+"] value to [1]", CommonUtils.debug, null);
					getServletContext().setAttribute(AMT_COUNTER, new Integer(1));		
					
					CommonUtils.writeToLog("Setting ServletContext param ["+AMT_NO_OF_RECORDS+"] value to ["+no_of_records+"]", CommonUtils.debug, null);
					getServletContext().setAttribute(AMT_NO_OF_RECORDS, new Integer(no_of_records));					

				    response.getWriter().write(Integer.toString(no_of_records)); //Query successfully run and results generated and stored to xml.
					
				}else{
					CommonUtils.writeToLog("Failed to set ServletContext param 'xmlfilename' value as it is null or emptry string", CommonUtils.error, null);
					CommonUtils.writeToLog("Resetting Servlet Context Params counter to 0 and no_of_records to -1", CommonUtils.warn, null);
					getServletContext().setAttribute(AMT_COUNTER, new Integer(0));
					getServletContext().setAttribute(AMT_NO_OF_RECORDS, new Integer(-1));	
					getServletContext().setAttribute(AMT_XMLFILENAME, "");
					response.getWriter().write(FAIL_NO_XML_GEN); 
					throw new FXAException("Failed to set ServletContext param 'xmlfilename' value as it is null or emptry string");
				}

			}else{
				getServletContext().setAttribute(AMT_COUNTER, new Integer(0));
				getServletContext().setAttribute(AMT_NO_OF_RECORDS, new Integer(-1));
				getServletContext().setAttribute(AMT_XMLFILENAME, "");
			    response.getWriter().write(FAIL_NO_RESULTS_GEN); 
				CommonUtils.writeToLog("Resetting Servlet Context Params counter to 0 and no_of_records to -1", CommonUtils.debug, null);
			}

		    
		}catch(Exception e){
			CommonUtils.writeToLog(" PTQA Error Message  "+e.getMessage(),CommonUtils.error, null);	
			e.printStackTrace();
		}
		CommonUtils.writeToLog("Leaving doPost() method of RunPTQAAmtQueryServlet", CommonUtils.debug, null);
	}

}
