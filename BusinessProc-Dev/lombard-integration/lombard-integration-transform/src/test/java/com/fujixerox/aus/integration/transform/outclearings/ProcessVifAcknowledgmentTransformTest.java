package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.processvalueinstructionfileacknowledgment.ProcessValueInstructionFileAcknowledgmentRequest;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 20/05/15
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessVifAcknowledgmentTransformTest {
    protected static final String JOB_IDENTIFIER = "aaa-bbb-ccc";

    @Test
    public void shouldProcessValueInstructionFileAcknowledgement() throws ParseException{
        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        ProcessValueInstructionFileAcknowledgmentTransform target = new ProcessValueInstructionFileAcknowledgmentTransform();
        ProcessValueInstructionFileAcknowledgmentRequest request = target.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
    }
}
