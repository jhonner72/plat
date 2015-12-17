package com.fujixerox.aus.lombard.integration;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.outclearings.endofday.CalendarUtilsTest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Before;
import org.junit.Test;

import com.fujixerox.aus.lombard.integration.delegate.Delegate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.reset;


public class LockedBoxValueProcessingTest extends AbstractBpmnTest {

    private static final String BUSINESS_KEY = "NLBC-1234-12345678";
	private final static String PROCESS_DEFINTION = "EAID_7F5BE6C2_D393_4e0e_A3E0_90256A75982B";
	private static final String VIF_PRODUCTION_PROCESS_ID = "EAID_F38CFEDF_958F_ZZZZ_BB83_B34D7336C257";
	private static final String GENERATE_REPORTS_PROCESS_ID = "EAID_7388FA74_40C0_406a_820A_FB3B53591A99";
	private final static String PROPERTY_VOUCHERS_FOUND  = "vouchersFound";
    private static final String BUSINESS_DAY = "2015/06/01";

    JobStore jobStore;
    MetadataStore metadataStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
        metadataStore = findBean("mockMetadata");
        reset(metadataStore);
    }

    // We need to invoke VIF production even if no vouchers are found
	@Deployment(resources = {"bpmn/locked_box_value_processing.bpmn"})
	@Test
	public void shouldInvokeVIFAndGenerateReportWhenNoVouchersFound() throws ParseException {
		final String businessKey = UUID.randomUUID().toString();
		boolean vouchersFound = false;
		int vouchersBatchCount = 0;

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setIsEndOfMonth(false);
        businessCalendar.setBusinessDay(CalendarUtilsTest.buildDate(BUSINESS_DAY));
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        // Prepare Mocks
		mockVIFProductionCall(businessKey, vouchersBatchCount);

        mockSubprocess(GENERATE_REPORTS_PROCESS_ID, "Reporting", "reporting");
		//mockGenerateReportsCall(BUSINESS_KEY);

		// Run Process
		this.runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, businessKey);
		
		
		// Set reply for Get Vouchers
		Map<String, Object> getVoucherResponseData = new HashMap<String, Object>();
		getVoucherResponseData.put(PROPERTY_VOUCHERS_FOUND, vouchersFound);
		runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforlockedbox.response", businessKey, getVoucherResponseData);
		

		
		// Verify
		verify(camelService).sendTo("direct:lombard-service-outclearings-getvouchersforlockedbox-request");
		verify(camelService, never()).sendTo("direct:lombard-service-outclearings-generatebulkcreditforlockedbox-request");
		verify(camelService, never()).sendTo("direct:lombard-service-outclearings-associategeneratedvoucherslbc-request");
	}

	@Deployment(resources = {"bpmn/locked_box_value_processing.bpmn"})
	@Test
	public void shouldRunWhenVouchersFoundProcessMultipleVIFsAndGenerateReport() throws ParseException {
		final String businessKey = UUID.randomUUID().toString();
		boolean vouchersFound = true;
		int vouchersBatchCount = 1;

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setIsEndOfMonth(false);
        businessCalendar.setBusinessDay(CalendarUtilsTest.buildDate(BUSINESS_DAY));
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        // Prepare Mocks
		mockVIFProductionCall(businessKey, vouchersBatchCount);

        mockSubprocess(GENERATE_REPORTS_PROCESS_ID, "Reporting", "reporting");
		//mockGenerateReportsCall(businessKey);
		
		// Run Process
		this.runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, businessKey);
		
		
		Map<String, Object> getVoucherResponseData = new HashMap<String, Object>();
		getVoucherResponseData.put(PROPERTY_VOUCHERS_FOUND, vouchersFound);
		runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforlockedbox.response", businessKey, getVoucherResponseData);
		
		runtimeService.correlateMessage("lombard.service.outclearings.copyvoucherslbc.response", businessKey);
		runtimeService.correlateMessage("lombard.service.outclearings.generatebulkcreditforlockedbox.response", businessKey);
		runtimeService.correlateMessage("lombard.service.outclearings.associategeneratedvoucherslbc.response", businessKey);
		
		// Verify
		verify(camelService).sendTo("direct:lombard-service-outclearings-getvouchersforlockedbox-request");
		verify(camelService).sendTo("direct:lombard-service-outclearings-copyvoucherslbc-request");
		verify(camelService).sendTo("direct:lombard-service-outclearings-generatebulkcreditforlockedbox-request");
		verify(camelService).sendTo("direct:lombard-service-outclearings-associategeneratedvoucherslbc-request");
	}
	
	
	public static class LockedBoxProcessingTestDelegate extends Delegate.MatchingParentBusinessKeyDelegate{
		
		private Expression vouchersBatchCount;
		private static int currentBatchCount = 0;

		@Override
		public void execute(DelegateExecution execution) throws Exception {

			super.execute(execution);
			
			int batchCount = Integer.valueOf(this.vouchersBatchCount.getValue(execution).toString());
			
			if(batchCount > currentBatchCount ){
				currentBatchCount++;
				execution.setVariable("vouchersRemaining", true);
				return;
			}
			
			execution.setVariable("vouchersRemaining", false);
		}

		public void setVouchersBatchCount(Expression vouchersBatchCount) {
			this.vouchersBatchCount = vouchersBatchCount;
		}
	}

	private void mockGenerateReportsCall(final String businessKey) {

		Map<String, Object> genReportsFields = new HashMap<>();
		genReportsFields.put(AbstractBpmnTest.PARENT_PROCESS_ID_KEY, businessKey);
		mockSubprocessCall(GENERATE_REPORTS_PROCESS_ID, genReportsFields, Delegate.MatchingParentBusinessKeyDelegate.class.getName());

	}

	private void mockVIFProductionCall(final String businessKey,
			int vouchersBatchCount) {
		Map<String, Object> vifProductionFields = new HashMap<>();
		vifProductionFields.put(AbstractBpmnTest.PARENT_PROCESS_ID_KEY, businessKey);
		vifProductionFields.put("vouchersBatchCount", vouchersBatchCount);
		
		mockSubprocessCall(VIF_PRODUCTION_PROCESS_ID, vifProductionFields , LockedBoxProcessingTestDelegate.class.getName());
	}

    public static class MockDelegate implements JavaDelegate {
        @Override
        public void execute(DelegateExecution execution) throws Exception {
            if (execution.getProcessDefinitionId().startsWith(GENERATE_REPORTS_PROCESS_ID)  ) {
                assertThat(execution.getBusinessKey(), is(not(BUSINESS_KEY)));
            } else {
                assertThat(execution.getBusinessKey(), is(BUSINESS_KEY));
            }
        }

    }
    private void mockSubprocess(String origProcessKey, String mockProcessName, String fileName) {
        mock(JavaDelegate.class);
        BpmnModelInstance modelInstance = Bpmn
                .createExecutableProcess(origProcessKey).name(mockProcessName)
                .startEvent().name("Start Point").serviceTask()
                .name("Log Something for Test")
                .camundaClass(MockDelegate.class.getName())
                .endEvent().name("End Point")
                .done();
        repositoryService.createDeployment().addModelInstance(fileName + ".bpmn", modelInstance).deploy();
    }
}
