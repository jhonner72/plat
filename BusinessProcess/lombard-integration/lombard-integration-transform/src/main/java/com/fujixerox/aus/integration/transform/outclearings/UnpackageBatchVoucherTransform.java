package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherRequest;

/**
 * Created by warwick on 11/03/2015.
 */
public class UnpackageBatchVoucherTransform extends AbstractOutclearingsTransform implements Transformer <UnpackageBatchVoucherRequest>{
    @Override
    public UnpackageBatchVoucherRequest transform(Job job) {

        ReceivedFile receivedFile = (ReceivedFile) job.getActivities().get(0).getRequest();
        UnpackageBatchVoucherRequest request = new UnpackageBatchVoucherRequest();
        request.setReceivedFile(receivedFile);
        request.setJobIdentifier(job.getJobIdentifier());

        return request;
    }
}
