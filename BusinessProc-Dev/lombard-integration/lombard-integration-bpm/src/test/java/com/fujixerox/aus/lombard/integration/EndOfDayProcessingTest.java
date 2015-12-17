package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.integration.store.SequenceNumberGenerator;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import com.fujixerox.aus.lombard.outclearings.endofday.CalendarUtilsTest;
import com.fujixerox.aus.lombard.outclearings.endofday.EndOfDayProcessingJobAdapter;
import com.fujixerox.aus.lombard.reporting.metadata.ReportFrequencyEnum;
import com.fujixerox.aus.lombard.reporting.metadata.ReportRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by warwick on 1/06/2015.
 */
public class EndOfDayProcessingTest extends AbstractBpmnTest {
    private static final int NEXT_SEQUENCE_NUMBER = 100001;

    private static final String BUSINESS_KEY = "ie_5678";
    private static final String BUSINESS_DAY = "2015/06/01";

    JobStore jobStore;
    SequenceNumberGenerator sequenceNumberGenerator;
    MetadataStore metadataStore;

    private static List<String> calledProcesses = new ArrayList<>();
    private static List<String> expectedProcesses = new ArrayList<>();
    private static List<String> pastBusinessKeys = new ArrayList<>();

    @Autowired
    EndOfDayProcessingJobAdapter endOfDayProcessingJobAdapter;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
        sequenceNumberGenerator = findBean("mockGenerator");
        reset(sequenceNumberGenerator);
        metadataStore = findBean("mockMetadata");
        reset(metadataStore);

