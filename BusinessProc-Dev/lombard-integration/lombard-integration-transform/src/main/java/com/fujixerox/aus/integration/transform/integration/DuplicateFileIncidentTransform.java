package com.fujixerox.aus.integration.transform.integration;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.integration.transform.outclearings.AbstractOutclearingsTransform;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;

/**
 * When a duplicate file is detected the details will be the received file.
 */
public class DuplicateFileIncidentTransform extends AbstractOutclearingsTransform implements Transformer<ReceivedFile> {
    @Override
    public ReceivedFile transform(Job job) {

        Activity activity = job.getActivities().get(0);
        return (ReceivedFile) activity.getRequest();
    }
}
