package com.fxa.nab.dctm.Viewer;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfLoginInfo;
import java.io.*;
import javax.servlet.ServletContext;
import javax.imageio.*;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.*;

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

public class GetImage implements IGetImage ,ViewerConstants{
	
		
	//public static final int debug=0;
	//public static final int error=1;
	//public static final int warning=2;

	//private static final String PATH_SEP = "/";	
	
	private IDfSession session=null;
	private IDfSessionManager sMgr=null;
	boolean logDebug = false;
	boolean m_bPNG = false;
	
	//private PrintWriter m_writer = null;
	public GetImage(boolean bisPNG){
		m_bPNG = bisPNG;
	}
	
	/**
 * 
 * @param docid
 * @param docbase
 * @param user
 * @param pwd
 * @param ctxb
 * @return png image path
 */
	public String getImage(String strDebug,String docid,String docbase, String user , String pwd,ServletContext ctx){
		String imagePath = null;
		if(strDebug != null){
			if(strDebug.equalsIgnoreCase("true")){
				logDebug = true;
			}
		}
		if(logDebug) System.out.println("{GetImage}{getImage}Request for {" + docid + "}");
		try{			
			
			String strPath = ctx.getRealPath(FWD_SLASH);
			if(logDebug) System.out.println("{GetImage}{getImage}Context Path {" + strPath + "}");
			String strLoc = strPath.replace(BK_SLASH, FWD_SLASH);
			//strLoc = strPath.replace("VoucherViewer", "D2");
			if(logDebug) System.out.println("{GetImage}{getImage}File Path {" + strLoc + "}");
			String strDctmFilePath = strLoc + FOL_IMAGES + FWD_SLASH + FOL_DOWNLOAD + FWD_SLASH + docid + EXT_TIF;
			String strPngPath = FOL_IMAGES + FWD_SLASH + docid + EXT_JPEG;	
			if(m_bPNG){
				//if(logDebug) System.out.println("{GetImage}{getImage}**** Before *** {" + strPngFullPath + "}");
				strPngPath = strPngPath.replace(".jpg", ".png");
				//if(logDebug) System.out.println("{GetImage}{getImage}**** after *** {" + strPngFullPath + "}");
			}
			String strPngFullPath = strLoc + strPngPath;
			if(logDebug) System.out.println("{GetImage}{getImage}JPEG/PNG File Path {" + strPngFullPath + "}");			
			if(fileExists(strPngFullPath)){				
				if(logDebug) System.out.println("{GetImage}{getImage}File Exists ...");
				imagePath = strPngPath;
			}else{
				session = getSession(docbase,user,pwd,null);
				String dctmFile = getDctmFile(session,docid,strDctmFilePath);
				if(logDebug) System.out.println("{GetImage}{getImage}Documentum File Path {" + dctmFile + "}");	
				if(logDebug) System.out.println("{GetImage}{getImage}Convert Extn PNG{" + m_bPNG + "}");		
				if(m_bPNG){
					imagePath = getConvertedFile(dctmFile,strPngFullPath,m_bPNG);
				}else{
					imagePath = getConvertedFile(dctmFile,strPngFullPath);
				}				
				if(logDebug) System.out.println("{GetImage}{getImage} JPEG file {" + imagePath + "}");
				if(fileExists(imagePath)){
					imagePath = strPngPath;
				}else{
					System.err.print("{GetImage}{getImage} JPEG file {" + imagePath + 
							"} is either empty file or does not exists, please check log for other errors");
				}
			}
		}catch(Exception e){
			System.out.println("{GetImage}{getImage} Error {" + e.getStackTrace().toString() + "}");
			e.printStackTrace();
			sMgr.release(session);
		}
		return imagePath; 
	}
	public String getImage(String strDebug,String docid,String docbase, String user , String pwd,ServletContext ctx,String strDiffCtx){
		String imagePath = null;
		if(strDebug != null){
			if(strDebug.equalsIgnoreCase("true")){
				logDebug = true;
			}
		}
		if(logDebug) System.out.println("{GetImage}{getImage}Request for -Diff Ctx{" + docid + "}");
		try{			
			
			String strPath = ctx.getRealPath(FWD_SLASH);
			if(logDebug) System.out.println("{GetImage}{getImage}Context Path {" + strPath + "}");
			String strLoc = strPath.replace(BK_SLASH, FWD_SLASH);
			strLoc = strPath.replace("VoucherViewer", "D2");
			if(logDebug) System.out.println("{GetImage}{getImage}File Path {" + strLoc + "}");
			// by ajit path issue - in SIT 14/09/2015
			String strDctmFilePath = strLoc + FWD_SLASH + FOL_IMAGES + FWD_SLASH + FOL_DOWNLOAD + FWD_SLASH + docid + EXT_TIF;
			if(logDebug) System.out.println("{GetImage}{getImage}Documentum Download File Path {" + strDctmFilePath + "}");
			String strPngPath =  FOL_IMAGES + FWD_SLASH + docid + EXT_JPEG;
			if(logDebug) System.out.println("{GetImage}{getImage}Convert Extn PNG{" + m_bPNG + "}");			
			if(m_bPNG){
				//if(logDebug) System.out.println("{GetImage}{getImage}**** Before *** {" + strPngFullPath + "}");
				strPngPath = strPngPath.replace(".jpg", ".png");
				//if(logDebug) System.out.println("{GetImage}{getImage}**** after *** {" + strPngFullPath + "}");
			}
			String strPngFullPath = strLoc + FWD_SLASH + strPngPath;
			if(logDebug) System.out.println("{GetImage}{getImage}JPEG/PNG File Path {" + strPngFullPath + "}");
			
			if(fileExists(strPngFullPath)){				
				if(logDebug) System.out.println("{GetImage}{getImage}File Exists ...");
				imagePath = strPngPath;
			}else{
				session = getSession(docbase,user,pwd,null);
				String dctmFile = getDctmFile(session,docid,strDctmFilePath);
				if(logDebug) System.out.println("{GetImage}{getImage}Documentum File Path {" + dctmFile + "}");					
				if(logDebug) System.out.println("{GetImage}{getImage}Convert Extn PNG{" + m_bPNG + "}");		
				if(m_bPNG){
					imagePath = getConvertedFile(dctmFile,strPngFullPath,m_bPNG);
				}else{
					imagePath = getConvertedFile(dctmFile,strPngFullPath);
				}
				if(logDebug) System.out.println("{GetImage}{getImage} JPEG/PNG file {" + imagePath + "}");
				if(fileExists(imagePath)){
					imagePath = strPngPath;
				}else{
					System.err.print("{GetImage}{getImage} JPEG file {" + imagePath + 
							"} is either empty file or does not exists, please check log for other errors");
				}
			}
		}catch(Exception e){
			System.out.println("{GetImage}{getImage} Error {" + e.getStackTrace().toString() + "}");
			e.printStackTrace();
			sMgr.release(session);
		}
		return imagePath; 
	}
	/**
	 * 
	 * @param docbase
	 * @param loginName
	 * @param ticket
	 * @param domain
	 * @return Documentum Session
	 * @throws DfException
	 */
	protected IDfSession getSession(String docbase,String loginName,String ticket,String domain) throws DfException
	   {
			if(logDebug) System.out.println("{GetImage}{getSession}Inside createSession method.......");
			IDfSession retSession = null;
			try{
			
			IDfClientX clientx = new DfClientX();
			IDfClient client = clientx.getLocalClient();

			sMgr = client.newSessionManager();
			IDfLoginInfo loginInfoObj = clientx.getLoginInfo();

			loginInfoObj.setUser(loginName);
			loginInfoObj.setPassword(ticket);

			if (domain != null) {
				loginInfoObj.setDomain(domain);
			}

			sMgr.setIdentity(docbase, loginInfoObj);

			retSession = sMgr.getSession(docbase);
			if(logDebug) System.out.println("{GetImage}{getSession}Session created for user {" + loginName + "}");
			} catch (DfException e) { 
				System.out.println("Error" + e.getStackTraceAsString());
				e.printStackTrace();
			}
			
			return retSession; 
	   }

