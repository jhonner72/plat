package com.fujixerox.aus.lombard.integration;

import com.cedarsoftware.util.DeepEquals;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Job;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Mock Rabbit
 * Embedded Camunda
 *
 * Created by warwick on 24/02/2015.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public abstract class AbstractComponentTest extends AbstractTest
{
    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected JobStore jobStore;

    @Autowired
    protected MetadataStore metadataStore;

    @Autowired
    @Qualifier("camelContextCommon")
    protected CamelContext camelContextCommon;

    protected void replaceTo(CamelContext camelContext, String routeId, final String endpoint) throws Exception {
        RouteDefinition rd = camelContext.getRouteDefinition(routeId);
        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                if (endpoint != null) {
                    mockEndpointsAndSkip(endpoint);
                }
            }
        });
    }
    protected void replaceFrom(CamelContext camelContext, String routeId, final String newFrom) throws Exception {
        RouteDefinition rd = camelContext.getRouteDefinition(routeId);
        rd.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception
            {
                if (newFrom != null) {
                    replaceFromWith(newFrom);
                }
            }
        });
    }

    protected void start(CamelContext camelContext) throws Exception {
        camelContext.removeComponent("rabbitmq");
        camelContext.addComponent("rabbitmq",  camelContext.getComponent("seda"));
        camelContext.start();
    }
    protected static final String CORRELATION = "17032015-3AEA-4069-A2DD-SSSS12345678";

    protected void shouldExecuteProcess(Resource testData, CamelContext camelContext) throws Exception {

        replaceFrom(camelContextCommon, "lombard.job", "direct:job");
        replaceFrom(camelContextCommon, "lombard.dlx.retry", "direct:lombard-retry");

        ServiceTest serviceTest = loadTestData(testData, CORRELATION);

        // Setup mocks, replace to and from that are binding to queues
        MockEndpoint[] requestMocks = new MockEndpoint[serviceTest.getActivities().length];

        int i = 0;
        for (ActivityTest activity : serviceTest.getActivities())
        {
            if (activity.getRequest() == null && activity.getResponse() == null) {
                continue;
            }
            if (activity.getRequest() == null && activity.getResponse() != null) {
                replaceFrom(camelContext, activity.getActivityName() + ".response", "direct:" + activity.getActivityName().replaceAll("\\.", "-") + "-response");
                continue;
            }
            String queueName = activity.getQueueName() != null ? activity.getQueueName() : activity.getActivityName();
            replaceTo(camelContext, activity.getActivityName()+ ".request", "rabbitmq://localhost:5672/" + queueName + ".request");
            requestMocks[i++] = MockEndpoint.resolve(camelContext, "mock:rabbitmq:localhost:5672/" + queueName + ".request");
            replaceFrom(camelContext, activity.getActivityName() + ".response", "direct:" + activity.getActivityName().replaceAll("\\.", "-") + "-response");
        }


        // Avoid rabbitmq starting up consumers.
        start(camelContext);
        start(camelContextCommon);

        // Define the input and start the job route
        ProducerTemplate producerTemplate = camelContextCommon.createProducerTemplate();
        Endpoint endpoint = camelContextCommon.getEndpoint("direct:job");
        Exchange exchange = endpoint.createExchange();
        String body = objectMapper.writeValueAsString(serviceTest.getJob());
        exchange.getIn().setBody(body);
        producerTemplate.send(endpoint, exchange);

        // Validate the process
        i = 0;
        for (ActivityTest activity : serviceTest.getActivities())
        {
        	if (activity.getActivityName().equalsIgnoreCase("lombard.service.adjustment.letter.send")) {
        		continue;
        	}

            if (activity.getRequest() == null && activity.getResponse() == null) {
                Job inJob = serviceTest.getJob();
                Job foundJob = jobStore.findJob(inJob.getJobIdentifier());

                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(foundJob.getJobIdentifier()).singleResult();

                // Fire the timer
                TimerEntity timer = (TimerEntity) managementService.createJobQuery().timers().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
                managementService.executeJob(timer.getId());
                continue;
            }
            if (activity.getRequest() == null && activity.getResponse() != null) {
                simulateResponse(camelContext, activity.getActivityName(), activity);
                continue;
            }
            if(activity.getActivityName().equalsIgnoreCase("lombard.service.outclearings.checkthirdparty")) {
                String responseBody = objectMapper.writeValueAsString(activity.getRequest());
            }
            assertRequest(requestMocks[i++], activity);
            simulateResponse(camelContext, activity.getActivityName(), activity);
        }

        Job inputJob = serviceTest.getJob();

        // We no longer capture the process identifier
//        Job finalJob = jobStore.findJob(inputJob.getJobIdentifier());
//        String processIdentifier = finalJob.getProcessIdentifier();
//        assertThat(processIdentifier, is(notNullValue()));

        ProcessInstance result = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(inputJob.getJobIdentifier())
                .singleResult();

        // Ensure the process is no longer running, that is we got to the end.
        assertThat(result, is(nullValue()));

// To lookup the history we would need the process identifier, cant seem to lookup using businessKey
//        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(processIdentifier).list();
//        assertThat(list.size(), is(serviceTest.getActivityCount()));
    }

    private void simulateResponse(CamelContext camelContext, String activity, ActivityTest activityTest) throws JsonProcessingException {
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        Endpoint endpoint = camelContext.getEndpoint("direct:" + activity.replaceAll("\\.", "-") + "-response");
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setHeader("rabbitmq.CORRELATIONID", CORRELATION);

        String responseBody = objectMapper.writeValueAsString(activityTest.getResponse());
        exchange.getIn().setBody(responseBody);
        producerTemplate.send(endpoint, exchange);
    }

    protected void assertRequest(MockEndpoint mockEndpoint, ActivityTest activityTest) throws IOException {
        List<Exchange> receivedExchanges1 = mockEndpoint.getExchanges();
        if (activityTest.getActivityName().equalsIgnoreCase("lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus")
        		|| activityTest.getActivityName().equalsIgnoreCase("lombard.service.adjustment.letter.send"))
        {
            assertThat(receivedExchanges1.size(), is(2));

            String correlation = receivedExchanges1.get(0).getIn().getHeader("rabbitmq.CORRELATIONID", String.class);
            assertThat(correlation, is(CORRELATION));

            String body = receivedExchanges1.get(1).getIn().getBody(String.class);
            Object request = objectMapper.readValue(body, activityTest.getRequest().getClass());

            if (!DeepEquals.deepEquals(request, activityTest.getRequest()))
            {
                fail("Invalid request for " + activityTest.getActivityName());
            }
            return;
        }

        assertThat(receivedExchanges1.size(), is(1));
        String correlation = receivedExchanges1.get(0).getIn().getHeader("rabbitmq.CORRELATIONID", String.class);
        assertThat(correlation, is(CORRELATION));

        String body = receivedExchanges1.get(0).getIn().getBody(String.class);
        Object request = objectMapper.readValue(body, activityTest.getRequest().getClass());

        if (!DeepEquals.deepEquals(request, activityTest.getRequest()))
        {
            fail("Invalid request for " + activityTest.getActivityName());
        }
    }

}
