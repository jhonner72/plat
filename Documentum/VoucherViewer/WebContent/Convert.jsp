<%@page contentType="text/html" %>
<%@page import="com.fxa.nab.dctm.Viewer.*" %>
<%! static int iC=0;%> 
<% 

	String strObjId = (String)request.getParameter("id");
	String strDocbase = (String)request.getParameter("_docbase");
	String strUser = (String)request.getParameter("_username");
	String strTicket = (String)request.getParameter("_password");
	String strDebug = (String)request.getParameter("debug");
	String strJspDebug = (String)request.getParameter("debugJsp");
	String strDiffCtx = (String)request.getParameter("diffCtx");
	

	strDebug = "true";
	out.println("\n Docbase{" + strDocbase);
	out.println("\n ObjId{" + strObjId);
	out.println("\n User{" + strUser);
	out.println("\n Ticket{" + strTicket);	
	out.println("\n JSP Debug{" + strJspDebug);
	out.println("Diff ctx" + strDiffCtx);		
	iC++;
	out.println("\n Counter >>" + iC);
	if(iC > 10000){
		IImagePurge purImg = new ImagePurge ();
		String strPurStat = "NOT ABLE TO PURGE";
		if(strDiffCtx != null){
			strPurStat = purImg.purgeImages(strDebug,getServletContext(),strDiffCtx);
		}else{
			strPurStat = purImg.purgeImages(strDebug,getServletContext());
		}
		
		//System.out.println("\n Status >>>>>>>>> " + strPurStat);
		out.println("\n Status >>>>>>>>> " + strPurStat);	
		iC = 0;
	}
	String imagePath = null;
	IGetImage conImg = new GetImage(true);
	if(strDiffCtx.equalsIgnoreCase("true")){ 
		 imagePath= conImg.getImage(strDebug,strObjId,strDocbase,strUser,strTicket,getServletContext(),strDiffCtx);
	}else{
		//out.println(getServletContext());
		try{
			imagePath= conImg.getImage(strDebug,strObjId,strDocbase,strUser,strTicket,getServletContext());
		}catch(Exception e){
			
		}
	}
	System.out.println("\n Image Path >>>>>>>>> " + imagePath);	
	if(imagePath != null){
		//response.setHeader("Image-counter",iC);	
		response.setHeader("Image-Path",imagePath);	
	}else{
		out.println("\n Image Path >>>>>>>>> NULL ");	
	}
	
%>



