package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesRequest;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by vidya on 11/09/15.
 */
public class InwardIEReconciliationProcessingTest extends AbstractBpmnTest{

    private static final String BUSINESS_KEY = "17032015-3AEA-4069-A2DD-SSSS12345678";
    private static final String PROCESS_DEFINTION = "EAID_9DDCC424_7F67_4779_945C_A5DA6E90F876";



    JobStore jobStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);

    }

    @Deployment(resources = {"bpmn/inward_image_exchange_reconciliation.bpmn"})
    @Test
    public void shouldRunProcess_Matched()
    {
        mockJob(BUSINESS_KEY);

        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("isMatched", true);

        runtimeService.correlateMessage("lombard.service.inclearings.getreceivedfiles.response", BUSINESS_KEY, processVariables);
        runtimeService.correlateMessage("lombard.service.inclearings.storereceivedfiles.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-inclearings-getreceivedfiles-request");
        verify(camelService).sendTo("direct:lombard-service-inclearings-storereceivedfiles-request");
    }


    @Deployment(resources = {"bpmn/inward_image_exchange_reconciliation.bpmn"})
    @Test
    public void shouldRunProcess_UnMatched()
    {
        mockJob(BUSINESS_KEY);

        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("isMatched", false);

        runtimeService.correlateMessage("lombard.service.inclearings.getreceivedfiles.response", BUSINESS_KEY, processVariables);
        runtimeService.correlateMessage("lombard.service.inclearings.storereceivedfiles.response", BUSINESS_KEY);

        verify(camelService).sendTo("direct:lombard-service-inclearings-receivedfiles-nomatch-incident");
        verify(camelService).sendTo("direct:lombard-service-inclearings-storereceivedfiles-request");

    }


    private void mockJob(String businessKey) {
        Job job = new Job();
        Activity activity = new Activity();
        GetReceivedFilesResponse response = new GetReceivedFilesResponse();

        ReceivedFile test = new ReceivedFile();
        test.setFileIdentifier("IMGEXCH.20120704.20120705.223010.9999999999999999.ANZ.RBA .000001.xml");
        test.setReceivedDateTime(new Date());
        test.setTransmissionDateTime(new Date());
        response.getReceivedFiles().add(test);

        ReceivedFile test1 = new ReceivedFile();
        test1.setFileIdentifier("IMGEXCH.20120704.20120705.223010.9999999999999999.ANZ.RBA.000002.xml");
        test1.setReceivedDateTime(new Date());
        test1.setTransmissionDateTime(new Date());
        response.getReceivedFiles().add(test1);

        activity.setResponse(response);
        activity.setSubject("receivedfiles");
        activity.setPredicate("get");

        job.getActivities().add(activity);
        when(jobStore.findJob(businessKey)).thenReturn(job);

        MatchFilesRequest matchFilesRequest = new MatchFilesRequest();
        matchFilesRequest.getReceivedFilenames().add("hhsbigdsuyfdutevdgvcbhwveuhw");


    }

}
