package com.fxa.nab.d2.ptqa;

import org.apache.log4j.Logger;
import com.documentum.fc.common.DfLogger;

public class CommonUtils {

	static Logger logger=Logger.getLogger("BM_Logger");
	
	public static final int debug=0;
	public static final int info=1;
	public static final int warn=2;
	public static final int error=3;
	
	public static boolean commandLineRun=true;
	
		
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
		
   
	   public static void main(String[] args){
		   String arg=null+"-"+"3";
		   System.out.println(arg);
	   }


}
