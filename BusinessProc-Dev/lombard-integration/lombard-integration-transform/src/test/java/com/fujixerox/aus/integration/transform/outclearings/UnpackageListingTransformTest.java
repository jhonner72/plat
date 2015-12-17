package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingRequest;
import org.junit.Test;

import java.text.ParseException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by vidyavenugopal on 7/07/15.
 */
public class UnpackageListingTransformTest {

    private static final String JOB_IDENTIFIER = "98929382938";

    @Test
    public void shouldTransformRequest() throws ParseException {
        UnPackageListingTransform unPackageListingTransform = new UnPackageListingTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        UnpackageListingRequest unpackageListingRequest = unPackageListingTransform.transform(job);

        assertThat(unpackageListingRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
    }
}
