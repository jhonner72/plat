package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.DocumentExchangeEnum;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.common.voucher.WorkTypeEnum;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.QueryLinkTypeEnum;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created with IntelliJ IDEA.
 * User: au019670
 * Date: 29/09/15
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetEclVouchersTransformTest {
    private static final String JOB_IDENTIFIER = "111222333444";

    @Test
    public void shouldTransformRequest() {
        GetEclVouchersTransform getEclVouchersTransform = new GetEclVouchersTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);

        GetVouchersRequest request = getEclVouchersTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getMinReturnSize(), is(0));
        assertThat(request.getMaxReturnSize(), is(-1));
        assertThat(request.getVoucherStatusFrom(), is(VoucherStatus.ON_HOLD));
        assertThat(request.getVoucherStatusTo(), is(VoucherStatus.ON_HOLD));
        assertThat(request.getImageType(), is(ImageType.JPEG));
        assertThat(request.getQueryLinkType(), is(QueryLinkTypeEnum.NONE));
        assertThat(request.getVoucherTransfer(), is(DocumentExchangeEnum.VIF_OUTBOUND));
        assertThat(request.getTargetEndPoint(), is(WorkTypeEnum.NABCHQ_APOST.value()));
    }

}
