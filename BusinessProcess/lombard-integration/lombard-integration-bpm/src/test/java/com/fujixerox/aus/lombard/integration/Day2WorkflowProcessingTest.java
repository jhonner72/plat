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
 * Date: 30/06/15
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Day2WorkflowProcessingTest extends AbstractBpmnTest{

    private static final String BUSINESS_KEY = "d2_11";
    private static final String PROCESS_DEFINTION = "EAID_C0F4FFC7_B700_406b_8434_C2C4049F0866";

    @Deployment(resources = {"bpmn/day2_workflow.bpmn"})
    @Test
    public void shouldRunProcess()
    {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        runtimeService.correlateMessage("lombard.service.outclearings.triggerworkflow.response", BUSINESS_KEY);
        verify(camelService).sendTo("direct:lombard-service-outclearings-triggerworkflow-request");
    }

}
