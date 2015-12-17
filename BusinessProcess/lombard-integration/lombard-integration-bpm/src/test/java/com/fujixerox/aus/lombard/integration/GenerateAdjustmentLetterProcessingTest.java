package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.outclearings.endofday.CalendarUtilsTest;
import com.fujixerox.aus.lombard.outclearings.endofday.EndOfDayProcessingJobAdapter;
import com.fujixerox.aus.lombard.reporting.metadata.ReportRequest;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Henry Niu
 * 20/07/2015
 */
public class GenerateAdjustmentLetterProcessingTest extends AbstractBpmnTest {
	
    private static final String BUSINESS_KEY = "d2_11";
    private static final String PROCESS_DEFINTION = "EAID_DP000000_B334_445e_AD98_D03FC21A1700";

    @Deployment(resources = {"bpmn/generate_adjustment_letters.bpmn"})
    @Test
    public void shouldRunProcess()
    {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("vouchersRemaining", true);

        orchestrate(runtimeService, "ie_5678", processVariables);  

        verify(camelService).sendTo("direct:lombard-service-adjustment-letter-required-search-request");
        verify(camelService).sendTo("direct:lombard-service-adjustment-letter-create-request");
        verify(camelService).sendTo("direct:lombard-service-adjustment-letter-store-request");
    }
    
    private void orchestrate(RuntimeService runtimeService, String businessKey, Map<String, Object> processVariables) {
        if (processVariables.get("vouchersRemaining").equals(false)) {
            runtimeService.correlateMessage("lombard.service.adjustment.letter.required.search.response", BUSINESS_KEY, processVariables);
        } else {
        	runtimeService.correlateMessage("lombard.service.adjustment.letter.required.search.response", BUSINESS_KEY, processVariables);
            runtimeService.correlateMessage("lombard.service.adjustment.letter.create.response", BUSINESS_KEY);
            runtimeService.correlateMessage("lombard.service.adjustment.letter.store.response", BUSINESS_KEY);
        }
    }

}
