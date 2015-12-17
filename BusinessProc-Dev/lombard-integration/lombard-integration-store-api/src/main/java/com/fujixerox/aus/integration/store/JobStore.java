package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.metadata.AssetRetention;

import java.util.List;

/**
 * Store and Retrieve a job
 * Created by warwick on 2/03/2015.
 */
public interface JobStore
{
    public void addRequest(Job job, Object request, String subject, String predicate, String activityIdentifier);
    public void addRequestOnce(Job job, Object request, String subject, String predicate, String activityIdentifier);
    
    /**
     * Adds the response object to the last activity in the job
     * @param job
     * @param response
     */
    public void addResponse(Job job, Object response, String subject, String predicate);

    public void storeJob(Job job);

    public Job findJob(String jobIdentifier);

    public int deleteJob(AssetRetention assetRetention);

    public List<String> getJobListForRemoval(AssetRetention assetRetention);
}
