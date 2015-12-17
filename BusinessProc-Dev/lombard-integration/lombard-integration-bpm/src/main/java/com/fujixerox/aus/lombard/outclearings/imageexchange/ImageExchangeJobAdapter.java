package com.fujixerox.aus.lombard.outclearings.imageexchange;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.ImageExchange;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This class exists to simplify writing the Image Exchange Business processes.
 * Its primary purpose is to create a job for child sub-processes.
 * Created by warwick on 6/05/2015.
 */
public class ImageExchangeJobAdapter {
    
    private JobStore jobStore;
    private MetadataStore metadataStore;

    public List<String> getAgencyBanksEndpoints()
    {
        List<String> endpoints = new ArrayList<>();
        AgencyBanksImageExchange metadata = metadataStore.getMetadata(AgencyBanksImageExchange.class);
        for (AgencyBankDetails agencyBankDetails : metadata.getAgencyBanks()) {
            endpoints.add(agencyBankDetails.getTargetEndpoint());
        }
        return endpoints;
    }

    public List<String> getTierOneBanksEndpoints()
    {
        TierOneBanksImageExchange metadata = metadataStore.getMetadata(TierOneBanksImageExchange.class);
        return metadata.getTargetEndpoints();
    }

    public String createLimitedJob(String endpoint, String initiatingJobIdentifier)
    {
        TierOneBanksImageExchange metadata = metadataStore.getMetadata(TierOneBanksImageExchange.class);
        return createJob(endpoint, metadata.getMaxQuerySize(), "NIET-", initiatingJobIdentifier);
    }

    public String createUnlimitedJob(String endpoint, String initiatingJobIdentifier)
    {
        return createJob(endpoint, -1, "NIEO-", initiatingJobIdentifier);
    }

    private String createJob(String endpoint, int maxQuerySize, String prefix, String initiatingJobIdentifier) {
        String guid = prefix + UUID.randomUUID().toString();

        Job job = new Job();
        job.setJobIdentifier(guid);
        job.setPredicate("imageexchange");
        job.setSubject("outclearings");
        job.setInitiatingJobIdentifier(initiatingJobIdentifier);

        ImageExchangeRequest imageExchangeRequest = new ImageExchangeRequest();
        imageExchangeRequest.setTargetEndPoint(endpoint);
        imageExchangeRequest.setMaxQuerySize(maxQuerySize);

        Activity parameterActivity = new Activity();
        parameterActivity.setPredicate("imageexchange");
        parameterActivity.setSubject("outclearings");
        parameterActivity.setJobIdentifier(guid);
        parameterActivity.setRequest(imageExchangeRequest);
        parameterActivity.setRequestDateTime(new Date());

        job.getActivities().add(parameterActivity);

        jobStore.storeJob(job);

        return guid;
    }

    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

    public void setMetadataStore(MetadataStore metadataStore) {
        this.metadataStore = metadataStore;
    }
}
