package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 15/07/15
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdatePendingVouchersTransformTest {
    private static final String JOB_IDENTIFIER = "98929382938";

    @Test
    public void shouldTransformRequest()  throws ParseException {
        UpdatePendingVouchersTransform updatePendingVouchersTransform = new UpdatePendingVouchersTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        GetVouchersRequest getVouchersRequest = updatePendingVouchersTransform.transform(job);

        assertThat(getVouchersRequest.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(getVouchersRequest.getVoucherStatusFrom(), is(VoucherStatus.PENDING));
        assertThat(getVouchersRequest.getVoucherStatusTo(), is(VoucherStatus.NEW));
    }

}
