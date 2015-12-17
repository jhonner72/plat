package com.fujixerox.aus.integration.transform.outclearings;

import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersResponse;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by warwick on 8/05/2015.
 */
public class GetVouchersForImageExchangeResponseTransformTest {

    @Test
    public void shouldNotBeVouchersRemaining_whenSmallNumberAreReturned() throws Exception {
        execute(100, 0, false);
    }

    @Test
    public void shouldNotBeVouchersRemaining_whenAgencyBanks() throws Exception {
        execute(-1, 0, false);
    }

    @Test
    public void shouldBeVouchersRemaining_whenTierOneBanksAndMaximumExtracted() throws Exception {
        execute(100, 100, true);
    }

    protected void execute(int maxQuerySize, int voucherCount, boolean result) {
        Job job = new Job();

        Activity activity = new Activity();
        activity.setSubject("vouchers");
        activity.setPredicate("get");
        job.getActivities().add(activity);

        GetVouchersRequest request = new GetVouchersRequest();
        request.setMaxReturnSize(maxQuerySize);
        activity.setRequest(request);

        GetVouchersResponse response = new GetVouchersResponse();
        response.setVoucherCount(voucherCount);
        activity.setResponse(response);

        GetVouchersForImageExchangeResponseTransform getVouchersForImageExchangeResponseTransform = new GetVouchersForImageExchangeResponseTransform();
        Map<String, Object> values = getVouchersForImageExchangeResponseTransform.transform(job);

        Boolean vouchersRemaining = (Boolean) values.get("vouchersRemaining");
        assertThat(vouchersRemaining, is(notNullValue()));
        assertThat(vouchersRemaining, is(result));
    }
}