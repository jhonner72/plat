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
public class GetVouchersForVifResponseTransformTest {

    @Test
    public void shouldNotBeVouchersRemaining_whenSmallNumberAreReturned() throws Exception {
        execute(false);
    }

    @Test
    public void shouldBeVouchersRemaining_whenGetResponseVoucherCountGreaterThanZero() throws Exception {
        execute(true);
    }

    protected void execute(boolean result) {
        Job job = new Job();

        Activity activity = new Activity();
        activity.setSubject("vouchers");
        activity.setPredicate("get");
        job.getActivities().add(activity);

        GetVouchersResponse response = new GetVouchersResponse();
        response.setVouchersRemaining(result);
        activity.setResponse(response);

        GetVouchersForValueInstructionFileResponseTransform transformer = new GetVouchersForValueInstructionFileResponseTransform();
        Map<String, Object> values = transformer.transform(job);

        Boolean vouchersRemaining = (Boolean) values.get("subVouchersRemaining");
        assertThat(vouchersRemaining, is(notNullValue()));
        assertThat(vouchersRemaining, is(result));
    }
}