	/**
	 * 
	 * @param sess
	 * @param strObj
	 * @param filePath
	 * @return documentum content file
	 */
		protected String getDctmFile(IDfSession sess, String strObj,String filePath){
			String retFilePath = null;
			try{
			if(logDebug) System.out.println("{GetImage}{getDctmFile} Object id {" + strObj + "}");
			if(logDebug) System.out.println("{GetImage}{getDctmFile} File Path {" + filePath + "}");	
			//IDfSysObject sysObj = (IDfSysObject)sess.getObject(new DfId(strObj));
			IDfSysObject sysObj = (IDfSysObject)sess.getObjectByQualification("dm_document where r_object_id='"+ 
					strObj + "'");
			 retFilePath = sysObj.getFile(filePath);
			}catch (DfException e){
				System.out.println("{GetImage}{getDctmFile} Error {" + e.getStackTraceAsString() + "}");	
				e.printStackTrace();
				sMgr.release(session);
			}
			return retFilePath;
			
		}

/**
 * 
 * @param tiffFile
 * @param fileOutputPath
 * @return converted file path
 * @throws IOException
 */
		protected String getConvertedFile(String strTiffFile, String fileOutputPath) throws IOException {
			String retPath = null;
			try{
				if(logDebug) System.out.println("{GetImage}{getConvertedFile} Tiff {" + strTiffFile + "}");
				if(logDebug) System.out.println("{GetImage}{getConvertedFile} JPEG {" + fileOutputPath + "}");
				File fileJPG = new File(fileOutputPath);
				if(!fileJPG.exists()){
					fileJPG.createNewFile();
				}
				File tiffFile = new File(strTiffFile);
				try{
					final BufferedImage tif = ImageIO.read(tiffFile);
					ImageIO.write(tif, "jpg", fileJPG);
					if(!this.fileExists(fileOutputPath)){
						throw new Exception("{GetImage}{getConvertedFile} Exception: First conversion failed, trying next");
					}					

				}catch(Exception eJpg){
					// log
					System.out.println("{GetImage}{getConvertedFile} Error converting to jpg{" + eJpg.getMessage() + "}");					
					try{
					   
						FileInputStream fis = new FileInputStream(tiffFile);  
					    final BufferedImage buffImage = ImageIO.read(fis);  
					    ImageIO.write(buffImage, "jpg", fileJPG);
						if(!this.fileExists(fileOutputPath)){
							throw new Exception("{GetImage}{getConvertedFile} Exception: Second conversion failed, trying next");
						}	
					    
					}catch(Exception eJpgSecond){
						System.out.println("{GetImage}{getConvertedFile} Error converting to jpg - with i/p stream{"
									+ eJpgSecond.getMessage() + "}");
						// try convert to PNG in case issue
						fileOutputPath = fileOutputPath.replace(".jpg", ".png");
						if(logDebug) System.out.println("{GetImage}{getConvertedFile} Trying with PNG {" + fileOutputPath + "}");
						File filePNG = new File(fileOutputPath.replace(".jpg", ".png"));
						if(!filePNG.exists()){
								filePNG.createNewFile();
						}
						final BufferedImage tif = ImageIO.read(tiffFile);
						ImageIO.write(tif, "png", filePNG);
					}
				}	
					
				
				retPath = fileOutputPath;
				/*try{
					tiffFile.delete();
					if(logDebug) System.out.println("{GetImage}{getConvertedFile} File Deleted {" + strTiffFile + "}");
				}catch(Exception eDctm){
					DfLogger.error(this,"{GetImage}{getConvertedFile} Unable to delete file");					
				}*/
			}catch(Exception eIO){
				System.out.println("{GetImage}{getConvertedFile} Error{" + eIO.getMessage() + "}");
				eIO.printStackTrace();
				sMgr.release(session);
			}
			return retPath;
		}
			/**
			 * 
			 * @param strTiffFile
			 * @param fileOutputPath
			 * @param bPNG
			 * @return
			 * @throws IOException
			 */
				protected String getConvertedFile(String strTiffFile, String fileOutputPath,boolean bPNG) throws IOException {
					String retPath = null;
					try{
						if(logDebug) System.out.println("{GetImage}{getConvertedFile}  ** PNG ** {" + fileOutputPath + "}");
						if(logDebug) System.out.println("{GetImage}{getConvertedFile} Tiff {" + strTiffFile + "}");
						
						File fileJPG = new File(fileOutputPath);
						if(!fileJPG.exists()){
							fileJPG.createNewFile();
						}
						File tiffFile = new File(strTiffFile);
						try{
							final BufferedImage tif = ImageIO.read(tiffFile);
							ImageIO.write(tif, "png", fileJPG);
							if(!this.fileExists(fileOutputPath)){
								throw new Exception("{GetImage}{getConvertedFile} Exception: First PNG conversion failed, trying next");
							}					

						}catch(Exception eJpg){
							// log
							System.out.println("{GetImage}{getConvertedFile} Error converting to jpg{" + eJpg.getMessage() + "}");	
							// try convert to jpeg in case issue
								fileOutputPath = fileOutputPath.replace(".png", ".jpg");
								if(logDebug) System.out.println("{GetImage}{getConvertedFile} Trying with JPEG {" + fileOutputPath + "}");
								// try convert to PNG in case issue
								File filePNG = new File(fileOutputPath);
								if(!filePNG.exists()){
										filePNG.createNewFile();
								}
								final BufferedImage tif = ImageIO.read(tiffFile);
								ImageIO.write(tif, "png", filePNG);
		
						}	
							
						
						retPath = fileOutputPath;
						/*try{
							tiffFile.delete();
							if(logDebug) System.out.println("{GetImage}{getConvertedFile} File Deleted {" + strTiffFile + "}");
						}catch(Exception eDctm){
							DfLogger.error(this,"{GetImage}{getConvertedFile} Unable to delete file");					
						}*/
					}catch(Exception eIO){
						System.out.println("{GetImage}{getConvertedFile} Error{" + eIO.getMessage() + "}");
						eIO.printStackTrace();
						sMgr.release(session);
					}
					return retPath;
				}
	/**
	 * 
	 * @param tiffFilePath
	 * @return true false
	 * @throws IOException
	 */
		protected boolean fileExists(String tiffFilePath) throws IOException {
			boolean bRet = false;
			try{
				
				File fileTiff = new File(tiffFilePath);
				if(fileTiff.exists()){
					if(logDebug) System.out.println("{GetImage}{fileExists} Exists {" + tiffFilePath + "}");
					if(fileTiff.length() > 0){
						bRet = true;
					}else{
						if(logDebug) System.out.println("{GetImage}{fileExists} Exists but Empty File");
					}
					
				}else{
					if(logDebug) System.out.println("{GetImage}{fileExists} Do not exist {" + tiffFilePath + "}");
					
				}

			}catch(Exception eIO){
				System.out.println("{GetImage}{fileExists} Error{" + eIO.getMessage() + "}");
				eIO.printStackTrace();				
			}
			return bRet;
		}


}
