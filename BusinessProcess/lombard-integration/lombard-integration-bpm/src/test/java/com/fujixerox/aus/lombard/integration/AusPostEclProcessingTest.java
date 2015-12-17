package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.job.Parameter;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherBatch;
import com.fujixerox.aus.lombard.common.voucher.VoucherInformation;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.integration.delegate.Delegate;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherResponse;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.reset;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 28/09/15
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AusPostEclProcessingTest extends AbstractBpmnTest {
    private static final String BUSINESS_KEY = "NECL-1234_12345678_12345678";
    private final static String GENERATE_BULKCREDIT_PROCESS_ID = "EAID_4C762DCE_4D2C_40e4_ABE6_1F201E047327";
    private final static String VIF_PROCESS_ID = "EAID_F38CFEDF_958F_ZZZZ_BB83_B34D7336C257";
    private final static String PROCESS_INSTANCE_ID = "EAID_DP000000_EC49_4d92_8EFA_DAE7A5CF1153";
    private final static String PROCESS_VARIABLE_VOUCHERS_FOUND  = "vouchersRemaining";
    private final static String PROCESS_VARIABLE_MATCHED_VOUCHERS_FOUND  = "matchedVouchersFound";

    JobStore jobStore;
    SequenceNumberGenerator sequenceNumberGenerator;
    MetadataStore metadataStore;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
        sequenceNumberGenerator = findBean("mockGenerator");
        reset(sequenceNumberGenerator);
        metadataStore = findBean("mockMetadata");
        reset(metadataStore);
    }

    @Deployment(resources={"bpmn/aus_post_ecl_process.bpmn"} )
    @Test
    public void shouldExecuteAllActivities() throws InterruptedException {
        mockJob();
        mockMetadata();

        int vouchersBatchCount = 1;
        boolean vouchersFound = true;
        boolean matchedVouchersFound = true;

        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put(PROCESS_VARIABLE_VOUCHERS_FOUND, vouchersFound);
        processVariables.put(PROCESS_VARIABLE_MATCHED_VOUCHERS_FOUND, matchedVouchersFound);

        this.runtimeService.startProcessInstanceByKey(PROCESS_INSTANCE_ID, BUSINESS_KEY, processVariables);

        mockSubprocess(GENERATE_BULKCREDIT_PROCESS_ID, "Generate Bulk Credit", "generate_bulkcredit");
        mockVIFProductionCall(BUSINESS_KEY, VIF_PROCESS_ID ,vouchersBatchCount);

        orchestrateValueInstructionFile(processVariables);
        verifyCamelService(1, processVariables);
    }

    @Deployment(resources={"bpmn/aus_post_ecl_process.bpmn"} )
    @Test
    public void shouldSkipCopyVouchersToDIPsActivity() throws InterruptedException {
        mockJob();
        mockMetadata();

        int vouchersBatchCount = 0;
        boolean vouchersFound = false;
        boolean matchedVouchersFound = true;

        this.runtimeService.startProcessInstanceByKey(PROCESS_INSTANCE_ID, BUSINESS_KEY);

        mockSubprocess(GENERATE_BULKCREDIT_PROCESS_ID, "Generate Bulk Credit", "generate_bulkcredit");
        mockVIFProductionCall(BUSINESS_KEY, VIF_PROCESS_ID, vouchersBatchCount);

        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put(PROCESS_VARIABLE_VOUCHERS_FOUND, vouchersFound);
        processVariables.put(PROCESS_VARIABLE_MATCHED_VOUCHERS_FOUND, matchedVouchersFound);

        orchestrateValueInstructionFile(processVariables);
        verifyCamelService(1, processVariables);
    }

    @Deployment(resources={"bpmn/aus_post_ecl_process.bpmn"} )
    @Test
    public void shouldSkipGenerateBulkCreditActivity() throws InterruptedException {
        mockJob();
        mockMetadata();

        int vouchersBatchCount = 1;
        boolean vouchersFound = true;
        boolean matchedVouchersFound = false;

        this.runtimeService.startProcessInstanceByKey(PROCESS_INSTANCE_ID, BUSINESS_KEY);

        mockVIFProductionCall(BUSINESS_KEY, VIF_PROCESS_ID, vouchersBatchCount);

        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put(PROCESS_VARIABLE_VOUCHERS_FOUND, vouchersFound);
        processVariables.put(PROCESS_VARIABLE_MATCHED_VOUCHERS_FOUND, matchedVouchersFound);

        orchestrateValueInstructionFile(processVariables);
        verifyCamelService(1, processVariables);
    }

    public static class AusPostEclProcessingTestDelegate extends Delegate.MatchingParentBusinessKeyDelegate{

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

    private void mockVIFProductionCall(final String businessKey, String processId, int vouchersBatchCount) {
        Map<String, Object> vifProductionFields = new HashMap<>();
        vifProductionFields.put(AbstractBpmnTest.PARENT_PROCESS_ID_KEY, businessKey);
        vifProductionFields.put("vouchersBatchCount", vouchersBatchCount);

        mockSubprocessCall(processId, vifProductionFields, AusPostEclProcessingTestDelegate.class.getName());
    }

    private void verifyCamelService(int count, Map<String, Object> processVariables) {
        verify(camelService).sendTo("direct:lombard-service-outclearings-geteclvouchers-request");
        if ((Boolean)processVariables.get(PROCESS_VARIABLE_VOUCHERS_FOUND)) {
            verify(camelService).sendTo("direct:lombard-service-outclearings-copyvouchers-request");
        }
        verify(camelService).sendTo("direct:lombard-service-outclearings-matchvouchers-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-associategeneratedvouchersecl-request");
        verify(camelService).sendTo("direct:lombard-service-outclearings-sendeclresponse-request");

    }

    protected void orchestrateValueInstructionFile(Map<String, Object> processVariables) throws InterruptedException {
        runtimeService.correlateMessage("lombard.service.outclearings.geteclvouchers.response", BUSINESS_KEY, processVariables);

        if ((Boolean)processVariables.get(PROCESS_VARIABLE_VOUCHERS_FOUND)) {
            runtimeService.correlateMessage("lombard.service.outclearings.copyvouchers.response", BUSINESS_KEY);
        }

        runtimeService.correlateMessage("lombard.service.outclearings.matchvouchers.response", BUSINESS_KEY, processVariables);
        runtimeService.correlateMessage("lombard.service.outclearings.associategeneratedvouchersecl.response", BUSINESS_KEY);
    }

    private void mockJob() {
        mockJob(BUSINESS_KEY);
    }

    private void mockJob(String businessKey) {
        Job job = new Job();

        Parameter parameter = new Parameter();
        parameter.setName("workType");
        parameter.setValue("NABCHQ_APOST");
        job.getParameters().add(parameter);

        Activity activity = new Activity();

        MatchVoucherResponse matchVoucherResponse = new MatchVoucherResponse();
//        VoucherInformation voucherInformation = new VoucherInformation();
//        VoucherBatch voucherBatch = new VoucherBatch();
//        voucherBatch.setCaptureBsb("085384");
//        voucherInformation.setVoucherBatch(voucherBatch);
//        matchVoucherResponse.getMatchedVouchers().add(voucherInformation);

        VoucherInformation voucherInformation2 = new VoucherInformation();
        VoucherBatch voucherBatch2 = new VoucherBatch();
        voucherBatch2.setCaptureBsb("082037");
        voucherInformation2.setVoucherBatch(voucherBatch2);
        matchVoucherResponse.getMatchedVouchers().add(voucherInformation2);

        activity.setResponse(matchVoucherResponse);
        activity.setSubject("vouchers");
        activity.setPredicate("match");

        job.getActivities().add(activity);
        when(jobStore.findJob(businessKey)).thenReturn(job);

    }

    private void mockMetadata() {
        ValueInstructionFile valueInstructionFile = new ValueInstructionFile();
        valueInstructionFile.setMaxQuerySize(8000);
        valueInstructionFile.setAckFileWaitPeriod("PT1M");
        valueInstructionFile.setSlaPeriod("PT10M");

//        ValueInstructionFileTarget target1 = new ValueInstructionFileTarget();
//        target1.setState(StateEnum.VIC);
//        target1.setCaptureBsb("085384");
//        target1.setFinancialInstitution("NAB");
//
//        ValueInstructionFileWorkTypeGroup workTypeGroup = new ValueInstructionFileWorkTypeGroup();
//        workTypeGroup.getTargetDetails().add(target1);
//        workTypeGroup.setWorkType(WorkTypeEnum.NABCHQ_APOST);
//
//        valueInstructionFile.getTargets().add(workTypeGroup);

        ValueInstructionFileTarget target2 = new ValueInstructionFileTarget();
        target2.setState(StateEnum.QLD);
        target2.setCaptureBsb("082037");
        target2.setFinancialInstitution("BQL");

        ValueInstructionFileWorkTypeGroup workTypeGroup2 = new ValueInstructionFileWorkTypeGroup();
        workTypeGroup2.getTargetDetails().add(target2);
        workTypeGroup2.setWorkType(WorkTypeEnum.NABCHQ_APOST);

        valueInstructionFile.getTargets().add(workTypeGroup2);

        when(metadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(valueInstructionFile);
    }

    public static class MockDelegate implements JavaDelegate {
        @Override
        public void execute(DelegateExecution execution) throws Exception {
            if (execution.getProcessDefinitionId().startsWith("EAID_4C762DCE_4D2C_40e4_ABE6_1F201E047327")  ) {
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
