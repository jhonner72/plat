package com.fujixerox.aus.lombard.integration;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 9/04/15
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageExchangeProcessingTest extends AbstractBpmnTest {
    JobStore jobStore;
    MetadataStore metadataStore;

    private static final int TIER_ONE_BANK_COUNT = 3;
    private static final int AGENCY_BANK_COUNT = 5 + 1;

    @Before
    public void setup()
    {
        jobStore = findBean("mockJob");
        reset(jobStore);
        metadataStore = findBean("mockMetadata");
        reset(metadataStore);
    }

    @Deployment(resources = {"bpmn/image_exchange_file.bpmn"})
    @Test
    public void shouldExecuteImageExchange()
    {
        runtimeService.startProcessInstanceByKey("EAID_860026AF_1EB0_4c90_9C56_B0F82AC54ECE", "ie_5678");
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("vouchersRemaining", true);

        orchestrateImageExchange(runtimeService, "ie_5678", processVariables);
        verifyCamelInteractions(1, processVariables);
    }

    @Deployment(resources =
            {
                    "bpmn/image_exchange_tier_one_banks.bpmn",
                    "bpmn/image_exchange_file.bpmn"
            })
    @Test
    public void shouldExecuteImageExchange_whenTierOneProcess()
    {
        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.setMaxQuerySize(10);
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANX");
        when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);

        runtimeService.startProcessInstanceByKey("EAID_CA99D859_42FD_4048_B795_D88B3EDE6254", "" + System.currentTimeMillis());

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore, times(TIER_ONE_BANK_COUNT)).storeJob(jobArgumentCaptor.capture());
        List<Job> allJobs = jobArgumentCaptor.getAllValues();

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("vouchersRemaining", false);

        for (Job job : allJobs) {
            orchestrateImageExchange(runtimeService, job.getJobIdentifier(), processVariables);
        }
        verifyCamelInteractions(allJobs.size(), processVariables);
    }

    @Deployment(resources =
            {
                    "bpmn/image_exchange_agency_banks.bpmn",
                    "bpmn/image_exchange_file.bpmn"
            })
    @Test
    public void shouldExecuteImageExchange_withAgencyBanks()
    {
        AgencyBankDetails agencyBankDetails1 = new AgencyBankDetails();
        agencyBankDetails1.setTargetEndpoint("MQG");

        AgencyBankDetails agencyBankDetails2 = new AgencyBankDetails();
        agencyBankDetails2.setTargetEndpoint("BQL");

        AgencyBankDetails agencyBankDetails3 = new AgencyBankDetails();
        agencyBankDetails3.setTargetEndpoint("JPM");

        AgencyBankDetails agencyBankDetails4 = new AgencyBankDetails();
        agencyBankDetails4.setTargetEndpoint("CIT");

        AgencyBankDetails agencyBankDetails5 = new AgencyBankDetails();
        agencyBankDetails5.setTargetEndpoint("ARB");

        AgencyBankDetails agencyBankDetails6 = new AgencyBankDetails();
        agencyBankDetails6.setTargetEndpoint("CUS");

        AgencyBanksImageExchange agencyBanksImageExchange = new AgencyBanksImageExchange();
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails1);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails2);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails3);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails4);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails5);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails6);
        when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanksImageExchange);

        runtimeService.startProcessInstanceByKey("EAID_60F251F2_DFCE_44f5_B578_A22CF3982900", "" + System.currentTimeMillis());

        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore, times(AGENCY_BANK_COUNT)).storeJob(jobArgumentCaptor.capture());
        List<Job> allJobs = jobArgumentCaptor.getAllValues();

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("vouchersRemaining", true);

        for (Job job : allJobs) {
            orchestrateImageExchange(runtimeService, job.getJobIdentifier(), processVariables);
        }
        verifyCamelInteractions(allJobs.size(), processVariables);
    }

    private void verifyCamelInteractions(int count, Map<String, Object> processVariables)
    {
        if (processVariables.get("vouchersRemaining").equals(false)) {
            verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-getvouchersforimageexchange-request");
        } else {
            verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-getvouchersforimageexchange-request");
            verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-createimageexchangefile-request");
            verify(camelService, times(count)).sendTo("direct:lombard-service-copyfile-request");
            verify(camelService, times(count)).sendTo("direct:lombard-service-outclearings-updateimageexchangevouchersstatus-request");
        }
    }

    private void orchestrateImageExchange(RuntimeService runtimeService, String businessKey, Map<String, Object> processVariables) {
        if (processVariables.get("vouchersRemaining").equals(false)) {
            runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforimageexchange.response", businessKey, processVariables);
        } else {
            runtimeService.correlateMessage("lombard.service.outclearings.getvouchersforimageexchange.response", businessKey, processVariables);
            runtimeService.correlateMessage("lombard.service.outclearings.createimageexchangefile.response", businessKey);
            runtimeService.correlateMessage("lombard.service.outclearings.updateimageexchangevouchersstatus.response", businessKey);
        }
    }

}
