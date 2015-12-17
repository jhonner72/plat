package com.fujixerox.aus.lombard.integration;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 15/07/15
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressPendingProcessingTest extends AbstractBpmnTest {

    private static final String PROCESS_DEFINTION = "EAID_9A788295_AB54_4101_A12C_45B43C26F990";

    @Deployment(resources = {"bpmn/progress_pending_vouchers.bpmn"})
    @Test
    public void testReportingProcessWithDelivery()
    {
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "progress_1234");
        verify(camelService).sendTo("direct:lombard-service-outclearings-updatependingvouchers-request");
        runtimeService.correlateMessage("lombard.service.outclearings.updatependingvouchers.response", "progress_1234");
    }

}
