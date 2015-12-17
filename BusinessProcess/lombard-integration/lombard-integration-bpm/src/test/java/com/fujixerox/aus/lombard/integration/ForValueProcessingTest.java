package com.fujixerox.aus.lombard.integration;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 27/04/15
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForValueProcessingTest extends AbstractBpmnTest {

    private static final String PROCESS_DEFINTION = "EAID_48F9E695_9818_44a9_8074_5C01CCADAD1B";

    @Deployment(resources = {"bpmn/for_value_vouchers_processing.bpmn"})
    @Test
    public void shouldExecuteCopyImagesCorrectCodelineRepostSteps()
    {
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "fv_1234");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("correctCodeline", true);
        processVariables.put("copySucceed", true);

        orchestrateImageExchange(runtimeService, processVariables);
        verifyCamelInteractions(processVariables);
    }

    @Deployment(resources = {"bpmn/for_value_vouchers_processing.bpmn"})
    @Test
    public void shouldExecuteCopyImagesSteps()
    {
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "fv_1234");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("correctCodeline", true);
        processVariables.put("copySucceed", false);

        orchestrateImageExchange(runtimeService, processVariables);
        verifyCamelInteractions(processVariables);
    }

    @Deployment(resources = {"bpmn/for_value_vouchers_processing.bpmn"})
    @Test
    public void shouldEndProcessAfterGet()
    {
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "fv_1234");

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("correctCodeline", false);

        orchestrateImageExchange(runtimeService, processVariables);
        verifyCamelInteractions(processVariables);
    }

    private void verifyCamelInteractions(Map<String, Object> processVariables)
    {
        if (processVariables.get("correctCodeline").equals(false)) {
            verify(camelService).sendTo("direct:lombard-service-inclearings-getinwardforvaluevouchers-request");
            return;
        }

        if (processVariables.get("copySucceed").equals(false)) {
            verify(camelService).sendTo("direct:lombard-service-inclearings-getinwardforvaluevouchers-request");
            verify(camelService).sendTo("direct:lombard-service-inclearings-copyimages-request");
            return;
        }

        verify(camelService).sendTo("direct:lombard-service-inclearings-getinwardforvaluevouchers-request");
        verify(camelService).sendTo("direct:lombard-service-inclearings-copyimages-request");
        verify(camelService).sendTo("direct:lombard-service-inclearings-forvaluecorrectcodeline-request");
        verify(camelService).sendTo("direct:lombard-service-inclearings-repostforvaluevouchers-request");
    }

    private void orchestrateImageExchange(RuntimeService runtimeService, Map<String, Object> processVariables)
    {
        if (processVariables.get("correctCodeline").equals(false)) {
            runtimeService.correlateMessage("lombard.service.inclearings.getinwardforvaluevouchers.response", "fv_1234", processVariables);
            return;
        }

        if (processVariables.get("copySucceed").equals(false)) {
            runtimeService.correlateMessage("lombard.service.inclearings.getinwardforvaluevouchers.response", "fv_1234", processVariables);
            runtimeService.correlateMessage("lombard.service.inclearings.copyimages.response", "fv_1234", processVariables);
            return;
        }

        runtimeService.correlateMessage("lombard.service.inclearings.getinwardforvaluevouchers.response", "fv_1234", processVariables);
        runtimeService.correlateMessage("lombard.service.inclearings.copyimages.response", "fv_1234", processVariables);
        runtimeService.correlateMessage("lombard.service.inclearings.forvaluecorrectcodeline.response", "fv_1234");
        runtimeService.correlateMessage("lombard.service.inclearings.repostforvaluevouchers.response", "fv_1234");
    }
}
