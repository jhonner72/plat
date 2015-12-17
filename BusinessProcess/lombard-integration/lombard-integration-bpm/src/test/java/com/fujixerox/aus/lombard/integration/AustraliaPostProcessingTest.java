package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.spring.annotations.BusinessKey;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

/**
 * Created by warwick on 23/02/2015.
 */
public class AustraliaPostProcessingTest extends AbstractBpmnTest
{
    private static final String BUSINESS_KEY = "bk_AP";
    private static final String BUSINESS_KEY2 = "bk_AP2";
    private static final String FILE_NAME = "aaabbbccc.zip";
    private static final String BUSINESS_PROCESS_DEFN = "EAID_DP000000_A683_4de1_B5D7_CA12D289C066";
    private static final String BPM_AUSTRALIA_POST_PATH= "bpmn/australia_post.bpmn";

    JobStore jobStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
    }

    @Deployment(resources = {BPM_AUSTRALIA_POST_PATH})
    @Test
    public void shouldCompleteProcess_whenChequesAreCleanAndReceivedForTheFirstTime()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", true);

        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));

        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-storebatchvoucher-request");
    }

    @Deployment(resources = {BPM_AUSTRALIA_POST_PATH})
    @Test
    public void shouldAbortProcess_whenProcessIsAlreadyInProgressWithSameFilename_inflight()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));
        
        mockJob(BUSINESS_KEY2);
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY2);
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapostitemprocess-checkduplicatefile-incident");
    }

    @Deployment(resources = {BPM_AUSTRALIA_POST_PATH})
    @Test
    public void shouldAbortProcess_whenFileWasAlreadyStoredWithSameFilename_documentum()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", true);
        
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapostitemprocess-checkduplicatefile-incident");
    }

    @Deployment(resources = {BPM_AUSTRALIA_POST_PATH})
    @Test
    public void shouldCompleteProcess_whenCorrectCodelineRequired()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", false);

        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));

        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.correctcodeline.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.australiapost.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-correctcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-australiapost-storebatchvoucher-request");
    }

    private void mockJob() {
        mockJob(BUSINESS_KEY);
    }

    private void mockJob(String businessKey) {
        Job job = new Job();
        Activity activity = new Activity();
        ReceivedFile receivedFile = new ReceivedFile();
        receivedFile.setFileIdentifier(FILE_NAME);
        activity.setRequest(receivedFile);
        job.getActivities().add(activity);
        when(jobStore.findJob(businessKey)).thenReturn(job);
    }

}
