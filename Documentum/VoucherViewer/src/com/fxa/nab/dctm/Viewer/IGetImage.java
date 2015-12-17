package com.fxa.nab.dctm.Viewer;

import javax.servlet.ServletContext;

/**
 * Author: Ajit Dangal
 * Project: NAB Cheques Digitisation
 *
 * Objective: Interface front ending for Image display
 * 
 * @Assumption : 
 * @TFS story :   
 */	

public interface IGetImage {
	
	public String getImage(String strDebug,String docid,String docbase, String user , String pwd,ServletContext ctx);	
	public String getImage(String strDebug,String docid,String docbase, String user , String pwd,ServletContext ctx,String strDiffCtx);	

}
