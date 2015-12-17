package com.fxa.nab.dctm.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;

/**
 * ITERATION02 (Getting the data from Interim Processor):
 * This program is to Bulk Migrate Cheque images in 'batches' from FISERV to FXA as follows :
 * #) Read the 'ingestion.properties' file.
 * #) Connect to the Interim Database
 * #) LOOP01: From the manifest table, read 'batch_per_cycle' no of available batches
 * #) LOOP02: Read all the vouchers related to the manifest and the file system path of the cheque images.
 * #) Create the folders if doesn't exist.
 * #) Store the vouchers into Repository and update the status of the manifest
 * #) Pickup next manifest and repeat LOOP02.
 * #) repeat LOOP01, till 'total_batch_to_process' has been processed.
 * 
 * Exception Handling:
 * 1) Invalid properties value should halt the program with proper error message. Includes Database session and Dctm Session check using values provided.
 *    Result: Throws FXAPropertyValueException.
 * 2) 
 * 
 * Session Leak:
 * 
 * Performance Improvement:
 * 
 */


public class BulkMigrator implements IBulkMigrator {

	public static final int debug=CommonUtils.debug;
	public static final int info=CommonUtils.info;
	public static final int warn=CommonUtils.warn;
	public static final int error=CommonUtils.error;

	//instatiate BMProperties to read all the properties
	BMProperties props=new BMProperties();
	CommonUtils utils=null;
	ConnectToInterim cti=null;
	DctmActivities dctm=null;
	
	public void initAndValidate(boolean isCommandLineRun,String propFile) throws Exception{
		   
		try{
		//DON'T CHANGE THE ORDER!!!!
		try{
			//read all the values from properties file
			ResourceBundle bundle=BMProperties.loadPropsFileAndLogLocation(propFile);
			if(bundle == null)return;

		   CommonUtils.writeToLog("***************************************************************************************************",debug,null);
		   CommonUtils.writeToLog("***************************************************************************************************",debug,null);
		   CommonUtils.writeToLog("Batch processing starts at "+new Date(), debug, null);
		   CommonUtils.writeToLog(" ", debug, null);			
			
			
			if(!BMProperties.readProps(bundle,propFile)){
//				CommonUtils.writeToLog("Error while reading properties file. Cannot proceed further. Check log file for more info.",debug,null);	
				throw new FXAInitAndValidateException("Error while reading properties file. Cannot proceed further. Check log file for more info.");
			}else{
				CommonUtils.writeToLog("\n\nProperties file read successfully. \n\n",debug,null);
			}
			
		}catch(FXAInitAndValidateException e){
			String message="******\n\n\nThe following keys should be defined in the properties file: \n" +
					"dctm_login_user,dctm_password_encrypted,repository_name,\n target_dctm_location,doc_object_type,folder_object_type," +
					"folder_acl_name,folder_acl_domain,doc_acl_name,doc_acl_domain,\n dbinstancename,dbname,dbport,dbusername,dbpassword,\n batch_pickup_status," +
					"batch_upload_complete_status,batch_upload_fail_status,\nlog_dctm_location,total_batch_to_process\n\n\n ******";
					
			CommonUtils.writeToLog(message, error, null);
			CommonUtils.writeToLog(e.getMessage(), error, e);
			e.printStackTrace();
			throw e;
		}
		
		utils=new CommonUtils();		
		CommonUtils.commandLineRun=isCommandLineRun;
		

		try{
			//Instantiate Interim Database Class
			cti=new ConnectToInterim();
			cti.getConnection();
			
			//instantiate DCTM 
			dctm=new DctmActivities();
			dctm.getSession();
			CommonUtils.writeToLog("Database and Documentum connections tested successfully!!.Proceeding to process batches.", debug, null);
		
		}catch(FXAInitAndValidateException | DfException e){
			CommonUtils.writeToLog(e.getMessage(), error, e);
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			CommonUtils.writeToLog(e.getMessage(), error, e);
			e.printStackTrace();
			throw e;
		}
		
		
		//Start to process batches
		calcAndSetBatchesPerCycle();
		
		}finally{
			if(cti != null) cti.closeConnection();
			if(dctm != null) dctm.releaseSession();
		}

		CommonUtils.writeToLog("Batch processing finished at "+new Date(), debug, null);
		CommonUtils.writeToLog("***************************************************************************************************",debug,null);
		CommonUtils.writeToLog("***************************************************************************************************",debug,null);		
	}
	

