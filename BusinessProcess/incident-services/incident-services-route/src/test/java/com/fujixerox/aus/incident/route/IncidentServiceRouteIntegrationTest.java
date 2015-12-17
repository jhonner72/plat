package com.fujixerox.aus.incident.route;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.incident.Incident;

/**
 * 09/06/2015 Henry Niu
 */
public class IncidentRouteIntegrationTest implements AbstractIntegrationTest {
	
	@Test
    public void shouldInvokeCreateIncident() throws Exception {
        
    	JndiContext jndiContext = new JndiContext(); 
        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        IncidentRoute incidentRoute = new IncidentRoute();
        incidentRoute.setOptions("");
        incidentRoute.setHostname("localhost");
        incidentRoute.setPort("9576");
        
        incidentRoute.setIncidentHost("http://hniu.sysaidit.com:80/services/SysaidApiService");
        incidentRoute.setIncidentAccount("hniu");
        incidentRoute.setIncidentUsername("sysaid");
        incidentRoute.setIncidentPassword("changeit");

        camelContext.addRoutes(incidentRoute);
        
        for (RouteDefinitionHolder holder : RouteDefinitionBuilder.getRouteDefinitionHolders()) {
        	String endPointMocked = holder.getEndPointForNotTesting(); 
        	RouteDefinition rd = camelContext.getRouteDefinition(holder.getRouteDefinition());
            rd.adviceWith(camelContext, new AdviceWithRouteBuilderImpl(holder.getEndPoint(), endPointMocked));
        }

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:common-route");
        Exchange exchange = endpoint.createExchange();   
        exchange.getIn().setBody(buildIncident());
        Exchange responseExchange = producerTemplate.send(endpoint, exchange);
        
        assertNotNull(responseExchange);
      
    }
    
    private String buildIncident() throws JsonProcessingException {
    	Incident incident = new Incident();
    	incident.setDatetimeRaised(new Date());
    	incident.setDetails("incident detials");
    	incident.setJobIdentifier("JOB_ID");
    	incident.setPredicate("PREDICATE");
    	incident.setProcessDefintionName("PROCESSING DEFINITION NAME");
    	incident.setSubject("INCIDENT SUBJECT");
    	
    	ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JaxbAnnotationModule());
		return mapper.writeValueAsString(incident);
    } 

}