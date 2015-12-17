package com.fxa.nab.dctm.Viewer;

import javax.servlet.ServletContext;

/**
 * Author: Ajit Dangal
 * Project: NAB Cheques Digitisation
 *
 * Objective: Interface front ending for Image Clean up
 * 
 * @Assumption : 
 * @TFS story :   
 */	

public interface IImagePurge {
	
	public String purgeImages(String strDebug,ServletContext ctx);	
	public String purgeImages(String strDebug,ServletContext ctx,String strDiffCtx);	

}