	private void calcAndSetBatchesPerCycle() throws Exception{
		CommonUtils.writeToLog("Method calcAndSetBatchesPerCycle() : Starting to process batches........", debug, null);
		
		int batchPerCycle=Integer.parseInt(BMProperties.batch_per_cycle);
		int total_batches=Integer.parseInt(BMProperties.total_batch_to_process);

//		try{
			if(total_batches < batchPerCycle){
				batchPerCycle = total_batches;
			}
			
			int loops=total_batches/batchPerCycle;
			
		   for(int count=1;count<=loops;count++){
			   CommonUtils.writeToLog("Fetching "+batchPerCycle+" batches for run "+count+" out of "+loops+" runs.", debug, null);
			   if(BMProperties.SERVICE_ON){
				   //IMPORTANT!! WORK PENDING TO UPDATE THE STATUS OF THE BATCHES IN DB THAT ARE NOT PROCESSED YET and STILL WITH STATUS 'IN-PROGRESS'
				   getAndUploadBatchesPerCycle(batchPerCycle);	
			   }
		   }
		   
		   if(total_batches > batchPerCycle){
		   int remainder=total_batches%batchPerCycle;
		   CommonUtils.writeToLog("Fetching "+remainder+" batches for processing the remainders of "+BMProperties.total_batch_to_process, debug, null);	   
			   if(remainder > 0){
				   if(BMProperties.SERVICE_ON){  //This condition is not required here... but just adding for more grip...
					   batchPerCycle=remainder;
					   getAndUploadBatchesPerCycle(batchPerCycle);	
				   }
			   }
		   }
	}
	
