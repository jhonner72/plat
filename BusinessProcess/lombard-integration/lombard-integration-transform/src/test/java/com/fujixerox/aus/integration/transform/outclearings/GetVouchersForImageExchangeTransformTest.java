package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.voucher.VoucherStatus;
import com.fujixerox.aus.lombard.outclearings.imageexchange.ImageExchangeRequest;
import com.fujixerox.aus.lombard.repository.common.ImageType;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * User: Eloisa.Redubla
 * Date: 16/04/15
 */
public class GetVouchersForImageExchangeTransformTest {

    private static final String JOB_IDENTIFIER = "98929382938";

    @Test
    public void shouldTransformRequest_forAgencyBanks() {
        GetVouchersForImageExchangeTransform getVouchersForImageExchangeTransform = new GetVouchersForImageExchangeTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockRequest(-1, "MQB"));

        GetVouchersRequest request = getVouchersForImageExchangeTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getMinReturnSize(), is(1));
        assertThat(request.getMaxReturnSize(), is(-1));
        assertThat(request.getVoucherStatusFrom(), is(VoucherStatus.NEW));
        assertThat(request.getVoucherStatusTo(), is(VoucherStatus.IN_PROGRESS));
        assertThat(request.getImageType(), is(ImageType.JPEG));
        assertThat(request.getTargetEndPoint(), is("MQB"));
    }

    @Test
    public void shouldTransformRequest_forCuscal() {
        GetVouchersForImageExchangeTransform getVouchersForImageExchangeTransform = new GetVouchersForImageExchangeTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockRequest(-1, "CUS"));

        GetVouchersRequest request = getVouchersForImageExchangeTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getMinReturnSize(), is(1));
        assertThat(request.getMaxReturnSize(), is(-1));
        assertThat(request.getVoucherStatusFrom(), is(VoucherStatus.NEW));
        assertThat(request.getVoucherStatusTo(), is(VoucherStatus.IN_PROGRESS));
        assertThat(request.getImageType(), is(ImageType.JPEG));
        assertThat(request.getTargetEndPoint(), is("CUS"));
    }

    @Test
    public void shouldTransformRequest_forTierOne() {
        GetVouchersForImageExchangeTransform getVouchersForImageExchangeTransform = new GetVouchersForImageExchangeTransform();

        Job job = new Job();
        job.setJobIdentifier(JOB_IDENTIFIER);
        job.getActivities().add(mockRequest(101, "FIS"));

        GetVouchersRequest request = getVouchersForImageExchangeTransform.transform(job);

        assertThat(request.getJobIdentifier(), is(JOB_IDENTIFIER));
        assertThat(request.getMinReturnSize(), is(1));
        assertThat(request.getMaxReturnSize(), is(101));
        assertThat(request.getVoucherStatusFrom(), is(VoucherStatus.NEW));
        assertThat(request.getVoucherStatusTo(), is(VoucherStatus.IN_PROGRESS));
        assertThat(request.getImageType(), is(ImageType.JPEG));
        assertThat(request.getTargetEndPoint(), is("FIS"));
    }

    private Activity mockRequest(int maxQuerySize, String endpoint) {
        Activity activity = new Activity();
        activity.setSubject("outclearings");
        activity.setPredicate("imageexchange");
        ImageExchangeRequest request = new ImageExchangeRequest();
        request.setMaxQuerySize(maxQuerySize);
        request.setTargetEndPoint(endpoint);
        activity.setRequest(request);
        return activity;
    }
}