        calledProcesses.clear();
        expectedProcesses.clear();
        pastBusinessKeys.clear();
    }

    @Deployment(resources =
            {
                    "bpmn/end_of_day_initial.bpmn",
                    "bpmn/end_of_day_final.bpmn"
            })
    
    //@Test
    public void shouldProcessEndOfDay_whenOneVifAndNotEom() throws ParseException {
        executeProcess(1, false);
    }

    @Deployment(resources =
            {
                    "bpmn/end_of_day_initial.bpmn",
                    "bpmn/end_of_day_final.bpmn"
            })
    
   //@Test
    public void shouldProcessEndOfDay_whenOneVifAndEom() throws ParseException {
        executeProcess(1, true);
    }

    @Deployment(resources =
            {
                    "bpmn/end_of_day_initial.bpmn",
                    "bpmn/end_of_day_final.bpmn"
            })
    
    //@Test
    public void shouldProcessEndOfDay_whenThreeVifAndEom() throws ParseException {
        executeProcess(3, true);
    }

    protected void executeProcess(int vifCount, boolean eom) throws ParseException {
        EndOfDayProcessingTest.vifCount = vifCount;

        BusinessCalendar businessCalendar = new BusinessCalendar();
        businessCalendar.setIsEndOfMonth(eom);
        businessCalendar.setBusinessDay(CalendarUtilsTest.buildDate(BUSINESS_DAY));
        when(metadataStore.getMetadata(BusinessCalendar.class)).thenReturn(businessCalendar);

        mockSubprocess("EAID_0399963C_7C0C_4291_A57A_82B7D67DD027", "Clear Adjustments", "clear_adjustments");
        mockSubprocess("EAID_C9699240_ADBC_4549_998F_824F1811B8FC", "Close Surplus And Suspense Pools", "close_pools");
        for (int i = 0; i < vifCount; i++) {
            mockSubprocess("EAID_F38CFEDF_958F_ZZZZ_BB83_B34D7336C257", "VIF Production", "value_instruction_file_states");
        }
        mockSubprocess("EAID_CA99D859_42FD_4048_B795_D88B3EDE6254", "Image Exchange", "image_exchange_tier_one_banks");
        mockSubprocess("EAID_60F251F2_DFCE_44f5_B578_A22CF3982900", "Generate Agency File", "image_exchange_agency_banks");
        mockSubprocess("EAID_7388FA74_40C0_406a_820A_FB3B53591A99", "Generate EOD reports", "reporting");
        if (eom) {
            mockSubprocess("EAID_7388FA74_40C0_406a_820A_FB3B53591A99", "Generate EOM reports", "reporting");
        }
        mockSubprocess("EAID_C0F4FFC7_B700_406b_8434_C2C4049F0866", "Day2 Workflow", "day2_workflow");
        mockSubprocess("EAID_9A788295_AB54_4101_A12C_45B43C26F990", "Progress Pending Vouchers", "progress_pending_vouchers");
        mockSubprocess("EAID_EB43A3C1_69BC_409d_B289_3EB69E9B5B46", "Asset Management", "asset_management");

        try {
            runtimeService.startProcessInstanceByKey("EAID_854DF9BB_1D46_4b17_A998_5FB8DC060A3C", BUSINESS_KEY);
        }
        catch (Exception e)
        {
            // For the moment we expect an error at the end of the processing when cammunda writes to the history table.
        }

        assertThat(calledProcesses.size(), is(expectedProcesses.size()));
        for (int i = 0; i < calledProcesses.size(); i++)
        {
            assertThat(calledProcesses.get(i).startsWith(expectedProcesses.get(i)), is(true));
        }

        ArgumentCaptor<BusinessCalendar> argumentCaptor = ArgumentCaptor.forClass(BusinessCalendar.class);
        verify(metadataStore, times(2)).storeMetadata(argumentCaptor.capture());
        List<BusinessCalendar> allValues = argumentCaptor.getAllValues();

        // Check the second value has the values that we expect at the end of the process.
        BusinessCalendar businessCalendarActivityTwo = allValues.get(1);
        assertThat(businessCalendarActivityTwo.isInEndOfDay(), is(false));
        assertThat(businessCalendarActivityTwo.getBusinessDay(), is(CalendarUtilsTest.buildDate("2015/06/02")));

        assertThat(pastBusinessKeys.size(), is(eom ? 2 : 1));
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore, times(eom ? 2 : 1)).storeJob(jobCaptor.capture());

        Job jobEod = jobCaptor.getAllValues().get(0);
        assertThat(jobEod.getJobIdentifier(), is(pastBusinessKeys.get(0)));
        assertThat(jobEod.getInitiatingJobIdentifier(), is(BUSINESS_KEY));
        assertThat(((ReportRequest)jobEod.getActivities().get(0).getRequest()).getFrequency(), is(ReportFrequencyEnum.END_OF_DAY));
        assertThat(((ReportRequest)jobEod.getActivities().get(0).getRequest()).getBusinessDay(), is(CalendarUtilsTest.buildDate(BUSINESS_DAY)));

        if (eom)
        {
            Job jobEom = jobCaptor.getAllValues().get(1);
            assertThat(jobEom.getJobIdentifier(), is(pastBusinessKeys.get(1)));
            assertThat(jobEom.getInitiatingJobIdentifier(), is(BUSINESS_KEY));
            assertThat(((ReportRequest)jobEom.getActivities().get(0).getRequest()).getFrequency(), is(ReportFrequencyEnum.END_OF_MONTH));
            assertThat(((ReportRequest)jobEom.getActivities().get(0).getRequest()).getBusinessDay(), is(CalendarUtilsTest.buildDate(BUSINESS_DAY)));
        }
    }


    static int vifCount = 0;
    public static class MockDelegate implements JavaDelegate {
        @Override
        public void execute(DelegateExecution execution) throws Exception {
            calledProcesses.add(execution.getProcessDefinitionId());
            if (execution.getProcessDefinitionId().startsWith("EAID_F38CFEDF_958F_ZZZZ_BB83_B34D7336C257")) {
                execution.setVariable("vouchersRemaining", --vifCount != 0);
            }
            if (execution.getProcessDefinitionId().startsWith("EAID_7388FA74_40C0_406a_820A_FB3B53591A99") ||
                    execution.getProcessDefinitionId().startsWith("EAID_48F9E695_9818_44a9_8074_5C01CCADAD1B")    ) {
                assertThat(execution.getBusinessKey(), is(not(BUSINESS_KEY)));
                pastBusinessKeys.add(execution.getBusinessKey());
            } else {
                assertThat(execution.getBusinessKey(), is(BUSINESS_KEY));
            }
        }

    }
    private void mockSubprocess(String origProcessKey, String mockProcessName, String fileName) {
        expectedProcesses.add(origProcessKey);
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
