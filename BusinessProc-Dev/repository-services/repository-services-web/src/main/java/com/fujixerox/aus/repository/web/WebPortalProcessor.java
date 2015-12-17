package com.fujixerox.aus.repository.web;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.repository.api.RepositoryService;
import com.fujixerox.aus.repository.api.RepositoryServiceImpl;
import com.fujixerox.aus.repository.util.FileUtil;
import com.fujixerox.aus.repository.util.dfc.DocumentumSessionFactory;

public class WebPortalProcessor extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
		request.setAttribute("requestBody", request.getParameter("request"));
		request.setAttribute("responseBody", getResponse(request));
		
		String nextJSP = "/index.jsp";
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
		dispatcher.forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}	
	  
	private String getResponse(HttpServletRequest request) throws ServletException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JaxbAnnotationModule());
			
			String serviceName = request.getParameter("serviceName");
			Class requestClass = ServiceFactory.getServiceClass(serviceName);		
			Object requestObject = mapper.readValue(request.getParameter("request"), requestClass);
					
			RepositoryServiceImpl service = new RepositoryServiceImpl();
			service.setDocumentumSessionFactory(getDocumentumSessionFactory());
			service.setFileUtil(new FileUtil("C:\\Temp"));

			Method method = RepositoryService.class.getMethod(serviceName, requestClass);
			Object responseObject = method.invoke(service, requestObject);
			
			StringWriter stringWriter = new StringWriter();
			mapper.writeValue(stringWriter, responseObject);
					 
			return stringWriter.toString();
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}
	
	private DocumentumSessionFactory getDocumentumSessionFactory() {
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());		
		return (DocumentumSessionFactory) applicationContext.getBean("documentumSessionFactory");
	}

}
