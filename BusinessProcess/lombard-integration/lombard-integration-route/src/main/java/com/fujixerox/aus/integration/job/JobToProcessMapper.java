package com.fujixerox.aus.integration.job;

import com.fujixerox.aus.lombard.common.job.Job;

/**
 * Created by warwick on 2/03/2015.
 */
public interface JobToProcessMapper {
    public String mapJobToProcess(Job job);
}
