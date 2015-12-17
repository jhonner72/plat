package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFile;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileTarget;
import com.fujixerox.aus.lombard.common.metadata.ValueInstructionFileWorkTypeGroup;
import com.fujixerox.aus.lombard.common.voucher.StateEnum;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.outclearings.valueinstructionfilecommon.ValueInstructionFileRequest;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

/**
 * Orchestrate the Value Instruction File business processes.
 * User: Warwick Slade
 */
public class ValueInstructionFileProcessingTest extends AbstractBpmnTest{

    private static final int NEXT_SEQUENCE_NUMBER = 100001;
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

    @Deployment(resources = {"bpmn/value_instruction_file.bpmn"})
    @Test
    public void shouldProcessValueInstructionFile_forOneEndpoint() throws InterruptedException {
        when(camelService.sendTo("direct:lombard-service-outclearings-retrievevalueinstructionfileack-request")).thenReturn(true);

        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("EAID_F38CFEDF_958F_40cc_BB83_B34D7336C257", "vif_1234");
        
        ValueInstructionFile valueInstructionFile = new ValueInstructionFile();
        valueInstructionFile.setAckFileWaitPeriod("PT1M");
        valueInstructionFile.setSlaPeriod("PT10M");
        when(metadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(valueInstructionFile);

        orchestrateValueInstructionFile(processInstance, "vif_1234", false);
        verifyCamelService(1);
    }


    @Deployment(resources =
            {
                    "bpmn/value_instruction_file_states.bpmn",
                    "bpmn/value_instruction_file.bpmn"
            })
    @Test
    public void shouldProcessInstructionFiles_forAllEndpoints() throws InterruptedException {

        when(sequenceNumberGenerator.nextSequenceNumber(ValueInstructionFile.class)).thenReturn(NEXT_SEQUENCE_NUMBER);
        when(camelService.sendTo("direct:lombard-service-outclearings-retrievevalueinstructionfileack-request")).thenReturn(true);

        ValueInstructionFile valueInstructionFile = new ValueInstructionFile();
        valueInstructionFile.setMaxQuerySize(8000);
        valueInstructionFile.setAckFileWaitPeriod("PT1M");
        valueInstructionFile.setSlaPeriod("PT10M");
        ValueInstructionFileTarget target1 = new ValueInstructionFileTarget();
        target1.setState(StateEnum.VIC);
        target1.setCaptureBsb("085384");
        target1.setFinancialInstitution("NAB");
        ValueInstructionFileWorkTypeGroup workTypeGroup = new ValueInstructionFileWorkTypeGroup();
        workTypeGroup.getTargetDetails().add(target1);
		valueInstructionFile.getTargets().add(workTypeGroup);
        ValueInstructionFileTarget target2 = new ValueInstructionFileTarget();
        target2.setState(StateEnum.QLD);
        target2.setCaptureBsb("082037");
        target2.setFinancialInstitution("BQL");
        ValueInstructionFileWorkTypeGroup workTypeGroup2 = new ValueInstructionFileWorkTypeGroup();
        workTypeGroup2.getTargetDetails().add(target2);
		valueInstructionFile.getTargets().add(workTypeGroup2);        
        when(metadataStore.getMetadata(ValueInstructionFile.class)).thenReturn(valueInstructionFile);

        RuntimeService runtimeService = processEngineRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("EAID_F38CFEDF_958F_ZZZZ_BB83_B34D7336C257");

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore, times(2)).storeJob(jobArgumentCaptor.capture());
        List<Job> allJobs = jobArgumentCaptor.getAllValues();

        String[] expectedIdentifiers =
                {
                        "085384",
                        "082037"
                };

        List<ProcessInstance> childProcesses = processEngineRule.getRuntimeService().createProcessInstanceQuery().processDefinitionKey("EAID_F38CFEDF_958F_40cc_BB83_B34D7336C257").list();

        int i = 0;
        for (Job job : allJobs)
        {
            assertThat(job.getJobIdentifier().startsWith("NVIF-"), is(true));

            orchestrateValueInstructionFile(childProcesses.get(i), job.getJobIdentifier(), i == 1);
            ValueInstructionFileRequest request = (ValueInstructionFileRequest) job.getActivities().get(0).getRequest();
            assertThat(request.getMaxQuerySize(), is(8000));
            assertThat(request.getEndpoint(), is(expectedIdentifiers[i++]));
            assertThat(request.getTarget(), is(notNullValue()));
        }
        verifyCamelService(allJobs.size());

        List<HistoricVariableInstance> historicVariableInstances = processEngineRule.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(processInstance.getProcessInstanceId()).listPage(0, 1000);
        HistoricVariableInstance historicVariableInstance = historicVariableInstances.get(0);
        assertThat(historicVariableInstance.getName(), is("vouchersRemaining"));
        assertThat((boolean)historicVariableInstance.getValue(), is(true));
    }

    private void verifyCamelService(int count) {
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-getvouchersforvalueinstructionfile-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-createvalueinstructionfile-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-sendvalueinstructionfile-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-retrievevalueinstructionfileack-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-updatevalueinstructionfilevouchersstatus-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-processvalueinstructionfileack-request");
        verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-updatevalueinstructionfilevouchersackstatus-request");
    }

    protected void orchestrateValueInstructionFile(ProcessInstance processInstance, String identifier, boolean remaining) throws InterruptedException {
        Map<String, Object> getparameters = new HashMap<>();
        getparameters.put("vouchersRemaining", remaining);
        runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforvalueinstructionfile.response", identifier, getparameters);
        runtimeService.correlateMessage("lombard.service.outclearings.createvalueinstructionfile.response", identifier);
        
        runtimeService.correlateMessage("lombard.service.outclearings.updatevalueinstructionfilevouchersstatus.response", identifier);

        // Fire the timer
        TimerEntity timer = (TimerEntity) processEngineRule.getManagementService().createJobQuery().timers().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        assertThat(timer, is(notNullValue()));
        processEngineRule.getManagementService().executeJob(timer.getId());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("vouchersValid", true);
        runtimeService.correlateMessage("lombard.service.outclearings.processvalueinstructionfileack.response", identifier, parameters);
        runtimeService.correlateMessage("lombard.service.outclearings.updatevalueinstructionfilevouchersackstatus.response", identifier);
    }
}
