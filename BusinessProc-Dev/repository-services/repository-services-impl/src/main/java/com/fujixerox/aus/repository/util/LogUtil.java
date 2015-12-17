package com.fujixerox.aus.repository.util;

import org.apache.log4j.Logger;

/** 
 * Henry Niu
 * 26/03/2015
 */ 
public class LogUtil {

	private static Logger logger = Logger.getLogger("Repository_Logger");
	
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	
	   public static void log(String message, int level, Throwable e) {
		   message += "\n";
			if (level == DEBUG) {
				logger.debug(message);
			} else if (level == INFO) {
				logger.info(message);
			} else if (level == WARN) {
				logger.warn(message);
			} else {
				logger.error(message);
				if (e != null) {
					logger.error("\n\n\n <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");							
					logger.error("!!! Error ", e);
					logger.error("\n\n\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					e.printStackTrace();
				}
			}	
	}		
}
