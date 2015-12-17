package com.fujixerox.aus.integration.transform;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    protected List<Activity> retrieveAllActivities(Job job, String subject, String predicate)
    {
        List<Activity> activities = new ArrayList<>();

        for (Activity activity : job.getActivities())
        {
            if (predicate.equals(activity.getPredicate()) && subject.equals(activity.getSubject()))
            {
                activities.add(activity);
            }
        }
        return activities;
    }

    protected Job deleteActivity(Job job, String subject, String predicate)
    {
        Iterator<Activity> iterator = job.getActivities().iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (predicate.equals(activity.getPredicate()) && subject.equals(activity.getSubject())) {
                iterator.remove();
            }
        }
        return job;
    }
}
