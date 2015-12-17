package com.fujixerox.aus.integration.job;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.common.job.Job;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class JobRouteTest
{
    @Test
    public void shouldInvokeRoute() throws Exception {

        JndiContext jndiContext = new JndiContext();
        jndiContext.bind("json-gson", new JacksonDataFormat(JaxbMapperFactory.createWithoutAnnotations(), null));

        DefaultCamelContext camelContext = new DefaultCamelContext(jndiContext);

        JobStore jobStore = mock(JobStore.class);
        JobToProcessMapper jobToProcessMapper = mock(JobToProcessMapper.class);

        JobRoute jobRoute = new JobRoute();
        jobRoute.setHostname("localhost");
        jobRoute.setPort("5672");
        jobRoute.setExchangeName("job");
        jobRoute.setJobStore(jobStore);
        jobRoute.setJobToProcessMapper(jobToProcessMapper);
        camelContext.addRoutes(jobRoute);

        Job job = new Job();
        Mockito.when(jobStore.findJob(Matchers.anyString())).thenReturn(job);

        RouteDefinition rd = camelContext.getRouteDefinition("lombard.job");
        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                replaceFromWith("direct:job");
                mockEndpointsAndSkip("camunda-bpm:start");
            }
        });
        MockEndpoint camunda = MockEndpoint.resolve(camelContext, "mock:camunda-bpm:start");
        camunda.expectedHeaderReceived("CamundaBpmProcessDefinitionKey", "lombard-ping");

        Mockito.when(jobToProcessMapper.mapJobToProcess(Matchers.any(Job.class))).thenReturn("lombard-ping");

        camelContext.start();

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:job");
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setBody("{ \"jobIdentifier\" : \"aaa-bbb-ccc\" }");
        producerTemplate.send(endpoint, exchange);

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        Mockito.verify(jobStore, times(1)).storeJob(jobArgumentCaptor.capture());

        job = jobArgumentCaptor.getAllValues().get(0);
        assertThat(job.getJobIdentifier(), is("aaa-bbb-ccc"));

        camunda.assertIsSatisfied();
    }
}