	private void getAndUploadBatchesPerCycle(int batchPerCycle) throws Exception{
		   ArrayList<Manifest> list=(ArrayList<Manifest>)cti.getManifestToProcess(batchPerCycle); 
		   
			for(int count=0;count<list.size();count++){//Getting each batch and processing it
				if(BMProperties.SERVICE_ON){				
				 Manifest manifest=list.get(count);
				 processAndUploadABatch(manifest);
				}
			}				
	}
	
	
	//Processing and Uploading one whole batch
	//If error, report it and move on to the next batch
	private void processAndUploadABatch(Manifest manifest){
		String processingDate=null;
		String entryNumber=null;
		String batchName=null;
		String batchStoragePath=null;
		
		boolean batchProcessingSuccess=false;
		
		ArrayList<String> alreadyIngestedVouchersList=null;
				
		try{
			   processingDate=manifest.getProcessingDate();
			   entryNumber=manifest.getEntryNumber();
			   batchName=manifest.getBatchName();
			   batchStoragePath=manifest.getOutputPath();			
			
		   	/* Start - Reconciliation Process - Sprint22 - Duplicate Check - check if this batch processed already to avoid duplicates - Yogesh */
			boolean batchAlreadyProcessed=isBatchProcessedBefore(manifest);
			
			if(batchAlreadyProcessed){
				CommonUtils.writeToLog("This batch ["+batchName+"] has already been processed earlier and now, it will be RECONCILED. Only the NEW Vouchers will be ingested. NO CHANGES/UPDATES will be done to vouchers alreay ingested during previous upload!!!", warn, null);
				alreadyIngestedVouchersList=new ArrayList<String>();
				String query=String.format("select fxa_drn from fxa_voucher where fxa_migration_batch_no='%s'",batchName);
				IDfCollection col=dctm.readQuery(query);
				 while (col.next())
				 {
					 String drn=col.getString("fxa_drn");
					 alreadyIngestedVouchersList.add(drn);
					 //CommonUtils.writeToLog(drn,info,null);
				 }
				 col.close();
			}else{
				CommonUtils.writeToLog("This batch ["+batchName+"] is a new batch [ie., it is not ingested to NAB Repo before]. All vouchers from this batch will be ingested.", info, null);
			}
			/* End - Reconciliation Process - Sprint22 - Duplicate Check */

		   
		   CommonUtils.writeToLog("------- Start -------------",debug,null);
		   
		   CommonUtils.writeToLog("Processing Manifest : "+batchName+"; Day : "+processingDate+"; Entry : "+entryNumber, debug, null);
		   HashMap<String,String> transactionAndDRN=new HashMap<String,String> ();
		   /* commented for story 13887
		   ArrayList<FxaVoucher> voucherList=cti.createVouchersListForABatch(batchName,processingDate,entryNumber,transactionAndDRN); */
		   
		   /** start - Added for story 13887 */
		   File batchFolder=new File(batchStoragePath);
		   if(!batchFolder.isDirectory()){
			   //log error and continue for next batch...
			   CommonUtils.writeToLog("The batch location ["+batchStoragePath+"] is not a directory. Skipping this batch ["+batchName+"]",debug,null);
			   throw new FXAException("ERROR - The batch location ["+batchStoragePath+"] is not a directory. Skipping this batch ["+batchName+"]");
		   }
		   ArrayList<FxaVoucher> voucherList=createVouchersListForABatch(batchFolder,batchName,processingDate,entryNumber,transactionAndDRN,alreadyIngestedVouchersList);
		   /** start - Added for story 13887 */
		   
		   String folderPath=dctm.checkExistenseOfFolders(BMProperties.target_dctm_location, processingDate.substring(0,4), processingDate.substring(4,6), processingDate.substring(6), entryNumber);

		   if(dctm.uploadVouchersInABatch(batchName,batchStoragePath,voucherList,transactionAndDRN,folderPath)){
			   batchProcessingSuccess=true; 
		   }


		}catch (FileNotFoundException e) {
			CommonUtils.writeToLog("FileNotFoundException from method createVouchersListForABatch() while reading csv for batch ["+batchName+"]: Exception is "+e.getMessage(), error, e);
			e.printStackTrace();
		}catch (IOException e) {
			CommonUtils.writeToLog("IOException from method createVouchersListForABatch() while reading csv for batch ["+batchName+"]: Exception is "+e.getMessage(), error, e);
			e.printStackTrace();
		}catch(SQLException e){
			CommonUtils.writeToLog("Exception from Method processAndUploadABatch(): Exception is "+e.getMessage(), error, null);
			e.printStackTrace();	
		}catch(FXAInitAndValidateException e){
			CommonUtils.writeToLog("Exception from Method processAndUploadABatch(): Exception is "+e.getMessage(), error, null);
			e.printStackTrace();	
		}catch(Exception e){
			CommonUtils.writeToLog("Exception from Method processAndUploadABatch(): Exception is "+e.getMessage(), error, null);
			e.printStackTrace();	
		}finally{
			if(batchProcessingSuccess){
				updateBatchStatusInInterim(true,processingDate,entryNumber,BMProperties.batch_upload_complete_status,manifest.getDctmIngestedDates());
			}else{
				updateBatchStatusInInterim(false,processingDate,entryNumber,BMProperties.batch_upload_fail_status,null);
			}
		}
		
		CommonUtils.writeToLog("-------End of processing Manifest : "+batchName+"------",debug,null);		
	}
	
   
	private void updateBatchStatusInInterim(boolean isSuccess,String processingDate,String entryNumber,String status,String dctmIngestedDates){
		String query=null;

		if(isSuccess){		
			
			query=String.format("update Manifest set Status='%s', DctmIngestStatus='%s', DctmIngestedDates='%s' where Day='%s' and Entry='%s'",status,BMProperties.DCTM_INGEST_STATUS,getDate(dctmIngestedDates),processingDate,entryNumber);
			CommonUtils.writeToLog("Query used to update the status is ["+query+"]", debug, null);
		}else{
			query=String.format("update Manifest set Status='%s' where Day='%s' and Entry='%s'",status,processingDate,entryNumber);
			CommonUtils.writeToLog("Query used to update the status is ["+query+"]", debug, null);			
		}
		
		cti.updateQuery(query);
	}
	
	
	public String getDate(String dctmIngestedDates){
//		(dctmIngestedDates.equals(null) && dctmIngestedDates.trim().length()!=0) ? dctmIngestedDates+getDate()+";" : ""+getDate()+";";
		
		GregorianCalendar gc=new GregorianCalendar();
		int month=gc.get(GregorianCalendar.MONTH) + 1;
		String strMonth= month < 10 ? "0"+ (month) : ""+(month);
		
		int day=gc.get(GregorianCalendar.DAY_OF_MONTH);
		String strDay= day < 10 ? "0"+ (day) : ""+(day);
		
//		String day= gc.DAY_OF_MONTH < 10 ? "0"+ (gc.DAY_OF_MONTH) : ""+(gc.DAY_OF_MONTH);
		String todayDate=strDay+"-"+strMonth+"-"+gc.get(GregorianCalendar.YEAR)+";";
		
		if(dctmIngestedDates != null && dctmIngestedDates.trim().length()!= 0){
			dctmIngestedDates+=todayDate;
		}else{
			dctmIngestedDates=todayDate;
		}
		return dctmIngestedDates;
	}

	
	public ArrayList<FxaVoucher> createVouchersListForABatch(File batchFolder,String batchName,String processingDate,String entryNumber,HashMap<String,String> transactionAndDRN,ArrayList<String> alreadyIngestedVouchersList) throws SQLException,FXAInitAndValidateException,FileNotFoundException,IOException,Exception{
		
		ArrayList<FxaVoucher> docList=new ArrayList<FxaVoucher>();
		
		
		File[] csvFile=batchFolder.listFiles(new FileFilter(){
			public boolean accept(File pathName){
				return (pathName.getName().endsWith(BMProperties.CSV_EXT));
			}
		});
		//.getAbsolutePath()+PATH_SEP+batchFolder+".csv";
		BufferedReader br = null;
		String line = "";
		String transaction="";
		
		if(csvFile == null || csvFile.length == 0){
			//log error and move to next
			String message="No csv file found in batch folder "+batchFolder.getName()+". Skipping it...";
			CommonUtils.writeToLog(message,error,null);
			throw new FXAInitAndValidateException("None csv file found. Skipping this batch.");
		}else if(csvFile.length != 1){
			String message="More than one csv file found in batch folder "+batchFolder.getName()+".";
			message="Csv files are :\n";
			for (int i=0;i<csvFile.length;i++)
				message+=csvFile[i].getName()+"\n";
				message+="Skipping it...";
				CommonUtils.writeToLog(message,error,null);
				throw new FXAInitAndValidateException("Multiple csv files found. Skipping this batch.");
		}else{
			
			String message="The csv file for batch folder "+batchFolder.getName()+" is "+csvFile[0].getName();
			CommonUtils.writeToLog(message,debug,null);
	 
			try {
	 
			br = new BufferedReader(new FileReader(csvFile[0]));
			FxaVoucher cheque=null;
		
			while ((line = br.readLine()) != null) {
				StringBuffer messageBuffer=new StringBuffer();	
				
			    // use comma as separator
				String[] docData=line.split(BMProperties.CSV_SEP);

				
				if(docData != null && docData.length < BMProperties.NO_OF_COLS_IN_CSV){ //Covering all possible RUNTIME EXCEPTIONS...
					CommonUtils.writeToLog("Error!! The current line is empty or the number of columns is less than ["+BMProperties.NO_OF_COLS_IN_CSV+"]. Skipping this batch ["+batchName+"]", error, null);
					throw new FXAInitAndValidateException("Error!! The current line is empty or the number of columns is less than ["+BMProperties.NO_OF_COLS_IN_CSV+"]. Skipping this batch ["+batchName+"]");
				}				
				
				/* Start - Reconciliation Process - Sprint22 - Duplicate Check - Yogesh */
				if(alreadyIngestedVouchersList != null && alreadyIngestedVouchersList.contains(docData[9].trim())){ //docData[9].trim() can throw RUNTIME exception: ArrayIndexOutOfBoundsException. Handle it
					CommonUtils.writeToLog("For batchname ["+batchName+"], the voucher with DRN ["+docData[9]+"] already exist in Repository. Skipping it.",warn,null);
					continue;
				}
				/* End - Reconciliation Process - Sprint22 - Duplicate Check */

				
				CommonUtils.writeToLog("No of columns for this line in CSV is [ "+docData.length+" ]",debug,null);				
				
				cheque=new FxaVoucher();
				cheque.setFxa_processing_date(docData[0]);
				messageBuffer.append("fxa_processing_date : "+docData[0]+"\n");
				
				cheque.setObject_name(docData[1]);
				messageBuffer.append("; object_name : "+docData[1]+"\n");
				
				cheque.setFxa_extra_aux_dom(docData[3]);
				messageBuffer.append("; fxa_extra_aux_dom : "+docData[3]+"\n");
				
				cheque.setFxa_aux_dom(docData[4]);
				messageBuffer.append("; fxa_aux_dom : "+docData[4]+"\n");
				
				cheque.setFxa_bsb(docData[5]);
				messageBuffer.append("; fxa_bsb : "+docData[5]+"\n");
				
				cheque.setFxa_account_number(docData[6]);
				messageBuffer.append("; fxa_account_number : "+docData[6]+"\n");
				
				cheque.setFxa_trancode(docData[7]);
				messageBuffer.append("; fxa_trancode : "+docData[7]+"\n");
				
				cheque.setFxa_amount(docData[8]);
				messageBuffer.append("; fxa_amount : "+docData[8]+"\n");
				
				cheque.setFxa_drn(docData[9]);
				messageBuffer.append("; fxa_drn : "+docData[9]+"\n");
				
				cheque.setFxa_classification(docData[10]);
				messageBuffer.append("; fxa_classification : "+docData[10]+"\n");
				
				cheque.setFxa_collecting_bsb(docData[11]);
				messageBuffer.append("; fxa_collecting_bsb : "+docData[11]+"\n");
				
				cheque.setFxa_m_entry_number(docData[12]);
				messageBuffer.append("; fxa_m_entry_number : "+docData[12]+"\n");
				
				cheque.setFxa_m_batch_number(docData[13]);
				messageBuffer.append("; fxa_m_batch_number : "+docData[13]+"\n");
				
				cheque.setFxa_m_bal_seq_for_deposit(docData[14]);
				messageBuffer.append("; fxa_m_bal_seq_for_deposit : "+docData[14]+"\n");
				
				cheque.setFxa_m_balanced_sequence(docData[15]);
				messageBuffer.append("; fxa_m_balanced_sequence : "+docData[15]+"\n");	
				
				transaction=docData[12]+"-"+docData[13]+"-"+docData[14];
				cheque.setTransaction(transaction);
				messageBuffer.append("; Transaction : "+transaction+"\n");
				
				if(docData[10].equalsIgnoreCase("CR")){
					transactionAndDRN.put(transaction, docData[9]);
				}
			  
				cheque.setFxa_checksum(docData[17]);
				messageBuffer.append("; fxa_checksum : "+cheque.getFxa_checksum());
			  
				cheque.setFxa_checksum_type(BMProperties.CHECKSUM_TYPE);		  
				messageBuffer.append("; CHECKSUM_TYPE : "+cheque.getFxa_checksum_type());				
				
				CommonUtils.writeToLog(messageBuffer.toString(),debug,null);
				
				docList.add(cheque); //adding the object to arraylist
				
			}
			
			CommonUtils.writeToLog("Total number of documents for "+batchFolder.getName()+" is "+docList.size(),debug,null);
	 
			}  finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						CommonUtils.writeToLog("IOException from method createVouchersListForABatch() while closing Buffered Reader. Batch being processed ["+batchName+"]: Exception is "+e.getMessage(), error, e);
					}
				}
			}
		}
