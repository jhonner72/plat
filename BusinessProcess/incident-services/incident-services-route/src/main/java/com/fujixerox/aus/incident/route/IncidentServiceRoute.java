package com.fujixerox.aus.incident.route;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import com.fujixerox.aus.lombard.common.incident.Incident;
import com.ilient.api.Login;
import com.ilient.api.Logout;
import com.ilient.api.Save;

/**
 * 02/06/2015 Henry Niu
 */
public class IncidentRoute extends RouteBuilder {

    private String hostname;
    private String port;
    private String options;
    
    private String incidentHost;
	private String incidentAccount;
    private String incidentUsername;
    private String incidentPassword;

	@Override
    public void configure() throws Exception {
        String fromOptions = options.length() > 0 ? "&" + options : "";
              
        fromF("rabbitmq://%s:%s/lombard.service.support.incident?queue=lombard.service.support.incident.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-support-incident").
	        to("direct:common-route"); 
        
        fromF("rabbitmq://%s:%s/lombard.service.support.error?queue=lombard.service.support.error.queue%s", hostname, port, fromOptions).
	        routeId("lombard-service-support-error").
	        to("direct:common-route"); 
        
        from("direct:common-route").
	        log("Incident Service Start: Create Incident").
	        setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
	        unmarshal().json(JsonLibrary.Jackson, Incident.class).
	        process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					Incident incident = exchange.getIn().getBody(Incident.class);
					exchange.setProperty("incident", incident);
					
					Login login = new Login();
					login.setAccountId(incidentAccount);
					login.setUserName(incidentUsername);
					login.setPassword(incidentPassword);
					
					StringWriter requestXmlWriter = new StringWriter();
					JAXB.marshal(login, requestXmlWriter);					
					Source source = new StreamSource(new StringReader(requestXmlWriter.toString()));
					exchange.getIn().setBody(source);
				}    
            }).  
            log("Incident Service: login to SysAid server").
	        to("spring-ws:" + incidentHost + "?soapAction=login").
	        process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					DOMSource loginResponse = exchange.getIn().getBody(DOMSource.class);
					long sessionId = Long.valueOf(loginResponse.getNode().getFirstChild().getFirstChild().getNodeValue());
					exchange.setProperty("sessionId", sessionId);
					
					Incident incident = exchange.getProperty("incident", Incident.class);	
					Save save = new IncidentTransformer().transform(sessionId, incident);	
					
					StringWriter requestXmlWriter = new StringWriter();
					JAXB.marshal(save, requestXmlWriter);					
					Source source = new StreamSource(new StringReader(requestXmlWriter.toString()));
					exchange.getIn().setBody(source);
				}	        	
	        }).	        
            log("Incident Service: create instance to SysAid server").
	        to("spring-ws:" + incidentHost + "?soapAction=save").	        
            process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					long sessionId = exchange.getProperty("sessionId", Long.class);
					
					Logout logout = new Logout();
					logout.setSessionId(sessionId);
					
					StringWriter requestXmlWriter = new StringWriter();
					JAXB.marshal(logout, requestXmlWriter);					
					Source source = new StreamSource(new StringReader(requestXmlWriter.toString()));
					exchange.getIn().setBody(source);
				}            	
            }).
            log("Incident Service: logout SysAid server").
            to("spring-ws:" + incidentHost + "?soapAction=logout").	        
	        log("Service Done: Create Incident").
        end();         
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setOptions(String options) {
        this.options = options;
    }
    
    public void setIncidentHost(String incidentHost) {
		this.incidentHost = incidentHost;
	}

	public void setIncidentAccount(String incidentAccount) {
		this.incidentAccount = incidentAccount;
	}

	public void setIncidentUsername(String incidentUsername) {
		this.incidentUsername = incidentUsername;
	}

	public void setIncidentPassword(String incidentPassword) {
		this.incidentPassword = incidentPassword;
	}
    
    
}
