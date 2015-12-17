package com.fujixerox.aus.incident.route;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.List;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.fujixerox.aus.lombard.common.incident.Incident;

/**
 * 02/06/2015 Henry Niu
 */
public class IncidentRouteComponentTest implements AbstractComponentTest {
	
	private static final String END_POINT_MOCKED = "direct:test-endpoint";
	
	@Test
    public void shouldInvokeCreateForSLAIncident() throws Exception {
        List<Exchange> exchanges = invoke("shouldInvokeCreateForSLAIncident", buildIncident());        
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<Incident> incidentArgumentCaptor = ArgumentCaptor.forClass(Incident.class);
        //verify(incidentService).create(incidentArgumentCaptor.capture());
    }
	
	@Test
    public void shouldInvokeCreateForError() throws Exception {
        List<Exchange> exchanges = invoke("shouldInvokeCreateForError", "");        
        assertThat(exchanges.size(), is(0));

        ArgumentCaptor<Incident> incidentArgumentCaptor = ArgumentCaptor.forClass(Incident.class);
        //verify(incidentService).create(incidentArgumentCaptor.capture());
    }
	    
    private List<Exchange> invoke(String methodName, String exchangeBody) throws Exception {
        
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
        
        String endPointToTest = "";
        for (RouteDefinitionHolder holder : RouteDefinitionBuilder.getRouteDefinitionHolders()) {
        	String endPointMocked = holder.getEndPointForNotTesting();        	
        	if (holder.getMethodName().equals(methodName)) {
        		endPointMocked = END_POINT_MOCKED;
        		endPointToTest = holder.getEndPoint();
        	} 
        	RouteDefinition rd = camelContext.getRouteDefinition(holder.getRouteDefinition());
            rd.adviceWith(camelContext, new AdviceWithRouteBuilderImpl(holder.getEndPoint(), endPointMocked));
        }

        MockEndpoint mockResponseQueue = MockEndpoint.resolve(camelContext, "mock:" + endPointToTest);

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint(END_POINT_MOCKED);
        Exchange exchange = endpoint.createExchange();   
        exchange.getIn().setBody(exchangeBody);
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", "aaa-bbb-ccc");
        producerTemplate.send(endpoint, exchange);

        return mockResponseQueue.getExchanges();
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