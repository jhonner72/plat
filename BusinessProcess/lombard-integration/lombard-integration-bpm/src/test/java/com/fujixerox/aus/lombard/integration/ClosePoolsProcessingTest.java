package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 7/09/15
 * Time: 1:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClosePoolsProcessingTest extends AbstractBpmnTest{

    private static final String BUSINESS_KEY = "bk_closepools";
    private static final String PROCESS_DEFINTION = "EAID_C9699240_ADBC_4549_998F_824F1811B8FC";

    JobStore jobStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
    }

    @Deployment(resources = {"bpmn/close_pools.bpmn"})
    @Test
    public void shouldPassAllActivitiesInTheProcess()
    {
        mockJob();
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        Map<String, Object> vouchersRemainingVariables = new HashMap<>();
        vouchersRemainingVariables.put("vouchersRemaining", true);

        runtimeService.correlateMessage("lombard.service.outclearings.getreleaseditems.response", BUSINESS_KEY, vouchersRemainingVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.generatevouchers.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.associategeneratedvouchers.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-getreleaseditems-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-generatevouchers-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-associategeneratedvouchers-request");
    }

    @Deployment(resources = {"bpmn/close_pools.bpmn"})
    @Test
    public void shouldEndProcessAfterGet()
    {
        mockJob();
        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        Map<String, Object> vouchersRemainingVariables = new HashMap<>();
        vouchersRemainingVariables.put("vouchersRemaining", false);

        runtimeService.correlateMessage("lombard.service.outclearings.getreleaseditems.response", BUSINESS_KEY, vouchersRemainingVariables);
        verify(camelService).sendTo("direct:lombard-service-outclearings-getreleaseditems-request");
        verify(camelService, never()).sendTo("direct:lombard-service-outclearings-generatevouchers-request");
        verify(camelService, never()).sendTo("direct:lombard-service-outclearings-associategeneratedvouchers-request");
    }

    private void mockJob() {
        mockJob(BUSINESS_KEY);
    }

    private void mockJob(String businessKey) {
        Job job = new Job();
        Activity activity = new Activity();
        job.getActivities().add(activity);
        when(jobStore.findJob(businessKey)).thenReturn(job);
    }
}
