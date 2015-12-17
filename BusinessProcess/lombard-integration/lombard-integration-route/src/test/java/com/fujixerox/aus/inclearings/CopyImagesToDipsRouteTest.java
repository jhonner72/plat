package com.fujixerox.aus.inclearings;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 30/06/15
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CopyImagesToDipsRouteTest {
    JobStore jobStore = mock(JobStore.class);
    Transformer responseTransformer = mock(Transformer.class);

    private static final String VAR_NAME = "varname";
    private static final String VAR_VALUE = "varvalue";

    public static final String CORRELATION_ID = "aaa-bbb-ccc";

    @Test
    public void shouldExecuteResponseRoute() throws Exception {
        JndiContext jndiContext = new JndiContext();

        jndiContext.bind("jobStore", jobStore);

        Job job = new Job();
        job.setJobIdentifier(CORRELATION_ID);

        Activity activity = new Activity();
        activity.setActivityIdentifier("lombard.service.inclearings.copyimages.test");
        activity.setPredicate("predicate");
        activity.setSubject("subject");
        job.getActivities().add(activity);

        jndiContext.bind("transform.lombard.service.inclearings.copyimages.response", responseTransformer);

        HashMap<String, Object> map = new HashMap<>();
        map.put(VAR_NAME, VAR_VALUE);
        when(responseTransformer.transform(any(Job.class))).thenReturn(map);

        when(jobStore.findJob(anyString())).thenReturn(job);

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq", camelContext.getComponent("seda"));

        CopyImagesToDipsRoute copyImagesToDipsRoute = new CopyImagesToDipsRoute();
        copyImagesToDipsRoute.setServiceName("lombard.service.inclearings.copyimages");
        copyImagesToDipsRoute.setOptions("");
        copyImagesToDipsRoute.setHostname("localhost");
        copyImagesToDipsRoute.setPort("9576");
        copyImagesToDipsRoute.setPredicate("predicate");
        copyImagesToDipsRoute.setSubject("subject");

        camelContext.addRoutes(copyImagesToDipsRoute);

        RouteDefinition rd = camelContext.getRouteDefinition("lombard.service.inclearings.copyimages.response");

        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:test-endpoint");
                mockEndpointsAndSkip("camunda-bpm:message");
            }
        });

        MockEndpoint camundaEndpoint = MockEndpoint.resolve(camelContext, "mock:camunda-bpm:message");
        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:test-endpoint");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION_ID);
        exchange.getIn().setBody(CORRELATION_ID);
        producerTemplate.send(endpoint, exchange);

        List<Exchange> exchanges = camundaEndpoint.getExchanges();
        assertThat(exchanges.size(), is(1));

        Exchange actual = exchanges.get(0);
        assertThat(actual.getProperty("CamundaBpmBusinessKey", String.class), is(CORRELATION_ID));

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(responseTransformer).transform(jobArgumentCaptor.capture());
        Job jobCaptured = jobArgumentCaptor.getValue();
        assertThat(jobCaptured.getJobIdentifier(), is(CORRELATION_ID));

        Map body = actual.getIn().getBody(Map.class);
        assertThat(body.get(VAR_NAME), is((Object)VAR_VALUE));

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(jobStore).findJob(stringArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue(), is(CORRELATION_ID));
    }
}
