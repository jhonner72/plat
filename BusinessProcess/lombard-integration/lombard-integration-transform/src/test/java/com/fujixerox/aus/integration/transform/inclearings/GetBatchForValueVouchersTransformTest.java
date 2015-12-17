package com.fujixerox.aus.integration.transform.inclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 27/04/15
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBatchForValueVouchersTransformTest {

    private static final String JOB_IDENTIFIER = "22042015-3EEB-4069-A2DD-SSS987654321";

    @Test
    public void shouldTransformRequest() {
        GetBatchForValueVouchersTransform getBatchForValueVouchersTransform = new GetBatchForValueVouchersTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        GetVouchersRequest request = getBatchForValueVouchersTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getImageType(), is(ImageType.JPEG));
        assertThat(request.getVoucherTransfer(), is(DocumentExchangeEnum.INWARD_FOR_VALUE));
        assertThat(request.getMinReturnSize(), is(0));
        assertThat(request.getMaxReturnSize(), is(-1));
        assertThat(request.getTargetEndPoint(), is(nullValue()));
        assertThat(request.getVoucherStatusFrom(), is(VoucherStatus.NEW));
        assertThat(request.getVoucherStatusTo(), is(VoucherStatus.IN_PROGRESS));
    }
}
