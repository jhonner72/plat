package com.fujixerox.aus.integration.service;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.recognisecourtesyamount.RecogniseBatchCourtesyAmountRequest;
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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ServiceRouteTest
{
    JobStore jobStore = mock(JobStore.class);
    Transformer requestTransformer = mock(Transformer.class);
    Transformer responseTransformer = mock(Transformer.class);

    public static final String SERVICE_NAME = "lombard.service.service.recognisecourtesyamount";
    public static final String CORRELATION_ID = "aaa-bbb-ccc";

    public static final String ACTIVITY_IDENTIFIER = "lombard.service.service.someotherservice";
    @Test
    public void shouldInvokeRequestRoute() throws Exception {

        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        when(jobStore.findJob(anyString())).thenReturn(job);
        when(requestTransformer.transform(any(Job.class))).thenReturn(new RecogniseBatchCourtesyAmountRequest());

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq",  camelContext.getComponent("seda"));

        ServiceRoute recogniseCourtesyAmount = new ServiceRoute();
        recogniseCourtesyAmount.setServiceName(SERVICE_NAME);
        recogniseCourtesyAmount.setOptions("");
        recogniseCourtesyAmount.setHostname("localhost");
        recogniseCourtesyAmount.setPort("9576");
        recogniseCourtesyAmount.setProduce(true);
        camelContext.addRoutes(recogniseCourtesyAmount);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME + ".request");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                mockEndpointsAndSkip("rabbitmq://localhost:9576/" + SERVICE_NAME + ".request");
            }
        });

        MockEndpoint mockQueue = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:9576/" + SERVICE_NAME + ".request");
        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:" + SERVICE_NAME.replaceAll("\\.", "-") + "-request");
        Exchange exchange = endpoint.createExchange();

        exchange.setProperty("CamundaBpmBusinessKey", CORRELATION_ID);
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = mockQueue.getExchanges();
        assertThat(exchanges.size(), is(1));

        Exchange actual = exchanges.get(0);
        assertThat(actual.getIn().getHeader("rabbitmq.CORRELATIONID", String.class), is(CORRELATION_ID));
        assertThat(actual.getIn().getBody(String.class), is("{}"));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobStore).findJob(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(CORRELATION_ID));

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(requestTransformer).transform(jobArgumentCaptor.capture());
        assertThat(jobArgumentCaptor.getValue(), is(job));
    }

    @Test
    public void shouldInvokeResponseRoute_whenNoResponseHandler() throws Exception {
        executeResponseRoute(false);
    }

    @Test
    public void shouldInvokeResponseRoute_whenHasResponseHandler() throws Exception {
        executeResponseRoute(true);
    }

    private static final String VAR_NAME = "varname";
    private static final String VAR_VALUE = "varvalue";

    protected void executeResponseRoute(boolean hasResponseHandler) throws Exception
    {
        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);
        jndiContext.bind("transform." + SERVICE_NAME + ".request", requestTransformer);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        if (hasResponseHandler)
        {
            Activity activity = new Activity();
            activity.setActivityIdentifier(ACTIVITY_IDENTIFIER);
            activity.setPredicate("predicate");
            activity.setSubject("subject");
            job.getActivities().add(activity);

            jndiContext.bind("transform." + ACTIVITY_IDENTIFIER + ".response", responseTransformer);

            HashMap<String, Object> map = new HashMap<>();
            map.put(VAR_NAME, VAR_VALUE);
            when(responseTransformer.transform(any(Job.class))).thenReturn(map);
        }

        when(jobStore.findJob(anyString())).thenReturn(job);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        ServiceRoute recogniseCourtesyAmount = new ServiceRoute();
        recogniseCourtesyAmount.setServiceName(SERVICE_NAME);
        recogniseCourtesyAmount.setOptions("");
        recogniseCourtesyAmount.setHostname("localhost");
        recogniseCourtesyAmount.setPort("9576");
        recogniseCourtesyAmount.setTransformResponse(hasResponseHandler);
        recogniseCourtesyAmount.setPredicate("predicate");
        recogniseCourtesyAmount.setSubject("subject");
        recogniseCourtesyAmount.setConsume(true);
        recogniseCourtesyAmount.setProduce(true);

        camelContext.addRoutes(recogniseCourtesyAmount);

        RouteDefinition rd = camelContext.getRouteDefinition(SERVICE_NAME + ".response");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:test-endpoint");
                //mockEndpointsAndSkip("camunda-bpm:message?messageName=" + ACTIVITY_IDENTIFIER + ".response");
                mockEndpointsAndSkip("camunda-bpm:message");
            }
        });

        MockEndpoint camundaEndpoint = MockEndpoint.resolve(camelContext, "mock:camunda-bpm:message");
        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        exchange.getIn().setBody("{}");
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = camundaEndpoint.getExchanges();
        assertThat(exchanges.size(), is(1));

        Exchange actual = exchanges.get(0);
        assertThat(actual.getProperty("CamundaBpmBusinessKey", String.class), is(CORRELATION_ID));

        if (hasResponseHandler)
        {
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            verify(responseTransformer).transform(jobArgumentCaptor.capture());
            Job jobCaptured = jobArgumentCaptor.getValue();
            assertThat(jobCaptured.getJobIdentifier(), is(CORRELATION_ID));

            Map body = actual.getIn().getBody(Map.class);
            assertThat(body.get(VAR_NAME), is((Object)VAR_VALUE));
        }
        else
        {
            Object body = actual.getIn().getBody();
            assertThat(body, is(nullValue()));
        }

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobStore).findJob(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(CORRELATION_ID));
    }
}