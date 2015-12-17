package com.fujixerox.aus.integration.transform;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

/**
 * Created by warwick on 15/04/2015.
 */
public class AbstractTransform {
    protected Activity retrieveActivity(Job job, String subject, String predicate)
    {
        for (Activity activity : job.getActivities())
        {
            if (predicate.equals(activity.getPredicate()) && subject.equals(activity.getSubject()))
            {
                return activity;
            } 
        }
        return null;
    }

    protected <T> T retrieveActivityResponse(Job job, String subject, String predicate)
    {
        Activity activity = retrieveActivity(job, subject, predicate);
        if (activity == null)
        {
            return null;
        }
        return (T)activity.getResponse();
    }

    protected <T> T retrieveActivityRequest(Job job, String subject, String predicate)
    {
        Activity activity = retrieveActivity(job, subject, predicate);
        if (activity == null)
        {
            return null;
        }
        return (T)activity.getRequest();
    }
}
