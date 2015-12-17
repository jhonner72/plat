package com.fxa.nab.dctm.method;

import java.io.PrintWriter;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfLogger;

public class UseSynchMethodHelper {

	private IDfSession session=null;
	
	public static final int debug=0;
	public static final int error=1;
	public static final int warning=2;
	
	public static final String RG_TABLE_YESTERDAY = "dbo.nabusersynch_yesterday";
	public static final String RG_TABLE_TODAY = "dbo.nabusersynch_today";
	
	private PrintWriter m_writer = null;
	
	public UseSynchMethodHelper(IDfSession sess,PrintWriter writer ){
		session = sess;
		m_writer = writer;
	}
	public boolean cleanYesterdayData()throws Exception{
    	
		boolean bRet = false;
		IDfCollection coll = null;
		try{
			String strQuery = "DELETE from " + RG_TABLE_YESTERDAY + "";
    		IDfQuery query = new DfQuery();
    		writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Query {" + strQuery + "}",debug,null);	
    		query.setDQL(strQuery);
    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Data Removed successfully {" + RG_TABLE_YESTERDAY + "}",debug,null);
    		}else{
    			writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Unable to remove data {" + RG_TABLE_YESTERDAY + "}",debug,null);
    		}
    	}catch(Exception eDataDelete){
    		writeToLog("{UseSynchMethodHelper}{cleanYesterdayData} Error Deleting Data",error,null);
    		eDataDelete.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
	}
	public boolean insertData()throws Exception{
    	
		boolean bRet = false;
		IDfCollection coll = null;
		try{
			String strQuery = "INSERT INTO " + RG_TABLE_TODAY + "";
			IDfQuery query = new DfQuery();
    		writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Query {" + strQuery + "}",debug,null);	
    		query.setDQL(strQuery);
    		coll = query.execute(session, IDfQuery.DF_QUERY);	    			
    		if(coll.next()){
    			bRet = true;
    			writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Data Removed successfully {" + RG_TABLE_YESTERDAY + "}",debug,null);
    		}else{
    			writeToLog("{UseSynchMethodHelper}{cleanYesterdayData}Unable to remove data {" + RG_TABLE_YESTERDAY + "}",debug,null);
    		}
    	}catch(Exception eDataDelete){
    		writeToLog("{UseSynchMethodHelper}{cleanYesterdayData} Error Deleting Data",error,null);
    		eDataDelete.printStackTrace();
    	}finally{
    		coll.close();
    	}
		return bRet;
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
