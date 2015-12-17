package com.fxa.nab.dctm.Viewer;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import com.documentum.fc.common.DfLogger;


/**
 * Author: Ajit Dangal
 * Project: NAB Cheques
 *
 * Objective: Implementation class to retrieve image and make conversion. 
 * 
 * @Assumption : Only tiff files will be supplied
 * @TFS story : 
 * @TODO: log4j does not work switched to SOP
 */	

public class ImagePurge implements IImagePurge ,ViewerConstants{
	boolean logDebug = false;
	private boolean m_bDiffCtx = false;
	
	public String purgeImages(String strDebug,ServletContext ctx,String strDiffCtx){
		m_bDiffCtx = true;
		return purgeImages(strDebug, ctx);
		
	}
	public String purgeImages(String strDebug,ServletContext ctx){
		String status = PUR_ERR;
		
		try{
			if(strDebug != null){
				if(strDebug.equalsIgnoreCase("true")){
					logDebug = true;
				}
			}
			String strPath = ctx.getRealPath(FWD_SLASH);
			if(logDebug) System.out.println("{ImagePurge}{getImage}Context Path {" + strPath + "}");
			String strLoc = strPath.replace(BK_SLASH, FWD_SLASH);
			if(m_bDiffCtx){
				strLoc = strPath.replace("VoucherViewer", "D2");
				if(logDebug) System.out.println("{ImagePurge}{getImage}Context Path {" + strPath + "}");				
			}
			if(logDebug) System.out.println("{ImagePurge}{getImage}File Path {" + strLoc + "}");
			String strDctmDir = strLoc + FOL_IMAGES + FWD_SLASH + FOL_DOWNLOAD ;			
			// Tiff files
			File dirDctmDownload = new File(strDctmDir);
			
			Calendar calToday = Calendar.getInstance();
			Date today = new Date();
			calToday.setTime(today);
			int dayOfMonth = calToday.get(Calendar.DAY_OF_MONTH);
			if(logDebug) System.out.println("{ImagePurge}{getImage}Today's Day{" + dayOfMonth + "}");
			if(dirDctmDownload.isDirectory()){
				File[] fileList = dirDctmDownload.listFiles();
				for(int iFileCnt = 0 ; iFileCnt < fileList.length; iFileCnt++ ){
					File filePurge = fileList[iFileCnt];
					try{
						Date lastModified = new Date(filePurge.lastModified());
						Calendar calTempFile = Calendar.getInstance();			        	
						calTempFile.setTime(lastModified);	
						if(logDebug) System.out.println("{ImagePurge}{getImage}Tiff Last Modified Date {" + lastModified.toString() + "}");
						int iFileDay = calTempFile.get(Calendar.DAY_OF_MONTH);
						if(logDebug) System.out.println("{ImagePurge}{getImage}Tiff File Day{" + iFileDay + "}");						
						if(!(iFileDay == dayOfMonth )){							
							if(filePurge.isFile()){
								filePurge.delete();
								if(logDebug) System.out.println("{ImagePurge}{getImage}Deleting Tiff File Path {" + filePurge.getAbsolutePath() + "}");
								status = PUR_OK;
							}
						}else{
							if(logDebug) System.out.println("{ImagePurge}{getImage} Todays Tiff File - No delete"); 
							
						}
						status = PUR_SUCC;
						
					}catch(Exception eFileDelete){
						System.err.print("{ImagePurge}{getConvertedFile} Unable to delete tif file" + 
								eFileDelete.getStackTrace());
						eFileDelete.printStackTrace();
					
					}
				}
			}
			String strPngDir = strLoc + FOL_IMAGES ;
			File dirPngDownload = new File(strPngDir);
			if(dirPngDownload.isDirectory()){
				File[] fileList = dirPngDownload.listFiles();
				for(int iFileCnt = 0 ; iFileCnt < fileList.length; iFileCnt++ ){
					File filePurge = fileList[iFileCnt];
					try{
						Date lastModified = new Date(filePurge.lastModified());
						if(logDebug) System.out.println("{ImagePurge}{getImage}PNG Last Modified Date {" + lastModified.toString() + "}");
						Calendar calTempFile = Calendar.getInstance();			        	
						calTempFile.setTime(lastModified);	
						//if(logDebug) System.out.println("{ImagePurge}{getImage}PNG Last Modified Date {" + lastModified.toString() + "}");
						int iFileDay = calTempFile.get(Calendar.DAY_OF_MONTH);
						if(logDebug) System.out.println("{ImagePurge}{getImage}PNG File Day{" + iFileDay + "}");						
						if(!(iFileDay == dayOfMonth )){	
							if(filePurge.isFile()){
								filePurge.delete();
								if(logDebug) System.out.println("{ImagePurge}{getImage}Deleting PNG File Path {" + filePurge.getAbsolutePath() + "}");
								status = PUR_OK;
							}
						}else{
							if(logDebug) System.out.println("{ImagePurge}{getImage} Todays PNG File - No delete"); 
							
						}
						status = PUR_SUCC;
						
					}catch(Exception eFileDelete){
						System.err.print("{ImagePurge}{getConvertedFile} Unable to delete PNG file" + 
								eFileDelete.getStackTrace());
						eFileDelete.printStackTrace();
					
					}
				}
			}
		}catch(Exception ePurge){
			DfLogger.error(this,"{ImagePurge}{getConvertedFile} Unable to delete file" + 
						ePurge.getStackTrace(),null,null);
			ePurge.printStackTrace();
			
		}	
		
		return status;
		
	}
	/*public static void main(String[] args) {
		Calendar calToday = Calendar.getInstance();
		int b = calToday.get(Calendar.DAY_OF_MONTH);
		System.out.println(b);
	}*/
}
