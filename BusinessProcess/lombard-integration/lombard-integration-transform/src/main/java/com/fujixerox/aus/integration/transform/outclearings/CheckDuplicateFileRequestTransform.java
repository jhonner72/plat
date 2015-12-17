package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.outclearings.checkduplicatefile.CheckDuplicateFileRequest;

/**
 * Check duplicate file request transform
 * 
 * @author Alex.Park
 * @since 2/11/2015
 */
public class CheckDuplicateFileRequestTransform  extends AbstractOutclearingsTransform implements Transformer<CheckDuplicateFileRequest> {
    @Override
    public CheckDuplicateFileRequest transform(Job job) {

        Activity activity = job.getActivities().get(0);
        ReceivedFile receivedFile = (ReceivedFile) activity.getRequest();
        String fileName = receivedFile.getFileIdentifier();
        CheckDuplicateFileRequest checkDuplicateFileRequest = new CheckDuplicateFileRequest();
        checkDuplicateFileRequest.setFilename(fileName);

        return checkDuplicateFileRequest;
    }
}
