	package com.fxa.nab.dctm.method;

	/**
	 * Author: Ajit Dangal
	 * Project: NAB Cheques
	 *
	 * This is workflow method called from workflow activity, this sets attribute values 
	 * on fxa_voucher when workflow activity calls this method
	 * 
	 */	
	import com.documentum.fc.client.*;
	import com.documentum.fc.common.*;

	import com.documentum.fc.methodserver.IDfMethod;

	import java.util.*;
	import java.io.PrintWriter;

	public class WorkflowPostTrasQAMethod implements IDfMethod
	{

	    protected IDfSessionManager m_sessionMgr = null;
	    protected String m_docbase = null;
	    protected String m_userName = null;
	    protected String m_workitemId = null;
	    protected String m_ticket = null;
	    

	    private static final String USER_KEY = "user";
	    private static final String DOCBASE_KEY = "docbase_name";
	    private static final String WORKITEM_KEY_2 = "workitemId";  
	    private static final String TICKET_KEY = "ticket";
	    private static final String WORKITEM_KEY = "packageId";
	    
	    private IDfId docIdObj;

		public static final int debug=0;
		public static final int error=1;
		public static final int warning=2;
		private PrintWriter m_writer = null;
		private boolean bAbort = false;
	   
	    @Override
		public int execute(Map params, PrintWriter pstream) throws Exception {
	    
	    	writeToLog("{WorkflowPostTrasQAMethod}{execute}Starting ....",debug,null);
	    	m_writer = pstream;
	    	initWorkflowParams(params);
	        IDfSessionManager sessionManager = login();
	        IDfSession session = null;
	        String strPassFail = "ERROR";
	        
	        try {
	            IDfId workitemID = new DfId(m_workitemId);
	            session = sessionManager.getSession(m_docbase);
	            IDfWorkitem workitem = (IDfWorkitem)session.getObject(workitemID);
	            
	            IDfId workflowID = workitem.getWorkflowId(); 


	            workitem.acquire();
	            
	              
	            IDfCollection pkgColl = null;		
		    			//System.out.println("Getting packages and promoting document(s)");
	            writeToLog("{WorkflowPostTrasQAMethod}{execute}Getting voucher(s)",debug,null);
	            pkgColl = workitem.getPackages("");
	            String strAuditStatus = "NOUPDATE";
	            
	            if (pkgColl != null)
	                 {
	                  while (pkgColl.next())
	                    {
				 			
				 			String docId = pkgColl.getString("r_component_id");
	                        // System.out.println(docId);
	                         writeToLog("DocId" + docId, debug,null);
	                         int docCount = pkgColl.getValueCount("r_component_id");
	                             
	                         for (int i=0; i <=(docCount-1); i++)
	                               {
	                                 docIdObj = pkgColl.getRepeatingId("r_component_id", i);
	                                 if (docIdObj!=null) 
	                                    {
	                                      IDfId sysobjID = new DfId(docId);
	                                      IDfSysObject doc = (IDfSysObject)session.getObject(sysobjID);
	                                      writeToLog("Doc Name" + doc.getObjectName(), debug,null);
	                                      //doc.promote(lifeCycleState, lifeCycleOverride, lifeCycleTestOnly);
	                                      //IDfId queueItemId = workitem.getQueueItemId();
	                                      //IDfQueueItem qi = (IDfQueueItem) session.getObject(queueItemId);
	                                      //writeToLog("item name" + qi.getItemName(),debug,null);
	                                      IDfActivity dfActivity=workitem.getActivity();
	                                      String actName = dfActivity.getString("object_name");
	                                      writeToLog("Activity Name"+ actName,debug,null);
	                                      
	                                      if(actName.equalsIgnoreCase("End")){
	                                    	//just complete to go to end 
	                                    	  //doc.setString("fxa_release_flag","COMPLETE");
	                                    	  writeToLog("{WorkflowPostTrasQAMethod}{execute}workflow complete",debug,null);
	                                      }else{
	                                    	  if (doc.isCheckedOut()){
	                                    		  writeToLog("{WorkflowPostTrasQAMethod}{execute}Unable to checkout",debug,null);
	                                    		  throw new Exception ("Document is locked or already checkout!!");

	                                    	  }else{
	                                    		  writeToLog("{WorkflowPostTrasQAMethod}{execute}Check out called",debug,null);
	                                    		  doc.checkout();  
	                                    	  }                                  
	                                          if(actName.equalsIgnoreCase("pass")){
		                                    	  //doc.setString("fxa_release_flag","RELEASE");
	                                        	  strAuditStatus = "Passed";
		                                      }
		                                      if(actName.equalsIgnoreCase("fail")){
		                                    	 // doc.setString("fxa_release_flag","REJECT");
		                                    	  strAuditStatus = "Failed";
		                                      }                                      
		                                      if(actName.equalsIgnoreCase("abort")){
		                                    	 // doc.setString("fxa_release_flag","ABORT");
		                                    	  strAuditStatus = "Aborted";
		                                      } 
		                                      if(actName.equalsIgnoreCase("NotifyIncomingPool")){
		                                    	//just complete to go to end 
		                                    	 // doc.setString("fxa_release_flag","ABORTED");
		                                    	  //bAbort = true;
		                                    	 // AbortWorkflow(session,workflowID.getId());
		                                    	 // writeToLog("{WorkflowPostTrasQAMethod}{execute}workflow aborted",debug,null);
		                                      }
		                                      doc.setString("fxa_qa_post_tran_flag","false");                                      
		                                      //doc.save();
		                                      if (doc.isCheckedOut()){
		                                    	  writeToLog("{WorkflowPostTrasQAMethod}{execute}Check in called",debug,null);
		                                    	  doc.checkin(false,"CURRENT");
		                                    	  writeToLog("{WorkflowPostTrasQAMethod}{execute}New ID" +
		                                    			  doc.getObjectId().toString(),debug,null);
		                                      }else{
		                                    	  writeToLog("{WorkflowPostTrasQAMethod}{execute}Unable to checkin",debug,null);
		                                    	  throw new Exception ("Document should be checked in checked out in same session !!");
		                                      }
		                                      writeToLog("{WorkflowPostTrasQAMethod}{execute}Update attribute",debug,null);
		                                      // Update audit table
		                                      writeToLog("{WorkflowPostTrasQAMethod}{execute}Updating Audit Table",debug,null);
		                                      StringBuffer qryBuff = new StringBuffer("INSERT INTO dm_dbo.fxa_voucher_audit");
		                                      qryBuff.append("(");
		                                      qryBuff.append("i_chronicle_id,");
		                                      qryBuff.append("subject_area,");
		                                      qryBuff.append("attribute_name,");
		                                      qryBuff.append("pre_value,");
		                                      qryBuff.append("post_value");
		                                      qryBuff.append(")");
		                                      qryBuff.append(" VALUES ");
		                                      qryBuff.append("(");
		                                      qryBuff.append("'");
		                                      qryBuff.append(getChronicalId(session,docId));
		                                      qryBuff.append("'");
		                                      qryBuff.append(",");
		                                      qryBuff.append("'");
		                                      qryBuff.append("WF Post Transmission QA");
		                                      qryBuff.append("'");
		                                      qryBuff.append(",");
		                                      qryBuff.append("'");
		                                      qryBuff.append("STATUS");
		                                      qryBuff.append("'");
		                                      qryBuff.append(",");
		                                      qryBuff.append("'");
		                                      qryBuff.append("true");
		                                      qryBuff.append("'");
		                                      qryBuff.append(",");
		                                      qryBuff.append("'");
		                                      qryBuff.append(strAuditStatus);
		                                      qryBuff.append("'");
		                                      qryBuff.append(")");                               
		                                      	                                      
		                                      /* i_chronicle_id string(40),
		                                	subject_area string(50),
		                                	attribute_name string(50),
		                                	pre_value string(50),
		                                	post_value string(50)*/
		                                  	
		                                      updateAuditTable(session,qryBuff.toString());
		                                      writeToLog("{WorkflowPostTrasQAMethod}{execute}Audit Table updated",debug,null);
	                                     }//end of else - check end 
	                                }// docid null   
	                               }// end of for
	                     }// end of while
	                     pkgColl.close();
	                   }		
	            workitem.complete();

	            return 0;
	           
	        } 
	        catch (DfException e)
	        {
	                writeToLog(e.getMessage(),debug,null);
	                e.printStackTrace();    // spit out to stderr as well
	                throw e;
	        } finally
	        {
	            if ( session != null ){
	                sessionManager.release(session);
	                writeToLog("{WorkflowPostTrasQAMethod}{execute}Ending ....",debug,null);
	            }
	            
	        }
	        
	    }
	    
	    private void AbortWorkflow(IDfSession session, String workflow_object_id)throws DfException{
	    	
	    	IDfWorkflow workflowObj = (IDfWorkflow)session.getObject(new DfId(workflow_object_id)); 
	    	String workflow_object_name = workflowObj.getObjectName();
	    	if (workflowObj.getRuntimeState() == 1 || workflowObj.getRuntimeState() == 3) // 1 = running, 3 = halted  
	    	{  
	    		//Log("Stopping all activities for workflow, " + workflowObj.getObjectName() + ", workflow Object ID, " + workflowALLIDsList[rowIndex]);   
	    		writeToLog("Stopping all activities for workflow, " + workflow_object_name + ", workflow Object ID, " + workflow_object_id,debug,null);   
	    		workflowObj.abort();  
	    		writeToLog("Deleting workflow, " + workflow_object_name +
	    				", workflow Object ID, " + workflow_object_id,debug,null);   
	    		workflowObj.destroy();   
	    		writeToLog("Deleted Workflow, " + workflow_object_name +
	    				", workflow Object ID, " + workflow_object_id,debug,null);   

	      
	    	}else {// Any other workflows state -- delete the workflow from the repository  
	    	
	    		writeToLog("Deleting workflow, " + workflow_object_name +
	    				", workflow Object ID, " + workflow_object_id,debug,null);   
	    		workflowObj.destroy();  
	    		writeToLog("Deleted Workflow, " + workflow_object_name +
	    				", workflow Object ID, " + workflow_object_id,debug,null);   

	    	} 
	    }
	    /**
	     * 
	     * @param params
	     */
	    protected void initWorkflowParams(Map params)
	    {
	        // get the 4 WF-related parameters always passed in by Server
	       Set keys = params.keySet();
	       Iterator iter = keys.iterator();
	       while (iter.hasNext())
	       {
	           String key = (String) iter.next();
	           if( (key == null) || (key.length() == 0) )
	           {
	               continue;
	           }
	           String []value = (String[])params.get(key);
	           writeToLog("Key:{"+ key + "}",debug,null);
	           writeToLog("value:{"+ value[0] + "}",debug,null);
	           if ( key.equalsIgnoreCase(USER_KEY) )
	               m_userName = (value.length > 0) ? value[0] : "";
	           else if ( key.equalsIgnoreCase(DOCBASE_KEY) )
	               m_docbase = (value.length > 0) ? value[0] : "";
	           else if ( key.equalsIgnoreCase(WORKITEM_KEY_2 ) )
	               m_workitemId = (value.length > 0) ? value[0] : "";
	           else if ( key.equalsIgnoreCase(WORKITEM_KEY ) )
	               m_workitemId = (value.length > 0) ? value[0] : "";
	           else if ( key.equalsIgnoreCase(TICKET_KEY) )
	               m_ticket = (value.length > 0) ? value[0] : "";
	               
	               
	       }
	   }
	   /**
	    * 
	    * @param session
	    * @param objId
	    * @return chronicle id
	    * @throws DfException
	    * Return chronicle ID or return same object id to be stored in audit table
	    */
	    protected String getChronicalId(IDfSession session,String objId) throws DfException{
	    	
	    	String qry = "select i_chronicle_id from dm_document (ALL) where r_object_id='" + objId + "'";
	    	writeToLog("{WorkflowPostTrasQAMethod}{getChronicalId}Query {" + qry + "}",debug,null);		
	    	String returnChronicalId = objId;
	    	IDfQuery query = new DfQuery();
	    			query.setDQL(qry);
	    			IDfCollection coll = query.execute(session, IDfQuery.DF_QUERY);
	    	try{		
	    			int updateCount = 0;
	    			if (coll.next())
	    			{
	    				returnChronicalId = coll.getString("i_chronicle_id");
	    				writeToLog("{getChronicalId}Record Chronicle Id {" + returnChronicalId + "}",debug,null);
	    			}else{
	    				writeToLog("{getChronicalId}Unable to find chronicle id",debug,null);
	    			}
	    	}catch(Exception e){
	    		writeToLog("{getChronicalId}Unable to Update record "  ,debug,null);
	    		e.printStackTrace();
	    		
	    	}finally{
	    			
	    			coll.close();
	    	}
	    	return returnChronicalId;
	    }
	    /**
	     * 
	     * @param session
	     * @param qry
	     * @throws DfException
	     */
	    protected void updateAuditTable(IDfSession session,String qry) throws DfException{
	    	
	    	
	    	writeToLog("{WorkflowPostTrasQAMethod}{updateAuditTable}Query {" + qry + "}",debug,null);		
	    	IDfQuery query = new DfQuery();
	    			query.setDQL(qry);
	    			IDfCollection coll = query.execute(session, IDfQuery.DF_QUERY);
	    	try{		
	    			int updateCount = 0;

	    			if (coll.next())
	    			{
	    				writeToLog("{updateAuditTable}Record updated in Audit Table ",debug,null);
	    			}else{
	    				writeToLog("{updateAuditTable}Unable to Update record ",debug,null);
	    			}
	    	}catch(Exception e){
	    		writeToLog("{updateAuditTable}Unable to Update record "  ,debug,null);
	    		e.printStackTrace();
	    		
	    	}finally{
	    			
	    			coll.close();
	    	}
	    }
	    
	   protected IDfSessionManager login() throws DfException
	   {
	       if (m_docbase == null || m_userName == null || m_ticket == null )
	           return null;
	           
	       // now login
	       IDfClient dfClient = DfClient.getLocalClient();

	       if (dfClient != null)
	       {
	           IDfLoginInfo li = new DfLoginInfo();
	           li.setUser(m_userName);
	           li.setPassword(m_ticket);
	           li.setDomain(null);

	           IDfSessionManager sessionMgr = dfClient.newSessionManager();
	           sessionMgr.setIdentity(m_docbase, li);
	           writeToLog("{login}Session created Docbase{"+ m_docbase + "}",debug,null);
	           writeToLog("{login}Session created User{"+ m_userName + "}",debug,null);
	           //writeToLog("{login}Session created Ticket{"+ m_ticket + "}",debug,null);
	           return sessionMgr;
	       }

	       return null;
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

	}

