package com.fujixerox.aus.lombard.integration;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 7/06/15
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportProcessingTest extends AbstractBpmnTest {

    private static final String PROCESS_DEFINTION = "EAID_7388FA74_40C0_406a_820A_FB3B53591A99";

    @Deployment(resources = {"bpmn/reporting.bpmn"})
    @Test
    public void testReportingProcessWithDelivery()
    {
        Map<String, Object> processVariables = new HashMap<>();

        List<String> endpoints = new ArrayList<>();
        endpoints.add("Email1");

        processVariables.put("emailEndpoints", endpoints);

        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "rpt_1234", processVariables);

        processVariables.put("storeReports", true);
        processVariables.put("deliverEmail", true);
        processVariables.put("deliverB2B", true);

        orchestrateImageExchange(runtimeService, processVariables);
        verifyCamelInteractions(processVariables);
    }

    @Deployment(resources = {"bpmn/reporting.bpmn"})
    @Test
    public void testReportingProcessWithNoDelivery()
    {
        Map<String, Object> processVariables = new HashMap<>();

        List<String> endpoints = new ArrayList<>();
        endpoints.add("Email1");

        processVariables.put("emailEndpoints", endpoints);

        runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, "rpt_1234", processVariables);

        processVariables.put("storeReports", false);
        processVariables.put("deliverEmail", false);
        processVariables.put("deliverB2B", false);

        orchestrateImageExchange(runtimeService, processVariables);
        verifyCamelInteractions(processVariables);
    }

    private void verifyCamelInteractions(Map<String, Object> processVariables)
    {
        verify(camelService).sendTo("direct:lombard-service-reporting-executereport-request");

        if (processVariables.get("storeReports").equals(true)) {
            verify(camelService).sendTo("direct:lombard-service-reporting-storerepositoryreports-request");
        }

//        if (processVariables.get("deliverEmail").equals(true)) {
//            verify(camelService).sendTo("direct:lombard-service-reporting-deliverreport-request");
//        }
//
//        if (processVariables.get("deliverB2B").equals(true)) {
//            verify(camelService).sendTo("direct:lombard-service-reporting-deliverb2b-request");
//        }
    }

    private void orchestrateImageExchange(RuntimeService runtimeService, Map<String, Object> processVariables) {
        runtimeService.correlateMessage("lombard.service.reporting.executereport.response", "rpt_1234");

        if (processVariables.get("storeReports").equals(true)) {
            runtimeService.correlateMessage("lombard.service.reporting.storerepositoryreports.response", "rpt_1234");
        }
    }
}
