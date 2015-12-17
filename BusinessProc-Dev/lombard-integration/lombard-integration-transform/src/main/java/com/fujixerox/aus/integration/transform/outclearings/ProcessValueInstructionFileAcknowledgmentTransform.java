package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.integration.store.Transformer;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 6/05/15
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessValueInstructionFileAcknowledgmentTransform implements Transformer<ProcessValueInstructionFileAcknowledgmentRequest> {

    @Override
    public ProcessValueInstructionFileAcknowledgmentRequest transform(Job job) {
        String jobIdentifier;

        if (job.getInitiatingJobIdentifier() == null || job.getInitiatingJobIdentifier().isEmpty())
        {
            jobIdentifier = job.getJobIdentifier();
        } else {
            jobIdentifier = job.getInitiatingJobIdentifier()+"/"+job.getJobIdentifier();
        }

        ProcessValueInstructionFileAcknowledgmentRequest request = new ProcessValueInstructionFileAcknowledgmentRequest();
        request.setJobIdentifier(jobIdentifier);

        return request;
    }
}
