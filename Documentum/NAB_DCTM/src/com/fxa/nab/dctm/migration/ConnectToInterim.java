package com.fxa.nab.dctm.migration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectToInterim implements IConnectToInterim {

	public static final int debug=CommonUtils.debug;
	public static final int info=CommonUtils.info;
	public static final int warn=CommonUtils.warn;
	public static final int error=CommonUtils.error;
	
	private Connection conn=null;
	public static HashMap<String,String> db_dctm_attr_mapping;
	
	static{
	
		db_dctm_attr_mapping=new HashMap<String,String>();
		db_dctm_attr_mapping.put("object_name","Front_image_file_name");
		db_dctm_attr_mapping.put("fxa_processing_date","ProcessingDate");
		db_dctm_attr_mapping.put("fxa_extra_aux_dom","Extra_aux_dom");	
		db_dctm_attr_mapping.put("fxa_aux_dom","Aux_dom");	
		db_dctm_attr_mapping.put("fxa_bsb","Bsb");
		db_dctm_attr_mapping.put("fxa_account_number","Account_no");	
		db_dctm_attr_mapping.put("fxa_trancode","Trancode");
		db_dctm_attr_mapping.put("fxa_amount","Amount");
		db_dctm_attr_mapping.put("fxa_drn","Drn");
		db_dctm_attr_mapping.put("fxa_classification","Classification");
		db_dctm_attr_mapping.put("fxa_collecting_bsb","Collecting_bsb");	
		db_dctm_attr_mapping.put("fxa_m_entry_number","Entry_no");
		db_dctm_attr_mapping.put("fxa_m_batch_number","Batch_no");	
		db_dctm_attr_mapping.put("fxa_m_bal_seq_for_deposit","Bal_seq_for_deposit");	
		db_dctm_attr_mapping.put("fxa_m_balanced_sequence","Item_sequence");
		db_dctm_attr_mapping.put("fxa_checksum","Md5_value_received");
		
		//Computed properties
		db_dctm_attr_mapping.put("fxa_migration_batch_no",""); //GET IT FROM MANIFEST COLUMN 'EntryPayloadFile'
		db_dctm_attr_mapping.put("fxa_cr_drn","");//RUN TIME COMPUTATION
		db_dctm_attr_mapping.put("fxa_checksum_type",""); //FIXED VALUE 'MD5'
	}

	
//   	String connectInfo="jdbc:sqlserver:VSQLD-NABHIST\\SQL1:1433;databaseName=VDBD_NABHIST;user=NABIngest_SVC_D;password=Holiday15";
//	
//	String sandboxConnectInfo="jdbc:sqlserver://VSQLD-DCP\\SQL3;databaseName=VDBD_NABDC01;user=NABDC_SQL_D;password=NaBDC1;";
//	
//	String myVMConnect="jdbc:sqlserver://DCTMMELB;databaseName=VDBD_NABHIST;user=dctmusr;password=dctmusr01;";
	
	//Connection info repo database for CS: SVME-9612DCP; instance: VSQLD-DCP\SQL3; login: NABDC_SQL_D; pwd: NaBDC1
	
	//String connectInfo="jdbc:sqlserver://"+instance+";databaseName="+database+";user="+dbuser+";password="+dbpwd;
	
	private Connection createConnection() throws SQLException {
		// TODO Auto-generated method stub
		String instance=BMProperties.dbinstancename;
		String database=BMProperties.dbname;
		String dbuser=BMProperties.dbusername;
		String dbpwd=BMProperties.dbpassword;
		String dbport=BMProperties.dbport;
				
		String connectInfo="jdbc:sqlserver://"+instance+":"+dbport+";databaseName="+database+";user="+dbuser+";password="+dbpwd;
//		String connectInfo="jdbc:sqlserver://"+instance+";databaseName="+database+";integratedSecurity=true;trustServerCertificate=true;";
//		String connectInfo="jdbc:sqlserver://VSQLD-NABHIST\\SQL1:1433;databaseName=VDBD_NABHIST;user=NABCS\\NABIngest_SVC_D;password=Holiday15;useNTLMv2=true;domain=NABCS";
//		String devRepoConnectInfo="jdbc:sqlserver://VSQLD-NABHIST\\SQL1:1433;databaseName=VDBD_NABDC;user=NABDC_SQL_D;password=N4bDCSQL";
//		String myVMConnect="jdbc:sqlserver://DCTMMELB;databaseName=VDBD_NABHIST;user=dctmusr;password=dctmusr01;";
//
//		String sandboxConnectInfo="jdbc:sqlserver://VSQLD-DCP\\SQL3;databaseName=VDBD_NABDC01;user=NABDC_SQL_D;password=NaBDC1;";
		
		try {
//			System.out.println("Connection "+connectInfo);
			conn=DriverManager.getConnection(connectInfo);
			CommonUtils.writeToLog("Connection String is : \n"+connectInfo,debug, null);
//			conn=DriverManager.getConnection(sandboxConnectInfo);
			CommonUtils.writeToLog("Database Connection created succesfully!",debug, null);
			System.out.println("Database Connection created succesfully!");
		} catch (SQLException e) {
			CommonUtils.writeToLog("Method createConnection() : Exception while creating connection to Database!! ",error, e);
			
			CommonUtils.writeToLog(e.getMessage(),error, e);
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return conn;
	}
	
	@Override
	public Connection getConnection() throws Exception {
		
		if(conn == null){
			conn=createConnection();
			if(conn == null){
				CommonUtils.writeToLog("Connection to database couldn't be created. Check the details in Properties file and the network connectivity.", error, null);
				throw new FXAInitAndValidateException("ERROR!!! Database Connection object is null.");
			}else{
				return conn;
			}
		}else{
			return conn;
		}
	}
	
	public void closeConnection() {
		if(conn!=null){
			try {
				conn.close();
				CommonUtils.writeToLog("Database Connection closed succesfully!",debug, null);
			} catch (SQLException e) {
				CommonUtils.writeToLog("Exception while closing the database connection : "+e.getMessage(),debug, null);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public ResultSet readQuery(String query){
		Statement st;
		try {
			st = getConnection().createStatement();
			ResultSet rs= st.executeQuery(query);
			CommonUtils.writeToLog("Method readQuery(): [ "+query+" ] executed successfully", debug, null);
			return rs;
		} catch (SQLException e) {
			CommonUtils.writeToLog("Method readQuery(): the following query failed : ", error, null);
			CommonUtils.writeToLog(query, error, e);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, null);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			CommonUtils.writeToLog("Method readQuery(): the following query failed : ", error, null);
			CommonUtils.writeToLog(query, error, e);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, null);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CommonUtils.writeToLog("Method readQuery(): Failed to execute query [ "+query+" ]. Returning 'null'.", debug, null);
		return null;
	}
	
	@Override
	public int updateQuery(String query){
		Statement st;
		try {
			st = getConnection().createStatement();
			int noOfRowsUpdated= st.executeUpdate(query);
			CommonUtils.writeToLog("Method updateQuery(): '"+noOfRowsUpdated+"' affected by updateQuery : "+query, debug, null);
			return noOfRowsUpdated;
		} catch (SQLException e) {
			CommonUtils.writeToLog("Method updateQuery(): the following query failed : ", error, null);
			CommonUtils.writeToLog(query, error, e);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, null);
			e.printStackTrace();
		} catch(Exception e){
			CommonUtils.writeToLog("Method updateQuery(): the following query failed : ", error, null);
			CommonUtils.writeToLog(query, error, e);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, null);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CommonUtils.writeToLog("Method updateQuery(): Failed to execute query [ "+query+" ]. Returning '0'.", debug, null);
		return 0;
	}
	
	
	public List<Manifest> getManifestToProcess(int batchPerCycle) throws Exception{
		CommonUtils.writeToLog("Inside method getManifestToProcess(...) ", debug, null);
		boolean isException=false;

		List<Manifest> list=new ArrayList<Manifest>();
		int id=0;
		
		//The whole code is written in a transaction.
		Connection conn=null;
		try {
			conn=getConnection();
			CommonUtils.writeToLog("Method:getManifestToProcess - Transaction started....",debug,null);
			conn.setAutoCommit(false);
			
			String query=String.format(BMProperties.batch_pickup_query,batchPerCycle,BMProperties.batch_pickup_status,BMProperties.batch_pickup_reconcile_status);
			CommonUtils.writeToLog("Query used to pickup batch is ["+query+"]", debug, null);
			ResultSet rs= readQuery(query);
			StringBuffer idConcat=new StringBuffer();
			
				while(rs.next()){
					   id=rs.getInt("ManifestID");
					   Manifest mf=new Manifest(id,rs.getString("Day"),rs.getString("Entry"),rs.getString("OutputPath"),rs.getString("BatchName"),rs.getString("Status"),rs.getString("DctmIngestStatus"),rs.getString("DctmIngestedDates"));
//					   if(rs.last())
//						   idConcat=idConcat.append(id);
//					   else
						   idConcat=idConcat.append(id+",");
					   
					   list.add(mf);
			    }
			
				
			CommonUtils.writeToLog("Manifest ID to be processed are ["+idConcat+"]", debug, null);	
			String idConcatStr=idConcat.toString();
			
			if(idConcatStr == null || idConcatStr.trim().length() == 0){
				CommonUtils.writeToLog("No batches available for processing.",warn,null);
			}else{
			String updateQuery=	String.format("update Manifest set Status='%s' where ManifestID in (%s)",BMProperties.batch_upload_inprogress_status,idConcatStr.substring(0,idConcatStr.length()-1));
			CommonUtils.writeToLog("Query to be used to update the status is ["+updateQuery+"]",debug,null);
			updateQuery(updateQuery);
			}
			
			conn.commit();
			CommonUtils.writeToLog("Transaction committed.",debug,null);
			
		} catch (SQLException e) {
			isException=true;
			CommonUtils.writeToLog("ERROR!! : Exception from Method getManifestToProcess() ", error, null);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, e);
			throw e;
//			e.printStackTrace();
		} catch (Exception e) {
			isException=true;
			CommonUtils.writeToLog("ERROR!! : Exception from Method getManifestToProcess() ", error, null);
			CommonUtils.writeToLog("Exception is "+e.getMessage(), error, e);
			throw e;			
//			e.printStackTrace();
		} finally{
			try {
				
				if(isException){conn.rollback();}
				
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				CommonUtils.writeToLog("ERROR!! : Exception from 'finally' section of Method getManifestToProcess() while setAutoCommit(true). ", error, null);
				CommonUtils.writeToLog("Exception is "+e.getMessage(), error, e);
				throw e;
			}catch (Exception e) {
				// TODO Auto-generated catch block
				CommonUtils.writeToLog("ERROR!! : Exception from 'finally' section of Method getManifestToProcess() while setAutoCommit(true). ", error, null);
				CommonUtils.writeToLog("Exception is "+e.getMessage(), error, e);
				throw e;				
			}
		}
		return list;
	}	
	
	public ArrayList<FxaVoucher> createVouchersListForABatch(String batchName,String processingDate,String entryNumber,HashMap<String,String> transactionAndDRN) throws SQLException{
		  String voucherQuery=String.format("select ProcessingDate,Entry_no,Created,Status,ProcessingDate,Front_image_file_name,Extra_aux_dom,Aux_dom,Bsb,Account_no,Trancode,Amount,Drn,Classification,Collecting_bsb,Entry_no,Batch_no,Bal_seq_for_deposit,Item_sequence,Md5_value_received from Voucher where Day='%s' and Entry='%s'",processingDate,entryNumber);
		  CommonUtils.writeToLog("Query used to pickup batch is ["+voucherQuery+"]", debug, null);
		  ResultSet rs= readQuery(voucherQuery);
		  ArrayList<FxaVoucher> voucherList=new ArrayList<FxaVoucher>();
		  FxaVoucher voucher=null;
		  StringBuffer messageBuffer=null;
		  
//		  String batchNo="nab_proce"
		  try{
			 int count=0;
		     while(rs.next()){
		      messageBuffer=new StringBuffer("");
			  voucher=new FxaVoucher();
			  
			  voucher.setFxa_account_number(rs.getString(db_dctm_attr_mapping.get("fxa_account_number")));
			  messageBuffer.append("fxa_account_number : "+voucher.getFxa_account_number());
			  
			  voucher.setFxa_amount(rs.getString(db_dctm_attr_mapping.get("fxa_amount")));
			  messageBuffer.append("; fxa_amount : "+voucher.getFxa_amount());
					  
			  voucher.setFxa_aux_dom(rs.getString(db_dctm_attr_mapping.get("fxa_aux_dom")));
			  messageBuffer.append("; fxa_aux_dom : "+voucher.getFxa_aux_dom());
					  
			  voucher.setFxa_bsb(rs.getString(db_dctm_attr_mapping.get("fxa_bsb")));
			  messageBuffer.append("; fxa_bsb : "+voucher.getFxa_bsb());
			  
			  voucher.setFxa_checksum(rs.getString(db_dctm_attr_mapping.get("fxa_checksum")));
			  messageBuffer.append("; fxa_checksum : "+voucher.getFxa_checksum());
			  
			  voucher.setFxa_checksum_type(BMProperties.CHECKSUM_TYPE);		  
			  messageBuffer.append("; CHECKSUM_TYPE : "+voucher.getFxa_checksum_type());
			  
			  voucher.setFxa_classification(rs.getString(db_dctm_attr_mapping.get("fxa_classification")));
			  messageBuffer.append("; fxa_classification : "+voucher.getFxa_classification());
			  
			  voucher.setFxa_collecting_bsb(rs.getString(db_dctm_attr_mapping.get("fxa_collecting_bsb")));
			  messageBuffer.append("; fxa_collecting_bsb : "+voucher.getFxa_collecting_bsb());
			  
//			  voucher.setFxa_cr_drn(rs.getString(db_dctm_attr_mapping.get("fxa_cr_drn")));
			  voucher.setFxa_drn(rs.getString(db_dctm_attr_mapping.get("fxa_drn")));
			  messageBuffer.append("; fxa_drn : "+voucher.getFxa_drn());
			  
			  voucher.setFxa_extra_aux_dom(rs.getString(db_dctm_attr_mapping.get("fxa_extra_aux_dom")));
			  messageBuffer.append("; fxa_extra_aux_dom : "+voucher.getFxa_extra_aux_dom());
			  
			  voucher.setFxa_m_bal_seq_for_deposit(rs.getString(db_dctm_attr_mapping.get("fxa_m_bal_seq_for_deposit")));
			  messageBuffer.append("; fxa_m_bal_seq_for_deposit : "+voucher.getFxa_m_bal_seq_for_deposit());
			  
			  voucher.setFxa_m_balanced_sequence(rs.getString(db_dctm_attr_mapping.get("fxa_m_balanced_sequence")));
			  messageBuffer.append("; fxa_m_balanced_sequence : "+voucher.getFxa_m_balanced_sequence());
			  
			  voucher.setFxa_m_batch_number(rs.getString(db_dctm_attr_mapping.get("fxa_m_batch_number")));
			  messageBuffer.append("; fxa_m_batch_number : "+voucher.getFxa_m_batch_number());
			  
			  voucher.setFxa_m_entry_number(rs.getString(db_dctm_attr_mapping.get("fxa_m_entry_number")));
			  messageBuffer.append("; fxa_m_entry_number : "+voucher.getFxa_m_entry_number());
			  
			  voucher.setFxa_migration_batch_no(batchName);
			  messageBuffer.append("; BatchName : "+voucher.getFxa_migration_batch_no());
			  
			  voucher.setFxa_processing_date(rs.getString(db_dctm_attr_mapping.get("fxa_processing_date")));
			  messageBuffer.append("; fxa_processing_date : "+voucher.getFxa_processing_date());
			  
			  voucher.setFxa_trancode(rs.getString(db_dctm_attr_mapping.get("fxa_trancode")));
			  messageBuffer.append("; fxa_trancode : "+voucher.getFxa_trancode());
			  
			  voucher.setObject_name(rs.getString(db_dctm_attr_mapping.get("object_name")));
			  messageBuffer.append("; object_name : "+voucher.getObject_name());
			  
			  //declaring 'transaction' inside the loop to make sure previous value shouldn't be used for new one (other option is to refresh the value at the begining or end of the loop)
			  String transaction=voucher.getFxa_m_entry_number()+"-"+voucher.getFxa_m_batch_number()+"-"+voucher.getFxa_m_bal_seq_for_deposit();
				voucher.setTransaction(transaction);
				messageBuffer.append("; Transaction : "+voucher.getTransaction());
				CommonUtils.writeToLog(messageBuffer.toString(), debug, null);
			
				//Adding CR DRN of a transaction to the HashMap
				 if(voucher.getFxa_classification().equalsIgnoreCase("CR")){
						transactionAndDRN.put(transaction, voucher.getFxa_drn());
						CommonUtils.writeToLog("The Credit DRN for transaction ["+voucher.getTransaction()+"] is : "+voucher.getFxa_drn(), debug, null);				
				 }	
			
			  voucherList.add(voucher);	
			  CommonUtils.writeToLog("Count "+count, debug, null);
			  count++;
		    }
		    return voucherList;
		    
		  }catch(SQLException e){
			  CommonUtils.writeToLog("Method createVouchersListForABatch(): Exception is "+e.getMessage(), error, null);
			  e.printStackTrace();	
			  throw e;
		  }
    }
	
	
	/**
	 * 1) Connect to the database
	 * 2) 
	 */	
	public static void main(String[] args){
		ConnectToInterim obj=new ConnectToInterim();
		try {
			obj.getConnection();
//			obj.viewTable(conn,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
	
}
