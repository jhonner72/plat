package com.fujixerox.aus.lombard.integration;


import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Test;
import org.mockito.Mockito;

import com.fujixerox.aus.lombard.integration.delegate.Delegate;


public class CorporateLockedBoxProcessingTest extends AbstractBpmnTest {

	private final static String PROCESS_DEFINTION = "EAID_7F5BE6C2_D393_4e0e_A3E0_90256A75982B";
	private static final String VIF_PRODUCTION_PROCESS_ID = "EAID_F38CFEDF_958F_40cc_BB83_B34D7336C257";
	private static final String GENERATE_REPORTS_PROCESS_ID = "EAID_7388FA74_40C0_406a_820A_FB3B53591A99";
	private final static String PROPERTY_VOUCHERS_FOUND  = "vouchersFound";
	private final static String PROPERTY_IS_LOCKEDBOX_CUST  = "isLockedBoxCust";
	
	
	

	// We need to invoke VIF production even if no vouchers are found
	@Deployment(resources = {"bpmn/corporate_locked_box_value_processing.bpmn"})
	@Test
	public void shouldInvokeVIFAndGenerateReportWhenNoVouchersFound() {
		final String businessKey = UUID.randomUUID().toString();
		boolean vouchersFound = false;
		int vouchersBatchCount = 0; 
		boolean generateReport = true;
		
		// Prepare Mocks
		mockVIFProductionCall(businessKey, vouchersBatchCount);

		mockGenerateReportsCall(businessKey, generateReport);

		// Run Process
		this.runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, businessKey);
		
		
		// Set reply for Get Vouchers
		Map<String, Object> getVoucherResponseData = new HashMap<String, Object>();
		getVoucherResponseData.put(PROPERTY_VOUCHERS_FOUND, vouchersFound);
		getVoucherResponseData.put(PROPERTY_IS_LOCKEDBOX_CUST, generateReport);
		runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforcorporatelockedbox.response", businessKey, getVoucherResponseData);
		

		
		// Verify
		verify(camelService).sendTo("direc:lombard-service-outclearings-getvouchersforcorporatelockedbox-request");
		verify(camelService, never()).sendTo("direc:lombard-service-outclearings-generatebulkcredit-request");
		verify(camelService, never()).sendTo("direc:lombard-service-outclearings-associategeneratedvoucher-request");		
	}



	
	@Deployment(resources = {"bpmn/corporate_locked_box_value_processing.bpmn"})
	@Test
	public void shouldRunWhenVouchersFoundProcessMultipleVIFsAndGenerateReport() {
		final String businessKey = UUID.randomUUID().toString();
		boolean vouchersFound = true;
		boolean generateReport = true;
		int vouchersBatchCount = 1; 
		
		
		// Prepare Mocks
		mockVIFProductionCall(businessKey, vouchersBatchCount);
		
		mockGenerateReportsCall(businessKey, generateReport);
		
		// Run Process
		this.runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, businessKey);
		
		
		Map<String, Object> getVoucherResponseData = new HashMap<String, Object>();
		getVoucherResponseData.put(PROPERTY_VOUCHERS_FOUND, vouchersFound);
		getVoucherResponseData.put(PROPERTY_IS_LOCKEDBOX_CUST, generateReport);
		runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforcorporatelockedbox.response", businessKey, getVoucherResponseData);
		
		runtimeService.correlateMessage("lombard.service.outclearings.generatebulkcredit.response", businessKey);
		runtimeService.correlateMessage("lombard.service.outclearings.associategeneratedvoucher.response", businessKey);
		
		// Verify
		verify(camelService).sendTo("direc:lombard-service-outclearings-getvouchersforcorporatelockedbox-request");
		verify(camelService).sendTo("direc:lombard-service-outclearings-generatebulkcredit-request");
		verify(camelService).sendTo("direc:lombard-service-outclearings-associategeneratedvoucher-request");		
	}
	
	@Deployment(resources = {"bpmn/corporate_locked_box_value_processing.bpmn"})
	@Test
	public void shouldNotGenerateReportsWhenNoLockedBoxCustomer() {
		final String businessKey = UUID.randomUUID().toString();
		boolean vouchersFound = true;
		boolean generateReport = false;
		int vouchersBatchCount = 1; 

		
		// Prepare Mocks
		mockVIFProductionCall(businessKey, vouchersBatchCount);
		
		mockGenerateReportsCall(businessKey, generateReport);
			
		// Run Process
		this.runtimeService.startProcessInstanceByKey(PROCESS_DEFINTION, businessKey);
		

        Map<String, Object> getVoucherResponseData = new HashMap<String, Object>();
		getVoucherResponseData.put(PROPERTY_VOUCHERS_FOUND, vouchersFound);
		getVoucherResponseData.put(PROPERTY_IS_LOCKEDBOX_CUST, generateReport);
		runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforcorporatelockedbox.response", businessKey, getVoucherResponseData);

		runtimeService.correlateMessage("lombard.service.outclearings.generatebulkcredit.response", businessKey);
		runtimeService.correlateMessage("lombard.service.outclearings.associategeneratedvoucher.response", businessKey);

		// Verify
        verify(camelService).sendTo("direc:lombard-service-outclearings-getvouchersforcorporatelockedbox-request");
        verify(camelService).sendTo("direc:lombard-service-outclearings-generatebulkcredit-request");
        verify(camelService).sendTo("direc:lombard-service-outclearings-associategeneratedvoucher-request");		
	}
	
	
	public static class CorporateLockedBoxProcessingTestDelegate extends Delegate.MatchingParentBusinessKeyDelegate{
		
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

	private void mockGenerateReportsCall(final String businessKey, boolean generateReport) {
		
		if (generateReport) {
			Map<String, Object> genReportsFields = new HashMap<>();
			genReportsFields.put(AbstractBpmnTest.PARENT_PROCESS_ID_KEY, businessKey);
			mockSubprocessCall(GENERATE_REPORTS_PROCESS_ID, genReportsFields, Delegate.MatchingParentBusinessKeyDelegate.class.getName());
			return;
		}
		
		mockSubprocessCall(GENERATE_REPORTS_PROCESS_ID, Delegate.InvocationNotExpectedDelegate.class.getName());

	}

	private void mockVIFProductionCall(final String businessKey,
			int vouchersBatchCount) {
		Map<String, Object> vifProductionFields = new HashMap<>();
		vifProductionFields.put(AbstractBpmnTest.PARENT_PROCESS_ID_KEY, businessKey);
		vifProductionFields.put("vouchersBatchCount", vouchersBatchCount);
		
		mockSubprocessCall(VIF_PRODUCTION_PROCESS_ID, vifProductionFields , CorporateLockedBoxProcessingTestDelegate.class.getName());
	}

}
