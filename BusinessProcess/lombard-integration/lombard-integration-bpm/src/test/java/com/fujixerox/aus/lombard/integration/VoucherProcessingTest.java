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
 * Created by warwick on 23/02/2015.
 */
public class VoucherProcessingTest extends AbstractBpmnTest
{
    private static final String BUSINESS_KEY = "bk_42";
    private static final String BUSINESS_KEY2 = "bk_43";
    private static final String FILE_NAME = "aaabbbccc.zip";
    private static final String BUSINESS_PROCESS_DEFN = "EAID_C469F557_7479_4732_A91B_DE4CA8C6FA12";

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

    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldCompleteProcess_whenChequesAreCleanAndReceivedForTheFirstTime()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);

        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", true);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateCodelineVariables.put("validateTransaction", true);
        validateTransactionVariables.put("checkThirdParty", false);
        
        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));
        
        runtimeService.correlateMessage("lombard.service.outclearings.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-storebatchvoucher-request");
    }

    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldAbortProcess_whenProcessIsAlreadyInProgressWithSameFilename_inflight()
    {
        mockJob();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        assertThat((String) runtimeService.getVariable(processInstance.getProcessInstanceId(), "inboundFilename"), is(FILE_NAME));
        
        mockJob(BUSINESS_KEY2);
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY2);
        
        verify(camelService).sendTo("direct:lombard-service-outclearings-voucherprocess-checkduplicatefile-incident");
    }

    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldAbortProcess_whenFileWasAlreadyStoredWithSameFilename_documentum()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", true);
        
        runtimeService.correlateMessage("lombard.service.outclearings.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        
        verify(camelService).sendTo("direct:lombard-service-outclearings-voucherprocess-checkduplicatefile-incident");
    }

    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldCompleteProcess_whenManualCorrectionIsRequired()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);

        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", false);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateCodelineVariables.put("validateTransaction", false);
        validateCodelineVariables.put("checkThirdParty", false);

        runtimeService.correlateMessage("lombard.service.outclearings.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.correctcodeline.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.correcttransaction.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-correctcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-correcttransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-storebatchvoucher-request");
    }


    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldCompleteProcess_whenTPCRequired_CorrectionIsRequired()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", false);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateCodelineVariables.put("validateTransaction", false);
        validateCodelineVariables.put("checkThirdParty", true);

        runtimeService.correlateMessage("lombard.service.outclearings.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.correctcodeline.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.correcttransaction.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.checkthirdparty.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-correctcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-correcttransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-checkthirdparty-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-storebatchvoucher-request");
    }

    @Deployment(resources = {"bpmn/voucher_processing.bpmn"})
    @Test
    public void shouldCompleteProcess_whenTPCRequired_NOCorrectionIsRequired()
    {
        mockJob();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(BUSINESS_PROCESS_DEFN, BUSINESS_KEY);
        
        Map<String, Object> isDuplicatedFile = new HashMap<>();
        isDuplicatedFile.put("isDuplicatedFile", false);
        
        Map<String, Object> validateCodelineVariables = new HashMap<>();
        validateCodelineVariables.put("validateCodeline", false);

        Map<String, Object> validateTransactionVariables = new HashMap<>();
        validateCodelineVariables.put("validateTransaction", true);
        validateCodelineVariables.put("checkThirdParty", true);

        runtimeService.correlateMessage("lombard.service.outclearings.checkduplicatefile.response", BUSINESS_KEY, isDuplicatedFile);
        runtimeService.correlateMessage("lombard.service.outclearings.unpackagevoucher.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.recognisecourtesyamount.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatecodeline.response", BUSINESS_KEY, validateCodelineVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.correctcodeline.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.validatetransaction.response", BUSINESS_KEY, validateTransactionVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.checkthirdparty.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.outclearings.storebatchvoucher.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-outclearings-checkduplicatefile-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-unpackagevoucher-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-recognisecourtesyamount-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatecodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-correctcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-validatetransaction-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-checkthirdparty-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-storebatchvoucher-request");
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
