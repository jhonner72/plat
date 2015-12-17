package com.fujixerox.aus.integration.job;

import com.fujixerox.aus.lombard.common.job.Job;

import java.util.Map;

/**
 * Created by warwick on 3/03/2015.
 */
public class SimpleJobToProcessMapper implements JobToProcessMapper
{
    private Map<String, String> map;


    @Override
    public String mapJobToProcess(Job job)
    {
        return map.get(job.getPredicate() + ":" + job.getSubject());
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
