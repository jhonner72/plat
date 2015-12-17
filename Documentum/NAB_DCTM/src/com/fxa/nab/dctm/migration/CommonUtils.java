package com.fxa.nab.dctm.migration;

import org.apache.log4j.Logger;
import com.documentum.fc.common.DfLogger;

public class CommonUtils {

	static Logger logger=Logger.getLogger("BM_Logger");
	
	public static final int debug=0;
	public static final int info=1;
	public static final int warn=2;
	public static final int error=3;
	
	public static boolean commandLineRun=true;
	
//	//auditing
//	static{
//		
//		System.out.println("CommonUtils is loading........");
//		
//		RollingFileAppender appender=null;
//		 
//	       try{
//	    	   File logFileWithPath=null;
//	    	   String fileName="Bulk_Migrator_"+new Date().getTime()+".log";
//	    	   
//	    	   System.out.println("BMProperties.log_location   is   "+BMProperties.log_location);
//	    	   
//	    	   if(BMProperties.log_location != null && BMProperties.log_location.trim().length() > 0){
//	    		   logFileWithPath=new File(BMProperties.log_location,fileName);
//	    	   }else{
////	   				String workingDir= CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().toString();
////	   				workingDir=workingDir.substring(6); //to truncate 'file:/' that is prefixed
//	   				logFileWithPath=new File(fileName);	
//	    	   }
//	    	   
//	    	   appender=new RollingFileAppender(new SimpleLayout(),logFileWithPath.getAbsolutePath());
//	    	   
//	    	   if(logFileWithPath != null) {System.out.println(logFileWithPath.getAbsolutePath());	}
//
////	    		writeToLog("Appender file is "+logFile.getAbsolutePath(),debug,null);
//
//	            //The log file size is set to 100 KB (1024bytes * 100)
//	            appender.setMaximumFileSize(102400);
//	            
//	            //The Maximum number of Backup files is 1000.
//	            appender.setMaxBackupIndex(100);
//	            logger.addAppender(appender);
//	            
//	            }catch(Exception e){
//	            	System.out.println("Error while creating log appender for parent.");
////	            	System.out.println(e.getMessage());
//	            	e.printStackTrace();
//	            	
//	            }
//	       
////		writeToLog("Leaving createBatchSpecificLog",debug,null);		
//	}

		
	   public static void writeToLog(String message,int level,Throwable e){
		   message+="\n";
			if(commandLineRun){
				if(logger != null){
					if(level == debug){
					logger.debug(message);
					}else if(level == info){
						logger.info(message);
					}else if(level == warn){
							logger.warn("!!!WARNING : "+message);
							System.out.println("!!!WARNING : "+message);
					}else{
						logger.error("!!! Error : "+message);
						System.out.println("!!! Error : "+message);
						if(e!=null){
							logger.error("\n\n\n <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");							
							logger.error("!!! Error ",e);
							logger.error("\n\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							e.printStackTrace();
						}
					}
				}else{
					System.out.println(message);
				}
				
			}else {
				if(level == debug){
					DfLogger.debug(null,message,null,null);
				}else if(level == info){
					DfLogger.info(null,message,null,null);
				}else if(level == warn){
					DfLogger.warn(null,"!!!WARNING : "+message,null,null);
				}else{
					DfLogger.debug(null,"!!! Error "+message,null,null);
					if(e!=null){
						DfLogger.error(null,"\n\n\n <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<",null,null);							
						DfLogger.error(null,e.getMessage(), null, e);
						DfLogger.error(null,"\n\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",null,null);						
						e.printStackTrace();
					}
				}			
			}
			System.out.println(message);
		}
		
/*
 * 		DON'T DELETE - MIGHT BE USED LATER 
 * 		//auditing
 
		public RollingFileAppender createBatchSpecificLog(String batchName){
			
			writeToLog("Inside createBatchSpecificLog",debug,null);
			
			RollingFileAppender appender=null;
			 
		       try{
		        	File logFile=new File(BMProperties.log_location,batchName+".log");
		            appender=new RollingFileAppender(new SimpleLayout(),logFile.getName());
		            
		    		writeToLog("Appender file is "+logFile.getAbsolutePath(),debug,null);

		            //The log file size is set to 100 KB (1024bytes * 100)
		            appender.setMaximumFileSize(102400);
		            
		            //The Maximum number of Backup files is 1000.
		            appender.setMaxBackupIndex(1);
		            logger.addAppender(appender);
		            
		            }catch(Exception e){
		            	writeToLog("Error while creating appender for batch "+batchName,error,e);
		            	return null;
		            }
		       
			writeToLog("Leaving createBatchSpecificLog",debug,null);	       
		       
			return appender;
		}
		

		//auditing
	   public boolean closeBatchSpecificLog(RollingFileAppender appender,String batchName){
			
//			 RollingFileAppender appender=null;
			 
		       try{

		            logger.removeAppender(appender);
		            
		            }catch(Exception e){
		            	writeToLog("Error while removing appender for batch "+batchName,error,e);
		            	return false;
		            }
		       
			return true;
		} */
	   
	   public static void main(String[] args){
		   String arg=null+"-"+"3";
		   System.out.println(arg);
	   }


}
