package com.fujixerox.aus.lombard.integration;

import java.util.Date;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;

import static org.mockito.Mockito.*;

/**
 * Henry Niu
 * 21/07/2015
 */
public class ClearAdjustmentProcessingTest extends AbstractBpmnTest {
	
    private static final String BUSINESS_KEY = "d2_11";
    private static final String PROCESS_DEFINTION = "EAID_0399963C_7C0C_4291_A57A_82B7D67DD027";
    
    JobStore jobStore;
    MetadataStore metadataStore;
    
    @Before
    public void setup() {
        jobStore = findBean("mockJob");
        reset(jobStore);
        metadataStore = findBean("mockMetadata");
        reset(metadataStore);
    }

    @Deployment(resources = {"bpmn/clear_adjustments.bpmn"})
    @Test
    public void shouldRunProcess() {        
        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setBusinessDay(new Date());
        businessCalendar.setInEndOfDay(true);
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);        
        
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, BUSINESS_KEY);

        runtimeService.correlateMessage("lombard.service.adjustment.search.response", BUSINESS_KEY);
        runtimeService.correlateMessage("lombard.service.adjustment.update.response", BUSINESS_KEY);
        
        verify(camelService).sendTo("direct:lombard-service-adjustment-search-request");
        verify(camelService).sendTo("direct:lombard-service-adjustment-update-request");
    }

}
