package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.RepositoryStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 21/04/15
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class InwardImageExchangeProcessingTest extends AbstractBpmnTest {

    private static final String FILE_NAME = "aaabbbccc.zip";
    private static final String BUSINESS_KEY = "bk_42";
    private static final String PROCESS_DEFINTION = "EAID_5ADCF598_C32A_49e8_9487_F525E3CB6C06";

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

    @Deployment(resources = {"bpmn/inward_image_exchange_file.bpmn"})
    @Test
    public void shouldRunProcessToEnd_whenFileHasNotBeenReceivedBefore()
    {
        mockJob();
        when(repositoryStore.queryFileHasBeenReceivedBefore(FILE_NAME, DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND)).thenReturn(false);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        runtimeService.correlateMessage("lombard.service.inclearings.unpackimageexchangebatch.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.inclearings.storeinwardimageexchangedocumentum.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-inclearings-unpackimageexchangebatch-request");
        verify(camelService).sendTo("direct:lombard-service-inclearings-storeinwardimageexchangedocumentum-request");

        verify(jobStore).findJob(BUSINESS_KEY);
        verify(repositoryStore).queryFileHasBeenReceivedBefore(FILE_NAME, DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND);
    }

    @Deployment(resources = {"bpmn/inward_image_exchange_file.bpmn"})
    @Test
    public void shouldRunAbortProcess_whenFileHasBeenReceivedBefore()
    {
        mockJob();
        when(repositoryStore.queryFileHasBeenReceivedBefore(FILE_NAME, DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND)).thenReturn(true);

        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        verify(camelService, never()).sendTo("direct:lombard-service-inclearings-unpackimageexchangebatch-request");
        verify(camelService, never()).sendTo("direct:lombard-service-inclearings-storeinwardimageexchangedocumentum-request");

        verify(camelService).sendTo("direct:lombard-service-inclearings-inwardimageexchange-checkduplicatefile-incident");

        verify(jobStore).findJob(BUSINESS_KEY);
        verify(repositoryStore).queryFileHasBeenReceivedBefore(FILE_NAME, DocumentExchangeEnum.IMAGE_EXCHANGE_INBOUND);
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
