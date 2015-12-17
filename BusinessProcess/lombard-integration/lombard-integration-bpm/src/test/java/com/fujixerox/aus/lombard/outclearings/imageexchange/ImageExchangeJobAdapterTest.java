package com.fujixerox.aus.lombard.outclearings.imageexchange;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.integration.store.MetadataStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AgencyBankDetails;
import com.fujixerox.aus.lombard.common.metadata.AgencyBanksImageExchange;
import com.fujixerox.aus.lombard.common.metadata.TierOneBanksImageExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

/**
 * Created by warwick on 7/05/2015.
 */
public class ImageExchangeJobAdapterTest {
    JobStore jobStore;
    MetadataStore metadataStore;
    ImageExchangeJobAdapter target;

    private static final String ENDPOINT = "WPC";
    private static final Integer QUERY_SIZE = 100;
    private static final String PARENT_JOBID = "11-22-33";

    @Before
    public void setup()
    {
        jobStore = mock(JobStore.class);
        metadataStore = mock(MetadataStore.class);

        target = new ImageExchangeJobAdapter();
        target.setJobStore(jobStore);
        target.setMetadataStore(metadataStore);
    }

    @Test
    public void testCreateJob_whenImageExchangeForTierOneBanks() throws Exception {
        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.setMaxQuerySize(QUERY_SIZE);
        when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);

        String jobIdentifier = target.createLimitedJob(ENDPOINT, PARENT_JOBID);
        assertThat(jobIdentifier.startsWith("NIET-"), is(true));

        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore).storeJob(argumentCaptor.capture());

        Job job = argumentCaptor.getValue();
        assertThat(job.getJobIdentifier(), is(jobIdentifier));
        assertThat(job.getSubject(), is("outclearings"));
        assertThat(job.getPredicate(), is("imageexchange"));
        assertThat(job.getInitiatingJobIdentifier(), is(PARENT_JOBID));

        List<Activity> activities = job.getActivities();
        assertThat(activities.size(), is(1));

        Activity activity = activities.get(0);
        assertThat(activity.getRequestDateTime(), is(notNullValue()));
        assertThat(activity.getSubject(), is("outclearings"));
        assertThat(activity.getPredicate(), is("imageexchange"));
        assertThat(activity.getJobIdentifier(), is(jobIdentifier));
        assertThat(activity.getResponse(), is(nullValue()));

        ImageExchangeRequest request = (ImageExchangeRequest) activity.getRequest();
        assertThat(request.getMaxQuerySize(), is(QUERY_SIZE));
        assertThat(request.getTargetEndPoint(), is(ENDPOINT));
    }

    @Test
    public void testCreateJob_whenImageExchangeForAgencyBank() throws Exception {
        String jobIdentifier = target.createUnlimitedJob(ENDPOINT, PARENT_JOBID);
        assertThat(jobIdentifier.startsWith("NIEO-"), is(true));

        ArgumentCaptor<Job> argumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobStore).storeJob(argumentCaptor.capture());

        Job job = argumentCaptor.getValue();
        assertThat(job.getJobIdentifier(), is(jobIdentifier));
        assertThat(job.getSubject(), is("outclearings"));
        assertThat(job.getPredicate(), is("imageexchange"));
        assertThat(job.getInitiatingJobIdentifier(), is(PARENT_JOBID));

        List<Activity> activities = job.getActivities();
        assertThat(activities.size(), is(1));

        Activity activity = activities.get(0);
        assertThat(activity.getRequestDateTime(), is(notNullValue()));
        assertThat(activity.getSubject(), is("outclearings"));
        assertThat(activity.getPredicate(), is("imageexchange"));
        assertThat(activity.getJobIdentifier(), is(jobIdentifier));
        assertThat(activity.getResponse(), is(nullValue()));

        ImageExchangeRequest request = (ImageExchangeRequest) activity.getRequest();
        assertThat(request.getMaxQuerySize(), is(-1));
        assertThat(request.getTargetEndPoint(), is(ENDPOINT));
    }

    @Test
    public void shouldFetchEndpoints_whenAgencyBank()
    {
        AgencyBankDetails agencyBankDetails1 = new AgencyBankDetails();
        agencyBankDetails1.setTargetEndpoint("MQG");

        AgencyBankDetails agencyBankDetails2 = new AgencyBankDetails();
        agencyBankDetails2.setTargetEndpoint("BQL");

        AgencyBankDetails agencyBankDetails3 = new AgencyBankDetails();
        agencyBankDetails3.setTargetEndpoint("CUS");

        AgencyBanksImageExchange agencyBanksImageExchange = new AgencyBanksImageExchange();
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails1);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails2);
        agencyBanksImageExchange.getAgencyBanks().add(agencyBankDetails3);

        when(metadataStore.getMetadata(AgencyBanksImageExchange.class)).thenReturn(agencyBanksImageExchange);

        List<String> agencyBanksEndpoints = target.getAgencyBanksEndpoints();
        assertThat(agencyBanksEndpoints.size(), is(3));

        verify(metadataStore).getMetadata(AgencyBanksImageExchange.class);
    }

    @Test
    public void shouldFetchEndpoints_whenTierOneBank()
    {
        TierOneBanksImageExchange tierOneBanksImageExchange = new TierOneBanksImageExchange();
        tierOneBanksImageExchange.getTargetEndpoints().add("FIS");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANZ");
        tierOneBanksImageExchange.getTargetEndpoints().add("ANX");

        when(metadataStore.getMetadata(TierOneBanksImageExchange.class)).thenReturn(tierOneBanksImageExchange);

        List<String> tierOneBanksEndpoints = target.getTierOneBanksEndpoints();
        assertThat(tierOneBanksEndpoints.size(), is(3));

        verify(metadataStore).getMetadata(TierOneBanksImageExchange.class);

    }
}