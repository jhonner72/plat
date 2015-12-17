package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.RepositoryStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

/**
 * Created by Zaka Lei on 18/10/2015.
 */
public class LockedBoxProcessingTest extends AbstractBpmnTest
{
	private static final String BPM_RESOURCE = "bpmn/locked_box_processing.bpmn";
    private static final String BUSINESS_KEY = "bk_01";
    private static final String BUSINESS_KEY2 = "bk_02";
    private static final String FILE_NAME = "aaabbbccc.zip";
    private static final String BUSINESS_PROCESS_DEFN = "EAID_NSBL0000_A176_40a4_B82A_8917B9A0ADA1";

    JobStore jobStore;
    RepositoryStore repositoryStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
        repositoryStore = findBean("mockRepository");
        reset(repositoryStore);
    }
    
    @Deployment(resources = {BPM_RESOURCE})
    @Test
    public void shouldAbortProcess_whenProcessIsAlreadyInProgressWithSameFilename_inflight()
    {
        mockJob();
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));
        
        mockJob(BUSINESS_KEY2);
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY2);

        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedboxprocess-checkduplicatefile-incident");
    }

    @Deployment(resources = {BPM_RESOURCE})
    @Test
    public void shouldAbortProcess_whenFileWasAlreadyStoredWithSameFilename_documentum()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", true);
        
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);

        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedboxprocess-checkduplicatefile-incident");
    }
    
    @Deployment(resources = {BPM_RESOURCE})
    @Test
    public void shouldPassProcess_whenIsNotListPayBatch()
    {
        mockJob();
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> recogniseCourtesyAmountVariables = new HashMap<>();
        recogniseCourtesyAmountVariables.put("isListPayBatch", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", true);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateTransactionVariables.put("validateTransaction", true);

        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.recognisecourtesyamount.response", BUSINESS_KEY, recogniseCourtesyAmountVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-storebatchvoucher-request");
    }
    
    @Deployment(resources = {BPM_RESOURCE})
    @Test
    public void shouldPassCompleteProcess_whenIsNotListPayBatch()
    {
        mockJob();
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);

        Map<String, Object> recogniseCourtesyAmountVariables = new HashMap<>();
        recogniseCourtesyAmountVariables.put("isListPayBatch", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", false);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateTransactionVariables.put("validateTransaction", false);

        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.recognisecourtesyamount.response", BUSINESS_KEY, recogniseCourtesyAmountVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.correctcodeline.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.correcttransaction.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-correctcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-correcttransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-storebatchvoucher-request");
    }
    
    @Deployment(resources = {BPM_RESOURCE})
    @Test
    public void shouldPassProcess_whenIsListPayBatch()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> recogniseCourtesyAmountVariables = new HashMap<>();
        recogniseCourtesyAmountVariables.put("isListPayBatch", true);
        
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.recognisecourtesyamount.response", BUSINESS_KEY, recogniseCourtesyAmountVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.correcttransaction.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.lockedbox.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-correcttransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-lockedbox-storebatchvoucher-request");
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