//		System.out.println("Done");
		
		return docList;		
    }
	
	private boolean isBatchProcessedBefore(Manifest manifest){
		
		if((manifest.getStatus() != null && manifest.getStatus().trim().equals(BMProperties.batch_pickup_reconcile_status)) 
				|| (manifest.getDctmIngestStatus() != null && manifest.getDctmIngestStatus().trim().equals(BMProperties.DCTM_INGEST_STATUS))){
			return true;
		}
		
		return false;
	}
	
	
	public static void main( String[] args ){
		
        System.out.println( "Bulk Migration starts...." );
        String start="start";
        String stop="stop";
        System.out.println("Arguments are "+args[0]+"   "+args[1]);
        BulkMigrator ingest=new BulkMigrator();
        try {
        	if(args[0].trim().equals(start)){
        	  BMProperties.SERVICE_ON=true;
        	  System.out.println("if BMProperties.SERVICE_ON "+BMProperties.SERVICE_ON);
			  ingest.initAndValidate(true,args[1]);
			  
        	}else if(args[0].trim().equals(stop)){
        		System.out.println("if else BMProperties.SERVICE_ON "+BMProperties.SERVICE_ON);
        		BMProperties.SERVICE_ON=false;
        	}else{
        		System.out.println("else condition BMProperties.SERVICE_ON "+BMProperties.SERVICE_ON);
        	  BMProperties.SERVICE_ON=false;
        	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception "+e.getMessage());
		}
        
		System.out.println("End of Bulk Upload.");
    }
}