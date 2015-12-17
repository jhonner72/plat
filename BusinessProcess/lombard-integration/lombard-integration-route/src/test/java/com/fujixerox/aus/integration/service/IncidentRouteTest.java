package com.fujixerox.aus.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.incident.Incident;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileAcknowledgementIncident;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class IncidentRouteTest {
    JobStore jobStore = mock(JobStore.class);
    Transformer requestTransformer = mock(Transformer.class);

    public static final String SERVICE_NAME = "lombard.service.outclearings.processvalueinstructionfileack.incident";
    public static final String CORRELATION_ID = "aaa-bbb-ccc";
    public static final String QUEUE_NAME = "lombard.service.support.incident";
    public static final String PREDICATE = "predicate";
    public static final String SUBJECT = "subject";

    @Test
    public void shouldInvokeRequestRoute() throws Exception {

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME, requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        ValueInstructionFileAcknowledgementIncident valueInstructionFileAcknowledgementIncident = new ValueInstructionFileAcknowledgementIncident();
        when(requestTransformer.transform(job)).thenReturn(valueInstructionFileAcknowledgementIncident);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        IncidentRoute incidentRoute = new IncidentRoute();
        incidentRoute.setServiceName(SERVICE_NAME);
        incidentRoute.setOptions("");
        incidentRoute.setHostname("localhost");
        incidentRoute.setPort("9576");
        incidentRoute.setSubject(SUBJECT);
        incidentRoute.setPredicate(PREDICATE);
        camelContext.addRoutes(incidentRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME);

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/" + QUEUE_NAME);
            }
        });

        MockEndpoint mockQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/"+QUEUE_NAME);
        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:lombard-service-outclearings-processvalueinstructionfileack-incident");
        Exchange exchange = endpoint.createExchange();

        exchange.setProperty("CamundaBpmBusinessKey", CORRELATION_ID);
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        Exchange actual = exchanges.get(0);
        assertThat(actual.getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is(CORRELATION_ID));
        String body = actual.getIn().getBody(String.class);
        ObjectMapper objectMapper = JaxbMapperFactory.createWithoutAnnotations();
        Incident incident = objectMapper.readValue(body, Incident.class);
        assertThat(incident.getJobIdentifier(), is(CORRELATION_ID));
        assertThat(incident.getPredicate(), is(PREDICATE));
        assertThat(incident.getSubject(), is(SUBJECT));
        assertThat(incident.getProcessDefintionName(), is(SERVICE_NAME));
        assertThat(incident.getDatetimeRaised(), is(notNullValue()));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobStore).findJob(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(CORRELATION_ID));

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(requestTransformer).transform(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getValue(), is(job));
    }
}
