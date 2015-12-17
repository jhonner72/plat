package com.fujixerox.aus.outclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.incident.Incident;
import com.fujixerox.aus.lombard.common.job.Job;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
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
public class RaiseSlaIncidentRouteTest {
    JobStore jobStore = mock(JobStore.class);
    Transformer requestTransformer = mock(Transformer.class);

    public static final String SERVICE_NAME = "lombard.service.outclearings.valueinstructionfile.incident";
    public static final String CORRELATION_ID = "aaa-bbb-ccc";
    public static final String QUEUE_NAME = "lombard.service.support.incident";

    @Test
    public void shouldInvokeRequestRoute() throws Exception {

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME, requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);

        Incident incident = new Incident();
        incident.setJobIdentifier(CORRELATION_ID);
        when(requestTransformer.transform(job)).thenReturn(incident);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq",  camelContext.getComponent("seda"));

        RaiseSlaIncidentRoute raiseSlaIncidentRoute = new RaiseSlaIncidentRoute();
        raiseSlaIncidentRoute.setServiceName(SERVICE_NAME);
        raiseSlaIncidentRoute.setOptions("");
        raiseSlaIncidentRoute.setHostname("localhost");
        raiseSlaIncidentRoute.setPort("9576");
        raiseSlaIncidentRoute.setPort("9576");
        raiseSlaIncidentRoute.setQueueName(QUEUE_NAME);
        camelContext.addRoutes(raiseSlaIncidentRoute);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME);

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/"+QUEUE_NAME);
            }
        });

        MockEndpoint mockQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/"+QUEUE_NAME);
        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:lombard-service-outclearings-valueinstructionfile-incident");
        Exchange exchange = endpoint.createExchange();

        Map<String, String> body = new HashMap<String, String>();
        body.put("jobIdentifier", CORRELATION_ID);

        exchange.getIn().setBody(body);
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        Exchange actual = exchanges.get(0);
        assertThat(actual.getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is(CORRELATION_ID));
        assertThat(actual.getIn().getBody(String.class), is(String.format("{\"jobIdentifier\":\"%s\"}", CORRELATION_ID)));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobStore).findJob(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(CORRELATION_ID));

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(requestTransformer).transform(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getValue(), is(job));
    }

}
