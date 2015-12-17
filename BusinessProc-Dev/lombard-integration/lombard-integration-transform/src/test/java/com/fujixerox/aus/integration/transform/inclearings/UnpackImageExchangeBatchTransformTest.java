package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.inclearings.unpackimageexchangebatch.UnpackImageExchangeBatchRequest;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 23/04/15
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnpackImageExchangeBatchTransformTest {
    private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";

    @Test
    public void shouldTransformRequest() {
        UnpackImageExchangeBatchTransform unpackImageExchangeBatchTransform = new UnpackImageExchangeBatchTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        UnpackImageExchangeBatchRequest unpackImageExchangeBatchRequest = unpackImageExchangeBatchTransform.transform(job);

        assertThat(unpackImageExchangeBatchRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
    }
}
