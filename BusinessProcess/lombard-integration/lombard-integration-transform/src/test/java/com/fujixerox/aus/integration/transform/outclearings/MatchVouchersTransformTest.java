package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.outclearings.matchvoucher.MatchVoucherRequest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MatchVouchersTransformTest {
    private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";

    @Test
    public void shouldTransformRequest() {
        MatchVouchersTransform matchVouchersTransform = new MatchVouchersTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        MatchVoucherRequest request = matchVouchersTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
    }
}
