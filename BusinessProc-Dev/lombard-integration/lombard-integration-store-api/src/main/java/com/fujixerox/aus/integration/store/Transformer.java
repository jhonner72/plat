package com.fujixerox.aus.integration.store;

import com.fujixerox.aus.lombard.common.job.Job;

/**
 * Created by warwick on 11/03/2015.
 */
public interface Transformer<T> {
    public T transform(Job job);
}
